package Client;

import Common.ClientType;
import Server.Server;
import javafx.application.Application;
import javafx.stage.Stage;

import java.io.File;

public class Client extends Application {
    private final static String hostname = "localhost";
    private final static int port = Server.PORT;

    public static void main(String[] args) {
        launch();
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        primaryStage.setTitle("Food ordering simulation - LOGIN");
        SceneStartUp.show(primaryStage, this);
    }

    public void createAccount(String userName, ClientType clientType, File file, Stage primaryStage) {
        switch (clientType) {
            case USER:
                User clientUser = new User(userName, hostname, port, primaryStage);
                clientUser.execute();
                break;
            case RESTAURANT:
                Restaurant clientRestaurant = new Restaurant(userName, file, hostname, port, primaryStage);
                clientRestaurant.execute();
                break;
            case COURIER:
                //TODO za kurira
                break;
        }
    }
}