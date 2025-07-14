package Client;

import Common.ClientType;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.util.concurrent.atomic.AtomicReference;

public class SceneStartUp {

    // Na klik "Prijavi me" Clientu se vracaju uneseni podaci za dalje kreiranje naloga

    public static void show(Stage primaryStage, Client client) {
        VBox root = new VBox(40);
        root.setPadding(new Insets(20, 20, 20, 20));

        Label lblTitle = new Label("Neki kreativan naziv");
        Label lblUserName = new Label("Unesite korisničko ime");
        Label lblClientType = new Label("Izaberite tip naloga");
        lblTitle.setStyle("-fx-text-fill: #4d6a88; -fx-font: 24 'Comic Sans MS';");

        TextField tfUserName = new TextField();
        TextField tfFile = new TextField();
        tfUserName.setPromptText("Novak Djokovic");
        tfFile.setPromptText("Putanja/do/jelovnika");
        tfFile.setEditable(false);

        ChoiceBox<ClientType> cbClientType = new ChoiceBox<>();
        cbClientType.getItems().setAll(ClientType.values());

        Button btnLogIn = new Button("Prijavi me");
        Button btnFileChooser = new Button("Učitaj meni");
        AtomicReference<File> selectedFile = new AtomicReference<>();

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

        VBox vBoxlbl = new VBox(40);
        vBoxlbl.getChildren().addAll(lblUserName, lblClientType);
        VBox vBoxfields = new VBox(20);
        vBoxfields.getChildren().addAll(tfUserName, cbClientType);

        HBox hBoxLogIn = new HBox(40);
        hBoxLogIn.getChildren().addAll(vBoxlbl, vBoxfields);
        hBoxLogIn.setAlignment(Pos.CENTER);
        hBoxLogIn.setPadding(new Insets(10, 0, 10, 0));

        root.getChildren().addAll(lblTitle, hBoxLogIn, hBoxFile, btnLogIn);
        root.setAlignment(Pos.CENTER);
        root.setStyle("-fx-font: 16 'Comic Sans MS';");

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