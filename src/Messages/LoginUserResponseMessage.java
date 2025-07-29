package Messages;

import Common.RestaurantInfo;

import java.util.Set;

public class LoginUserResponseMessage extends LoginResponseMessage {
    private Set<RestaurantInfo> restaurants;

    public LoginUserResponseMessage(MessageType type, int clientID, Set<RestaurantInfo> restaurants) {
        super(type, clientID);
        this.restaurants = restaurants;
    }

    public Set<RestaurantInfo> getRestaurants() {
        return restaurants;
    }

    @Override
    public String toString() {
        return " restaurants: " + restaurants;
    }
}