package Messages;

import Common.Order;

public class NewOrderMessage extends Message {
    private Order order;

    public NewOrderMessage(MessageType type, Order order) {
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