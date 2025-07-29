package Server;

import Common.ClientType;
import Messages.*;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ClientHandler extends Thread {
    private final Server server;
    private final Socket client;
    private BufferedReader fromClient;
    private PrintWriter toClient;

    private ClientType clientType;
    private String userName;
    private int clientID;
    private Gson gson = new Gson();

    ClientHandler(Server server, Socket client, int clientID) throws IOException {
        this.server = server;
        this.client = client;
        this.clientID = clientID;

        fromClient = new BufferedReader(new InputStreamReader(client.getInputStream()));
        toClient = new PrintWriter(client.getOutputStream(), true);
    }

    // prihvata poruke od klijenta i poziva odgovarajuce metode
    @Override
    public void run() {
        try {
            String jsonMsg;
            while ((jsonMsg = fromClient.readLine()) != null) {
                Message msg = gson.fromJson(jsonMsg, Message.class);
                if (msg.getType() == MessageType.LOGIN)
                    handleLogin(jsonMsg);
                if (msg.getType() == MessageType.NEW_ORDER)
                    handleNewOrder(jsonMsg);
                if (msg.getType() == MessageType.ORDER_STATE)
                    handleOrderState(jsonMsg);
            }
        } catch (IOException e) {
            System.out.println("Error in Thread: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // vraca korisnicima ID (user-u vraca i restorane)
    private void handleLogin(String jsonMsg) throws IOException {
        LogInMessage loginMsg = gson.fromJson(jsonMsg, LogInMessage.class);
        this.userName = loginMsg.getUserName();
        this.clientType = loginMsg.getClientType();

        if (loginMsg.getClientType() == ClientType.RESTAURANT) {
            LogInRestaurantMessage loginRestaurantMsg = gson.fromJson(jsonMsg, LogInRestaurantMessage.class);
            server.addRestaurant(this, loginRestaurantMsg.getMenu());
            this.sendMessage(gson.toJson(new LoginResponseMessage(MessageType.LOGIN_RESPONSE, clientID)));
        } else if (loginMsg.getClientType() == ClientType.USER) {
            server.addUser(this);
            this.sendMessage(gson.toJson(new LoginUserResponseMessage(MessageType.LOGIN_RESPONSE, clientID, server.getRestaurantInfos())));
        } else {
            server.addCourier(this);
            this.sendMessage(gson.toJson(new LoginResponseMessage(MessageType.LOGIN_RESPONSE, clientID)));
        }
    }

    // prosljedjuje narudzbu restoranu
    private void handleNewOrder(String jsonMsg) {
        NewOrderMessage newOrderMsg = gson.fromJson(jsonMsg, NewOrderMessage.class);
        System.out.println(newOrderMsg);
        ClientHandler restaurant = server.getRestaurant(newOrderMsg.getOrder().getRestaurantID());
        restaurant.sendMessage(jsonMsg);
    }

    // prosljedjuje stanje narudzbe klijentu
    private void handleOrderState(String jsonMsg) {
        OrderStateMessage orderStateMsg = gson.fromJson(jsonMsg, OrderStateMessage.class);
        ClientHandler user = server.getUser(orderStateMsg.getUserID());
        user.sendMessage(jsonMsg);
    }

    // salje poruku klijentu
    private void sendMessage(String jsonMsg) {
        toClient.println(jsonMsg);
    }

    public ClientType getClientType() {
        return clientType;
    }

    public String getUserName() {
        return userName;
    }

    public int getClientID() {
        return clientID;
    }
}