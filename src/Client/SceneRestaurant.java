package Client;

import Common.Order;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class SceneRestaurant {
    public static void show(Stage primaryStage, Restaurant restaurant) {
        VBox root = new VBox(40);
        root.setPadding(new Insets(20, 20, 20, 20));

        Label lblTitle = new Label("Restoran: " + restaurant.getName() + " (ID: " + restaurant.getRestaurantID() + ")");
        Label lblPending = new Label("Na cekanju...");
        Label lblExecution = new Label("U pripremi");

        ListView<Order> listPendingOrders = new ListView<>();
        ListView<Order> listExecutingOrders = new ListView<>();
        listPendingOrders.getItems().setAll(restaurant.getPendingOrders());
        listExecutingOrders.getItems().setAll(restaurant.getOrdersInProgress());

        // dodati scenu za pregled odabrane narudzbe

        VBox vBoxLeft = new VBox(20);
        vBoxLeft.getChildren().addAll(lblPending, listPendingOrders);
        VBox vBoxRight = new VBox(20);
        vBoxRight.getChildren().addAll(lblExecution, listExecutingOrders);
        HBox hBoxMain = new HBox(40);
        hBoxMain.getChildren().addAll(vBoxLeft, vBoxRight);
        hBoxMain.setAlignment(Pos.CENTER);

        root.getChildren().addAll(lblTitle, hBoxMain);
        root.setAlignment(Pos.CENTER);
        root.setStyle("-fx-font: 16 'Comic Sans MS';");

        Scene restaurantScene = new Scene(root, 750, 600);
        primaryStage.setScene(restaurantScene);
        primaryStage.show();
    }

    public static void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setContentText(message);
        alert.showAndWait();
    }
}