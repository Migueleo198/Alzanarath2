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
    
    // Initialize keyHandler here for the clients
    private KeyHandler keyH;
    
    // Data structures to manage client connections and their streams
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

            new Thread(() -> {
                while (true) {
                    try {
                        Socket clientSocket = serverSocket.accept();
                        System.out.println("A new client has joined the game!");

                        PrintWriter clientOut = new PrintWriter(clientSocket.getOutputStream(), true);
                        BufferedReader clientIn = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

                        clientWriters.put(clientSocket, clientOut);
                        clientReaders.put(clientSocket, clientIn);

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
        String playerId = isServer ? nameServer : nameClient;
        String message = "PLAYER_REGISTER " + playerId + " " + player.getUsername() + " " + player.getWorldX() + " " + player.getWorldY() + " " + player.getDirection();
        
        if (isServer) {
            for (PrintWriter writer : clientWriters.values()) {
                writer.println(message);
            }
        } else {
            out.println(message);
        }
    }

    public void sendPlayerUpdate(Player player) {
        String playerId = isServer ? nameServer : nameClient;
        int x = player.getWorldX();
        int y = player.getWorldY();
        String direction = player.getDirection();
        int spriteNum = player.getSpriteNum(); // Include animation state
        long timestamp = System.currentTimeMillis();

        String message = "PLAYER_UPDATE " + playerId + " " + player.getUsername() + " " + x + " " + y + " " + direction + " " + spriteNum + " " + timestamp;

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
        if (tokens[0].equals("PLAYER_UPDATE")) {
            String playerId = tokens[1];
            String username = tokens[2];
            int x = Integer.parseInt(tokens[3]);
            int y = Integer.parseInt(tokens[4]);
            String direction = tokens[5];
            int spriteNum = Integer.parseInt(tokens[6]); // Get animation state
            long timestamp = Long.parseLong(tokens[7]);

            PlayerData playerData = new PlayerData(playerId, x, y, direction, spriteNum, timestamp);
            gamePanel.updateOtherPlayer(playerId, playerData);
        } else if (tokens[0].equals("PLAYER_REGISTER")) {
            // Handle player registration if needed
            // No animation state here, if necessary add it similarly
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

