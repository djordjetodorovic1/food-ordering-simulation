package Client;

import Common.ClientType;
import Common.Order;
import Common.OrderState;
import Messages.*;
import com.google.gson.Gson;
import javafx.stage.Stage;

import java.io.*;
import java.net.Socket;
import java.util.Set;

public class Courier {
    private final String userName;
    private int courierID;
    private final int executionTime;
    private Order order;

    private final String hostname;
    private final int port;
    private final Stage primaryStage;
    private final Gson gson = new Gson();
    private Thread deliveryThread;

    private final Socket socket;
    private final BufferedReader fromServer;
    private final PrintWriter toServer;

    public Courier(String name, String hostname, int port, Stage primaryStage) throws IOException {
        this.userName = name;
        this.hostname = hostname;
        this.port = port;
        this.primaryStage = primaryStage;
        this.executionTime = 7000; // milliseconds

        this.socket = new Socket(this.hostname, this.port);
        this.toServer = new PrintWriter(socket.getOutputStream(), true);
        this.fromServer = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        System.err.println("Courier started...");
    }

    // nit za primanje poruka od servera i gui
    public void execute() {
        Thread receiverThread = new Thread(() -> {
            try {
                String responseJson;
                while ((responseJson = fromServer.readLine()) != null) {
                    Message msg = gson.fromJson(responseJson, Message.class);
                    if (msg.getType() == MessageType.LOGIN_RESPONSE)
                        handleLoginResponse(responseJson);
                    else if (msg.getType() == MessageType.ORDER_STATE)
                        handleNewOrder(responseJson);
                    else if (msg.getType() == MessageType.CANCELED_ORDER)
                        handleCanceledOrder(responseJson);
                    else if (msg.getType() == MessageType.LOGOUT)
                        handleLostUser(responseJson);
                }
            } catch (IOException e) {
                if (socket.isClosed())
                    System.err.println("Connection closed...");
                else {
                    System.err.println("Server not responding...");
                    SceneRestaurant.showAlert("Server not responding... Connection closed...");
                    shutdown();
                }
            }
        });
        receiverThread.setDaemon(true); // automatski se gasi kad se GUI zatvori
        receiverThread.start();

        toServer.println(gson.toJson(new LogInMessage(MessageType.LOGIN, ClientType.COURIER, userName)));
        primaryStage.setTitle("Food Ordering Simulation - COURIER");
        primaryStage.setOnCloseRequest(event -> shutdown());
        SceneCourier.show(this, primaryStage);
    }

    // postavlja ID koji dodjeljuje server
    private void handleLoginResponse(String responseJson) {
        System.out.println(gson.fromJson(responseJson, LoginResponseMessage.class));
        int clientID = gson.fromJson(responseJson, LoginResponseMessage.class).getClientID();
        this.setCourierID(clientID);
        SceneCourier.updateID(clientID);
    }

    // simulira dostavu
    private void handleNewOrder(String responseJson) {
        System.out.println(gson.fromJson(responseJson, OrderStateMessage.class));
        order = gson.fromJson(responseJson, OrderStateMessage.class).getOrder();
        order.setCourierID(courierID);
        order.setState(OrderState.DELIVERING);
        SceneCourier.updateOrder(order);
        toServer.println(gson.toJson(new OrderStateMessage(MessageType.ORDER_STATE, order)));

        deliveryThread = new Thread(() -> {
            try {
                Thread.sleep(this.executionTime); // simulacija dostave
                order.setState(OrderState.DELIVERED);
                toServer.println(gson.toJson(new OrderStateMessage(MessageType.ORDER_STATE, order)));
            } catch (InterruptedException | NullPointerException e) {
                System.err.println("Delivering interrupted...");
            } finally {
                SceneCourier.clearOrder();
                order = null;
            }
        });
        deliveryThread.start();
    }

    // prekida dostavu u slucaju otkazivanja
    private void handleCanceledOrder(String responseJson) {
        System.out.println(gson.fromJson(responseJson, CancelOrderMessage.class));
        if (order != null && gson.fromJson(responseJson, CancelOrderMessage.class).getOrder().equals(order))
            if (deliveryThread != null && deliveryThread.isAlive())
                deliveryThread.interrupt();
    }

    // prekida dostavu u slucaju iskljucivanja User-a
    private void handleLostUser(String responseJson) {
        System.out.println(gson.fromJson(responseJson, LogOutMessage.class));
        if (order != null && gson.fromJson(responseJson, LogOutMessage.class).getOrders().contains(order))
            if (deliveryThread != null && deliveryThread.isAlive())
                deliveryThread.interrupt();
    }

    private void shutdown() {
        try {
            if (order != null)
                toServer.println(gson.toJson(new LogOutMessage(MessageType.LOGOUT, ClientType.COURIER, this.courierID, Set.of(order))));
            else
                toServer.println(gson.toJson(new LogOutMessage(MessageType.LOGOUT, ClientType.COURIER, this.courierID)));

            if (toServer != null)
                toServer.close();
            if (fromServer != null)
                fromServer.close();
            if (socket != null && !socket.isClosed())
                socket.close();
            System.err.println("Courier stopped...");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getUserName() {
        return userName;
    }

    public int getCourierID() {
        return courierID;
    }

    public void setCourierID(int courierID) {
        this.courierID = courierID;
    }
}