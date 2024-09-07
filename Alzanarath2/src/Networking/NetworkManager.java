package Networking;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.*;
import javax.swing.JOptionPane;

import Entity.Entity;
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
    
    private Map<Socket, String> socketToPlayerId = new ConcurrentHashMap<>();
    
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
                        sendMonsterDataToAllClients(); // Send updated monster data to all clients
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
    	long timestampo = System.currentTimeMillis();
        for (PlayerData playerData : otherPlayers.values()) {
            String message = String.format("PLAYER_REGISTER %s %s %d %d %s %d %d %b %d %d", 
                                            playerData.getPlayerId(),  
                                            playerData.getUsername(), 
                                            playerData.getX(), 
                                            playerData.getY(), 
                                            playerData.getDirection(),
                                            playerData.getSpriteNum(),
                                            playerData.getLevel(),
                                            playerData.isAttacking(),
                                            playerData.getSpriteCounter(),
                                            playerData.getInvincibleCounter());
            								
            clientOut.write(message + "\n");
            clientOut.flush();
        }
    }

    public void handleClient(Socket socket) {
        try (BufferedReader clientIn = clientReaders.get(socket)) {
            String inputLine;
            while ((inputLine = clientIn.readLine()) != null) {
                handleReceivedData(inputLine);

                // Broadcast data to all clients except the sender
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
        int invincibleCounter = player.getInvincibleCounter();
        String message = String.format("PLAYER_REGISTER %s %s %d %d %s %d %d %d %b %d %d", 
                playerId, username, 
                player.getWorldX(), player.getWorldY(), 
                player.getDirection(), player.getSpriteNum(), 
                System.currentTimeMillis(), level,
                isAttacking,spriteCounter,invincibleCounter);

        PlayerData playerData = new PlayerData(playerId, username, player.getWorldX(), player.getWorldY(), player.getDirection(), player.getSpriteNum(), System.currentTimeMillis(), level, isAttacking, spriteCounter,invincibleCounter);
        otherPlayers.put(playerId, playerData);
        gamePanel.updateOtherPlayer(playerId, playerData);

        // Update socket to player ID mapping
        if (isServer) {
            for (Map.Entry<Socket, BufferedWriter> entry : clientWriters.entrySet()) {
                Socket clientSocket = entry.getKey();
                String clientId = socketToPlayerId.get(clientSocket); // Assuming client ID is updated somewhere
                if (clientId != null) {
                    // Update player info on the server
                    if (clientId.equals(playerId)) {
                        socketToPlayerId.put(clientSocket, playerId);
                    }
                }
                BufferedWriter writer = entry.getValue();
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
        int spriteCounter = player.getSpriteCounter();
        int invincibleCounter = player.getInvincibleCounter(); // Get invincibleCounter

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

        String message = String.format("PLAYER_UPDATE %s %s %d %d %s %d %d %d %b %d %d",
                playerId, username, 
                worldX, worldY, 
                player.getDirection(), player.getSpriteNum(), 
                timestamp, level,
                isAttacking, spriteCounter, invincibleCounter);

        PlayerData playerData = new PlayerData(playerId, username, worldX, worldY, player.getDirection(), player.getSpriteNum(), timestamp, level, isAttacking, spriteCounter, invincibleCounter);
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
        // Split the data by space
        String[] tokens = data.split(" ");
        
        // Ensure we have at least two tokens for command and username
        if (tokens.length < 2) {
            System.err.println("Insufficient data: " + data);
            return;
        }

        // Extract the command and sender username
        String command = tokens[0];
        String senderUsername = tokens[1];
        String payload = String.join(" ", Arrays.copyOfRange(tokens, 2, tokens.length));
        
        // Debug: Print tokens for inspection
        for (int i = 0; i < tokens.length; i++) {
            System.out.println(tokens[i]);
        }
        
        switch (command) {
            case "PLAYER_UPDATE":
            case "PLAYER_REGISTER":
                if (tokens.length < 11) { // Ensure there are enough tokens
                    System.err.println("Error: Data format is incorrect for player. Expected at least 11 parts, but got " + tokens.length);
                    return;
                }
                
                try {
                    String playerId = tokens[1];
                    String username = tokens[2];
                    int x = Integer.parseInt(tokens[3]);
                    int y = Integer.parseInt(tokens[4]);
                    String direction = tokens[5];
                    int spriteNum = Integer.parseInt(tokens[6]);
                    long timestamp = Long.parseLong(tokens[7]);
                    int level = Integer.parseInt(tokens[tokens.length - 4]);
                    boolean isAttacking = Boolean.parseBoolean(tokens[tokens.length - 3]);
                    int spriteCounter = Integer.parseInt(tokens[tokens.length - 2]);
                    int invincibleCounter = Integer.parseInt(tokens[tokens.length - 1]);

                    // Updated PlayerData to include invincibleCounter
                    PlayerData playerData = new PlayerData(playerId, username, x, y, direction, spriteNum, timestamp, level, isAttacking, spriteCounter, invincibleCounter);
                    otherPlayers.put(playerId, playerData);
                    gamePanel.updateOtherPlayer(playerId, playerData);

                } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
                    System.err.println("Error parsing player data: " + e.getMessage());
                    e.printStackTrace();
                }
                break;

            case "MONSTER_UPDATE":
            case "MONSTER_REGISTER":
                if (tokens.length < 11) { // Ensure enough tokens for monster data
                    System.err.println("Error: Data format is incorrect for monster. Expected at least 11 parts, but got " + tokens.length);
                    return;
                }
                
                System.out.println("works");

                try {
                    // Extract monsterId
                    String monsterId = tokens[1];
                    
                    // Reconstruct name from the tokens (enclosed in quotes)
                    StringBuilder nameBuilder = new StringBuilder();
                    boolean insideQuotes = false;
                    for (int i = 2; i < tokens.length; i++) {
                        if (tokens[i].startsWith("\"") && !insideQuotes) {
                            insideQuotes = true;
                            nameBuilder.append(tokens[i].substring(1)); // Remove starting quote
                        } else if (tokens[i].endsWith("\"") && insideQuotes) {
                            insideQuotes = false;
                            nameBuilder.append(" ").append(tokens[i].substring(0, tokens[i].length() - 1)); // Remove ending quote
                            break;
                        } else if (insideQuotes) {
                            nameBuilder.append(" ").append(tokens[i]);
                        }
                    }
                    String name = nameBuilder.toString();
                    
                    // Extract other monster data
                    int x = Integer.parseInt(tokens[3]);
                    int y = Integer.parseInt(tokens[4]);
                    String direction = tokens[5];
                    int spriteNum = Integer.parseInt(tokens[6]);
                    int health = Integer.parseInt(tokens[7]);
                    int maxHealth = Integer.parseInt(tokens[8]);
                    int speed = Integer.parseInt(tokens[9]);
                    int attack = Integer.parseInt(tokens[10]);

                    // Update or register the monster
                    MonsterData monsterData = new MonsterData(x, y, name, speed, health, maxHealth, attack, direction, spriteNum);
                    gamePanel.updateMonster(monsterId, monsterData);

                } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
                    System.err.println("Error parsing monster data: " + e.getMessage());
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
    
    public void sendMonsterDataToAllClients() {
    	
        long timestamp = System.currentTimeMillis(); // Add timestamp if necessary
        for (Entity monster : gamePanel.monster) {
            if (monster != null) {
                String data = formatMonsterData(monster, timestamp); // Pass timestamp if required
                System.out.println("Sending monster data: " + data);
                for (BufferedWriter writer : clientWriters.values()) {
                    try {
                        writer.write(data + "\n");
                        writer.flush();
                    } catch (IOException e) {
                        System.err.println("Error sending monster data to client: " + e.getMessage());
                    }
                }
            }
        }
    }

    public String formatMonsterData(Entity monster, long timestamp) {
        // Generate a unique ID for the monster if not provided (assuming monsters have an ID field)
        String monsterId = monster.getMonsterId(); // Ensure this method exists in your Entity class

        // Format the monster data into a string
        String data = String.format("MONSTER_UPDATE %s \"%s\" %d %d %s %d %d %d %d %d %d",
            monsterId,         // Unique monster ID
            monster.getName(), // Name (enclosed in quotes)
            monster.getWorldX(), // X position
            monster.getWorldY(), // Y position
            monster.getDirection(), // Current direction
            monster.getSpeed(), // Speed
            monster.getHealth(), // Current health
            monster.getMaxHealth(), // Maximum health
            monster.getAttack(), // Attack power
            monster.getSpriteNum(), // Sprite number (for animation state)
            timestamp // Current timestamp, if relevant
        );

        return data;
    }
    
    public void sendMonsterData(String monsterId, Entity monster, boolean isRegister) {
        // Check if this code is running on the server side
        if (!isServer) {
            return; // Exit if not on the server
        }

        String command = isRegister ? "MONSTER_REGISTER" : "MONSTER_UPDATE";
        
        String data = String.format("%s %s \"%s\" %d %d %s %d %d %d %d %d",
            command,
            monsterId,                 // Unique monster ID
            monster.getName(),         // Name (enclosed in quotes)
            monster.getWorldX(),       // X position
            monster.getWorldY(),       // Y position
            monster.getDirection(),    // Current direction
            monster.getSpeed(),        // Speed
            monster.getHealth(),       // Current health
            monster.getMaxHealth(),    // Maximum health
            monster.getAttack(),       // Attack power
            monster.getSpriteNum()     // Sprite number (for animation state)
        );

        // Send data to all clients
        for (String playerId : socketToPlayerId.values()) {
            sendMonsterDataToPlayer(playerId, data);
        }
    }

    
   
    
   
    
    //HANDLING MONSTERS 
    
   

    // Converts the MonsterData object to a string or format appropriate for your network protocol
    private String convertMonsterDataToString(MonsterData monsterData) {
        return String.format("%s %d %d %s %d %d %d %d %d",
            monsterData.getName(),               // Monster name
            monsterData.getWorldX(),             // X position
            monsterData.getWorldY(),             // Y position
            monsterData.getDirection(),          // Direction the monster is facing
            monsterData.getSpriteNum(),          // Sprite number (for animation state)
            monsterData.getHealth(),             // Current health
            monsterData.getMaxHealth(),          // Maximum health
            monsterData.getSpeed(),              // Speed
            monsterData.getAttack()              // Attack power
        );
    }

    private void sendMonsterDataToPlayer(String playerId, String monsterData) {
        // Find the corresponding socket for the playerId
        Socket playerSocket = socketToPlayerId.entrySet().stream()
            .filter(entry -> playerId.equals(entry.getValue()))
            .map(Map.Entry::getKey)
            .findFirst()
            .orElse(null);

        if (playerSocket != null) {
            BufferedWriter writer = clientWriters.get(playerSocket);

            if (writer != null) {
                try {
                    writer.write(monsterData + "\n");
                    writer.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                System.err.println("BufferedWriter for player with ID " + playerId + " not found.");
            }
        } else {
            System.err.println("Player with ID " + playerId + " not found.");
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