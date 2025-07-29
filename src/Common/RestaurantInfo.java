package Common;

import java.util.ArrayList;

public class RestaurantInfo {
    private int restaurantID;
    private String restaurantName;
    private ArrayList<String> menu;

    public RestaurantInfo(int restaurantID, String restaurantName, ArrayList<String> menu) {
        this.restaurantID = restaurantID;
        this.restaurantName = restaurantName;
        this.menu = menu;
    }

    public int getRestaurantID() {
        return restaurantID;
    }

    public String getRestaurantName() {
        return restaurantName;
    }

    public ArrayList<String> getMenu() {
        return menu;
    }

    @Override
    public String toString() {
        return "restaurant: '" + restaurantName + "\'(ID" + restaurantID + ")" + ", menu=" + menu;
    }
}