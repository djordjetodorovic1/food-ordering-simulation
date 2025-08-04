package Client;

import Common.Order;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class SceneCourier {
    private static Label lblID;
    private static TextArea taOrder;

    public static void show(Courier courier, Stage primaryStage) {
        VBox root = new VBox(40);
        root.setPadding(new Insets(20, 20, 20, 20));

        Label lblUserName = new Label("Dostavljač: " + courier.getUserName());
        lblID = new Label(" (ID: " + courier.getCourierID() + ")");
        taOrder = new TextArea();
        taOrder.setEditable(false);

        HBox hBoxMain = new HBox(40, lblUserName, lblID);
        hBoxMain.setAlignment(Pos.CENTER);
        root.getChildren().addAll(hBoxMain, taOrder);
        root.setStyle("-fx-font: 16 'Comic Sans MS';");

        Scene courierScene = new Scene(root, 750, 600);
        primaryStage.setScene(courierScene);
    }

    public static void updateID(int clientID) {
        Platform.runLater(() -> lblID.setText(" (ID: " + clientID + ")"));
    }

    public static void updateOrder(Order order) {
        Platform.runLater(() -> {
            taOrder.clear();
            taOrder.appendText("OrderID: " + order.getOrderID());
            taOrder.appendText("\nUserID: " + order.getUserID());
            taOrder.appendText("\nRestaurantID: " + order.getRestaurantID());
            taOrder.appendText("\nState: " + order.getState());
            taOrder.appendText("\nItems: " + order.getOrderItems());
        });
    }

    public static void clearOrder() {
        Platform.runLater(() -> taOrder.clear());
    }
}