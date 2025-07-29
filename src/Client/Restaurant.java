package Client;

import Common.ClientType;
import Common.Order;
import Common.OrderItem;
import Common.OrderState;
import Messages.*;
import com.google.gson.Gson;
import javafx.stage.Stage;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

public class Restaurant {
    private String name;
    private int restaurantID;
    private ArrayList<String> menu = new ArrayList<>();
    private BlockingQueue<Order> pendingOrders = new LinkedBlockingQueue<>();
    private BlockingQueue<Order> ordersInProgress = new LinkedBlockingQueue<>();
    private int executionTime = 3000; // milliseconds
    private ExecutorService restaurantWorkersThreadPool;

    private String hostname;
    private int port;
    private Stage primaryStage;
    Gson gson = new Gson();

    private Socket socket;
    private BufferedReader fromServer;
    private PrintWriter toServer;

    public Restaurant(String name, File file, String hostname, int port, Stage primaryStage) throws IOException {
        this.name = name;
        this.hostname = hostname;
        this.port = port;
        this.primaryStage = primaryStage;
        this.restaurantWorkersThreadPool = Executors.newFixedThreadPool(2);

        try (Scanner sc = new Scanner(file)) {
            while (sc.hasNext())
                this.menu.addAll(Arrays.asList(sc.nextLine().split("\\s*,\\s*")));
        } catch (FileNotFoundException e) {
            SceneRestaurant.showAlert(e.getMessage());
        }

        socket = new Socket(this.hostname, this.port);
        toServer = new PrintWriter(socket.getOutputStream(), true);
        fromServer = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        System.err.println("Connected");
    }

    // nit za primanje poruka od servera i gui za restoran
    public void execute() {
        Thread receiverThread = new Thread(() -> {  // thread za primanje poruka od servera (GUI na main tredu)
            try {
                String responseJson;
                while ((responseJson = fromServer.readLine()) != null) {
                    System.out.println(responseJson); // test
                    Message msg = gson.fromJson(responseJson, Message.class);
                    if (msg.getType() == MessageType.LOGIN_RESPONSE)
                        handleLoginResponse(responseJson);
                    if (msg.getType() == MessageType.NEW_ORDER)
                        handleNewOrder(responseJson);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        receiverThread.setDaemon(true); // automatski se gasi kad se GUI zatvori
        receiverThread.start();

        String json = gson.toJson(new LogInRestaurantMessage(MessageType.LOGIN, ClientType.RESTAURANT, name, menu));
        toServer.println(json);
        System.out.println(json); // test

        primaryStage.setTitle("Food Ordering Simulation - RESTAURANT");
        SceneRestaurant.show(this, primaryStage);
    }
    // Napomena obezbjediti threadPool shutDown i zatvoriti konekcije (stream, socket)

    // postavlja ID koji dodjeljuje server
    private void handleLoginResponse(String responseJson) {
        LoginResponseMessage idMsg = gson.fromJson(responseJson, LoginResponseMessage.class);
        this.setRestaurantID(idMsg.getClientID());
        SceneRestaurant.updateID(idMsg.getClientID());
    }

    // Dodaje novu narudzbu
    public void handleNewOrder(String responseJson) {
        Order newOrder = gson.fromJson(responseJson, NewOrderMessage.class).getOrder();
        newOrder.setPreparationTime(executionTime * newOrder.getOrderItems().stream()
                .mapToInt(OrderItem::getQuantity).sum());
        // System.out.println(newOrder);
        pendingOrders.add(newOrder);
        prepareOrder(newOrder);
        SceneRestaurant.addNewOrder(newOrder);
    }

    // simulira pripremu narudzbe - azurira GUI i obavjestava user-a o stanju narudzbe
    public void prepareOrder(Order order) {
        Runnable assignment = () -> {
            pendingOrders.remove(order);
            ordersInProgress.add(order);
            order.setState(OrderState.PREPARING);
            SceneRestaurant.updateOrders(this);
            toServer.println(gson.toJson(new OrderStateMessage(MessageType.ORDER_STATE, order.getOrderID(), order.getUserID(), order.getState())));

            try {
                Thread.sleep(order.getPreparationTime());
            } catch (InterruptedException e) {
                e.printStackTrace();
                // obraditi ako se ne izvrsi
            }

            ordersInProgress.remove(order);
            order.setState(OrderState.WAITING_FOR_DELIVERY);
            SceneRestaurant.updateOrders(this);
            toServer.println(gson.toJson(new OrderStateMessage(MessageType.ORDER_STATE, order.getOrderID(), order.getUserID(), order.getState())));
        };

        restaurantWorkersThreadPool.submit(assignment);
    }

    public String getName() {
        return name;
    }

    public int getRestaurantID() {
        return restaurantID;
    }

    public void setRestaurantID(int restaurantID) {
        this.restaurantID = restaurantID;
    }

    public ArrayList<String> getMenu() {
        return menu;
    }

    public BlockingQueue<Order> getPendingOrders() {
        return pendingOrders;
    }

    public BlockingQueue<Order> getOrdersInProgress() {
        return ordersInProgress;
    }

    @Override
    public String toString() {
        return "Restaurant{" +
                "name='" + name + '\'' +
                ", restaurantID=" + restaurantID +
                ", menu=" + menu +
                ", ordersInProgress=" + ordersInProgress +
                ", hostname='" + hostname + '\'' +
                ", port=" + port +
                ", primaryStage=" + primaryStage +
                '}';
    }
}