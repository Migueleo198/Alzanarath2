package Networking;

import java.net.*;
import javax.swing.JOptionPane;
import java.io.*;

public class NetworkManager {
	private ServerSocket serverSocket;
	private Socket clientSocket;
	private PrintWriter out;
	private BufferedReader in;
    private String nameServer;
    private String nameClient;
	private boolean isServer;
	private Configuration config;

	public NetworkManager(boolean isServer, Configuration config) {
		this.isServer = isServer;
		this.config = config;
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
			System.out.println("The server has started on port " + config.getPort() + " !");

			new Thread(() -> {
				while (true) {
					try {
						clientSocket = serverSocket.accept();
						System.out.println(nameClient + " has joined in the game!");
//						handleClient(clientSocket);
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
			System.out.println("You joined at " + nameServer + " . Have fun playing!");
			
			out = new PrintWriter(clientSocket.getOutputStream(), true);
	        in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
	        
//	        new Thread(this::readMessage).start();
		}  catch(IOException e) {
			e.printStackTrace();
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
	        } else if(name.length() > 8){
	        		JOptionPane.showMessageDialog(null, "Username cannot have more than 8 character, please input less.", "Invalid lenght", JOptionPane.ERROR_MESSAGE);
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
    
    public PrintWriter getOutput() {
        return out;
    }

    public BufferedReader getInput() {
        return in;
    }
}
