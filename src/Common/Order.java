package Common;

import java.util.ArrayList;

public class Order {
    private int userID;
    private int restaurantID;
    private int courierID;
    private int preparationTime;
    private ArrayList<OrderItem> orderItems = new ArrayList<>();

    public Order(int userID, int restaurantID, ArrayList<OrderItem> orderItems) {
        this.userID = userID;
        this.restaurantID = restaurantID;
        this.orderItems = orderItems;
    }

    public int getUserID() {
        return userID;
    }

    public int getRestaurantID() {
        return restaurantID;
    }

    public int getCourierID() {
        return courierID;
    }

    public int getPreparationTime() {
        return preparationTime;
    }

    public ArrayList<OrderItem> getOrderItems() {
        return orderItems;
    }
}