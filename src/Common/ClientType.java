package Common;

public enum ClientType {
    USER, RESTAURANT, COURIER;

    @Override
    public String toString() {
        String s = name().toLowerCase();
        return s.substring(0, 1).toUpperCase() + s.substring(1);
    }
}
