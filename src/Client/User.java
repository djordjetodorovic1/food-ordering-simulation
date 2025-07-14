package Client;

import Common.ClientType;
import Common.Message;
import Common.MessageType;
import Common.Order;
import com.google.gson.Gson;
import javafx.stage.Stage;

import java.util.ArrayList;

public class User {
    private String userName;
    private int UserID;
    private ArrayList<Order> activeOrders = new ArrayList<>();
    private ArrayList<Order> previousOrders = new ArrayList<>();

    private String hostname;
    private int port;
    private Stage primaryStage;

    public User(String userName, String hostname, int port, Stage primaryStage) {
        this.userName = userName;
        this.hostname = hostname;
        this.port = port;
        this.primaryStage = primaryStage;
    }

    public void execute() {
        // dodati soket i nit za mrezu pa onda gui ScenaUser na glavnoj niti
        // test json
        Gson gson = new Gson();
        Message message = new Message(MessageType.LOGIN, userName, ClientType.USER);
        String json = gson.toJson(message);
        System.out.println(json);
        primaryStage.setTitle("Food Ordering Simulation - USER");
        SceneUser.show(primaryStage, this);
        /*
        try (Socket clientSocket = new Socket(this.hostname, this.port)) {


        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
         */
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
        return UserID;
    }

    public void setUserID(int userID) {
        UserID = userID;
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