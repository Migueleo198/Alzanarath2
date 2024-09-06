package Networking;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.*;
import javax.swing.JOptionPane;
import Entity.Player;
import Inputs.KeyHandler;
import main.GamePanel;

public class NetworkManager {
    private ServerSocket serverSocket;
    private Socket clientSocket;
    private BufferedWriter out;
    private BufferedReader in;
    private String nameServer;
    private String nameClient;
    private boolean isServer;
    private Configuration config;

    private GamePanel gamePanel;
    private Map<String, PlayerData> otherPlayers = new ConcurrentHashMap<>();
    private KeyHandler keyH;
    private Map<Socket, BufferedWriter> clientWriters = new ConcurrentHashMap<>();
    private Map<Socket, BufferedReader> clientReaders = new ConcurrentHashMap<>();
    
    private ExecutorService clientExecutor = Executors.newCachedThreadPool();
    
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
            String radminIP = getRadminIPAddress();
            serverSocket = new ServerSocket(config.getPort(), 50, InetAddress.getByName(radminIP));
            System.out.println("Server started on Radmin IP " + radminIP + " and port " + config.getPort() + "!");
            serverSocket.setPerformancePreferences(1, 0, 2);
            
            Player hostPlayer = gamePanel.getPlayer();
            if (hostPlayer != null) {
                registerPlayer(hostPlayer); // Register the host
            }

