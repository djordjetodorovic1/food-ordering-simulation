package Client;

import Common.ClientType;
import Common.Order;
import Messages.LogInRestaurantMessage;
import Messages.MessageType;
import com.google.gson.Gson;
import javafx.stage.Stage;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class Restaurant {
    private String name;
    private int restaurantID;
    private ArrayList<String> menu = new ArrayList<>();
    private BlockingQueue<Order> pendingOrders = new LinkedBlockingQueue<>();
    private ArrayList<Order> ordersInProgress = new ArrayList<>();
    private int executionTime = 3000; // miliseconds

    private String hostname;
    private int port;
    private Stage primaryStage;

    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;

    public Restaurant(String name, File file, String hostname, int port, Stage primaryStage) throws IOException {
        this.name = name;
        this.hostname = hostname;
        this.port = port;
        this.primaryStage = primaryStage;

        try (Scanner sc = new Scanner(file)) {
            while (sc.hasNext())
                this.menu.addAll(Arrays.asList(sc.nextLine().split("\\s*,\\s*")));
        } catch (FileNotFoundException e) {
            SceneRestaurant.showAlert(e.getMessage());
        }

        socket = new Socket(this.hostname, this.port);
        out = new PrintWriter(socket.getOutputStream(), true);
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
    }


    public void execute() {
        Gson gson = new Gson();
        Thread receiverThread = new Thread(() -> {
            try {
                String response;
                while ((response = in.readLine()) != null) {
                    System.out.println(response); // test
                    //Platform.runLater(() -> ...);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        receiverThread.setDaemon(true); // automatski se gasi kad se GUI zatvori
        receiverThread.start();

        System.out.println("Connected");

        LogInRestaurantMessage message = new LogInRestaurantMessage(MessageType.LOGIN, ClientType.RESTAURANT, name, menu);
        String json = gson.toJson(message);
        System.out.println(json); // test
        primaryStage.setTitle("Food Ordering Simulation - RESTAURANT");
        SceneRestaurant.show(primaryStage, this);
    }

    public String getName() {
        return name;
    }

    public int getRestaurantID() {
        return restaurantID;
    }

    public ArrayList<String> getMenu() {
        return menu;
    }

    public BlockingQueue<Order> getPendingOrders() {
        return pendingOrders;
    }

    public ArrayList<Order> getOrdersInProgress() {
        return ordersInProgress;
    }

    public PrintWriter getOut() {
        return out;
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
