package Client;

import Common.Order;
import Common.OrderItem;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.File;

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
        listPendingOrders.setOnMouseClicked(event -> {
            Order selectedOrder = listPendingOrders.getSelectionModel().getSelectedItem();
            if (selectedOrder != null)
                showOrder(selectedOrder, restaurant, primaryStage);
        });
        listExecutingOrders.setOnMouseClicked(event -> {
            Order selectedOrder = listExecutingOrders.getSelectionModel().getSelectedItem();
            if (selectedOrder != null)
                showOrder(selectedOrder, restaurant, primaryStage);
        });

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

    private static void showOrder(Order order, Restaurant restaurant, Stage primaryStage) {
        VBox root = new VBox(40);
        root.setPadding(new Insets(20, 20, 20, 20));

        Label lblOrderID = new Label("OrderID: " + order.getOrderID());
        Label lblUserID = new Label("UserID: " + order.getUserID());
        Label lblState = new Label("Order state: " + order.getState());
        Label lblTime = new Label("Preparation time: " + order.getPreparationTime());

        ListView<OrderItem> listOrderItems = new ListView<>();
        listOrderItems.getItems().setAll(order.getOrderItems());

        Image backArrowImg = new Image((new File("resources/backArrow.png")).toURI().toString());
        ImageView backArrow = new ImageView(backArrowImg);
        backArrow.setFitWidth(20);
        backArrow.setFitHeight(20);
        Button btnReturn = new Button("", backArrow);
        btnReturn.setOnAction(actionEvent -> show(restaurant, primaryStage));

        VBox vBoxLeft = new VBox(20, lblOrderID, lblUserID, lblState, lblTime);
        HBox hBoxMain = new HBox(40, vBoxLeft, listOrderItems);
        hBoxMain.setAlignment(Pos.CENTER);

        root.getChildren().addAll(btnReturn, hBoxMain);
        root.setStyle("-fx-font: 16 'Comic Sans MS';");

        Scene orderScene = new Scene(root, 750, 600);
        primaryStage.setScene(orderScene);
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
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText(message);
            alert.showAndWait();
            Platform.exit();
        });
    }
}