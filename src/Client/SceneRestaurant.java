package Client;

import javafx.scene.control.Alert;

public class SceneRestaurant {
    public static void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setContentText(message);
        alert.showAndWait();
    }
}