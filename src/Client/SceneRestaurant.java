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
        VBox root = new VBox(20);
        root.setPadding(new Insets(20));
        root.setStyle("-fx-background-color: linear-gradient(to bottom, #1e1e2e, #15151f); -fx-font-family: 'Segoe UI';");

        Label lblTitle = new Label("Restoran: \"" + restaurant.getName() + "\"");
        lblID = new Label("(ID: " + restaurant.getRestaurantID() + ")");

        Label lblPending = new Label("Na čekanju...");
        Label lblExecution = new Label("U pripremi:");

        lblTitle.setStyle("-fx-text-fill: white; -fx-font-size: 22px; -fx-font-weight: bold;");
        lblID.setStyle("-fx-text-fill: #cbd5e1; -fx-font-size: 15px;");
        lblPending.setStyle("-fx-text-fill: white; -fx-font-size: 15px; -fx-font-weight: bold;");
        lblExecution.setStyle("-fx-text-fill: white; -fx-font-size: 15px; -fx-font-weight: bold;");

        ImageView ikonica = new ImageView(new Image(
                new File("resources/bag_food_delivery_icon_221020.png").toURI().toString()
        ));
        ikonica.setFitWidth(40);
        ikonica.setFitHeight(40);

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

        listPendingOrders.setCellFactory(param -> new ListCell<Order>() {
            @Override
            protected void updateItem(Order order, boolean empty) {
                super.updateItem(order, empty);

                if (empty || order == null) {
                    setText(null);
                } else {
                    setText("Narudžba #" + order.getOrderID());
                    setStyle("-fx-text-fill: white;");
                }
            }
        });

        listExecutingOrders.setCellFactory(param -> new ListCell<Order>() {
            @Override
            protected void updateItem(Order order, boolean empty) {
                super.updateItem(order, empty);

                if (empty || order == null) {
                    setText(null);
                } else {
                    setText("Narudžba #" + order.getOrderID());
                    setStyle("-fx-text-fill: #facc15;");
                }
            }
        });

        listPendingOrders.setStyle("-fx-background-color: #0f172a; -fx-control-inner-background: #0f172a; -fx-background-radius: 12; -fx-border-radius: 12;");
        listExecutingOrders.setStyle("-fx-background-color: #0f172a; -fx-control-inner-background: #0f172a; -fx-background-radius: 12; -fx-border-radius: 12;");

        HBox hBoxTitle = new HBox(10, ikonica, lblTitle, lblID);
        hBoxTitle.setAlignment(Pos.CENTER);
        VBox vBoxLeft = new VBox(20, lblPending, listPendingOrders);
        VBox vBoxRight = new VBox(20, lblExecution, listExecutingOrders);
        HBox hBoxMain = new HBox(40, vBoxLeft, vBoxRight);
        hBoxMain.setAlignment(Pos.CENTER);

        root.getChildren().addAll(hBoxTitle, hBoxMain);
        root.setAlignment(Pos.CENTER);

        Scene restaurantScene = new Scene(root, 750, 600);
        primaryStage.setScene(restaurantScene);
        primaryStage.show();
    }

    private static void showOrder(Order order, Restaurant restaurant, Stage primaryStage) {
        VBox root = new VBox(30);
        root.setPadding(new Insets(20));
        root.setStyle("-fx-background-color: linear-gradient(to bottom, #1e1e2e, #15151f); -fx-font-family: 'Segoe UI';");

        Image backArrowImg = new Image((new File("resources/backArrow.png")).toURI().toString());
        ImageView backArrow = new ImageView(backArrowImg);
        backArrow.setFitWidth(20);
        backArrow.setFitHeight(20);

        Button btnReturn = new Button("", backArrow);
        btnReturn.setOnAction(actionEvent -> show(restaurant, primaryStage));
        btnReturn.setStyle("-fx-background-color: #334155; -fx-background-radius: 10;");

        Label lblTitle = new Label("Narudžba");
        lblTitle.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #111827;");

        Label lblOrderID = new Label("ID narudžbe: " + order.getOrderID());
        Label lblUserID = new Label("ID kupca: " + order.getUserID());

        String stanje = order.getState().toString();

        Label lblState = new Label("Stanje: " + stanje);
        Label lblTime = new Label("Vrijeme pripreme: " + (order.getPreparationTime()/60000) + " min");
        Label lblTotal = new Label("Cijena: " + order.getTotalPrice() + " KM");

        String infoStyle = "-fx-font-size: 15px; -fx-text-fill: #374151;";
        lblOrderID.setStyle(infoStyle);
        lblUserID.setStyle(infoStyle);
        lblState.setStyle(infoStyle);
        lblTime.setStyle(infoStyle);
        lblTotal.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #111827;");

        ListView<OrderItem> listOrderItems = new ListView<>();
        listOrderItems.getItems().setAll(order.getOrderItems());

        listOrderItems.setStyle("-fx-background-color: white; -fx-control-inner-background: white; -fx-background-radius: 10; -fx-border-radius: 10; -fx-border-color: #d1d5db; -fx-text-fill: black;");

        listOrderItems.setPrefHeight(220);

        VBox receiptBox = new VBox(20,
                lblTitle,
                lblOrderID,
                lblUserID,
                lblState,
                lblTime,
                new Separator(),
                listOrderItems,
                lblTotal
        );

        receiptBox.setMaxWidth(420);
        receiptBox.setPadding(new Insets(30));
        receiptBox.setAlignment(Pos.CENTER);

        receiptBox.setStyle("-fx-background-color: #f8fafc; -fx-background-radius: 20; -fx-border-radius: 20; -fx-border-color: #cbd5e1;");

        root.getChildren().addAll(btnReturn, receiptBox);

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