package Common;

public class Message {
    private MessageType type;
    private String userName;
    private ClientType clientType;

    public Message(MessageType type, String userName, ClientType clientType) {
        this.type = type;
        this.userName = userName;
        this.clientType = clientType;
    }
}