package Client;

import Common.Order;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class SceneUser {
    public static void show(Stage primaryStage, User user) {
        VBox root = new VBox(40);
        root.setPadding(new Insets(20, 20, 20, 20));

        Label lblTitle = new Label("Korisnik: " + user.getUserName() + " (ID: " + user.getUserID() + ")");
        Label lblRestaurants = new Label("Restorani");
        Label lblOrders = new Label("Narudzbe");

        // Dodati funkcionalnosti za listview, radiobutton

        ListView<Restaurant> listRestaurants = new ListView<>();
        ListView<Order> listOrders = new ListView<>();
        listRestaurants.getItems().setAll(user.getRestaurants());
        listOrders.getItems().setAll(user.getActiveOrders());

        RadioButton rbActive = new RadioButton("Aktivne");
        RadioButton rbPrevious = new RadioButton("Prošle");
        ToggleGroup toggleGroup = new ToggleGroup();
        toggleGroup.getToggles().addAll(rbActive, rbPrevious);
        rbActive.setSelected(true);

        HBox hBoxRB = new HBox(30, rbActive, rbPrevious);
        VBox vBoxLeft = new VBox(20);
        vBoxLeft.getChildren().addAll(lblRestaurants, listRestaurants);
        VBox vBoxRight = new VBox(20);
        vBoxRight.getChildren().addAll(lblOrders, listOrders, hBoxRB);

        HBox hBoxMain = new HBox(40);
        hBoxMain.getChildren().addAll(vBoxLeft, vBoxRight);
        hBoxMain.setAlignment(Pos.CENTER);

        root.getChildren().addAll(lblTitle, hBoxMain);
        root.setAlignment(Pos.CENTER);
        root.setStyle("-fx-font: 16 'Comic Sans MS';");

        Scene userScene = new Scene(root, 750, 600);
        primaryStage.setScene(userScene);
        primaryStage.show();
    }
}