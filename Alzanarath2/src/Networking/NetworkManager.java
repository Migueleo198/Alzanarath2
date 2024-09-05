package Networking;
import java.io.*;
import java.net.*;
import java.util.concurrent.*;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.Map;
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
    
    // Thread pool to handle client connections
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
            serverSocket.setPerformancePreferences(1,0,2);
            
            Player hostPlayer = gamePanel.getPlayer();
            if (hostPlayer != null) {
                registerPlayer(hostPlayer); // Register the host
            }

            // Use thread pool to accept and handle client connections
            clientExecutor.submit(() -> {
                while (true) {
                    try {
                        Socket clientSocket = serverSocket.accept();
                        System.out.println("New client joined!");

                        BufferedWriter clientOut = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));
                        BufferedReader clientIn = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

                        clientWriters.put(clientSocket, clientOut);
                        clientReaders.put(clientSocket, clientIn);

                        // Send current state of all players to the new client
                        sendAllPlayersToClient(clientOut);

                        // Handle client communication in a separate thread
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
            clientSocket.setPerformancePreferences(1,0,2);
            clientSocket.setTcpNoDelay(true);
            out = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

            // Handle incoming messages in a separate thread
            clientExecutor.submit(this::readMessages);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void sendAllPlayersToClient(BufferedWriter clientOut) throws IOException {
        for (PlayerData playerData : otherPlayers.values()) {
            String message = String.format("PLAYER_REGISTER %s %s %d %d %s %d %d", 
                                            playerData.getPlayerId(),  
                                            playerData.getUsername(), 
                                            playerData.getX(), 
                                            playerData.getY(), 
                                            playerData.getDirection(), 
                                            playerData.getSpriteNum(),
                                            playerData.getLevel());
            clientOut.write(message + "\n");
            clientOut.flush();
        }
    }

    public void handleClient(Socket socket) {
        try (BufferedReader clientIn = clientReaders.get(socket)) {
            String inputLine;
            while ((inputLine = clientIn.readLine()) != null) {
                handleReceivedData(inputLine);

                // Broadcast to all clients except sender
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
        String message = String.format("PLAYER_REGISTER %s %s %d %d %s %d %d",
                                        playerId, username, 
                                        player.getWorldX(), player.getWorldY(), 
                                        player.getDirection(), player.getSpriteNum(), level);

        PlayerData playerData = new PlayerData(playerId, username, player.getWorldX(), player.getWorldY(), player.getDirection(), player.getSpriteNum(), System.currentTimeMillis(), level);
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
        String message = String.format(
            "PLAYER_UPDATE %s %s %d %d %s %d %d %d",
            playerId, username, player.getWorldX(), player.getWorldY(), player.getDirection(),
            player.getSpriteNum(), System.currentTimeMillis(), player.getLevel()
        );

        PlayerData playerData = new PlayerData(playerId, username, player.getWorldX(), player.getWorldY(), player.getDirection(), player.getSpriteNum(), System.currentTimeMillis(), player.getLevel());
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

