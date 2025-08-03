package Messages;

import Common.ClientType;

public class LogInMessage extends Message {
    private final ClientType clientType;
    private final String userName;

    public LogInMessage(MessageType type, ClientType clientType, String userName) {
        super(type);
        this.clientType = clientType;
        this.userName = userName;
    }

    public ClientType getClientType() {
        return clientType;
    }

    public String getUserName() {
        return userName;
    }

    @Override
    public String toString() {
        return super.toString() + " clientType: " + clientType + " userName: " + userName;
    }
}