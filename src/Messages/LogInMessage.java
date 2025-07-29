package Messages;

import Common.ClientType;

public class LogInMessage extends Message {
    private ClientType clientType;
    private String userName;

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
        return super.toString() + " ClientType: " + clientType + " UserName: " + userName;
    }
}