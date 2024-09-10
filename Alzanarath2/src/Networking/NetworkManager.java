package Networking;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

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
    private final Map<String, Socket> playerToSocket = new ConcurrentHashMap<>();
    private BufferedWriter serverWriter;
    private ExecutorService clientExecutor = Executors.newCachedThreadPool();
    //save damage dealt to monster for the next update cycle
    int damage = 0;
    
    String name;
    public NetworkManager(boolean isServer, Configuration config, GamePanel gamePanel, KeyHandler keyH) {
        this.keyH = keyH;
        this.isServer = isServer;
        this.config = config;
        this.gamePanel = gamePanel;

        if (isServer) {
        	
            startServer();
        } else {
            this.nameClient = promptInputName("Client");
            startClient();
        }
    }
    

    private void startServer() {
        try {
            String radminIP = getRadminIPAddress();
            serverSocket = new ServerSocket(config.getPort(), 50, InetAddress.getByName(radminIP));
            System.out.println("Server started on Radmin IP " + radminIP + " and port " + config.getPort() + "!");
            serverSocket.setPerformancePreferences(1, 1, 2);
            serverSocket.setReceiveBufferSize(1024 * 64);
            
            
            clientExecutor.submit(() -> {
                while (true) {
                    try {
                        Socket clientSocket = serverSocket.accept();
                        clientSocket.setTcpNoDelay(true);  // Disable Nagle's algorithm for real-time
                        System.out.println("New client joined!");

                        BufferedWriter clientOut = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));
                        BufferedReader clientIn = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

                        clientWriters.put(clientSocket, clientOut);
                        clientReaders.put(clientSocket, clientIn);

                        sendAllPlayersToClient(clientOut); // Send existing players

                        clientExecutor.submit(() -> handleClient(clientSocket)); // Handle client
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    
 // Remove the player from the game
    private synchronized void removePlayer(String playerId) {
        otherPlayers.remove(playerId);  // Remove from player data
        gamePanel.removeOtherPlayer(playerId);  // Remove from the game panel
        playerToSocket.remove(playerId);  // Remove from socket mapping
    }

    // Notify all clients that a player has disconnected
    private void broadcastPlayerDisconnection(String playerId) {
        String message = String.format("PLAYER_DISCONNECTED %s", playerId);
        for (BufferedWriter writer : clientWriters.values()) {
            try {
                writer.write(message + "\n");
                writer.flush();
            } catch (IOException e) {
                System.err.println("Error broadcasting player disconnection: " + e.getMessage());
            }
        }
    }



    public void startClient() {
        try {
            String radminIP = "26.154.96.167"; // Replace with actual server IP
            clientSocket = new Socket(InetAddress.getByName(radminIP), config.getPort());
            System.out.println("Connected to server at Radmin IP " + radminIP + ":" + config.getPort());
            clientSocket.setPerformancePreferences(1, 0, 2);
            clientSocket.setTcpNoDelay(true);
            clientSocket.setReceiveBufferSize(1024 * 64);
            clientSocket.setSendBufferSize(1024 * 64);
            // Initialize BufferedWriter and BufferedReader
            out = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

            // Store the BufferedWriter in clientWriters
            synchronized (this) {
                clientWriters.put(clientSocket, out);
                clientReaders.put(clientSocket, in);
            }

            // Start a thread to read messages from the server
            clientExecutor.submit(this::readMessages);

            // Register the client player after connecting
            Player clientPlayer = gamePanel.getPlayer();
            if (clientPlayer != null) {
                registerPlayer(clientPlayer);
                requestExistingPlayersData();
            }

            // Request existing players' data from the server
           

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void requestExistingPlayersData() throws IOException {
        if (out != null) {
            // Send a request to the server to get existing player data
            out.write("REQUEST_PLAYERS_DATA\n");
            out.flush();
        }
    }


    private void sendAllPlayersToClient(BufferedWriter clientOut) throws IOException {
        long timestamp = System.currentTimeMillis(); // Current timestamp

        // Iterate through all registered players
        for (PlayerData playerData : otherPlayers.values()) {
            // Format the player data message
            String message = String.format("PLAYER_REGISTER %s %s %d %d %s %d %d %d %b %d %d", 
                    playerData.getPlayerId(),  
                    playerData.getUsername(), 
                    playerData.getX(), 
                    playerData.getY(), 
                    playerData.getDirection(),
                    playerData.getSpriteNum(),
                    timestamp, // Current timestamp
                    playerData.getLevel(),
                    playerData.isAttacking(),
                    playerData.getSpriteCounter(),
                    playerData.getInvincibleCounter());

            // Send the message to the client
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

    
    private String compressData(String data) {
        try {
            ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
            GZIPOutputStream gzipStream = new GZIPOutputStream(byteStream);
            gzipStream.write(data.getBytes("UTF-8"));
            gzipStream.close();
            return Base64.getEncoder().encodeToString(byteStream.toByteArray());
        } catch (IOException e) {
            e.printStackTrace();
            return data; // Fallback to original data if compression fails
        }
    }

    private String decompressData(String compressedData) {
        try {
            byte[] compressedBytes = Base64.getDecoder().decode(compressedData);
            GZIPInputStream gzipStream = new GZIPInputStream(new ByteArrayInputStream(compressedBytes));
            BufferedReader reader = new BufferedReader(new InputStreamReader(gzipStream, "UTF-8"));
            StringBuilder decompressed = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                decompressed.append(line);
            }
            return decompressed.toString();
        } catch (IOException e) {
            e.printStackTrace();
            return compressedData; // Fallback if decompression fails
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
    //Register the Server as a player (HOST)
    public synchronized void registerServerPlayer(Player serverPlayer) {
        if (serverPlayer == null) return;

        // Assign the server a unique player ID, like "SERVER"
        String playerId = "SERVER";
        String username = serverPlayer.getUsername();
        int level = serverPlayer.getLevel();
        boolean isAttacking = serverPlayer.isAttacking();
        int spriteCounter = serverPlayer.getSpriteCounter();
        int invincibleCounter = serverPlayer.getInvincibleCounter();

        // Create the registration message
        String message = String.format("PLAYER_REGISTER %s %s %d %d %s %d %d %d %b %d %d", 
                playerId, username, 
                serverPlayer.getWorldX(), serverPlayer.getWorldY(), 
                serverPlayer.getDirection(), serverPlayer.getSpriteNum(), 
                System.currentTimeMillis(), level,
                isAttacking, spriteCounter, invincibleCounter);

        // Register the server player
        PlayerData playerData = new PlayerData(playerId, username, serverPlayer.getWorldX(), serverPlayer.getWorldY(), serverPlayer.getDirection(), serverPlayer.getSpriteNum(), System.currentTimeMillis(), level, isAttacking, spriteCounter, invincibleCounter);

        // Store the player data and map it to the "server" socket (can be null if server-side)
        otherPlayers.put(playerId, playerData);
        gamePanel.updateOtherPlayer(playerId, playerData);

        // Server player doesn't need a socket mapping, but if you're using one:
        playerToSocket.put(playerId, null);  // Null socket for server player
    }

    public synchronized void registerPlayer(Player player) {
        if (player == null) return;

        // Determine player ID and other details
        String playerId = isServer ? nameServer : nameClient;
        String username = player.getUsername();
        int level = player.getLevel();
        boolean isAttacking = player.isAttacking();
        int spriteCounter = player.getSpriteCounter();
        int invincibleCounter = player.getInvincibleCounter();
        
        // Create the registration message
        String message = String.format("PLAYER_REGISTER %s %s %d %d %s %d %d %d %b %d %d", 
                playerId, username, 
                player.getWorldX(), player.getWorldY(), 
                player.getDirection(), player.getSpriteNum(), 
                System.currentTimeMillis(), level,
                isAttacking, spriteCounter, invincibleCounter);

        PlayerData playerData = new PlayerData(playerId, username, player.getWorldX(), player.getWorldY(), player.getDirection(), player.getSpriteNum(), System.currentTimeMillis(), level, isAttacking, spriteCounter, invincibleCounter);

        otherPlayers.put(playerId, playerData);
        gamePanel.updateOtherPlayer(playerId, playerData);

        // Update player-to-socket mapping
        if (isServer) {
            playerToSocket.put(playerId, clientSocket);
        } else {
            playerToSocket.put(playerId, clientSocket);
        }

        // Send registration message to all clients or the server
        if (isServer) {
            for (Map.Entry<Socket, BufferedWriter> entry : clientWriters.entrySet()) {
                Socket clientSocket = entry.getKey();
                BufferedWriter writer = entry.getValue();
                try {
                    writer.write(message + "\n");
                    writer.flush();
                    System.out.println("Sent registration data to client: " + message);
                } catch (IOException e) {
                    System.err.println("Error sending player registration data: " + e.getMessage());
                }
            }
        } else {
            try {
                out.write(message + "\n");
                out.flush();
                System.out.println("Sent registration data to server: " + message);
            } catch (IOException e) {
                System.err.println("Error sending player registration data to server: " + e.getMessage());
            }
        }
    }



    public void sendPlayerUpdate(Player player) {
        if (player == null) return;

        String playerId = nameClient;
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
        
        while (true) {
            name = gamePanel.keyH.username;
            if (name == null || name.trim().isEmpty() || name.length() > 8) {
                JOptionPane.showMessageDialog(null, "Invalid username. Please enter a valid name.", "Error", JOptionPane.ERROR_MESSAGE);
            } else {
                return name;
            }
        }
    }

    public void receiveMessage(String message) {
        gamePanel.ui.appendGlobalChatMessage(message);

        // Limit the number of messages in the chat to 5
        if (gamePanel.ui.getGlobalChatMessages().size() > 5) {
            gamePanel.ui.getGlobalChatMessages().remove(0); // Remove the oldest message
        }
    }

    private void handleReceivedData(String data) {
        // Split the data by space
        String[] tokens = data.split(" ");

        // Ensure we have at least two tokens for command and sender username
        if (tokens.length < 2) {
            System.err.println("Insufficient data: " + data);
            return;
        }

        // Extract the command and sender username
        String command = tokens[0];
        String senderUsername = tokens[1];
        String payload = String.join(" ", Arrays.copyOfRange(tokens, 2, tokens.length));

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
                try {
                    // Extract monsterId
                    String monsterId = tokens[1];

                    // Reconstruct name from the tokens (enclosed in quotes)
                    String name = tokens[2];
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
                    MonsterData monsterData = new MonsterData(monsterId, x, y, name, speed, health, maxHealth, attack, direction, spriteNum);
                    gamePanel.updateMonster(monsterId, monsterData);

                } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
                    System.err.println("Error parsing monster data: " + e.getMessage());
                    e.printStackTrace();
                }
                break;
                
            case "PLAYER_DISCONNECT":
                if (tokens.length < 2) { // Ensure there are enough tokens for playerId
                    System.err.println("Error: Data format is incorrect for player disconnection. Expected at least 2 parts, but got " + tokens.length);
                    return;
                }

                try {
                    // Extract the playerId
                    String playerId = tokens[1];

                    // Remove the player from the game
                    gamePanel.removeOtherPlayer(playerId);

                    System.out.println("Player " + playerId + " has disconnected.");
                } catch (Exception e) {
                    System.err.println("Error handling player disconnection: " + e.getMessage());
                    e.printStackTrace();
                }
                break;

            case "MONSTER_DEATH":
                if (tokens.length < 2) { // Ensure there are enough tokens
                    System.err.println("Error: Data format is incorrect for monster death. Expected at least 2 parts, but got " + tokens.length);
                    return;
                }
                try {
                    // Extract monsterId
                    String monsterId = tokens[1];
                    
                    // Find the monster and set it to null
                    removeMonsterById(monsterId);
                }
                    
                catch (Exception e) {
                    System.err.println("Error handling monster death: " + e.getMessage());
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

    public void sendMonsterHit(String monsterId, int damage, int health) {
        for (int i = 0; i < gamePanel.monster.length; i++) {
            if (gamePanel.monster[i].getMonsterId().equals(monsterId)) {
                gamePanel.monster[i].hitMonster(monsterId, damage, health);

                // If the monster is dead, set it to null
                if (gamePanel.monster[i].isAlive()==false) {
                    gamePanel.monster[i] = null; // Set dead monster to null
                }

                break;
            }
        }
    }
    
    public void removeMonsterById(String monsterId) {
        for (int i = 0; i < gamePanel.monster.length; i++) {
            Entity monster = gamePanel.monster[i];
            
            if (monster != null) {
                if (monster.getMonsterId() != null && monster.getMonsterId().equals(monsterId)) {
                    // Remove the monster if the ID matches
                    gamePanel.monster[i] = null;
                    break;
                } else if (monster.getMonsterId() == null) {
                    // Assign the monster ID if it doesn't already have one
                    monster.setMonsterId(monsterId);
                }
            }
        }
    }
        
       
    



    public void sendMonsterDataToAllClients(String monsterId, Entity monster) {
        long timestamp = System.currentTimeMillis(); // Optional timestamp

        // Format the monster data string with relevant fields
        String data = formatMonsterData(monster, timestamp);

        // Send the data to all clients
        for (BufferedWriter writer : clientWriters.values()) {
            try {
                writer.write(data + "\n");
                writer.flush();
            } catch (IOException e) {
                System.err.println("Error sending monster data to client: " + e.getMessage());
            }
        }
    }
    
    

    
    //ON MONSTER DEATH
    
    public void sendMonsterDeathToAllClients(String monsterId) {
        // Create a message to broadcast the monster death
        String message = String.format("MONSTER_DEATH %s", monsterId);
        
        // Iterate over all client writers and send the message
        for (Map.Entry<Socket, BufferedWriter> entry : clientWriters.entrySet()) {
            try {
                BufferedWriter writer = entry.getValue();
                writer.write(message + "\n");
                writer.flush();
            } catch (IOException e) {
                System.err.println("Error sending monster death data to client: " + e.getMessage());
            }
        }
    }

    public String formatMonsterData(Entity monster, long timestamp) {
        // Generate a unique ID for the monster if not provided (assuming monsters have an ID field)
        String monsterId = monster.getMonsterId(); // Ensure this method exists in your Entity class

        // Format the monster data into a string
        String data = String.format("MONSTER_UPDATE %s %s %d %d %s %d %d %d %d %d %d",
            monsterId,         // Unique monster ID
            monster.getName(), // Name (enclosed in quotes)
            monster.getWorldX(), // X position
            monster.getWorldY(), // Y position
            monster.getDirection(), // Current direction
            monster.getSpriteNum(), // Sprite number (for animation state)
            monster.getHealth(), // Current health
            monster.getMaxHealth(), // Maximum health
            monster.getSpeed(), // Speed
            monster.getAttack(), // Attack power
            timestamp // Current timestamp, if relevant
        );

        return data;
    }

    public void sendMonsterData(String monsterId, Entity monster, boolean isRegister) {
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
            monster.getDirection(),    // Direction
            monster.getSpriteNum(),    // Sprite number
            monster.getHealth(),       // Current health
            monster.getMaxHealth(),    // Maximum health
            monster.getSpeed(),        // Speed
            monster.getAttack()        // Attack power
        );

        
    }
    
    

    public void sendMonsterDataToServer(String playerId, String monsterId, Entity monster) {
        System.out.println("Player ID: " + playerId);
        System.out.println("Other Players: " + otherPlayers.keySet());

        if (otherPlayers != null && otherPlayers.containsKey(playerId)) {
            Socket playerSocket = playerToSocket.get(playerId);

            if (playerSocket != null) {
                BufferedWriter writer = clientWriters.get(playerSocket);
                if (writer != null) {
                    String data = formatMonsterData(monster, System.currentTimeMillis());
                    try {
                        writer.write(data + "\n");
                        writer.flush();
                        System.out.println("Monster data sent to player with ID " + playerId);
                    } catch (IOException e) {
                        System.err.println("Error sending monster data to player with ID " + playerId + ": " + e.getMessage());
                    }
                } else {
                    System.err.println("BufferedWriter for player with ID " + playerId + " not found.");
                }
            } else {
                System.err.println("Socket for player with ID " + playerId + " not found.");
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

	public Socket getClientSocket() {
		// TODO Auto-generated method stub
		return clientSocket;
	}
}