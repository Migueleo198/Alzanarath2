package Networking;

import java.io.*;
import java.net.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;
import javax.swing.JOptionPane;
import Entity.Player;
import Inputs.KeyHandler;
import main.GamePanel;

public class NetworkManager {
    private ServerSocket serverSocket;
    private Socket clientSocket;
    private PrintWriter out;
    private BufferedReader in;
    private String nameServer;
    private String nameClient;
    private boolean isServer;
    private Configuration config;

    private GamePanel gamePanel;
    private Map<String, PlayerData> otherPlayers = new ConcurrentHashMap<>();
    
    private KeyHandler keyH;
    private Map<Socket, PrintWriter> clientWriters = new ConcurrentHashMap<>();
    private Map<Socket, BufferedReader> clientReaders = new ConcurrentHashMap<>();

    public NetworkManager(boolean isServer, Configuration config, GamePanel gamePanel, KeyHandler keyH) {
        this.keyH = keyH;
        this.isServer = isServer;
        this.config = config;
        this.gamePanel = gamePanel;

        if (isServer) {
            this.nameServer = promptInputName("Server");
            startServer();
        } else {
            this.nameClient = promptInputName("Client");
            startClient();
        }
    }

    public void startServer() {
        try {
            serverSocket = new ServerSocket(config.getPort());
            System.out.println("The server has started on port " + config.getPort() + "!");

            // Register the host player immediately after the server starts
            Player hostPlayer = gamePanel.getPlayer();
            if (hostPlayer != null) {
                registerPlayer(hostPlayer); // Ensure the host is registered and sent to all clients
            }

            new Thread(() -> {
                while (true) {
                    try {
                        Socket clientSocket = serverSocket.accept();
                        System.out.println("A new client has joined the game!");

                        PrintWriter clientOut = new PrintWriter(clientSocket.getOutputStream(), true);
                        BufferedReader clientIn = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

                        clientWriters.put(clientSocket, clientOut);
                        clientReaders.put(clientSocket, clientIn);

                        // Send initial data to the new client
                        for (PlayerData playerData : otherPlayers.values()) {
                            String message = "PLAYER_REGISTER " + playerData.getPlayerId() + " " + playerData.getX() + " " + playerData.getY() + " " + playerData.getDirection() + " " + playerData.getSpriteNum();
                            clientOut.println(message);
                        }

                        // Slightly move the host player to ensure the client sees it
                        if (isServer) {
                            
                            if (gamePanel.getPlayer() != null) {
                                
                                gamePanel.getPlayer().setDirection(gamePanel.getPlayer().getDirection());
                                
                                sendPlayerUpdate(gamePanel.getPlayer());
                            }
                        }

                        new Thread(() -> handleClient(clientSocket)).start();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void startClient() {
        try {
            clientSocket = new Socket(config.getIP(), config.getPort());
            System.out.println("Connected to server at " + config.getIP() + ":" + config.getPort());

            out = new PrintWriter(clientSocket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

            new Thread(this::readMessages).start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void handleClient(Socket socket) {
        try {
            BufferedReader clientIn = clientReaders.get(socket);
            String inputLine;
            while ((inputLine = clientIn.readLine()) != null) {
                handleReceivedData(inputLine);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            clientWriters.remove(socket);
            clientReaders.remove(socket);
        }
    }

    private void readMessages() {
        try {
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                handleReceivedData(inputLine);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void registerPlayer(Player player) {
        if (player == null) {
            System.err.println("Cannot register null player.");
            return;
        }

        String playerId = isServer ? nameServer : nameClient;
        int level = player.getLevel(); // Get the player's level
        String message = "PLAYER_REGISTER " + playerId + " " + player.getUsername() + " " + player.getWorldX() + " " + player.getWorldY() + " " + player.getDirection() + " " + player.getSpriteNum() + " " + level;

        PlayerData playerData = new PlayerData(playerId, player.getWorldX(), player.getWorldY(), player.getDirection(), player.getSpriteNum(), System.currentTimeMillis(), level);
        otherPlayers.put(playerId, playerData);
        gamePanel.updateOtherPlayer(playerId, playerData);

        if (isServer) {
            for (PrintWriter writer : clientWriters.values()) {
                writer.println(message);
            }
        } else {
            out.println(message);
        }
    }


    public void sendPlayerUpdate(Player player) {
        if (player == null) {
            System.err.println("Cannot update null player.");
            return;
        }

        String playerId = isServer ? nameServer : nameClient;
        int x = player.getWorldX();
        int y = player.getWorldY();
        String direction = player.getDirection();
        int spriteNum = player.getSpriteNum();
        long timestamp = System.currentTimeMillis();
        int level = player.getLevel(); // Get the player's level

        String message = "PLAYER_UPDATE " + playerId + " " + player.getUsername() + " " + x + " " + y + " " + direction + " " + spriteNum + " " + timestamp + " " + level;

        PlayerData playerData = new PlayerData(playerId, x, y, direction, spriteNum, timestamp, level);
        otherPlayers.put(playerId, playerData);
        gamePanel.updateOtherPlayer(playerId, playerData);

        if (isServer) {
            for (PrintWriter writer : clientWriters.values()) {
                writer.println(message);
            }
        } else {
            out.println(message);
        }
    }


    private void handleReceivedData(String data) {
        String[] tokens = data.split(" ");
        if (tokens[0].equals("PLAYER_UPDATE") || tokens[0].equals("PLAYER_REGISTER")) {
            try {
                String playerId = tokens[1];
                String username = tokens[2];
                int x = Integer.parseInt(tokens[3]);
                int y = Integer.parseInt(tokens[4]);
                String direction = tokens[5]; // Correctly parse direction as a String
                int spriteNum = Integer.parseInt(tokens[6]); // Get animation state
                long timestamp = (tokens[0].equals("PLAYER_UPDATE")) ? Long.parseLong(tokens[7]) : System.currentTimeMillis();
                int level = Integer.parseInt(tokens[tokens.length - 1]); // Parse the player's level
                PlayerData playerData = new PlayerData(playerId, x, y, direction, spriteNum, timestamp,level);
                otherPlayers.put(playerId, playerData);
                gamePanel.updateOtherPlayer(playerId, playerData);
            } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
                
            }
        }
    }

    public String promptInputName(String role) {
        String name;
        while (true) {
            name = JOptionPane.showInputDialog(null, "Input your username:", "Username Input", JOptionPane.PLAIN_MESSAGE);

            if (name == null) {
                return "Guest";
            } else if (name.trim().isEmpty()) {
                JOptionPane.showMessageDialog(null, "Username cannot be empty. Please enter a valid username.", "Invalid Input", JOptionPane.ERROR_MESSAGE);
            } else if (name.length() > 8) {
                JOptionPane.showMessageDialog(null, "Username cannot have more than 8 characters, please input less.", "Invalid length", JOptionPane.ERROR_MESSAGE);
            } else {
                return name;
            }
        }
    }

    public void close() {
        try {
            if (clientSocket != null)
                clientSocket.close();
            if (serverSocket != null)
                serverSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getNameServer() {
        return nameServer;
    }

    public String getNameClient() {
        return nameClient;
    }

    public boolean isServer() {
        return isServer;
    }
}


