package ru.gb.current;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Array;
import java.util.HashSet;
import java.util.Set;

public class ChatServer {

    private final ServerSocket socket;
    private final AuthenticationService authenticationService;
    private final Set<ClientHandler> loggedClients;

    public ChatServer() {
        try {
            authenticationService = new AuthenticationService();
            loggedClients = new HashSet<>();
            this.socket = new ServerSocket(8888);


            while (true) {
                System.out.println("Waiting for a new connection...");
                Socket client = socket.accept();
                System.out.println("Client accepted.");
                new Thread(() -> {
                    try {
                        new ClientHandler(client, this);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }).start();
            }
        } catch (IOException e) {
            throw new RuntimeException("Something went wrong during connection establishing.", e);
        }
    }

    public AuthenticationService getAuthenticationService() {
        return authenticationService;
    }

    public void addClient(ClientHandler client) {
        loggedClients.add(client);
    }

    public void removeUsername(ClientHandler client) {
        loggedClients.remove(client);
    }

    public boolean isUsernameOccupied(String username) {
//        for (String loggedUsername : loggedUsernames) {
//            if (loggedUsernames.equals(username)) {
//                return true;
//            }
//        }
//        return false;

        return loggedClients.stream()
                .anyMatch(c -> c.getName().equals(username));
    }

    public void broadcastMessage(String message) {
        loggedClients.forEach(ch -> ch.sendMessage(message));
    }

    public void MessageForSingleNick(String username, String message) {
        for (ClientHandler loggedUsernames : loggedClients) {
            if (loggedUsernames.getName().equals(username)) {
                loggedUsernames.sendMessage(message);
            }
        }
    }
}
