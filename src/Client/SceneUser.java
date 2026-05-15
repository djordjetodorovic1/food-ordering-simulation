package Client;

import Common.Order;
import Common.OrderItem;
import Common.OrderState;
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
    private static RadioButton rbActive;
    private static int selectedOrderID;

    public static void show(Stage primaryStage, User user) {
        VBox root = new VBox(20);
        root.setPadding(new Insets(20));
        root.setStyle("-fx-background-color: linear-gradient(to bottom, #1e1e2e, #15151f); -fx-font-family: 'Segoe UI';");

        ImageView avatar = new ImageView(new Image(
                new File("resources/account_username_people_avatar_profile_person_user_icon_258905.png").toURI().toString()
        ));
        avatar.setFitWidth(20);
        avatar.setFitHeight(20);

        Label lblTitle = new Label("Korisnik: " + user.getUserName() + "  |");
        lblID = new Label(" (ID: " + user.getUserID() + ")");
        lblTitle.setStyle("-fx-text-fill: white; -fx-font-size: 16px;");
        lblID.setStyle("-fx-text-fill: white; -fx-font-size: 16px;");

        Label lblRestaurants = new Label("Restorani:");
        Label lblOrders = new Label("Moje narudžbe:");
        lblOrders.setStyle("-fx-text-fill: white; -fx-font-size: 13px;");
        lblRestaurants.setStyle("-fx-text-fill: white; -fx-font-size: 13px;");

        Button btnRefresh = new Button("Osvježi");
        btnRefresh.setOnAction(event -> user.refreshRestaurants());
        btnRefresh.setStyle("-fx-background-color: #6366f1; -fx-text-fill: white; -fx-font-size: 13px; -fx-background-radius: 10; -fx-padding: 6 14;");

        String radioStyle = "-fx-text-fill: white; -fx-font-size: 13px;";
        rbActive = new RadioButton("Aktivne");
        RadioButton rbPrevious = new RadioButton("Prošle");
        ToggleGroup toggleGroup = new ToggleGroup();
        toggleGroup.getToggles().addAll(rbActive, rbPrevious);
        rbActive.setSelected(true);
        rbActive.setOnAction(e -> listOrders.getItems().setAll(user.getActiveOrders()));
        rbPrevious.setOnAction(e -> listOrders.getItems().setAll(user.getPreviousOrders()));

        rbActive.setStyle(radioStyle);
        rbPrevious.setStyle(radioStyle);

        listRestaurants.getItems().setAll(user.getRestaurants());
        listOrders.getItems().setAll(user.getActiveOrders());

        listRestaurants.setCellFactory(param -> new ListCell<RestaurantInfo>() {
            @Override
            protected void updateItem(RestaurantInfo r, boolean empty) {
                super.updateItem(r, empty);

                if (empty || r == null) {
                    setText(null);
                } else {
                    setText("Restoran \"" + r.getRestaurantName() + "\"");
                }
            }
        });

        listOrders.setCellFactory(param -> new ListCell<Order>() {
            @Override
            protected void updateItem(Order o, boolean empty) {
                super.updateItem(o, empty);

                if (empty || o == null) {
                    setText(null);
                } else {
                    String prikaz = o.getState().toString();

                    setText("Narudžba #" + o.getOrderID() + " | " + prikaz);
                    if (o.getState() == OrderState.DELIVERED) {
                        setStyle("-fx-text-fill: #22c55e;");
                    } else if (o.getState() == OrderState.CANCELED) {
                        setStyle("-fx-text-fill: #ef4444;");
                    } else {
                        setStyle("-fx-text-fill: white;");
                    }
                }
            }
        });

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

        listRestaurants.setStyle("-fx-background-color: #0f172a; -fx-control-inner-background: #0f172a; -fx-text-fill: white;");
        listOrders.setStyle("-fx-background-color: #0f172a; -fx-control-inner-background: #0f172a;");

        HBox hBoxTitle = new HBox(10, avatar, lblTitle, lblID);
        hBoxTitle.setAlignment(Pos.CENTER);
        HBox hBoxRB = new HBox(15, rbActive, rbPrevious);
        hBoxRB.setAlignment(Pos.CENTER);
        hBoxRB.setStyle("-fx-background-color: #1e293b; -fx-padding: 8 12; -fx-background-radius: 12;");

        VBox vBoxLeft = new VBox(20, lblRestaurants, listRestaurants, btnRefresh);
        VBox vBoxRight = new VBox(20, lblOrders, listOrders, hBoxRB);
        HBox hBoxMain = new HBox(40, vBoxLeft, vBoxRight);
        hBoxMain.setAlignment(Pos.CENTER);

        root.getChildren().addAll(hBoxTitle, hBoxMain);
        root.setAlignment(Pos.CENTER);

        Scene userScene = new Scene(root, 750, 600);
        primaryStage.setScene(userScene);
        primaryStage.show();
    }

    private static void showRestaurant(User user, RestaurantInfo restaurant, Stage primaryStage) {
        ArrayList<OrderItem> currentOrder = new ArrayList<>();
        VBox root = new VBox(40);
        root.setPadding(new Insets(20, 20, 20, 20));
        root.setStyle("-fx-background-color: linear-gradient(to bottom, #1e1e2e, #15151f); -fx-font-family: 'Segoe UI';");

        Label lblTitle = new Label("Resotran: \"" + restaurant.getRestaurantName() + "\"");
        lblTitle.setStyle("-fx-text-fill: white; -fx-font-size: 24px; -fx-font-weight: bold;");

        Label lblTotal = new Label("Ukupno: 0 KM");
        lblTotal.setStyle("-fx-text-fill: white; -fx-font-size: 13px;");

        ListView<String> listMenu = new ListView<>();
        ListView<OrderItem> listOrder = new ListView<>();
        listMenu.getItems().setAll(restaurant.getMenu());
        Spinner<Integer> spinnerQuantity = new Spinner<>(1, 10, 1);

        listMenu.setStyle("-fx-background-color: #0f172a; -fx-control-inner-background: #0f172a; -fx-text-fill: white;");
        listOrder.setStyle("-fx-background-color: #0f172a; -fx-control-inner-background: #0f172a; -fx-text-fill: white;");

        Image backArrowImg = new Image((new File("resources/backArrow.png")).toURI().toString());
        ImageView backArrow = new ImageView(backArrowImg);
        backArrow.setFitWidth(20);
        backArrow.setFitHeight(20);
        Button btnReturn = new Button("", backArrow);
        btnReturn.setOnAction(actionEvent -> show(primaryStage, user));

        Button btnAddToOrder = new Button("Dodaj");
        btnAddToOrder.setOnAction(event -> {
            if (listMenu.getSelectionModel().getSelectedItem() != null) {
                OrderItem orderItem = new OrderItem(listMenu.getSelectionModel().getSelectedItem(), spinnerQuantity.getValue());
                listOrder.getItems().add(orderItem);
                currentOrder.add(orderItem);
                lblTotal.setText("Ukupno: " + new Order(0, user.getUserID(), restaurant.getRestaurantID(), currentOrder).getTotalPrice() + " KM");
                spinnerQuantity.getValueFactory().setValue(1);
            }
        });

        Button btnRemoveFromOrder = new Button("Ukloni");
        btnRemoveFromOrder.setOnAction(event -> {
            if (listOrder.getSelectionModel().getSelectedItem() != null) {
                currentOrder.remove(listOrder.getSelectionModel().getSelectedItem());
                lblTotal.setText("Ukupno: " + new Order(0, user.getUserID(), restaurant.getRestaurantID(), currentOrder).getTotalPrice() + " KM");
                listOrder.getItems().remove(listOrder.getSelectionModel().getSelectedItem());
            }
        });

        Button btnOrder = new Button("Naruči");
        btnOrder.setOnAction(event -> {
            user.sendNewOrder(new Order(user.getOrderIDCounter(), user.getUserID(), restaurant.getRestaurantID(), currentOrder));
            show(primaryStage, user);
        });

        btnAddToOrder.setStyle("-fx-background-color: #22c55e; -fx-text-fill: white; -fx-background-radius: 8;");
        btnRemoveFromOrder.setStyle("-fx-background-color: #ef4444; -fx-text-fill: white; -fx-background-radius: 8;");
        btnOrder.setStyle("-fx-background-color: #6366f1; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 8;-fx-padding: 6 14;");
        btnReturn.setStyle("-fx-background-color: #334155; -fx-text-fill: white;");

        Label kol = new Label("Količina:");
        kol.setStyle("-fx-text-fill: white; -fx-font-size: 13px;");

        HBox quantityBox = new HBox(10, new Label(":"), spinnerQuantity);
        quantityBox.setAlignment(Pos.CENTER);

        HBox buttonsBox = new HBox(15, btnAddToOrder, btnRemoveFromOrder);
        buttonsBox.setAlignment(Pos.CENTER);

        VBox vBoxRight = new VBox(10, quantityBox, buttonsBox, listOrder,lblTotal, btnOrder);
        vBoxRight.setAlignment(Pos.CENTER);

        Label lblMenu = new Label("Meni");
        lblMenu.setStyle("-fx-text-fill: white; -fx-font-size: 13px; -fx-font-weight: bold;");

        Label lblCart = new Label("Moja narudžba");
        lblCart.setStyle("-fx-text-fill: white; -fx-font-size: 13px; -fx-font-weight: bold;");

        VBox leftBox = new VBox(10, lblMenu, listMenu);
        VBox rightBox = new VBox(10, lblCart, vBoxRight);
        leftBox.setPrefWidth(300);
        rightBox.setPrefWidth(300);
        HBox hBoxMain = new HBox(30, leftBox, rightBox);
        hBoxMain.setAlignment(Pos.CENTER);

        VBox vBoxMain = new VBox(40, lblTitle, hBoxMain);
        vBoxMain.setAlignment(Pos.CENTER);
        root.getChildren().addAll(btnReturn, vBoxMain);

        Scene newOrderScene = new Scene(root, 750, 600);
        primaryStage.setScene(newOrderScene);
    }

    private static void showOrder(Order order, User user, Stage primaryStage) {
        selectedOrderID = order.getOrderID();

        VBox root = new VBox(20);
        root.setPadding(new Insets(20));
        root.setStyle("-fx-background-color: linear-gradient(to bottom, #1e1e2e, #15151f); -fx-font-family: 'Segoe UI';");

        Image backArrowImg = new Image((new File("resources/backArrow.png")).toURI().toString());
        ImageView backArrow = new ImageView(backArrowImg);
        backArrow.setFitWidth(20);
        backArrow.setFitHeight(20);

        Button btnReturn = new Button("", backArrow);
        btnReturn.setOnAction(e -> show(primaryStage, user));
        btnReturn.setStyle("-fx-background-color: #334155; -fx-text-fill: white;");

        VBox receipt = new VBox(10);
        receipt.setPadding(new Insets(20));
        receipt.setStyle("-fx-background-color: #f8fafc;-fx-background-radius: 12;-fx-border-radius: 12;");

        Label lblTitle = new Label("RAČUN");
        lblTitle.setStyle("-fx-font-size: 18; -fx-font-weight: bold; -fx-text-fill: black;");

        Label lblOrderID = new Label("ID narudžbe: " + order.getOrderID());
        Label lblRestaurant = new Label("ID restorana: " + order.getRestaurantID());
        lblCourierID = new Label("ID dostavljača: " + order.getCourierID());
        lblState = new Label("Stanje narudžbe: " + order.getState());
        Label lblTotal = new Label("Cijena: " + order.getTotalPrice() + " KM");

        lblOrderID.setStyle("-fx-text-fill: black;");
        lblRestaurant.setStyle("-fx-text-fill: black;");
        lblCourierID.setStyle("-fx-text-fill: black;");
        lblState.setStyle("-fx-text-fill: black;");
        lblTotal.setStyle("-fx-text-fill: black; -fx-font-weight: bold;");

        ListView<OrderItem> listOrderItems = new ListView<>();
        listOrderItems.getItems().setAll(order.getOrderItems());
        listOrderItems.setPrefHeight(150);

        receipt.getChildren().addAll(
                lblTitle,
                lblOrderID,
                lblRestaurant,
                lblCourierID,
                lblState,
                listOrderItems,
                lblTotal
        );

        Button btnRepeat = new Button("Ponovi narudžbu");
        Button btnCancel = new Button("Otkaži narudžbu");

        btnRepeat.setOnAction(event -> {
            user.sendNewOrder(new Order(
                    user.getOrderIDCounter(),
                    user.getUserID(),
                    order.getRestaurantID(),
                    order.getOrderItems()
            ));
            show(primaryStage, user);
        });

        btnCancel.setOnAction(event -> {
            if (order.getState() != OrderState.DELIVERED &&
                    order.getState() != OrderState.CANCELED &&
                    order.getState() != OrderState.FAILED) {
                user.cancelOrder(order);
                show(primaryStage, user);
            }
        });


        btnRepeat.setStyle("-fx-background-color: #6366f1; -fx-text-fill: white; -fx-background-radius: 8;-fx-padding: 6 14;");
        btnCancel.setStyle("-fx-background-color: #ef4444; -fx-text-fill: white; -fx-background-radius: 8;-fx-padding: 6 14;");

        HBox buttons = new HBox(15, btnRepeat, btnCancel);
        buttons.setAlignment(Pos.CENTER);

        VBox center = new VBox(20, receipt, buttons);
        center.setAlignment(Pos.CENTER);

        root.getChildren().addAll(btnReturn, center);

        Scene orderScene = new Scene(root, 750, 600);
        primaryStage.setScene(orderScene);
    }

    public static void updateID(int ID) {
        Platform.runLater(() -> lblID.setText("(ID: " + ID + ")"));
    }

    public static void updateRestaurants(Set<RestaurantInfo> restaurants) {
        Platform.runLater(() -> listRestaurants.getItems().setAll(restaurants));
    }

    public static void updateOrders(User user) {
        if (rbActive.isSelected())
            Platform.runLater(() -> listOrders.getItems().setAll(user.getActiveOrders()));
        else
            Platform.runLater(() -> listOrders.getItems().setAll(user.getPreviousOrders()));
    }

    public static void updateOrder(Order order) {
        if (selectedOrderID != 0 && selectedOrderID == order.getOrderID())
            Platform.runLater(() -> {
                lblState.setText("Order state: " + order.getState());
                lblCourierID.setText("CourierID: " + order.getCourierID());
            });
    }
}