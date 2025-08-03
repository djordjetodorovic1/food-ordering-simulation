package Server;

import Common.ClientType;
import Common.Order;
import Common.OrderState;
import Messages.*;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Set;

public class ClientHandler extends Thread {
    private final Server server;
    private final Socket client;
    private final BufferedReader fromClient;
    private final PrintWriter toClient;

    private String userName;
    private ClientType clientType;
    private final int clientID;
    private final Gson gson = new Gson();

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
                Message msg = gson.fromJson(jsonMsg, Message.class);
                if (msg.getType() == MessageType.LOGIN)
                    handleLogin(jsonMsg);
                else if (msg.getType() == MessageType.NEW_ORDER)
                    handleNewOrder(jsonMsg);
                else if (msg.getType() == MessageType.ORDER_STATE)
                    handleOrderState(jsonMsg);
                else if (msg.getType() == MessageType.RESTAURANT_INFO_REFRESH)
                    handleRestaurantInfoRefresh();
                else if (msg.getType() == MessageType.CANCELED_ORDER)
                    handleCancelOrder(jsonMsg);
                else if (msg.getType() == MessageType.LOGOUT)
                    handleLogout(gson.fromJson(jsonMsg, LogOutMessage.class));
            }
        } catch (IOException e) {
            this.handleLogout(new LogOutMessage(MessageType.LOGOUT, this.clientType, this.clientID));
            System.err.println("Error in Thread: " + e.getMessage());
            // e.printStackTrace();
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
        System.out.println(loginMsg);
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
        System.out.println(newOrderMsg);
        ClientHandler restaurant = server.getRestaurant(newOrderMsg.getOrder().getRestaurantID());
        if (restaurant != null)
            restaurant.sendMessage(jsonMsg);
        else
            this.sendMessage(gson.toJson(new LogOutMessage(MessageType.LOGOUT, ClientType.RESTAURANT, newOrderMsg.getOrder().getRestaurantID())));
    }

    // prosljedjuje stanje narudzbe klijentu
    // trazi dostupnog dostavljaca ako je na cekanju ili ga vraca u red za cekanje ako je zavrsio dostavu
    private void handleOrderState(String jsonMsg) {
        System.out.println(gson.fromJson(jsonMsg, OrderStateMessage.class));
        Order order = gson.fromJson(jsonMsg, OrderStateMessage.class).getOrder();
        ClientHandler user = server.getUser(order.getUserID());
        if (user == null) {
            this.sendMessage(gson.toJson(new CancelOrderMessage(MessageType.CANCELED_ORDER, order)));
            if (order.getState() == OrderState.DELIVERING)
                server.addCourierToQueue(this);
        } else {
            user.sendMessage(jsonMsg);
            if (order.getState() == OrderState.WAITING_FOR_DELIVERY) {
                ClientHandler courier = server.getAvailableCourier();
                if (courier != null)
                    server.assignCourierToOrder(courier, order);
                else
                    server.addPendingOrder(order);
            }
        }
        if (order.getState() == OrderState.DELIVERED)
            server.addCourierToQueue(this);
    }

    // vraca nove inforamcije o restoranima
    private void handleRestaurantInfoRefresh() {
        this.sendMessage(gson.toJson(new RestaurantInfoRefreshMessage(MessageType.RESTAURANT_INFO_REFRESH, server.getRestaurantInfos())));
    }

    // prosljedjuje otkazanu narudzbu odgovarajucem restoranu/dostavljacu
    private void handleCancelOrder(String jsonMsg) {
        System.out.println(gson.fromJson(jsonMsg, CancelOrderMessage.class));
        Order canceledOrder = gson.fromJson(jsonMsg, CancelOrderMessage.class).getOrder();
        ClientHandler restaurant = server.getRestaurant(canceledOrder.getRestaurantID());
        if (restaurant != null)
            restaurant.sendMessage(jsonMsg);
        if (canceledOrder.getCourierID() != 0) {
            ClientHandler courier = server.getCourier(canceledOrder.getCourierID());
            if (courier != null) {
                courier.sendMessage(jsonMsg);
                server.addCourierToQueue(courier);
            }
        }
        server.removePendingOrder(canceledOrder);
    }

    // uklanja klijenta
    private void handleLogout(LogOutMessage logoutMsg) {
        System.out.println(logoutMsg);
        switch (logoutMsg.getClientType()) {
            case RESTAURANT:
                server.removeRestaurant(logoutMsg.getClientID());
                server.broadcastToUsers(gson.toJson(logoutMsg));
                break;
            case USER:
                server.removeUser(this);
                server.broadcastUserDisconnected(logoutMsg);
                break;
            case COURIER:
                ClientHandler user;
                if (!logoutMsg.getOrders().isEmpty()) {
                    user = server.getUser(logoutMsg.getOrders().iterator().next().getUserID());
                    if (user != null)
                        user.sendMessage(gson.toJson(logoutMsg));
                } else {
                    Order order = server.getCourierToOrder(logoutMsg.getClientID());
                    if (order != null) {
                        user = server.getUser(order.getUserID());
                        if (user != null)
                            user.sendMessage(gson.toJson(new LogOutMessage(logoutMsg.getType(), logoutMsg.getClientType(), logoutMsg.getClientID(), Set.of(order))));
                    }
                }
                server.removeCourier(this);
                break;
        }
    }

    // salje poruku klijentu
    public void sendMessage(String jsonMsg) {
        toClient.println(jsonMsg);
    }

    public String getUserName() {
        return userName;
    }

    public int getClientID() {
        return clientID;
    }
}