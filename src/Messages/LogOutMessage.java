package Messages;

import Common.ClientType;

public class LogOutMessage extends Message {
    private ClientType clientType;
    private int clientID;

    public LogOutMessage(MessageType type, ClientType clientType, int clientID) {
        super(type);
        this.clientType = clientType;
        this.clientID = clientID;
    }

    public ClientType getClientType() {
        return clientType;
    }

    public int getClientID() {
        return clientID;
    }

    @Override
    public String toString() {
        return super.toString() + " ClientType: " + clientType + " ClientID: " + clientID;
    }
}