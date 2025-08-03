package Messages;

import Common.ClientType;

import java.util.ArrayList;

public class LogInRestaurantMessage extends LogInMessage {
    private final ArrayList<String> menu;

    public LogInRestaurantMessage(MessageType type, ClientType clientType, String userName, ArrayList<String> menu) {
        super(type, clientType, userName);
        this.menu = menu;
    }

    public ArrayList<String> getMenu() {
        return menu;
    }

    @Override
    public String toString() {
        return super.toString() + " menu: " + menu;
    }
}