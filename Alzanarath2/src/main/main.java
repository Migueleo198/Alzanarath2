package main;

import javax.swing.JFrame;
import Networking.Configuration;
import Networking.NetworkManager;

public class main {
	private static JFrame window;

	public static void main(String[] args) {
		Configuration config = new Configuration(5050, "127.0.0.1");
		boolean isServer = false;
		NetworkManager networkManager = new NetworkManager(isServer, config);
		
		window = new JFrame();
		GamePanel gp = new GamePanel(networkManager);
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		window.setResizable(false);
		window.setTitle("Alzanarath 2");

		window.setLocationRelativeTo(null);
		window.add(gp);

		window.setVisible(true);
		gp.startGameThread();
		window.pack();

	}

}
