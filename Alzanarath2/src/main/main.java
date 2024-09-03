package main;

import javax.swing.JFrame;
import Networking.Configuration;
import Networking.NetworkManager;

public class main {
	private static JFrame window;

	public static void main(String[] args) {
		GamePanel gp = new GamePanel();
		
		
		
		
		window = new JFrame();
		
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
