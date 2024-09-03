package main;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import javax.swing.JPanel;

import Networking.Configuration;
import Networking.NetworkManager;
import Entity.Entity;
import Entity.NpcOldMan;
import Entity.Player;
import Inputs.KeyHandler;
import Tile.TileManager;
import UI.UI;

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
    KeyHandler keyH = new KeyHandler(this);

    // PLAYER ENTITY INSTANTIATION
    Player player;
    
    //NPCs entities instantiation
    private Entity npc[] = new Entity[10];
    private int currentNpcNum;

    // TILE MANAGER
    TileManager tileM = new TileManager(this);

    // Check collisions
    private ColissionChecker cChecker = new ColissionChecker(this);

    //GAME BGM AND SE
    Sound sound = new Sound();
    
    //Asset setter
    AssetSetter aSetter = new AssetSetter(this);
    //GAME STATAES
    private int gameState;
    private int titleState=1;
    private int playState=2;
    
    //Initialize the UI management class
    
    public UI ui = new UI(this);
    
   //IS SERVER or Client? NETWORK CONFIGS
    public boolean isServer = false;
    Configuration config = new Configuration(5050, "127.0.0.1");
	int stop=0;
	NetworkManager networkManager;

	public GamePanel() { // Updated constructor to accept NetworkManager
        this.setPreferredSize(new Dimension(screenWidth, screenHeight));
        this.setDoubleBuffered(true);
        this.setFocusable(true);
        this.setBackground(Color.black);
        this.addKeyListener(keyH);
        //Set the game state to title screen
        gameState=titleState;
        
        setupGame();
        
        playMusic(0);
    }
	
	public void setupGame() {
		aSetter.setObject();
		aSetter.setNpc();
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
            currentTime = System.nanoTime();
            delta += (currentTime - lastTime) / drawInterval;
            lastTime = currentTime;
           
            if(gameState==playState && stop==0) {
          networkManager = new NetworkManager(isServer, config);
          // Initialize player with networkManager
          this.player = new Player(this, keyH, networkManager);
          stop++;
            }
            if (delta >= 1) {
            	
                update();
            	
                repaint();
                delta--;
            }
        }
        System.out.println("ERROR: PROGRAM STOPPED RUNNING");
    }

    public void update() {
    	if(gameState==playState) {
    	//PLAYER
        player.update();
        //NPC
        for(int i=0; i<npc.length;i++) {
        	if(npc[i]!=null) {
        		npc[i].update();
        	}
        }
    	}
    }

    public void paintComponent(Graphics g) {
    	super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        
    	if(gameState==playState) {
        g2.setColor(getBackground());
        tileM.draw(g2);
        
        for(int i=0; i<getNpc().length;i++) {
        	if(getNpc()[i]!=null) {
        	getNpc()[i].draw(g2);
        	currentNpcNum=i;
        	}
        
        player.draw(g2);
        
        //Draw NPCS
       
    	}
        
    	}
    	
    	
    	ui.drawUI(g2);
    	
        g2.dispose();
    }
    
    public void playMusic(int i) {
    	sound.setFile(i);
    	sound.play();
    	sound.loop();
    }
    
    public void stopMusic() {
    	sound.stop();
    }
    
    public void playSE(int i) {
    	sound.setFile(i);
    	sound.play();
    	
    }
    
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

	public int getTitleState() {
		return titleState;
	}

	public void setTitleState(int titleState) {
		this.titleState = titleState;
	}

	public int getGameState() {
		return gameState;
	}

	public void setGameState(int gameState) {
		this.gameState = gameState;
	}
	
	 public int getPlayState() {
			return playState;
		}

	public void setPlayState(int playState) {
			this.playState = playState;
	}

	public int getCurrentNpcNum() {
		return currentNpcNum;
	}

	public void setCurrentNpcNum(int currentNpcNum) {
		this.currentNpcNum = currentNpcNum;
	}

	public Entity[] getNpc() {
		return npc;
	}

	public void setNpc(Entity npc[]) {
		this.npc = npc;
	}
}
