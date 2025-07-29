package Client;

import Common.ClientType;
import Common.RestaurantInfo;
import Messages.*;
import Common.Order;
import com.google.gson.Gson;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class User {
    private String userName;
    private int userID;
    private ArrayList<Order> activeOrders = new ArrayList<>();
    private ArrayList<Order> previousOrders = new ArrayList<>();
    private Set<RestaurantInfo> restaurants = new HashSet<>();
    private int orderIDCounter = 0;

    private String hostname;
    private int port;
    private Stage primaryStage;

    private Socket socket;
    private BufferedReader fromServer;
    private PrintWriter toServer;
    private Gson gson = new Gson();

    public User(String userName, String hostname, int port, Stage primaryStage) throws IOException {
        this.userName = userName;
        this.hostname = hostname;
        this.port = port;
        this.primaryStage = primaryStage;

        socket = new Socket(this.hostname, this.port);
        toServer = new PrintWriter(socket.getOutputStream(), true);
        fromServer = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        System.err.println("Connected");
    }

    // slicno kao kod restorana
    public void execute() {
        Thread receiverThread = new Thread(() -> {
            try {
                String responseJson;
                while ((responseJson = fromServer.readLine()) != null) {
                    System.out.println(responseJson); // test
                    Message msg = gson.fromJson(responseJson, Message.class);
                    if (msg.getType() == MessageType.LOGIN_RESPONSE)
                        handleLoginRespone(responseJson);
                    if (msg.getType() == MessageType.ORDER_STATE)
                        handleOrderState(responseJson);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        receiverThread.setDaemon(true); // automatski se gasi kad se GUI zatvori
        receiverThread.start();

        String json = gson.toJson(new LogInMessage(MessageType.LOGIN, ClientType.USER, userName));
        toServer.println(json);
        System.out.println(json); // test

        primaryStage.setTitle("Food Ordering Simulation - USER");
        SceneUser.show(primaryStage, this);
    }

    // postavlja ID i restorane
    private void handleLoginRespone(String responseJson) {
        LoginUserResponseMessage idMsg = gson.fromJson(responseJson, LoginUserResponseMessage.class);
        this.setUserID(idMsg.getClientID());
        this.setRestaurants(idMsg.getRestaurants());
        SceneUser.updateID(idMsg.getClientID());
        SceneUser.updateRestaurants(idMsg.getRestaurants());
    }

    // azurira stanje narudzbe
    private void handleOrderState(String responseJson) {
        OrderStateMessage idMsg = gson.fromJson(responseJson, OrderStateMessage.class);
        Order order = getActiveOrder(idMsg.getOrderID());
        order.setState(idMsg.getState());
        System.out.println(order);
        // dodati - azuriranje orderState u GUI
    }

    // salje serveru novu narudzbu
    public void sendNewOrder(Order order) {
        addActiveOrder(order);
        String json = gson.toJson(new NewOrderMessage(MessageType.NEW_ORDER, order));
        toServer.println(json);
        System.out.println(json);
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public int getUserID() {
        return userID;
    }

    public void setUserID(int userID) {
        this.userID = userID;
    }

    public int getOrderIDCounter() {
        this.orderIDCounter++;
        return orderIDCounter;
    }

    public Set<RestaurantInfo> getRestaurants() {
        return restaurants;
    }

    public Set<RestaurantInfo> refreshRestaurants() {
        // trenutno rjesenje - treba ponovo da zatrazi listu restorana od servera
        return restaurants;
    }

    public void setRestaurants(Set<RestaurantInfo> restaurants) {
        this.restaurants = restaurants;
    }

    public ArrayList<Order> getActiveOrders() {
        return activeOrders;
    }

    public void addActiveOrder(Order order) {
        this.activeOrders.add(order);
    }

    public Order getActiveOrder(int orderID) {
        return this.activeOrders.stream().filter(o -> o.getOrderID() == orderID).findFirst().orElse(null);
    }

    public ArrayList<Order> getPreviousOrders() {
        return previousOrders;
    }

    public void addPreviousOrders(Order order) {
        this.previousOrders.add(order);
    }

    public void finishedOrder(Order order) {
        this.previousOrders.add(order);
        this.activeOrders.remove(order);
    }
}