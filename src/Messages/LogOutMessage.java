package Messages;

import Common.ClientType;
import Common.Order;

import java.util.HashSet;
import java.util.Set;

public class LogOutMessage extends Message {
    private final ClientType clientType;
    private final int clientID;
    private Set<Order> orders = new HashSet<>();

    public LogOutMessage(MessageType type, ClientType clientType, int clientID) {
        super(type);
        this.clientType = clientType;
        this.clientID = clientID;
    }

    public LogOutMessage(MessageType type, ClientType clientType, int clientID, Set<Order> orders) {
        this(type, clientType, clientID);
        this.orders = orders;
    }

    public ClientType getClientType() {
        return clientType;
    }

    public int getClientID() {
        return clientID;
    }

    public Set<Order> getOrders() {
        return orders;
    }

    @Override
    public String toString() {
        return super.toString() + " clientType: " + clientType + " clientID: " + clientID + " orders: " + orders;
    }
}