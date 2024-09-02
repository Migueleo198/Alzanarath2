package main;

import javax.swing.JFrame;

public class main {
	private static JFrame window;
	public static void main(String[] args) {
		window = new JFrame();
		GamePanel gp = new GamePanel();
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
