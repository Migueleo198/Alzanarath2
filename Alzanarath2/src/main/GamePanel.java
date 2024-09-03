package main;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import Entity.Player;
import Inputs.KeyHandler;
import Tile.TileManager;

public class GamePanel extends JPanel implements Runnable {

	// SCREEN SETTINGS
	private final int originalTileSize = 16; // 16x16 tile
	private int scale = 3;
	private final int tileSize = originalTileSize * scale;
	private final int maxScreenCol = 16;
	private final int maxScreenRow = 12;
	private final int screenWidth = tileSize * maxScreenCol; // 768 pixels
	private final int screenHeight = tileSize * maxScreenRow; // 576 pixels

	// WORLD SETTINGS
	private final int maxWorldCol = 36;
	private final int maxWorldRow = 32;
	private final int worldWidth = tileSize * maxWorldCol;
	private final int worldHeight = tileSize * maxWorldRow;

	// GAME THREAD
	Thread gameThread;

	// GAME FPS
	int FPS = 60;

	// HANDLING INPUTS
	KeyHandler keyH = new KeyHandler();

	// PLAYER ENTITY INSTANTIATION

	Player player = new Player(this, keyH);

	// TILE MANAGER
	TileManager tileM = new TileManager(this);

	// Check collisions
	private ColissionChecker cChecker = new ColissionChecker(this);

	public int getScale() {
		return scale;
	}

	public void setScale(int scale) {
		this.scale = scale;
	}

	public Player getPlayer() {
		return player;
	}

	public void setPlayer(Player player) {
		this.player = player;
	}

	public int getOriginalTileSize() {
		return originalTileSize;
	}

	public int getTileSize() {
		return tileSize;
	}

	public int getMaxScreenCol() {
		return maxScreenCol;
	}

	public int getMaxScreenRow() {
		return maxScreenRow;
	}

	public int getScreenWidth() {
		return screenWidth;
	}

	public int getScreenHeight() {
		return screenHeight;
	}

	public GamePanel() {
		this.setPreferredSize(new Dimension(screenWidth, screenHeight));
		this.setDoubleBuffered(true);
		this.setFocusable(true);
		this.setBackground(Color.black);
		this.addKeyListener(keyH);
	}

	public void startGameThread() {
		gameThread = new Thread(this);
		gameThread.start();
	}

	@Override
	public void run() {

		double drawInterval = 1000000000 / FPS;
		double delta = 0;
		long lastTime = System.nanoTime();
		long currentTime;

		while (gameThread != null) {

			// UPDATE AND REPAINT FPS RESTRICTION BASED ON TIME
			currentTime = System.nanoTime();

			// FUNCTIONS OF THIS METHOD

			delta += (currentTime - lastTime) / drawInterval;

			lastTime = currentTime;

			if (delta >= 1) {
				// 1 - UPDATE INFORMATION SUCH AS CHARACTER POSITION
				update();
				// 2 - REDRAW THE SCREEN IMAGE WITH THE UPDATED INFORMATION
				repaint();

				delta--;
			}

		}
		System.out.println("ERROR: PROGRAM STOPPED RUNNING");

	}

	public void update() {

		// Updates player params and positions
		player.update();
	}

	public void paintComponent(Graphics g) {
		super.paintComponent(g);

		Graphics2D g2 = (Graphics2D) g;

		g2.setColor(getBackground());

		// It goes for layers based on order; first the tile then the player so it
		// doesnt overlap
		tileM.draw(g2);

		player.draw(g2);

		g2.dispose();
	}

	public int getMaxWorldCol() {
		return maxWorldCol;
	}

	public int getMaxWorldRow() {
		return maxWorldRow;
	}

	public int getWorldWidth() {
		return worldWidth;
	}

	public int getWorldHeight() {
		return worldHeight;
	}

	public ColissionChecker getcChecker() {
		return cChecker;
	}

	public void setcChecker(ColissionChecker cChecker) {
		this.cChecker = cChecker;
	}
}
