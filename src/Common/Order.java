package Common;

import java.util.ArrayList;

public class Order {
    private final int orderID;
    private final int userID;
    private final int restaurantID;
    private int courierID;
    private int preparationTime;
    private OrderState state;
    private final ArrayList<OrderItem> orderItems;

    public Order(int orderID, int userID, int restaurantID, ArrayList<OrderItem> orderItems) {
        this.orderID = Integer.parseInt(userID + "" + orderID);
        this.userID = userID;
        this.restaurantID = restaurantID;
        this.orderItems = orderItems;
        this.state = OrderState.NEW;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null || getClass() != obj.getClass())
            return false;
        Order other = (Order) obj;
        return this.orderID == other.orderID;
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(orderID);
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

    public void setCourierID(int courierID) {
        this.courierID = courierID;
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
        return "Order->" +
                " orderID: " + orderID +
                ", userID: " + userID +
                ", restaurantID: " + restaurantID +
                ", courierID: " + courierID +
                ", state: " + state;
    }
}