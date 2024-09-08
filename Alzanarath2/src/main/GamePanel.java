package main;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import javax.swing.JPanel;

import Networking.Configuration;
import Networking.MonsterData;
import Networking.NetworkManager;
import Networking.PlayerData;
import Entity.Entity;
import Entity.NpcOldMan;
import Entity.Player;
import Inputs.KeyHandler;
import Monster.MON_Slime;
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

    //HANDLE SERVER-CLIENT MONSTER DEATHS
    private boolean stopUpdatingMonsters=false;
    private boolean stopUpdatingMonstersOnDeath=false;
    
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
    public Entity[] monster = new Entity[20];
    


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
    private final int titleState = 1;
    private final int playState = 2;
    private final int characterState = 3;
    private final int serverState=4;
    
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
        
        ui = new UI(this);
        cChecker = new ColissionChecker(this);
        
      //SETUP THE MONSTER ENTITIES ETC ENTITIES
        
        
       
        
       

        this.setBackground(Color.black);
        

        gameState = titleState;
        
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
    
   public void initializeServer() {
	   if (keyH == null) {
           System.err.println("KeyHandler is null! Initialization might be missing.");
           return;
       }
      
       networkManager = new NetworkManager(isServer, config, this, this.keyH);
       
       this.player = new Player(this, keyH, networkManager);
       
       aSetter = new AssetSetter(this, networkManager);
       setupGame();
       

       System.out.println("Game initialized. Player: " + (player != null ? "Initialized" : "Not Initialized"));
       
   }
   
    

   public void initializeGame() {
	    if (keyH == null) {
	        System.err.println("KeyHandler is null! Initialization might be missing.");
	        return;
	    }

	    networkManager = new NetworkManager(isServer, config, this, this.keyH);

	    this.player = new Player(this, keyH, networkManager);

	    // Start the client connection
	    networkManager.startClient();
	    
	    // Register the player with the server once the client is started
	    new Thread(() -> {
	        try {
	            Thread.sleep(1000); // Ensure connection is established
	            networkManager.registerPlayer(this.player);
	        } catch (InterruptedException e) {
	            e.printStackTrace();
	        }
	    }).start();

	    aSetter = new AssetSetter(this, networkManager);
	    setupGame();

	    System.out.println("Game initialized. Player: " + (player != null ? "Initialized" : "Not Initialized"));
	}

    long lastMonsterUpdateTime = 0;
    double updateInterval = 1000000000 / 60.00; // Update every 100 ms // Update every 180 frames (assuming FPS = 60)

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
            
           
            }

            if (delta >= 1) {
                update();  // Game update logic
                repaint(); // Repaint after all updates
                delta--;

                // Perform monster updates at the specified interval
                long currentUpdateTime = System.nanoTime();
                if (currentUpdateTime - lastMonsterUpdateTime >= updateInterval) {
                    // Check if this instance is the server and networkManager is initialized
                    if (isServer && networkManager != null) {
                        // Loop through the monsters and send data for each active one
                        for (int i = 0; i < monster.length; i++) {
                            Entity currentMonster = monster[i]; // Correctly reference each monster

                            if (currentMonster != null) {
                                // Send monster data to all clients
                            	 networkManager.sendMonsterDataToAllClients(currentMonster.getMonsterId(), currentMonster);
                            }
                        }
                    }
                    // Update the last monster update time
                    lastMonsterUpdateTime = currentUpdateTime;
            }
        }
        System.out.println("ERROR: PROGRAM STOPPED RUNNING");
    }


    public void update() {
    	
        if (keyH == null) {
            System.err.println("KeyHandler is not initialized.");
            return;
        }
        
        

        // Update NPCs
        for (int i = 0; i < npc.length; i++) {
            if (npc[i] != null) {
                npc[i].update();
            }
        }
        
        // Update Monsters
        for (int i = 0; i < monster.length; i++) {
            if (monster[i] != null && networkManager!=null && networkManager.isServer()) {
                monster[i].update();
                
            }
            else if(monster[i] != null && networkManager!=null){
            	monster[i].updateSprite();
               
            }
           
        }

                // Send monster data only when necessary
             // Check if monster state has changed
               
        
    
        


        // Update player and other players
        synchronized (keyH) {
            if (player != null) {
                player.update();
            }

            for (Player otherPlayer : otherPlayers.values()) {
                if (otherPlayer != null && !networkManager.isServer()) {
                    otherPlayer.update();
                }
            }
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;

        if (this.player != null ) {
            g2.setColor(getBackground());

            // Add all entities to the entityList
            if(!networkManager.isServer()) {
            entityList.add(player);
            getTileM().draw(g2);
            }

            for (int i = 0; i < npc.length; i++) {
                if (npc[i] != null) {
                    entityList.add(npc[i]);
                }
            }

            for (int i = 0; i < monster.length; i++) {
                if (monster[i] != null ) {
                    entityList.add(monster[i]);
                    
                }
            }

          //SORT
			Collections.sort(entityList, new Comparator<Entity>() {
			
			@Override
			public int compare(Entity e1,Entity e2 ) {
				int result = Integer.compare(e1.getWorldY(), e2.getWorldY());
				return result;
			}
			});

            // Draw entities
            for (int i=0; i<entityList.size();i++) {
            	if(!networkManager.isServer()) {
                entityList.get(i).draw(g2);
            	}
            }
            
            

            // Clear entityList after drawing
            entityList.clear();
        }

        // Draw other players
        for (Player otherPlayer : otherPlayers.values()) {
            if (otherPlayer != null) {
                otherPlayer.draw(g2);
                
            }
        }

        // Draw the UI
        ui.drawUI(g2);

        g2.dispose();
    }
    
    

    public void updateOtherPlayer(String playerId, PlayerData playerData) {
        if (!otherPlayers.containsKey(playerId)) {
            // Create new player if it doesn't exist
            Player newPlayer = new Player(this, null, networkManager);
            newPlayer.setUsername(playerData.getUsername());
            newPlayer.setWorldX(playerData.getX());
            newPlayer.setWorldY(playerData.getY());
            newPlayer.setDirection(playerData.getDirection());
            newPlayer.setSpriteNum(playerData.getSpriteNum());
            newPlayer.setLevel(playerData.getLevel());
            newPlayer.setIsAttacking(playerData.isAttacking());
            newPlayer.setInvincibleCounter(playerData.getInvincibleCounter());

            otherPlayers.put(playerId, newPlayer);
        } else {
            // Update existing player
            Player existingPlayer = otherPlayers.get(playerId);
            if (!playerData.isAttacking()) {
                existingPlayer.correctPosition(playerData.getX(), playerData.getY(), playerData.getDirection());
            }
            existingPlayer.setSpriteNum(playerData.getSpriteNum());
            existingPlayer.setLevel(playerData.getLevel());
            existingPlayer.setIsAttacking(playerData.isAttacking());
            existingPlayer.setInvincibleCounter(playerData.getInvincibleCounter());

            // Handle attack movement
            if (playerData.isAttacking()) {
                handleAttackMovement(existingPlayer, playerData);
            }
        }

        repaint();
    }

    public void updateMonster(String monsterId, MonsterData monsterData) {
        boolean monsterExists = false;
        int indexToUpdate = -1;
        
        // Check if the monster already exists
        for (int i = 0; i < monster.length; i++) {
            if (monster[i] != null && monster[i].getMonsterId().equals(monsterId)) {
                indexToUpdate = i;
                monsterExists = true;
                break;
            }
        }

        if (monsterExists) {
            // Update the existing monster
            Entity existingMonster = monster[indexToUpdate];
            existingMonster.setWorldX(monsterData.getWorldX());
            existingMonster.setWorldY(monsterData.getWorldY());
            existingMonster.setDirection(monsterData.getDirection());
            existingMonster.setSpeed(monsterData.getSpeed());
            existingMonster.setHealth(monsterData.getHealth());
            existingMonster.setMaxHealth(monsterData.getMaxHealth());
            existingMonster.setAttack(monsterData.getAttack());
            existingMonster.setSpriteNum(monsterData.getSpriteNum());
            
            // Notify all clients of the updated monster
            networkManager.sendMonsterData(monsterId, existingMonster, true);
        } else {
            // Create a new monster if it doesn't exist
            for (int i = 0; i < monster.length; i++) {
                if (monster[i] == null) {
                    Entity newMonster = new MON_Slime(this);
                    newMonster.setMonsterId(monsterId); // Set the ID for the new monster
                    newMonster.setName(monsterData.getName());
                    newMonster.setWorldX(monsterData.getWorldX());
                    newMonster.setWorldY(monsterData.getWorldY());
                    newMonster.setDirection(monsterData.getDirection());
                    newMonster.setSpriteNum(monsterData.getSpriteNum());
                    newMonster.setSpeed(monsterData.getSpeed());
                    newMonster.setHealth(monsterData.getHealth());
                    newMonster.setMaxHealth(monsterData.getMaxHealth());
                    newMonster.setAttack(monsterData.getAttack());
                    monster[i] = newMonster; // Assign the new monster to the slot
                    entityList.add(newMonster);
                    
                    // Notify all clients of the new monster
                    networkManager.sendMonsterData(monsterId, newMonster, false);
                    break;
                }
            }
        }

        repaint(); // Repaint after the update
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

   

    public int getGameState() {
        return gameState;
    }

    public void setGameState(int gameState) {
        this.gameState = gameState;
    }

    public int getPlayState() {
        return playState;
    }

    

    public int getCurrentNpcNum() {
        return currentNpcNum;
    }

    public void setCurrentNpcNum(int currentNpcNum) {
        this.currentNpcNum = currentNpcNum;
    }

    @SuppressWarnings("unused")
	public void setMonster(Entity monster) {
		this.monster[getCurrentMonsterNum()] = monster;
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


	public int getCharacterState() {
		return characterState;
	}


	public boolean isStopUpdatingMonstersOnDeath() {
		return stopUpdatingMonstersOnDeath;
	}


	public void setStopUpdatingMonstersOnDeath(boolean stopUpdatingMonstersOnDeath) {
		this.stopUpdatingMonstersOnDeath = stopUpdatingMonstersOnDeath;
	}


	public int getServerState() {
		return serverState;
	}
	
	 
}

