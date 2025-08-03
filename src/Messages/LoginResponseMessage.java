package Messages;

public class LoginResponseMessage extends Message {
    private final int clientID;

    public LoginResponseMessage(MessageType type, int clientID) {
        super(type);
        this.clientID = clientID;
    }

    public int getClientID() {
        return clientID;
    }

    @Override
    public String toString() {
        return super.toString() + " clientID: " + clientID;
    }
}