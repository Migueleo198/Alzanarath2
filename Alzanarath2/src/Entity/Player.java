package Entity;

import java.io.IOException;

import javax.imageio.ImageIO;

import Inputs.KeyHandler;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import main.GamePanel;
import javax.swing.JOptionPane;

import Networking.NetworkManager;
import Networking.PlayerData;

public class Player extends Entity {
    private GamePanel gp;
    private KeyHandler keyH;
    private NetworkManager networkManager;

    private final int screenX;
    private final int screenY;

    // Client-side prediction variables
    private int predictedX, predictedY;
    private boolean isPredicting;
    
    private PlayerData lastReceivedData; // Store the last received data

    public Player(GamePanel gp, KeyHandler keyH, NetworkManager networkManager) {
        super(gp);
        this.gp = gp;
        this.keyH = keyH;
        this.networkManager = networkManager;
       

        screenX = (gp.getScreenWidth() / 2) - gp.getTileSize() / 2;
        screenY = (gp.getScreenHeight() / 2) - gp.getTileSize() / 2;

        solidArea = new Rectangle();
        solidArea.x = 8;
        solidArea.y = 16;
        setSolidAreaDefaultX(solidArea.x);
        setSolidAreaDefaultY(solidArea.y);
        solidArea.width = 32;
        solidArea.height = 32;
        setLevel(1);
        setDefaultParams();
        getPlayerModel();
    }

    @Override
    public void update() {
        // Client-side movement
        if (keyH == null) {
            
            return;
        }

        boolean moved = false;

        if (keyH.isUpPressed() || keyH.isDownPressed() || keyH.isLeftPressed() || keyH.isRightPressed()) {
            moved = true;

            if (keyH.isUpPressed()) {
                direction = "up";
            } else if (keyH.isDownPressed()) {
                direction = "down";
            } else if (keyH.isLeftPressed()) {
                direction = "left";
            } else if (keyH.isRightPressed()) {
                direction = "right";
            }
            
            //Check Object collision(work in progess)
            
            //Check Npc collision
          
            	
            

            spriteCounter++;
            if (spriteCounter > 10) {
                spriteNum = (spriteNum == 1) ? 2 : 1;
                spriteCounter = 0;
            }
            
            collisionOn=false;
            gp.getcChecker().checkTile(this);
            
            int npcIndex =gp.getcChecker().checkEntity(this, gp.getNpc());
            int monsterIndex =gp.getcChecker().checkEntity(this, gp.getMonster());
            npcInteraction(npcIndex);

            if (collisionOn==false) {
                switch (direction) {
                    case "up": worldY -= speed; break;
                    case "down": worldY += speed; break;
                    case "left": worldX -= speed; break;
                    case "right": worldX += speed; break;
                }
            }

            // Send the update to the server
            if (networkManager != null) {
                try {
                    networkManager.sendPlayerUpdate(this);
                } catch (Exception e) {
                    System.err.println("Failed to send player update: " + e.getMessage());
                    e.printStackTrace();
                }
            } else {
                System.err.println("NetworkManager is not initialized.");
            }
        }

        // Smooth movement based on the last received data
        if (lastReceivedData != null) {
            long currentTime = System.currentTimeMillis();
            long timeDifference = currentTime - lastReceivedData.getTimestamp();
            double interpolationFactor = Math.min(1.0, timeDifference / 1000.0); // Cap the interpolation factor

            double interpolatedX = worldX + (lastReceivedData.getX() - worldX) * interpolationFactor;
            double interpolatedY = worldY + (lastReceivedData.getY() - worldY) * interpolationFactor;

            worldX = (int) interpolatedX;
            worldY = (int) interpolatedY;
        }
    }

    public void setLastReceivedData(PlayerData playerData) {
        this.lastReceivedData = playerData;
    }

   
    public void correctPosition(int x, int y, String direction) {
        this.worldX = x;
        this.worldY = y;
        this.direction = direction;

        // Reset prediction to server's authoritative position
        this.predictedX = x;
        this.predictedY = y;
        this.isPredicting = false;
    }
    
    public void npcInteraction(int i) {
    	if(i!=999) {
    		//System.out.println("collision with npc");
    	}
    }

   


	
	public void setDefaultParams() {
		worldX = 270;
		worldY = 270;
		usernamePlayer = networkManager != null ? (networkManager.isServer() ? networkManager.getNameServer() : networkManager.getNameClient()) : "SinglePlayer";
		speed = 4;
		direction = "down";
		maxHealth=100;
		setHealth(maxHealth);
	}


	public void draw(Graphics2D g2) {
	    BufferedImage image = null;

	    // Determine the correct image based on direction and sprite number
	    switch (direction) {
	        case "up":
	            image = (spriteNum == 1) ? up1 : up2;
	            break;
	        case "down":
	            image = (spriteNum == 1) ? down1 : down2;
	            break;
	        case "left":
	            image = (spriteNum == 1) ? left1 : left2;
	            break;
	        case "right":
	            image = (spriteNum == 1) ? right1 : right2;
	            break;
	    }

	    // Draw the player at the correct position
	    int drawX = screenX - gp.getPlayer().getWorldX() + worldX;
	    int drawY = screenY - gp.getPlayer().getWorldY() + worldY;
	    g2.drawImage(image, drawX, drawY, gp.getTileSize(), gp.getTileSize(), null);

	    // Draw the player's username
	    if (usernamePlayer != null && !usernamePlayer.isEmpty()) {
	        Font customFont = new Font("Comic Sans", Font.BOLD, 16);
	        g2.setFont(customFont);
	        g2.setColor(Color.white);

	        int textWidth = g2.getFontMetrics().stringWidth(usernamePlayer +" Lvl " + level);
	        int textX = drawX + (gp.getTileSize() / 2) - (textWidth / 2);
	        int textY = drawY - 5;

	        g2.drawString(usernamePlayer +" Lvl " + level, textX, textY);
	    }
	}


	public void getPlayerModel() {
		try {
			up1 = ImageIO.read(getClass().getResourceAsStream("/Player/SpritesJava(up).png"));
			up2 = ImageIO.read(getClass().getResourceAsStream("/Player/SpritesJava(up2).png"));
			down1 = ImageIO.read(getClass().getResourceAsStream("/Player/SpritesJava(down).png"));
			down2 = ImageIO.read(getClass().getResourceAsStream("/Player/SpritesJava(down2).png"));
			left1 = ImageIO.read(getClass().getResourceAsStream("/Player/SpritesJava(left).png"));
			left2 = ImageIO.read(getClass().getResourceAsStream("/Player/SpritesJava(left2).png"));
			right1 = ImageIO.read(getClass().getResourceAsStream("/Player/SpritesJava(right).png"));
			right2 = ImageIO.read(getClass().getResourceAsStream("/Player/SpritesJava(right2).png"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public int getWorldX() {
		return worldX;
	}

	public void setWorldX(int worldX) {
		this.worldX = worldX;
	}

	public int getWorldY() {
		return worldY;
	}

	public void setWorldY(int worldY) {
		this.worldY = worldY;
	}

	public int getPlayerSpeed() {
		return speed;
	}

	public void setPlayerSpeed(int playerSpeed) {
		this.speed = playerSpeed;
	}

	public int getScreenX() {
		return screenX;
	}

	public int getScreenY() {
		return screenY;
	}

}
