package Client;

import Common.Order;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.File;

public class SceneCourier {
    private static Label lblID;
    private static TextArea taOrder;

    public static void show(Courier courier, Stage primaryStage) {
        VBox root = new VBox(20);
        root.setPadding(new Insets(20));
        root.setStyle("-fx-background-color: linear-gradient(to bottom, #1e1e2e, #15151f); -fx-font-family: 'Segoe UI';");

        ImageView avatar = new ImageView(new Image(
                new File("resources/account_username_people_avatar_profile_person_user_icon_258905.png").toURI().toString()
        ));
        avatar.setFitWidth(20);
        avatar.setFitHeight(20);

        Label lblUserName = new Label("Dostavljač: " + courier.getUserName());
        lblID = new Label(" (ID: " + courier.getCourierID() + ")");
        taOrder = new TextArea();

        taOrder.setEditable(false);
        taOrder.setStyle("-fx-background-color: #f8fafc; -fx-control-inner-background: #f8fafc; -fx-text-fill: #0f172a; -fx-font-size: 15px; -fx-background-radius: 20;-fx-border-radius: 20;-fx-padding: 20;");
        lblUserName.setStyle("-fx-text-fill: white; -fx-font-size: 16px;");
        lblID.setStyle("-fx-text-fill: white; -fx-font-size: 16px;");

        HBox hBoxMain = new HBox(10,avatar, lblUserName, lblID);
        hBoxMain.setAlignment(Pos.CENTER);
        root.getChildren().addAll(hBoxMain, taOrder);

        Scene courierScene = new Scene(root, 750, 600);
        primaryStage.setScene(courierScene);
    }

    public static void updateID(int clientID) {
        Platform.runLater(() -> lblID.setText(" (ID: " + clientID + ")"));
    }

    public static void updateOrder(Order order) {
        Platform.runLater(() -> {
            taOrder.clear();
            taOrder.appendText("ID narudžbe: " + order.getOrderID());
            taOrder.appendText("\nID kupca: " + order.getUserID());
            taOrder.appendText("\nID restorana: " + order.getRestaurantID());
            taOrder.appendText("\nStanje narudžbe: " + order.getState());
            taOrder.appendText("\nStvari: " + order.getOrderItems());
        });
    }

    public static void clearOrder() {
        Platform.runLater(() -> taOrder.clear());
    }
}