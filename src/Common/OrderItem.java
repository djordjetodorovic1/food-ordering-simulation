package Common;

public class OrderItem {
    private final String itemName;
    private final int quantity;

    public OrderItem(String itemName, int quantity) {
        this.itemName = itemName;
        this.quantity = quantity;
    }

    public int getQuantity() {
        return quantity;
    }

    @Override
    public String toString() {
        return " ->" + itemName + " x" + quantity;
    }
}