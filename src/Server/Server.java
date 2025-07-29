package Server;

import Common.Order;
import Common.RestaurantInfo;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class Server {
    public static final int PORT = 12345;

    public static void main(String[] args) {
        Server server = new Server(PORT);
        server.execute();
    }

    private final int port;
    private Set<ClientHandler> users = Collections.synchronizedSet(new HashSet<>());
    private Set<RestaurantInfo> restaurantInfos = Collections.synchronizedSet(new HashSet<>());
    private Set<ClientHandler> restaurants = Collections.synchronizedSet(new HashSet<>());
    private Set<ClientHandler> couriers = Collections.synchronizedSet(new HashSet<>());
    private BlockingQueue<Order> orders = new LinkedBlockingQueue<>();
    private int counterID = 1;

    public Server(int port) {
        this.port = port;
    }

    // prihvata nove klijente, za svakog kreira novu nit i dodjeljuje ID
    public void execute() {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.err.println("Server listening on port " + port);
            while (true) {
                try {
                    Socket client = serverSocket.accept();
                    System.err.println("Client connected");

                    ClientHandler clientHandler = new ClientHandler(this, client, counterID);
                    clientHandler.start();
                    counterID++;
                } catch (IOException e) {
                    System.err.println("Client interrupted");
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Set<ClientHandler> getUsers() {
        return users;
    }

    public ClientHandler getUser(int userID) {
        return users.stream().filter(u -> u.getClientID() == userID).findFirst().orElse(null);
    }

    public void addUser(ClientHandler user) {
        this.users.add(user);
    }

    public void removeUser(ClientHandler user) {
        this.users.remove(user);
    }

    public Set<RestaurantInfo> getRestaurantInfos() {
        return restaurantInfos;
    }

    // restaurant for order
    public ClientHandler getRestaurant(int restaurantID) {
        return restaurants.stream().filter(r -> r.getClientID() == restaurantID).findFirst().orElse(null);
    }

    public void addRestaurant(ClientHandler restaurant, ArrayList<String> menu) {
        this.restaurantInfos.add(new RestaurantInfo(restaurant.getClientID(), restaurant.getUserName(), menu));
        this.restaurants.add(restaurant);
    }

    public void removeRestaurant(RestaurantInfo restaurant) {
        this.restaurantInfos.remove(restaurant);
    }

    public Set<ClientHandler> getCouriers() {
        return couriers;
    }

    public void addCourier(ClientHandler courier) {
        this.couriers.add(courier);
    }

    public void removeCourier(ClientHandler courier) {
        this.couriers.remove(courier);
    }

    public Order getOrder() throws InterruptedException {
        return orders.take();
    }

    public void addOrders(Order order) {
        this.orders.add(order);
    }
}