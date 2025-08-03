package Messages;

import Common.Order;

public class CancelOrderMessage extends Message {
    private final Order order;

    public CancelOrderMessage(MessageType type, Order order) {
        super(type);
        this.order = order;
    }

    public Order getOrder() {
        return order;
    }

    @Override
    public String toString() {
        return super.toString() + " order: " + order;
    }
}