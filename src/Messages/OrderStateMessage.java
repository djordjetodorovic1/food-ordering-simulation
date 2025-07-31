package Messages;

import Common.Order;

public class OrderStateMessage extends Message {
    private Order order;

    public OrderStateMessage(MessageType type, Order order) {
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