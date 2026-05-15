package Common;

public class OrderItem {
    private final String itemName;
    private final int quantity;
    private final double price;

    public OrderItem(String line, int quantity) {
        String[] parts = line.split("-");

        this.itemName = parts[0].trim();

        double parsedPrice = 0;

        if (parts.length > 1) {
            String pricePart = parts[1]
                    .replace("KM", "")
                    .trim();

            try {
                parsedPrice = Double.parseDouble(pricePart);
            } catch (NumberFormatException e) {
                parsedPrice = 0;
            }
        }

        this.price = parsedPrice;
        this.quantity = quantity;
    }

    public int getQuantity() {
        return quantity;
    }

    @Override
    public String toString() {
        return itemName + " x" + quantity;
    }

    public double getPrice() { return price; }

}
