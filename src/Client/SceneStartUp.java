package Client;

import Common.ClientType;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.util.concurrent.atomic.AtomicReference;

public class SceneStartUp {

    // Na klik "Prijavi me" klijentu se vracaju uneseni podaci za dalje kreiranje naloga
    public static void show(Stage primaryStage, Client client) {

        VBox root = new VBox(20);
        root.setPadding(new Insets(20));
        root.setStyle("-fx-background-color: linear-gradient(to bottom, #1e1e2e, #15151f); -fx-font-family: 'Segoe UI'; -fx-text-fill: white;");

        Label lblTitle = new Label("Za tren oka");
        Label lblUserName = new Label("Unesite korisničko ime");
        lblUserName.setStyle("-fx-text-fill: white;");
        Label lblClientType = new Label("Izaberite tip naloga");
        lblClientType.setStyle("-fx-text-fill: white;");
        lblTitle.setStyle("-fx-text-fill: #4d6a88; -fx-font-size: 28px; -fx-font-weight: bold; -fx-font-family: 'Segoe UI';");

        TextField tfUserName = new TextField();
        TextField tfFile = new TextField();
        tfUserName.setPromptText("korisnicko_ime");
        tfFile.setPromptText("jelovnik");
        tfFile.setEditable(false);

        ImageView icon = new ImageView(new Image(
                new File("resources/food_delivery_meal_order_icon_142268.png").toURI().toString()
        ));
        icon.setFitWidth(70);
        icon.setFitHeight(70);

        ChoiceBox<ClientType> cbClientType = new ChoiceBox<>();
        cbClientType.getItems().setAll(ClientType.values());

        Button btnLogIn = new Button("Prijavi me");
        Button btnFileChooser = new Button("Učitaj meni");
        AtomicReference<File> selectedFile = new AtomicReference<>();

        btnLogIn.setStyle("-fx-background-color: #6366f1; -fx-text-fill: white; -fx-font-size: 14px; -fx-background-radius: 10; -fx-padding: 8 16;");
        btnFileChooser.setStyle("-fx-background-color: #334155; -fx-text-fill: white; -fx-font-size: 14px; -fx-background-radius: 10; -fx-padding: 8 16;");

        btnFileChooser.setOnAction(event -> {
            FileChooser fileChooser = new FileChooser();
            File file = fileChooser.showOpenDialog(primaryStage);
            if (file != null) {
                selectedFile.set(file);
                tfFile.setText(file.getName());
            }
        });

        btnLogIn.setOnAction(event -> {
            if (tfUserName.getText().trim().isEmpty() || cbClientType.getValue() == null)
                showAlert("Nekorektno popunjena forma! Pokušajte ponovo.");
            else if (cbClientType.getValue() == ClientType.RESTAURANT && tfFile.getText().trim().isEmpty())
                showAlert("Niste učitali meni! Pokušajte ponovo.");
            else
                client.createAccount(tfUserName.getText().trim(), cbClientType.getValue(), selectedFile.get(), primaryStage);
        });
        root.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER)
                btnLogIn.fire();
        });

        HBox hBoxFile = new HBox(10, btnFileChooser, tfFile);
        hBoxFile.setAlignment(Pos.CENTER);
        hBoxFile.setVisible(false);

        cbClientType.setOnAction(event -> {
            ClientType type = cbClientType.getValue();
            if (type == ClientType.RESTAURANT)
                hBoxFile.setVisible(true);
            else {
                hBoxFile.setVisible(false);
                tfFile.clear();
                selectedFile.set(null);
            }
        });

        VBox VBoxHeader = new VBox(10, lblTitle, icon);
        VBoxHeader.setAlignment(Pos.CENTER);

        VBox vBoxlbl = new VBox(40, lblUserName, lblClientType);
        VBox vBoxfields = new VBox(20, tfUserName, cbClientType);
        HBox hBoxLogIn = new HBox(40, vBoxlbl, vBoxfields);
        hBoxLogIn.setAlignment(Pos.CENTER);
        hBoxLogIn.setPadding(new Insets(10, 0, 10, 0));

        root.getChildren().addAll(VBoxHeader, hBoxLogIn, hBoxFile, btnLogIn);
        root.setAlignment(Pos.CENTER);

        Scene startUpScene = new Scene(root, 750, 600);
        primaryStage.setScene(startUpScene);
        primaryStage.show();
    }

    public static void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setContentText(message);
        alert.showAndWait();
    }
}