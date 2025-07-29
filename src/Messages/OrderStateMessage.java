package Messages;

import Common.OrderState;

public class OrderStateMessage extends Message {
    private int orderID;
    private int userID;
    private OrderState state;

    public OrderStateMessage(MessageType type, int orderID, int userID, OrderState state) {
        super(type);
        this.orderID = orderID;
        this.userID = userID;
        this.state = state;
    }

    public int getOrderID() {
        return orderID;
    }

    public int getUserID() {
        return userID;
    }

    public OrderState getState() {
        return state;
    }

    @Override
    public String toString() {
        return super.toString() + " orderID: " + orderID + " userID: " + userID + " state: " + state;
    }
}