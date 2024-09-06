package main;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import javax.swing.JPanel;

import Networking.Configuration;
import Networking.NetworkManager;
import Networking.PlayerData;
import Entity.Entity;
import Entity.NpcOldMan;
import Entity.Player;
import Inputs.KeyHandler;
import Tile.TileManager;
import UI.UI;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

public class GamePanel extends JPanel implements Runnable {

    
    private final int tileSize = 48;
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
    private int FPS=60;

    // HANDLING INPUTS
    public KeyHandler keyH;

    // PLAYER ENTITY INSTANTIATION
    Player player;
    
    // NPC entities instantiation
    private Entity[] npc = new Entity[10];
    private int currentNpcNum;
    
    //Monster entities instantiation
    private Entity[] monster = new Entity[20];
    private int currentMonsterNum;
    
    //ArrayListFor storing every non player entity array
    private ArrayList<Entity> entityList = new ArrayList<>();
    
    
    
    public ArrayList<Entity> getEntityList() {
		return entityList;
	}


	public void setEntityList(ArrayList<Entity> entityList) {
		this.entityList = entityList;
	}


	// TILE MANAGER
    private TileManager tileM;

    // Check collisions
    private ColissionChecker cChecker;

    // GAME BGM AND SE
    Sound sound;
    int joinSound=0;
    
    // Asset setter
    AssetSetter aSetter;

    // GAME STATES
    private int gameState;
    private int titleState = 1;
    private int playState = 2;
    
    // Initialize the UI management class
    public UI ui;
    
    // IS SERVER or Client? NETWORK CONFIGS
    public boolean isServer = false;
    Configuration config = new Configuration(5050, "25.14.141.164");
    int stop = 0;
    NetworkManager networkManager;
    
    // Store other players' data
    private Map<String, Player> otherPlayers = new HashMap<>();

    public GamePanel() {
        System.out.println("Initializing GamePanel...");
        this.setPreferredSize(new Dimension(screenWidth, screenHeight));
        this.setDoubleBuffered(true);
        this.setFocusable(true);

        // Initialize components
        keyH = new KeyHandler(this);  // Initialize KeyHandler
        this.addKeyListener(keyH);    // Add KeyHandler as KeyListener
        System.out.println("KeyHandler initialized: " + (keyH != null)); // Debugging line
        setTileM(new TileManager(this));
        sound = new Sound();
        aSetter = new AssetSetter(this);
        ui = new UI(this);
        cChecker = new ColissionChecker(this);
        

        this.setBackground(Color.black);
        

        gameState = titleState;
        setupGame();
        playMusic(0);
    }


    public void setupGame() {
        aSetter.setObject();
        aSetter.setNpc();
        aSetter.setMonster();
    }

    public void startGameThread() {
        gameThread = new Thread(this);
        gameThread.start();
    }
    
   
    

