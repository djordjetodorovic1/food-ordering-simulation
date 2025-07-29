package Client;

import Common.Order;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class SceneRestaurant {
    private static ListView<Order> listPendingOrders = new ListView<>();
    private static ListView<Order> listExecutingOrders = new ListView<>();
    private static Label lblID;

    public static void show(Restaurant restaurant, Stage primaryStage) {
        VBox root = new VBox(40);
        root.setPadding(new Insets(20, 20, 20, 20));

        Label lblTitle = new Label("Restoran: " + restaurant.getName());
        lblID = new Label(" (ID: " + restaurant.getRestaurantID() + ")");
        Label lblPending = new Label("Na cekanju...");
        Label lblExecution = new Label("U pripremi");

        listPendingOrders.getItems().setAll(restaurant.getPendingOrders());
        listExecutingOrders.getItems().setAll(restaurant.getOrdersInProgress());

        // dodati scenu za pregled odabrane narudzbe iz listView

        HBox hBoxTitle = new HBox(20, lblTitle, lblID);
        hBoxTitle.setAlignment(Pos.CENTER);
        VBox vBoxLeft = new VBox(20);
        vBoxLeft.getChildren().addAll(lblPending, listPendingOrders);
        VBox vBoxRight = new VBox(20);
        vBoxRight.getChildren().addAll(lblExecution, listExecutingOrders);
        HBox hBoxMain = new HBox(40);
        hBoxMain.getChildren().addAll(vBoxLeft, vBoxRight);
        hBoxMain.setAlignment(Pos.CENTER);

        root.getChildren().addAll(hBoxTitle, hBoxMain);
        root.setAlignment(Pos.CENTER);
        root.setStyle("-fx-font: 16 'Comic Sans MS';");

        Scene restaurantScene = new Scene(root, 750, 600);
        primaryStage.setScene(restaurantScene);
        primaryStage.show();
    }

    public static void updateID(int ID) {
        Platform.runLater(() -> lblID.setText("(ID: " + ID + ")"));
    }

    public static void addNewOrder(Order newOrder) {
        Platform.runLater(() -> listPendingOrders.getItems().add(newOrder));
    }

    public static void updateOrders(Restaurant restaurant) {
        Platform.runLater(() -> {
            listPendingOrders.getItems().setAll(restaurant.getPendingOrders());
            listExecutingOrders.getItems().setAll(restaurant.getOrdersInProgress());
        });
    }

    public static void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setContentText(message);
        alert.showAndWait();
    }
}