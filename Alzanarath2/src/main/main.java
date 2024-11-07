package main;

import javax.swing.JFrame;
import Networking.Configuration;
import Networking.NetworkManager;

public class main {
	public static JFrame window;
    public static void main(String[] args) {
    	
        window = new JFrame("Game");
        GamePanel gamePanel = new GamePanel();
        window.setUndecorated(true);
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setResizable(false);
        window.add(gamePanel);
        window.pack();
        window.setLocationRelativeTo(null);  // Center the window
        window.setVisible(true);
        
        gamePanel.startGameThread();  // Start the game thread
       
    }
}