    public void initializeGame() {
        if (keyH == null) {
            System.err.println("KeyHandler is null! Initialization might be missing.");
            return;
        }
        networkManager = new NetworkManager(isServer, config, this,keyH);
        this.player = new Player(this, keyH, networkManager);
        
        // Register the player with the network manager
        networkManager.registerPlayer(this.player);

        System.out.println("Game initialized. Player: " + (player != null ? "Initialized" : "Not Initialized"));
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

            if (delta >= 1) {
                update();
                repaint();
                delta--;
            }
        }
        System.out.println("ERROR: PROGRAM STOPPED RUNNING");
    }

    public void update() {
        if (keyH == null) {
            System.err.println("KeyHandler is not initialized.");
            return;
        }
        
        for (int i=0; i<npc.length; i++) {
            if (npc[i]!=null) {
                npc[i].update();
            }
        }
        
        for(int i=0; i<monster.length; i++) {
        	if(monster[i] != null) {
        		monster[i].update();
        	}
        }

        synchronized (keyH) {
            // Update local player and other entities
            if (player != null) {
                player.update();
            }

            for (Player otherPlayer : otherPlayers.values()) {
                if (otherPlayer != null) {
                    otherPlayer.update();
                }
            }

            
        }
        
       
    }


    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;

        if (gameState == playState && this.player!=null) {
            g2.setColor(getBackground());
            getTileM().draw(g2);
            
            entityList.add(player);
            
            for (int i=0; i<npc.length; i++) {
            	if(npc[i]!=null) {
            entityList.add(npc[i]);
            	}
            }
            
            for (int i=0; i<monster.length; i++) {
            	if(monster[i]!=null) {
            entityList.add(monster[i]);
            	}
            }
            
            //SORT THE ARRAYLIST
            Collections.sort(entityList, new Comparator<Entity>() {

				@Override
				public int compare(Entity e1, Entity e2) {
					int result = Integer.compare(e1.getWorldY(),e2.getWorldY());
					return result;
				}
            });
        
            	
            //DRAW THE ENTITIES
            
            for (int i=0; i<entityList.size();i++) {
            	entityList.get(i).draw(g2);
            }
            
            //Empty ENTITY LIST
            
            entityList.clear();
            }

           
            
             

            // Draw other players
            for (Player otherPlayer : otherPlayers.values()) {
                if (otherPlayer != null) {
                   entityList.add(otherPlayer);
                    
                    if(joinSound==0) {
                    joinSound++;
                    playSE(1);
                    }
                    
                }
            }
          

        // Draw the UI
        ui.drawUI(g2);
        g2.dispose();
    }

    public void updateOtherPlayer(String playerId, PlayerData playerData) {
        if (!otherPlayers.containsKey(playerId)) {
            // Create a new Player instance if the player doesn't exist in the map
            Player newPlayer = new Player(this, null, networkManager);
            newPlayer.setUsername(playerData.getUsername()); // Set the username
            newPlayer.setWorldX(playerData.getX());
            newPlayer.setWorldY(playerData.getY());
            newPlayer.setDirection(playerData.getDirection());
            newPlayer.setSpriteNum(playerData.getSpriteNum()); // Set animation state for new player
            newPlayer.setLevel(playerData.getLevel()); // Set the player's level
            newPlayer.setIsAttacking(playerData.isAttacking()); // Set if its attacking

            otherPlayers.put(playerId, newPlayer);
        } else {
            // Update the existing player
            Player existingPlayer = otherPlayers.get(playerId);
            
            // Correct position if not attacking, otherwise maintain attack position
            if (!playerData.isAttacking()) {
                existingPlayer.correctPosition(playerData.getX(), playerData.getY(), playerData.getDirection());
            }

            existingPlayer.setSpriteNum(playerData.getSpriteNum()); // Update animation state
            existingPlayer.setLevel(playerData.getLevel()); // Update the player's level
            existingPlayer.setIsAttacking(playerData.isAttacking()); // Set if it's attacking

            // Apply additional logic for when the player is attacking
            if (playerData.isAttacking()) {
                handleAttackMovement(existingPlayer, playerData);
            }
        }
        
        repaint(); // Ensure the game panel is repainted to reflect the updates
    }

    private void handleAttackMovement(Player player, PlayerData playerData) {
        int tileSize = this.getTileSize(); // Assuming tileSize is 48 as mentioned

        // Adjust position during attack based on direction
        switch (playerData.getDirection()) {
            case "left":
                
                break;
            case "up":
                
                break;
            // Add other directions as necessary (right, down, etc.)
            default:
                break;
        }
    }

    public NetworkManager getNetworkManager() {
		return networkManager;
	}


	public void setNetworkManager(NetworkManager networkManager) {
		this.networkManager = networkManager;
	}


	// Ensure repaint happens when player updates
    public void refreshPlayers() {
        repaint(); // Trigger redraw
    }


    
    

    public Map<String, Player> getOtherPlayers() {
        return otherPlayers;
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

   

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
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

   

    public void setNpc(Entity npc[]) {
        this.npc = npc;
    }


	public Entity[] getNpc() {
		// TODO Auto-generated method stub
		return npc;
	}


	public Entity[] getMonster() {
		return monster;
	}


	public void setMonster(Entity[] monster) {
		this.monster = monster;
	}


	public int getCurrentMonsterNum() {
		return currentMonsterNum;
	}


	public void setCurrentMonsterNum(int currentMonsterNum) {
		this.currentMonsterNum = currentMonsterNum;
	}


	public TileManager getTileM() {
		return tileM;
	}


	public void setTileM(TileManager tileM) {
		this.tileM = tileM;
	}
}

