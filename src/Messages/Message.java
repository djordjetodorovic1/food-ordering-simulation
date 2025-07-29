package Messages;

public class Message {
    private MessageType type;

    public Message(MessageType type) {
        this.type = type;
    }

    public MessageType getType() {
        return type;
    }

    @Override
    public String toString() {
        return "Message: " + type;
    }
}