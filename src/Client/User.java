package Client;

import Common.ClientType;
import Messages.LogInMessage;
import Messages.MessageType;
import Common.Order;
import com.google.gson.Gson;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;

public class User {
    private String userName;
    private int userID;
    private ArrayList<Order> activeOrders = new ArrayList<>();
    private ArrayList<Order> previousOrders = new ArrayList<>();

    private String hostname;
    private int port;
    private Stage primaryStage;

    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;

    public User(String userName, String hostname, int port, Stage primaryStage) throws IOException {
        this.userName = userName;
        this.hostname = hostname;
        this.port = port;
        this.primaryStage = primaryStage;

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

        LogInMessage message = new LogInMessage(MessageType.LOGIN, ClientType.RESTAURANT, userName);
        String json = gson.toJson(message);
        System.out.println(json); // test
        primaryStage.setTitle("Food Ordering Simulation - USER");
        SceneUser.show(primaryStage, this);
    }

    public ArrayList<Restaurant> getRestaurants() {
        return new ArrayList<>();
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

    public ArrayList<Order> getActiveOrders() {
        return activeOrders;
    }

    public void setActiveOrders(ArrayList<Order> activeOrders) {
        this.activeOrders = activeOrders;
    }

    public ArrayList<Order> getPreviousOrders() {
        return previousOrders;
    }

    public void setPreviousOrders(ArrayList<Order> previousOrders) {
        this.previousOrders = previousOrders;
    }
}