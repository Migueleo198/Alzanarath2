package Networking;

import java.io.*;
import java.net.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Enumeration;
import java.util.Map;
import javax.swing.JOptionPane;
import Entity.Player;
import Inputs.KeyHandler;
import main.GamePanel;

import java.io.*;
import java.net.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;
import javax.swing.JOptionPane;

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
            String hamachiIP = getHamachiIPAddress();
            serverSocket = new ServerSocket(config.getPort(), 50, InetAddress.getByName(hamachiIP));
            System.out.println("The server has started on Hamachi IP " + hamachiIP + " and port " + config.getPort() + "!");

            Player hostPlayer = gamePanel.getPlayer();
            if (hostPlayer != null) {
                registerPlayer(hostPlayer); // Register the host
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

                        // Send the current state of all players to the new client
                        for (PlayerData playerData : otherPlayers.values()) {
                            String message = String.format("PLAYER_REGISTER %s %s %d %d %s %d %d", 
                                                            playerData.getPlayerId(),  
                                                            playerData.getUsername(), 
                                                            playerData.getX(), 
                                                            playerData.getY(), 
                                                            playerData.getDirection(), 
                                                            playerData.getSpriteNum(),
                                                            playerData.getLevel());
                            clientOut.println(message);
                        }

                        // Send new player data to all previously connected clients
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
    /**
     * Start the client and connect to the server using the Hamachi IP.
     */
    public void startClient() {
        try {
        	 String hamachiIP = getHamachiIPAddress();
            clientSocket = new Socket(InetAddress.getByName(hamachiIP), config.getPort());
            System.out.println("Connected to server at Hamachi IP " + config.getIP() + ":" + config.getPort());

            out = new PrintWriter(clientSocket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

            new Thread(this::readMessages).start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void handleClient(Socket socket) {
        try {
            BufferedReader clientIn = clientReaders.get(socket);
            String inputLine;
            while ((inputLine = clientIn.readLine()) != null) {
            	
                handleReceivedData(inputLine);
            	

                // Broadcast this message to all connected clients, including the host
                for (Map.Entry<Socket, PrintWriter> entry : clientWriters.entrySet()) {
                    Socket s = entry.getKey();
                    PrintWriter writer = entry.getValue();

                    // Avoid broadcasting to the sender client
                    if (s != socket) {
                        writer.println(inputLine);
                    }
                }
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

    /**
     * Retrieves the Hamachi IP address.
     */
    private String getHamachiIPAddress() {
        try {
            Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();
            
            while (networkInterfaces.hasMoreElements()) {
                NetworkInterface networkInterface = networkInterfaces.nextElement();
                String displayName = networkInterface.getDisplayName();
                
                // Print out the display names for debugging purposes
                System.out.println("Network Interface: " + displayName);
                
                if (displayName.toLowerCase().contains("hamachi")) {
                    Enumeration<InetAddress> inetAddresses = networkInterface.getInetAddresses();
                    
                    while (inetAddresses.hasMoreElements()) {
                        InetAddress inetAddress = inetAddresses.nextElement();
                        
                        // Check if it's an IPv4 address
                        if (inetAddress instanceof Inet4Address) {
                            return inetAddress.getHostAddress(); // Hamachi IP address
                        }
                    }
                }
            }
            
        } catch (SocketException e) {
            e.printStackTrace();
            // Log or handle the error appropriately
        }
        
        // Could not find Hamachi IP address
        System.err.println("Hamachi IP address not found.");
        return null;
    }

    // Register a player and send the username as well
    public void registerPlayer(Player player) {
        if (player == null) {
            System.err.println("Cannot register null player.");
            return;
        }

        String playerId = isServer ? nameServer : nameClient;
        String username = player.getUsername(); // Retrieve the username from the player object
        int level = player.getLevel();
        String message = String.format("PLAYER_REGISTER %s %s %d %d %s %d %d", 
                                        playerId, 
                                        username,  // Include username in the registration message
                                        player.getWorldX(), 
                                        player.getWorldY(), 
                                        player.getDirection(), 
                                        player.getSpriteNum(), 
                                        level);

        // Update the playerData object with the username
        PlayerData playerData = new PlayerData(playerId, username, player.getWorldX(), player.getWorldY(), player.getDirection(), player.getSpriteNum(), System.currentTimeMillis(), level);
        otherPlayers.put(playerId, playerData);
        gamePanel.updateOtherPlayer(playerId, playerData);

        // Broadcast the registration message to other clients
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
        String username = player.getUsername();
        int x = player.getWorldX();
        int y = player.getWorldY();
        String direction = player.getDirection();
        int spriteNum = player.getSpriteNum();
        long timestamp = System.currentTimeMillis();
        int level = player.getLevel();

        String message = String.format(
            "PLAYER_UPDATE %s %s %d %d %s %d %d %d",
            playerId, username, x, y, direction, spriteNum, timestamp, level
        );

        System.out.println("Sending player update: " + message);

        PlayerData playerData = new PlayerData(playerId, username, x, y, direction, spriteNum, timestamp, level);
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
    
    
    
    public void sendChatMessage(String message) {
    	String username = gamePanel.getPlayer().getUsername();
    
    	String formattedMessage = "CHAT " + username + " " + message;
        if (isServer) {
        	
        
            for (PrintWriter writer : clientWriters.values()) {
                writer.println(formattedMessage);
            }
        } else {
            out.println(formattedMessage);
        }
        
        
    }


    public void receiveMessage(String message) {
        gamePanel.ui.appendGlobalChatMessage(message);
    }

    private void handleReceivedData(String data) {
    	String[] tokens = data.split(" ");
    	String username;
    	 String payload = null;
    	 String senderUsername = tokens[1];
    	
       
    	
        
     //CHECK IF THERE ARE AT LEAST 3 TOKENS
        if (tokens.length > 2) {
            // Concatenate all tokens from index 2 onwards to form the payload
            StringBuilder payloadBuilder = new StringBuilder();
            for (int i = 2; i < tokens.length; i++) {
                if (i > 2) {
                    payloadBuilder.append(" "); // Add space between tokens
                }
                payloadBuilder.append(tokens[i]);
            }
            payload = payloadBuilder.toString();
        } else {
            // Handle the case where tokens[2] does not exist
            payload = ""; // Set to empty if no message is present
        }
           
        // Ensure the message is either an update or registration event
        if (tokens[0].equals("PLAYER_UPDATE") || tokens[0].equals("PLAYER_REGISTER")) {
            try {
                String playerId = tokens[1];
                username = tokens[2];  // Retrieve the username from the data
                int x = Integer.parseInt(tokens[3]);
                int y = Integer.parseInt(tokens[4]);
                String direction = tokens[5];
                int spriteNum = Integer.parseInt(tokens[6]);
                
                // Use timestamp if it's an update, otherwise set it to the current time
                long timestamp = (tokens[0].equals("PLAYER_UPDATE")) ? Long.parseLong(tokens[7]) : System.currentTimeMillis();
                int level = Integer.parseInt(tokens[tokens.length - 1]);

                // Create PlayerData with the username and other data
                PlayerData playerData = new PlayerData(playerId, username, x, y, direction, spriteNum, timestamp, level);
                
                // Update the otherPlayers map with the new or updated PlayerData
                otherPlayers.put(playerId, playerData);
                
                // Update the game panel with the new or updated player
                gamePanel.updateOtherPlayer(playerId, playerData);
                
            } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
                e.printStackTrace();
            }
        }
        
        else if (tokens[0].equals("PLAYER_UPDATE") && !tokens[0].equals("CHAT")) {
            // Handle chat message
            receiveMessage(payload);
        } else {
            System.err.println("Unknown command received: " + tokens[0]);
        }
        
        if (tokens[0].equals("CHAT") && !payload.isBlank()) {
            if (!senderUsername.equals(gamePanel.getPlayer().getUsername())) {
                // Add the message to the chat, showing the sender's username
                receiveMessage(senderUsername + ": " + payload);
            }
        } else {
            
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
