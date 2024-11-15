package main;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Toolkit;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;

import javax.swing.JFrame;
import javax.swing.JPanel;

import Database.DBConnection;
import Networking.Configuration;
import Networking.MonsterData;
import Networking.NetworkManager;
import Networking.PlayerData;
import Entity.Entity;
import Entity.NpcOldMan;
import Entity.Player;
import Inputs.KeyHandler;
import Monster.MON_Slime;
import Tile.MiniMap;
import Tile.TileManager;
import UI.UI;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GamePanel extends JPanel implements Runnable {

    private final int originalTileSize=16;
    private int scale=3;
    private int tileSize = 48;
    private final int maxScreenCol = 21;
    private final int maxScreenRow = 12;
    private int screenWidth = tileSize * maxScreenCol; // 768 pixels
    private int screenHeight = tileSize * maxScreenRow; // 576 pixels
    
    private int screenWidth2=screenWidth;  // 768 pixels
    private int screenHeight2=screenHeight; // 576 pixels

    // WORLD SETTINGS
    private  int maxWorldCol;
    private  int maxWorldRow;
    

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
    
    //MONSTER RESPAWN
    private static final long RESPAWN_DELAY_MS = 20000; // 20 seconds in milliseconds
    private List<Entity> removedMonsters =  new ArrayList<Entity>(); // Tracks removed monsters and their removal times
    public boolean respawn = false;

	private int currentMonsterNum;
    
    //ArrayListFor storing every non player entity array
    private ArrayList<Entity> entityList = new ArrayList<>();
    
   
    //MINIMAP AND MAP
    MiniMap map = new MiniMap(this);
    
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
 	private final int serverState = 4;
 	private final int loginState = 5;
 	private final int registerState = 6;
 	private final int screenState = 7;
 	private final int skillTreeState = 8;
 	private final int mapState = 9;
    
    // Initialize the UI management class
    public UI ui;
    
    // IS SERVER or Client? NETWORK CONFIGS
    public boolean isServer = false;
    Configuration config = new Configuration(5050, "25.14.141.164");
    int stop = 0;
    NetworkManager networkManager;
    
    // Store other players' data
    private Map<String, Player> otherPlayers = new HashMap<>();
    
    //SCREEN SETTINGS
    private BufferStrategy bufferStrategy;
   private Graphics2D g2;
   private BufferedImage tempScreen;
   
   //CONNECTION TO DATABASE
   public DBConnection connection = new DBConnection();
   public int monsterCounter;
   
   //OBJECTS
   public Entity[] Objects = new Entity[99];
   
   //CURRENT MAP VARIABLES
   
   public final int maxMap = 20;
   public int currentMap = 0;
   
   
   // Constructor
   public GamePanel() {
       System.out.println("Initializing GamePanel...");
       this.setPreferredSize(new Dimension(screenWidth, screenHeight));
       this.setDoubleBuffered(false);  // Set to false for manual double buffering
       this.setFocusable(true);

       gameState = screenState;
       
       //Initialize connection with the database
       getConnection().initializeConnection();
       
       keyH = new KeyHandler(this);
       this.addKeyListener(keyH);
       System.out.println("KeyHandler initialized: " + (keyH != null));
       
       init();
       
     
   }
   
  
   
   private void init() {
	   tempScreen = new BufferedImage(screenWidth, screenHeight, BufferedImage.TYPE_INT_ARGB);
       setG2((Graphics2D)tempScreen.getGraphics());
      
       ui = new UI(this);
       setFullScreenDimensions();
       this.setPreferredSize(new Dimension(screenWidth2,screenHeight2));
   }

   
    
    private void setFullScreenDimensions() {
    	Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		double width = screenSize.getWidth();
		double height = screenSize.getHeight();
		main.window.setExtendedState(JFrame.MAXIMIZED_BOTH);
		screenWidth2 = (int) width;
		screenHeight2 = (int) height;
		
		
		
       
    }
    

    public void setupGame() {
    	
        sound = new Sound();
       
        
        aSetter = new AssetSetter(this, networkManager);
       
        this.setBackground(Color.black);
        
        cChecker = new ColissionChecker(this);
        
        
        setTileM(new TileManager(this));
        
        setFullScreenDimensions();
       
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
	    
	    aSetter = new AssetSetter(this, networkManager);
	    
	    
	    
	    aSetter.setObject();

	    
	    
	   //UNLOCKS ALL THE PREVIOUSLY UNLOCKED SKILLS ON LOGIN
	    
	    
	    
	    
	    // Start the client connection
	    networkManager.startClient();
	    playMusic(0);
	    // Register the player with the server once the client is started
	  
	            if(player!=null && networkManager!=null) {
	            networkManager.registerPlayer(this.player);
	            }
	       

	   
	    
	    getConnection().initializeConnection();
	    

	    System.out.println("Game initialized. Player: " + (player != null ? "Initialized" : "Not Initialized"));
	    map.miniMapOn = true;
	  
	}

    long lastMonsterUpdateTime = 0;
    double updateInterval = 1000000000 / 60.00; // Update every 100 ms // Update every 180 frames (assuming FPS = 60)

    
    
    
    @Override
    public void run() {
        final double drawInterval = 1000000000 / FPS; // Interval for rendering
        final double monsterUpdateInterval = 1000000000 / 60; // Interval for monster updates
        double delta = 0;
        long lastTime = System.nanoTime();
        long currentTime;

        long lastMonsterUpdateTime = 0;

        while (gameThread != null) {
            currentTime = System.nanoTime();
            delta += (currentTime - lastTime) / drawInterval;
            lastTime = currentTime;

            if (delta >= 1) {
                update(); // Update game logic
                drawToTempScreen(); // Draw everything to the buffered image
                drawToScreen(); // Draw the buffered image to the screen
                delta--; // Reduce delta by 1 after processing the frame

                // Handle monster updates separately
                long currentUpdateTime = System.nanoTime();
                if (currentUpdateTime - lastMonsterUpdateTime >= monsterUpdateInterval) {
                    if (isServer && networkManager != null) {
                        // Send monster data to all clients
                        for (int i = 0; i < monster.length; i++) {
                            Entity currentMonster = monster[i]; // Reference each monster
                            if (currentMonster != null) {
                                networkManager.sendMonsterDataToAllClients(currentMonster.getMonsterId(), currentMonster);
                            }
                        }
                    }
                    lastMonsterUpdateTime = currentUpdateTime;
                }
            }

            // Calculate the time left to wait to maintain a consistent FPS
            long elapsedTime = System.nanoTime() - lastTime;
            long sleepTime = (long) ((drawInterval - elapsedTime) / 1000000); // Convert nanoseconds to milliseconds

            if (sleepTime > 0) {
                try {
                    Thread.sleep(sleepTime); // Sleep to maintain FPS
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
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

        // Update Monsters and Check for Respawn
        long currentTime = System.currentTimeMillis();
        for (int i = 0; i < monster.length; i++) {
            if (monster[i] != null) {
                // Update existing monsters
                if (networkManager != null && networkManager.isServer()) {
                    monster[i].update();
                } else if (networkManager != null) {
                    monster[i].updateSprite();
                }
            }
        }
            
           

            // Check if the monster should respawn
            if (removedMonsters!=null && respawn==true) {
            	
            		aSetter.respawnCounter++;
                    aSetter.respawnMonsters();
                    
            }
                   
                
            
        

        // Send monster data only when necessary
        // Check if monster state has changed
        // Implement logic here to send monster data if needed

        // Update player and other players
        synchronized (keyH) {
            if (player != null && networkManager!=null) {
            	
                player.update();
                
            }

            for (Player otherPlayer : otherPlayers.values()) {
                if (otherPlayer != null && !networkManager.isServer()) {
                    otherPlayer.update();
                }
            }
        }
    }

    
  
    
    public void drawToScreen() {
		Graphics g = getGraphics();
		g.drawImage(tempScreen, 0, 0, screenWidth2,screenHeight2,null);
		g.dispose();
	}
    
    protected void drawToTempScreen() {
    	
    	
		setG2((Graphics2D)tempScreen.getGraphics());
    	 Graphics2D  graphics = tempScreen.createGraphics();

 		graphics.setPaint ( new Color ( 0, 0, 0 ) );
 		graphics.fillRect ( 0, 0, tempScreen.getWidth(), tempScreen.getHeight());

        if (this.player != null ) {
            getG2().setColor(getBackground());

            // Add all entities to the entityList
            if(!networkManager.isServer()) {
            entityList.add(player);
            getTileM().draw(getG2());
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
            
            for (int i = 0; i < Objects.length; i++) {
                if (Objects[i] != null ) {
                    entityList.add(Objects[i]);
                    
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
                entityList.get(i).draw(getG2());
            	}
            }
            
            

            // Clear entityList after drawing
            entityList.clear();
        }

        // Draw other players
        for (Player otherPlayer : otherPlayers.values()) {
            if (otherPlayer != null) {
                otherPlayer.draw(getG2());
                
            }
        }

        // Draw the UI
        ui.drawUI(getG2());
        if(gameState==playState) {
        	map.miniMapOn=true;
        }
        else {
        	map.miniMapOn=false;
        }
        if(this.player!=null) {
        map.drawMiniMap(g2);
        }
       if(gameState==mapState) {
       map.drawFullMapScreen(g2);
       }
       
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
            newPlayer.solidArea.x=8;
            newPlayer.solidArea.y=16;
            newPlayer.solidArea.width=36;
            newPlayer.solidArea.height=36;
            newPlayer.setSolidAreaDefaultX(newPlayer.solidArea.x);
            newPlayer.setSolidAreaDefaultY(newPlayer.solidArea.y);
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


	public void removeOtherPlayer(String playerId) {
	    // Remove the player from the otherPlayers map
	    Player removedPlayer = otherPlayers.remove(playerId);
	    
	    if (removedPlayer != null) {
	        // Optionally, update the UI to remove the player's sprite or representation
	        
	    	removedPlayer=null;
	    	entityList.remove(removedPlayer);
	        // If you track player sockets or resources, remove them too
	        

	        System.out.println("Player " + playerId + " has been removed.");
	    } else {
	        System.out.println("Player " + playerId + " not found.");
	    }
	}


	public int getLoginState() {
		return loginState;
	}


	public int getRegisterState() {
		return registerState;
	}


	public int getScreenState() {
		return screenState;
	}


	public DBConnection getConnection() {
		return connection;
	}


	public void setConnection(DBConnection connection) {
		this.connection = connection;
	}


	


	public Object getaSetter() {
		// TODO Auto-generated method stub
		return aSetter;
	}


	public List<Entity> getRemovedMonsters() {
		return removedMonsters;
	}


	public void setRemovedMonsters(List<Entity> removedMonsters) {
		this.removedMonsters = removedMonsters;
	}


	public int getSkillTreeState() {
		return skillTreeState;
	}


	public Graphics2D getG2() {
		return g2;
	}


	public void setG2(Graphics2D g2) {
		this.g2 = g2;
	}


	public Entity[] getObjects() {
		return Objects;
	}


	public void setObjects(Entity[] objects) {
		Objects = objects;
	}


	public int getMapState() {
		return mapState;
	}


	public void setMaxWorldCol(int maxWorldCol) {
		this.maxWorldCol = maxWorldCol;
	}


	public void setMaxWorldRow(int length) {
		this.maxWorldRow=length;
		
	}
	
	 
}

