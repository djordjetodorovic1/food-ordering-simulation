package Messages;

import Common.RestaurantInfo;

import java.util.Set;

public class RestaurantInfoRefreshMessage extends Message {
    private final Set<RestaurantInfo> restaurants;

    public RestaurantInfoRefreshMessage(MessageType type, Set<RestaurantInfo> restaurants) {
        super(type);
        this.restaurants = restaurants;
    }

    public Set<RestaurantInfo> getRestaurants() {
        return restaurants;
    }

    @Override
    public String toString() {
        return super.toString() + " restaurants: " + restaurants;
    }
}