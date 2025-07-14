package Client;

import Common.Order;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

public class Restaurant {
    private String name;
    private int restaurantID;
    private ArrayList<String> menu = new ArrayList<>();
    private ArrayList<Order> ordersInProgress = new ArrayList<>();

    private String hostname;
    private int port;
    private Stage primaryStage;


    public Restaurant(String name, File file, String hostname, int port, Stage primaryStage) {
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
    }


    public void execute() {
        //test
        System.out.println(this);
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
