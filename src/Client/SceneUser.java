package Client;

import Common.Order;
import Common.OrderItem;
import Common.RestaurantInfo;
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
import java.util.ArrayList;
import java.util.Set;

public class SceneUser {
    private static ListView<RestaurantInfo> listRestaurants = new ListView<>();
    private static ListView<Order> listOrders = new ListView<>();
    private static Label lblID;
    private static Label lblState;
    private static Label lblCourierID;
    private static int selectedOrderID;

    // u svim klasama dodati provjere (null, ....)
    public static void show(Stage primaryStage, User user) {
        VBox root = new VBox(40);
        root.setPadding(new Insets(20, 20, 20, 20));

        Label lblTitle = new Label("Korisnik: " + user.getUserName());
        lblID = new Label(" (ID: " + user.getUserID() + ")");
        Label lblRestaurants = new Label("Restorani");
        Label lblOrders = new Label("Narudzbe");

        listRestaurants.getItems().setAll(user.getRestaurants());
        listOrders.getItems().setAll(user.getActiveOrders());

        Button btnRefresh = new Button("Osvježi");
        btnRefresh.setOnAction(event -> listRestaurants.getItems().setAll(user.refreshRestaurants()));

        RadioButton rbActive = new RadioButton("Aktivne");
        RadioButton rbPrevious = new RadioButton("Prošle");
        ToggleGroup toggleGroup = new ToggleGroup();
        toggleGroup.getToggles().addAll(rbActive, rbPrevious);
        rbActive.setSelected(true);
        rbActive.setOnAction(e -> listOrders.getItems().setAll(user.getActiveOrders()));
        rbPrevious.setOnAction(e -> listOrders.getItems().setAll(user.getPreviousOrders()));

        listRestaurants.setOnMouseClicked(event -> {
            RestaurantInfo selectedRestaurant = listRestaurants.getSelectionModel().getSelectedItem();
            if (selectedRestaurant != null) {
                showRestaurant(user, selectedRestaurant, primaryStage);
            }
        });

        listOrders.setOnMouseClicked(event -> {
            Order selectedOrder = listOrders.getSelectionModel().getSelectedItem();
            if (selectedOrder != null)
                showOrder(selectedOrder, user, primaryStage);
        });

        HBox hBoxTitle = new HBox(20, lblTitle, lblID);
        hBoxTitle.setAlignment(Pos.CENTER);
        HBox hBoxRB = new HBox(30, rbActive, rbPrevious);
        VBox vBoxLeft = new VBox(20);
        vBoxLeft.getChildren().addAll(lblRestaurants, listRestaurants, btnRefresh);
        VBox vBoxRight = new VBox(20);
        vBoxRight.getChildren().addAll(lblOrders, listOrders, hBoxRB);

        HBox hBoxMain = new HBox(40);
        hBoxMain.getChildren().addAll(vBoxLeft, vBoxRight);
        hBoxMain.setAlignment(Pos.CENTER);

        root.getChildren().addAll(hBoxTitle, hBoxMain);
        root.setAlignment(Pos.CENTER);
        root.setStyle("-fx-font: 16 'Comic Sans MS';");

        Scene userScene = new Scene(root, 750, 600);
        primaryStage.setScene(userScene);
        primaryStage.show();
    }

