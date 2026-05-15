package Common;

public enum OrderState {
    NEW, PREPARING, WAITING_FOR_DELIVERY, DELIVERING, CANCELED, FAILED, DELIVERED;

    @Override
    public String toString() {
        return switch (this) {
            case NEW -> "Nova";
            case PREPARING -> "U pripremi";
            case WAITING_FOR_DELIVERY -> "Na čekanju za dostavu";
            case DELIVERING -> "Dostavlja se";
            case DELIVERED -> "Isporučeno";
            case CANCELED -> "Otkazano";
            case FAILED -> "Neuspješno";
        };
    }
}