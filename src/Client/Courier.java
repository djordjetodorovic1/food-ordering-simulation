package Client;

import Common.ClientType;
import Common.Order;
import Common.OrderState;
import Messages.*;
import com.google.gson.Gson;
import javafx.stage.Stage;

import java.io.*;
import java.net.Socket;

public class Courier {
    private String userName;
    private int courierID;
    private int executionTime;

    private String hostname;
    private int port;
    private Stage primaryStage;
    private Gson gson = new Gson();

    private Socket socket;
    private BufferedReader fromServer;
    private PrintWriter toServer;

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
                    System.out.println(responseJson); // test
                    Message msg = gson.fromJson(responseJson, Message.class);
                    if (msg.getType() == MessageType.LOGIN_RESPONSE)
                        handleLoginResponse(responseJson);
                    if (msg.getType() == MessageType.ORDER_STATE)
                        handleNewOrder(responseJson);
                    if (msg.getType() == MessageType.LOGOUT)
                        handleLostUser(responseJson);
                    if (msg.getType() == MessageType.CANCELED_ORDER)
                        handleCanceledOrder(responseJson);
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
        int clientID = gson.fromJson(responseJson, LoginResponseMessage.class).getClientID();
        this.setCourierID(clientID);
        SceneCourier.updateID(clientID);
    }

    // simulira dostavu
    private void handleNewOrder(String responseJson) {
        Order order = gson.fromJson(responseJson, OrderStateMessage.class).getOrder();
        order.setCourierID(courierID);
        order.setState(OrderState.DELIVERING);
        SceneCourier.updateOrder(order);
        // System.out.println(order);
        toServer.println(gson.toJson(new OrderStateMessage(MessageType.ORDER_STATE, order)));

        try {
            Thread.sleep(this.executionTime);
        } catch (InterruptedException e) {
            System.err.println("Delivering interrupted...");
        }

        order.setState(OrderState.DELIVERED);
        SceneCourier.clearOrder();
        toServer.println(gson.toJson(new OrderStateMessage(MessageType.ORDER_STATE, order)));
    }

    private void handleLostUser(String responseJson) {
        LogOutMessage msg = gson.fromJson(responseJson, LogOutMessage.class);
        int userID = msg.getClientID();
        // dodati
    }

    private void handleCanceledOrder(String responseJson) {
        // dodati i za otkazivanje narudzbe
    }

    private void shutdown() {
        try {
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