    private static void showRestaurant(User user, RestaurantInfo restaurant, Stage primaryStage) {
        ArrayList<OrderItem> currentOrder = new ArrayList<>();
        VBox root = new VBox(40);
        root.setPadding(new Insets(20, 20, 20, 20));

        Label lblTitle = new Label("Resotran: " + restaurant.getRestaurantName() + " (ID: " + restaurant.getRestaurantID() + ")");
        ListView<String> listMenu = new ListView<>();
        listMenu.getItems().setAll(restaurant.getMenu());
        Spinner<Integer> spinnerQuantity = new Spinner<>(1, 10, 1);

        Image backArrowImg = new Image((new File("resources/backArrow.png")).toURI().toString());
        ImageView backArrow = new ImageView(backArrowImg);
        backArrow.setFitWidth(20);
        backArrow.setFitHeight(20);
        Button btnReturn = new Button("", backArrow);
        btnReturn.setOnAction(actionEvent -> show(primaryStage, user));

        Button btnOrder = new Button("Naruči");
        btnOrder.setOnAction(event -> {
            user.sendNewOrder(new Order(user.getOrderIDCounter(), user.getUserID(), restaurant.getRestaurantID(), currentOrder));
            show(primaryStage, user);
        });

        Button btnAddToOrder = new Button("Dodaj u narudzbu");
        btnAddToOrder.setOnAction(event -> {
            currentOrder.add(new OrderItem(listMenu.getSelectionModel().getSelectedItem(), spinnerQuantity.getValue()));
            spinnerQuantity.getValueFactory().setValue(1);
        });

        VBox vBoxRight = new VBox(20);
        vBoxRight.getChildren().addAll(spinnerQuantity, btnAddToOrder, btnOrder);

        HBox hBoxMain = new HBox(40);
        hBoxMain.getChildren().addAll(listMenu, vBoxRight);
        hBoxMain.setAlignment(Pos.CENTER);

        VBox vBoxMain = new VBox(40, lblTitle, hBoxMain);
        vBoxMain.setAlignment(Pos.CENTER);
        root.getChildren().addAll(btnReturn, vBoxMain);
        root.setStyle("-fx-font: 16 'Comic Sans MS';");

        Scene newOrderScene = new Scene(root, 750, 600);
        primaryStage.setScene(newOrderScene);
    }

    private static void showOrder(Order order, User user, Stage primaryStage) {
        selectedOrderID = order.getOrderID();
        VBox root = new VBox(40);
        root.setPadding(new Insets(20, 20, 20, 20));

        Label lblOrderID = new Label("OrderID: " + order.getOrderID());
        Label lblRestaurant = new Label("RestaurantID: " + order.getRestaurantID());
        lblCourierID = new Label("CourierID: " + order.getCourierID());
        lblState = new Label("Order state: " + order.getState());

        ListView<OrderItem> listOrderItems = new ListView<>();
        listOrderItems.getItems().setAll(order.getOrderItems());

        Image backArrowImg = new Image((new File("resources/backArrow.png")).toURI().toString());
        ImageView backArrow = new ImageView(backArrowImg);
        backArrow.setFitWidth(20);
        backArrow.setFitHeight(20);
        Button btnReturn = new Button("", backArrow);
        btnReturn.setOnAction(actionEvent -> show(primaryStage, user));

        Button btnRepeat = new Button("Ponovi narudžbu");
        Button btnCancel = new Button("Otkaži narudžbu");
        btnRepeat.setOnAction(event -> {
            user.sendNewOrder(new Order(user.getOrderIDCounter(), user.getUserID(), order.getRestaurantID(), order.getOrderItems()));
            show(primaryStage, user);
        });
        btnCancel.setOnAction(event -> {
            user.cancelOrder(order);
            SceneStartUp.showAlert("Narudžba je otkazana!");
            show(primaryStage, user);
        });

        VBox vBoxLeft = new VBox(20, lblOrderID, lblRestaurant, lblCourierID, lblState, btnRepeat, btnCancel);
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

    public static void updateRestaurants(Set<RestaurantInfo> restaurants) {
        Platform.runLater(() -> listRestaurants.getItems().setAll(restaurants));
    }

    public static void updateOrders(ArrayList<Order> orders) {
        Platform.runLater(() -> listOrders.getItems().setAll(orders));
    }

    public static void updateOrder(Order order) {
        if (selectedOrderID != 0 && selectedOrderID == order.getOrderID())
            Platform.runLater(() -> {
                lblState.setText("Order state: " + order.getState());
                lblCourierID.setText("CourierID: " + order.getCourierID());
            });
    }
}