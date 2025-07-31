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
import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

public class Restaurant {
    private String name;
    private int restaurantID;
    private ArrayList<String> menu = new ArrayList<>();
    private BlockingQueue<Order> pendingOrders = new LinkedBlockingQueue<>();
    private Set<Order> ordersInProgress = Collections.synchronizedSet(new HashSet<>());
    private int executionTime;
    private ExecutorService restaurantWorkersThreadPool;

    private String hostname;
    private int port;
    private Stage primaryStage;
    private Gson gson = new Gson();

    private Socket socket;
    private BufferedReader fromServer;
    private PrintWriter toServer;

    public Restaurant(String name, File file, String hostname, int port, Stage primaryStage) throws IOException {
        this.name = name;
        this.hostname = hostname;
        this.port = port;
        this.primaryStage = primaryStage;
        this.executionTime = 3000; // milliseconds
        this.restaurantWorkersThreadPool = Executors.newFixedThreadPool(2);

        try (Scanner sc = new Scanner(file)) {
            while (sc.hasNext())
                this.menu.addAll(Arrays.asList(sc.nextLine().split("\\s*,\\s*")));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        this.socket = new Socket(this.hostname, this.port);
        this.toServer = new PrintWriter(socket.getOutputStream(), true);
        this.fromServer = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        System.err.println("Restaurant started...");
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

        toServer.println(gson.toJson(new LogInRestaurantMessage(MessageType.LOGIN, ClientType.RESTAURANT, name, menu)));
        primaryStage.setTitle("Food Ordering Simulation - RESTAURANT");
        primaryStage.setOnCloseRequest(event -> shutdown());
        SceneRestaurant.show(this, primaryStage);
    }

    // postavlja ID koji dodjeljuje server
    private void handleLoginResponse(String responseJson) {
        int clientID = gson.fromJson(responseJson, LoginResponseMessage.class).getClientID();
        this.setRestaurantID(clientID);
        SceneRestaurant.updateID(clientID);
    }

    // Dodaje novu narudzbu
    private void handleNewOrder(String responseJson) {
        Order newOrder = gson.fromJson(responseJson, NewOrderMessage.class).getOrder();
        newOrder.setPreparationTime(executionTime * newOrder.getOrderItems().stream()
                .mapToInt(OrderItem::getQuantity).sum());
        // System.out.println(newOrder);
        pendingOrders.add(newOrder);
        prepareOrder(newOrder);
        SceneRestaurant.addNewOrder(newOrder);
    }

    // simulira pripremu narudzbe - azurira GUI i obavjestava user-a o stanju narudzbe
    private void prepareOrder(Order order) {
        Runnable assignment = () -> {
            pendingOrders.remove(order);
            ordersInProgress.add(order);
            order.setState(OrderState.PREPARING);
            SceneRestaurant.updateOrders(this);
            toServer.println(gson.toJson(new OrderStateMessage(MessageType.ORDER_STATE, order)));

            try {
                Thread.sleep(order.getPreparationTime());
            } catch (InterruptedException e) {
                System.err.println("Restaurant worker stopped...");
            }

            ordersInProgress.remove(order);
            order.setState(OrderState.WAITING_FOR_DELIVERY);
            SceneRestaurant.updateOrders(this);
            toServer.println(gson.toJson(new OrderStateMessage(MessageType.ORDER_STATE, order)));
        };

        restaurantWorkersThreadPool.submit(assignment);
    }

    private void handleLostUser(String responseJson) {
        LogOutMessage msg = gson.fromJson(responseJson, LogOutMessage.class);
        int userID = msg.getClientID();
        // dodati Future kad uradim user-a
    }

    private void handleCanceledOrder(String responseJson) {
        // dodati i za otkazivanje narudzbe
    }

    private void shutdown() {
        try {
            toServer.println(gson.toJson(new LogOutMessage(MessageType.LOGOUT, ClientType.RESTAURANT, this.restaurantID)));
            restaurantWorkersThreadPool.shutdownNow();
            if (toServer != null)
                toServer.close();
            if (fromServer != null)
                fromServer.close();
            if (socket != null && !socket.isClosed())
                socket.close();
            System.err.println("Restaurant closed...");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getName() {
        return name;
    }

    public int getRestaurantID() {
        return restaurantID;
    }

    private void setRestaurantID(int restaurantID) {
        this.restaurantID = restaurantID;
    }

    public ArrayList<String> getMenu() {
        return menu;
    }

    public BlockingQueue<Order> getPendingOrders() {
        return pendingOrders;
    }

    public Set<Order> getOrdersInProgress() {
        return ordersInProgress;
    }
}