package Client;

import Common.ClientType;
import Common.OrderState;
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
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class User {
    private final String userName;
    private int userID;
    private BlockingQueue<Order> activeOrders = new LinkedBlockingQueue<>();
    private ArrayList<Order> previousOrders = new ArrayList<>();
    private Set<RestaurantInfo> restaurants = new HashSet<>();
    private int orderIDCounter = 0;

    private final String hostname;
    private final int port;
    private final Stage primaryStage;
    private final Gson gson = new Gson();

    private final Socket socket;
    private final BufferedReader fromServer;
    private final PrintWriter toServer;

    public User(String userName, String hostname, int port, Stage primaryStage) throws IOException {
        this.userName = userName;
        this.hostname = hostname;
        this.port = port;
        this.primaryStage = primaryStage;

        this.socket = new Socket(this.hostname, this.port);
        this.toServer = new PrintWriter(socket.getOutputStream(), true);
        this.fromServer = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        System.err.println("User started...");
    }

    // slicno kao kod restorana
    public void execute() {
        Thread receiverThread = new Thread(() -> {
            try {
                String responseJson;
                while ((responseJson = fromServer.readLine()) != null) {
                    Message msg = gson.fromJson(responseJson, Message.class);
                    if (msg.getType() == MessageType.LOGIN_RESPONSE)
                        handleLoginRespone(responseJson);
                    else if (msg.getType() == MessageType.ORDER_STATE)
                        handleOrderState(responseJson);
                    else if (msg.getType() == MessageType.RESTAURANT_INFO_REFRESH)
                        handleRestaurantRefresh(responseJson);
                    else if (msg.getType() == MessageType.LOGOUT)
                        handleLostOrders(responseJson);
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

        toServer.println(gson.toJson(new LogInMessage(MessageType.LOGIN, ClientType.USER, userName)));
        primaryStage.setTitle("Food Ordering Simulation - USER");
        primaryStage.setOnCloseRequest(event -> shutdown());
        SceneUser.show(primaryStage, this);
    }

    // postavlja ID i restorane
    private void handleLoginRespone(String responseJson) {
        LoginUserResponseMessage idMsg = gson.fromJson(responseJson, LoginUserResponseMessage.class);
        System.out.println(idMsg);
        this.userID = idMsg.getClientID();
        this.setRestaurants(idMsg.getRestaurants());
        SceneUser.updateID(idMsg.getClientID());
        SceneUser.updateRestaurants(idMsg.getRestaurants());
    }

    // azurira stanje narudzbe
    private void handleOrderState(String responseJson) {
        System.out.println(gson.fromJson(responseJson, OrderStateMessage.class));
        Order orderMsg = gson.fromJson(responseJson, OrderStateMessage.class).getOrder();
        Order order = getActiveOrder(orderMsg.getOrderID());
        if (order != null) {
            order.setState(orderMsg.getState());
            if (orderMsg.getState() == OrderState.DELIVERING)
                order.setCourierID(orderMsg.getCourierID());
            if (orderMsg.getState() == OrderState.DELIVERED)
                this.finishedOrder(order);

            SceneUser.updateOrders(this);
            SceneUser.updateOrder(order);
        }
    }

    private void handleRestaurantRefresh(String responseJson) {
        this.restaurants = gson.fromJson(responseJson, RestaurantInfoRefreshMessage.class).getRestaurants();
        SceneUser.updateRestaurants(restaurants);
    }

    // azurira narudzbe u slucaju prekida veze sa kurirom ili restoranom
    private void handleLostOrders(String responseJson) {
        LogOutMessage msg = gson.fromJson(responseJson, LogOutMessage.class);
        System.out.println(msg);
        if (msg.getClientType() == ClientType.COURIER) {
            Order failedOrder = this.getActiveOrder(msg.getOrders().iterator().next().getOrderID());
            if (failedOrder != null) {
                failedOrder.setState(OrderState.FAILED);
                finishedOrder(failedOrder);
            }
        } else if (msg.getClientType() == ClientType.RESTAURANT) {
            restaurants.remove(restaurants.stream().filter(r -> r.getRestaurantID() == msg.getClientID()).findFirst().orElse(null));
            SceneUser.updateRestaurants(restaurants);
            for (Order order : activeOrders) {
                if (order.getRestaurantID() == msg.getClientID() && order.getCourierID() == 0 && order.getState() != OrderState.WAITING_FOR_DELIVERY) {
                    order.setState(OrderState.FAILED);
                    finishedOrder(order);
                }
            }
        }
        SceneUser.updateOrders(this);
    }

    // salje serveru novu narudzbu
    public void sendNewOrder(Order order) {
        addActiveOrder(order);
        toServer.println(gson.toJson(new NewOrderMessage(MessageType.NEW_ORDER, order)));
    }

    // otkazivanje narudzbe
    public void cancelOrder(Order order) {
        order.setState(OrderState.CANCELED);
        finishedOrder(order);
        toServer.println(gson.toJson(new CancelOrderMessage(MessageType.CANCELED_ORDER, order)));
    }

    // salje serveru zahtjev o novom spisku restorana
    public void refreshRestaurants() {
        toServer.println(gson.toJson(new Message(MessageType.RESTAURANT_INFO_REFRESH)));
    }

    private void shutdown() {
        try {
            toServer.println(gson.toJson(new LogOutMessage(MessageType.LOGOUT, ClientType.USER, this.userID, new HashSet<>(activeOrders))));
            if (toServer != null)
                toServer.close();
            if (fromServer != null)
                fromServer.close();
            if (socket != null && !socket.isClosed())
                socket.close();
            System.err.println("User stopped...");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getUserName() {
        return userName;
    }

    public int getUserID() {
        return userID;
    }

    public int getOrderIDCounter() {
        this.orderIDCounter++;
        return orderIDCounter;
    }

    public Set<RestaurantInfo> getRestaurants() {
        return restaurants;
    }

    private void setRestaurants(Set<RestaurantInfo> restaurants) {
        this.restaurants = restaurants;
    }

    public BlockingQueue<Order> getActiveOrders() {
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

    private void finishedOrder(Order order) {
        this.previousOrders.add(order);
        this.activeOrders.remove(order);
    }
}