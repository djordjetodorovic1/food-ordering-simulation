package Common;

import java.util.ArrayList;

public class Order {
    private int orderID;
    private int userID;
    private int restaurantID;
    private int courierID;
    private int preparationTime;
    private OrderState state;
    private ArrayList<OrderItem> orderItems = new ArrayList<>();

    public Order(int orderID, int userID, int restaurantID, ArrayList<OrderItem> orderItems) {
        this.orderID = Integer.parseInt(userID + "" + orderID);
        this.userID = userID;
        this.restaurantID = restaurantID;
        this.orderItems = orderItems;
        this.state = OrderState.NEW;
    }

    public int getOrderID() {
        return orderID;
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

    public ArrayList<OrderItem> getOrderItems() {
        return orderItems;
    }

    public int getPreparationTime() {
        return preparationTime;
    }

    public void setPreparationTime(int preparationTime) {
        this.preparationTime = preparationTime;
    }

    public OrderState getState() {
        return state;
    }

    public void setState(OrderState state) {
        this.state = state;
    }

    @Override
    public String toString() {
        return "Order{" +
                "orderID=" + orderID +
                ", userID=" + userID +
                ", restaurantID=" + restaurantID +
                ", courierID=" + courierID +
                ", preparationTime=" + preparationTime +
                ", state=" + state +
                ", orderItems=" + orderItems +
                '}';
    }
}