            clientExecutor.submit(() -> {
                while (true) {
                    try {
                        Socket clientSocket = serverSocket.accept();
                        System.out.println("New client joined!");

                        BufferedWriter clientOut = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));
                        BufferedReader clientIn = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

                        clientWriters.put(clientSocket, clientOut);
                        clientReaders.put(clientSocket, clientIn);

                        sendAllPlayersToClient(clientOut);
                        clientExecutor.submit(() -> handleClient(clientSocket));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void startClient() {
        try {
            String radminIP = "26.154.96.167";
            clientSocket = new Socket(InetAddress.getByName(radminIP), config.getPort());
            System.out.println("Connected to server at Radmin IP " + radminIP + ":" + config.getPort());
            clientSocket.setPerformancePreferences(1, 0, 2);
            clientSocket.setTcpNoDelay(true);
            out = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

            clientExecutor.submit(this::readMessages);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void sendAllPlayersToClient(BufferedWriter clientOut) throws IOException {
        for (PlayerData playerData : otherPlayers.values()) {
            String message = String.format("PLAYER_REGISTER %s %s %d %d %s %d %d %b %d", 
                                            playerData.getPlayerId(),  
                                            playerData.getUsername(), 
                                            playerData.getX(), 
                                            playerData.getY(), 
                                            playerData.getDirection(), 
                                            playerData.getSpriteNum(),
                                            playerData.getLevel(),
                                            playerData.isAttacking(),
                                            playerData.getSpriteCounter());
            clientOut.write(message + "\n");
            clientOut.flush();
        }
    }

    public void handleClient(Socket socket) {
        try (BufferedReader clientIn = clientReaders.get(socket)) {
            String inputLine;
            while ((inputLine = clientIn.readLine()) != null) {
                handleReceivedData(inputLine);

                for (Map.Entry<Socket, BufferedWriter> entry : clientWriters.entrySet()) {
                    if (entry.getKey() != socket) {
                        BufferedWriter writer = entry.getValue();
                        writer.write(inputLine + "\n");
                        writer.flush();
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            clientWriters.remove(socket);
            clientReaders.remove(socket);
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
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

    private String getRadminIPAddress() {
        try {
            Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();
            while (networkInterfaces.hasMoreElements()) {
                NetworkInterface networkInterface = networkInterfaces.nextElement();
                if (networkInterface.getDisplayName().toLowerCase().contains("radmin")) {
                    Enumeration<InetAddress> inetAddresses = networkInterface.getInetAddresses();
                    while (inetAddresses.hasMoreElements()) {
                        InetAddress inetAddress = inetAddresses.nextElement();
                        if (inetAddress instanceof Inet4Address) {
                            return inetAddress.getHostAddress();
                        }
                    }
                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void registerPlayer(Player player) {
        if (player == null) return;

        String playerId = isServer ? nameServer : nameClient;
        String username = player.getUsername();
        int level = player.getLevel();
        boolean isAttacking = player.isAttacking();
        int spriteCounter = player.getSpriteCounter(); // Get the player's sprite counter
        
        String message = String.format("PLAYER_REGISTER %s %s %d %d %s %d %d %d %b %d", 
                playerId, username, 
                player.getWorldX(), player.getWorldY(), 
                player.getDirection(), player.getSpriteNum(), 
                System.currentTimeMillis(), level,
                isAttacking, spriteCounter);

        PlayerData playerData = new PlayerData(playerId, username, player.getWorldX(), player.getWorldY(), player.getDirection(), player.getSpriteNum(), System.currentTimeMillis(), level, isAttacking, spriteCounter);
        otherPlayers.put(playerId, playerData);
        gamePanel.updateOtherPlayer(playerId, playerData);

        if (isServer) {
            for (BufferedWriter writer : clientWriters.values()) {
                try {
                    writer.write(message + "\n");
                    writer.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } else {
            try {
                out.write(message + "\n");
                out.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void sendPlayerUpdate(Player player) {
        if (player == null) return;

        String playerId = isServer ? nameServer : nameClient;
        String username = player.getUsername();
        boolean isAttacking = player.isAttacking();
        int level = player.getLevel();
        long timestamp = System.currentTimeMillis();
        int worldX = player.getWorldX();
        int worldY = player.getWorldY();
        int spriteCounter = player.getSpriteCounter(); // Get sprite counter from player

        int attackOffsetX = 0;
        int attackOffsetY = 0;

        if (isAttacking) {
            switch (player.getDirection()) {
                case "left":
                    attackOffsetX = -gamePanel.getTileSize(); 
                    break;
                case "right":
                    attackOffsetX = gamePanel.getTileSize(); 
                    break;
                case "up":
                    attackOffsetY = -gamePanel.getTileSize(); 
                    break;
                case "down":
                    attackOffsetY = gamePanel.getTileSize(); 
                    break;
            }
        }

        worldX += attackOffsetX;
        worldY += attackOffsetY;

        String message = String.format("PLAYER_UPDATE %s %s %d %d %s %d %d %d %b %d",
                playerId, username, 
                worldX, worldY, 
                player.getDirection(), player.getSpriteNum(), 
                timestamp, level,
                isAttacking, spriteCounter);

        PlayerData playerData = new PlayerData(playerId, username, worldX, worldY, player.getDirection(), player.getSpriteNum(), timestamp, level, isAttacking, spriteCounter);
        otherPlayers.put(playerId, playerData);
        gamePanel.updateOtherPlayer(playerId, playerData);

        if (isServer) {
            for (BufferedWriter writer : clientWriters.values()) {
                try {
                    writer.write(message + "\n");
                    writer.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } else {
            try {
                out.write(message + "\n");
                out.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void sendChatMessage(String message) {
        String username = gamePanel.getPlayer().getUsername();
        String formattedMessage = "CHAT " + username + " " + message;

        if (isServer) {
            for (BufferedWriter writer : clientWriters.values()) {
                try {
                    writer.write(formattedMessage + "\n");
                    writer.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } else {
            try {
                out.write(formattedMessage + "\n");
                out.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public String promptInputName(String role) {
        String name;
        while (true) {
            name = JOptionPane.showInputDialog(null, "Input your username:", "Username Input", JOptionPane.PLAIN_MESSAGE);
            if (name == null || name.trim().isEmpty() || name.length() > 8) {
                JOptionPane.showMessageDialog(null, "Invalid username. Please enter a valid name.", "Error", JOptionPane.ERROR_MESSAGE);
            } else {
                return name;
            }
        }
    }

    public void receiveMessage(String message) {
        gamePanel.ui.appendGlobalChatMessage(message);
    }

    private void handleReceivedData(String data) {
        String[] tokens = data.split(" ");
        if (tokens.length < 3) {
            System.err.println("Insufficient data: " + data);
            return;
        }

        String command = tokens[0];
        String senderUsername = tokens[1];
        String payload = String.join(" ", Arrays.copyOfRange(tokens, 2, tokens.length));

        switch (command) {
            case "PLAYER_UPDATE":
            case "PLAYER_REGISTER":
                try {
                    String playerId = tokens[1];
                    String username = tokens[2];
                    int x = Integer.parseInt(tokens[3]);
                    int y = Integer.parseInt(tokens[4]);
                    String direction = tokens[5];
                    int spriteNum = Integer.parseInt(tokens[6]);
                    long timestamp = Long.parseLong(tokens[7]);
                    int level = Integer.parseInt(tokens[tokens.length - 3]);
                    boolean isAttacking = Boolean.parseBoolean(tokens[tokens.length - 2]);
                    int spriteCounter = Integer.parseInt(tokens[tokens.length - 1]);

                    PlayerData playerData = new PlayerData(playerId, username, x, y, direction, spriteNum, timestamp, level, isAttacking, spriteCounter);;
                    otherPlayers.put(playerId, playerData);
                    gamePanel.updateOtherPlayer(playerId, playerData);

                } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
                    System.err.println("Error parsing data: " + e.getMessage());
                    e.printStackTrace();
                }
                break;

            case "CHAT":
                if (!payload.isBlank() && !senderUsername.equals(gamePanel.getPlayer().getUsername())) {
                    receiveMessage(senderUsername + ": " + payload);
                } else {
                    System.out.println("Received chat message with blank payload.");
                }
                break;

            default:
                System.err.println("Unknown command received: " + command);
                break;
        }
    }

    public void close() {
        try {
            if (clientSocket != null) clientSocket.close();
            if (serverSocket != null) serverSocket.close();
            for (BufferedWriter writer : clientWriters.values()) {
                writer.close();
            }
            for (BufferedReader reader : clientReaders.values()) {
                reader.close();
            }
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