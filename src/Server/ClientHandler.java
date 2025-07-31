package Server;

import Common.ClientType;
import Common.OrderState;
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

        this.fromClient = new BufferedReader(new InputStreamReader(client.getInputStream()));
        this.toClient = new PrintWriter(client.getOutputStream(), true);
    }

    // prihvata poruke od klijenta i poziva odgovarajuce metode
    @Override
    public void run() {
        try {
            String jsonMsg;
            while ((jsonMsg = fromClient.readLine()) != null) {
                System.out.println(jsonMsg);
                Message msg = gson.fromJson(jsonMsg, Message.class);
                if (msg.getType() == MessageType.LOGIN)
                    handleLogin(jsonMsg);
                if (msg.getType() == MessageType.NEW_ORDER)
                    handleNewOrder(jsonMsg);
                if (msg.getType() == MessageType.ORDER_STATE)
                    handleOrderState(jsonMsg);
                if (msg.getType() == MessageType.LOGOUT)
                    handleLogout(jsonMsg);
            }
        } catch (IOException e) {
            System.err.println("Error in Thread: " + e.getMessage());
            e.printStackTrace();
        } finally {
            try {
                if (fromClient != null) fromClient.close();
                if (toClient != null) toClient.close();
                if (client != null && !client.isClosed()) client.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            System.err.println("Client thread closed for clientID: " + clientID);
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
            this.sendMessage(gson.toJson(new LoginResponseMessage(MessageType.LOGIN_RESPONSE, clientID)));
            server.addCourier(this);
        }
    }

    // prosljedjuje narudzbu restoranu
    private void handleNewOrder(String jsonMsg) {
        NewOrderMessage newOrderMsg = gson.fromJson(jsonMsg, NewOrderMessage.class);
        // System.out.println(newOrderMsg);
        ClientHandler restaurant = server.getRestaurant(newOrderMsg.getOrder().getRestaurantID());
        restaurant.sendMessage(jsonMsg);
    }

    // prosljedjuje stanje narudzbe klijentu
    // trazi dostupnog dostavljaca ako je na cekanju
    private void handleOrderState(String jsonMsg) {
        OrderStateMessage orderStateMsg = gson.fromJson(jsonMsg, OrderStateMessage.class);
        ClientHandler user = server.getUser(orderStateMsg.getOrder().getUserID());
        user.sendMessage(jsonMsg);

        if (orderStateMsg.getOrder().getState() == OrderState.WAITING_FOR_DELIVERY) {
            ClientHandler courier = server.getAvailableCourier();
            if (courier != null)
                server.assignCourierToOrder(courier, orderStateMsg.getOrder());
            else
                server.addPendingOrder(orderStateMsg.getOrder());
        }
    }

    // uklanja klijenta
    private void handleLogout(String jsonMsg) {
        LogOutMessage logoutMsg = gson.fromJson(jsonMsg, LogOutMessage.class);
        switch (logoutMsg.getClientType()) {
            case RESTAURANT:
                server.removeRestaurant(logoutMsg.getClientID());
                server.broadcastToUsers(jsonMsg);
                break;
            case USER:
                server.removeUser(this);
                // obavjesti ostale koji pripremaju narudzbe
                break;
            case COURIER:
                server.removeCourier(this);
                // obavjesti user-a ako ima narudzba
                break;
        }
    }

    // salje poruku klijentu
    public void sendMessage(String jsonMsg) {
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