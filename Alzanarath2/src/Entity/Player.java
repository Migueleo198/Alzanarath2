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
        solidArea.height = 26;
        setLevel(1);
        setDefaultParams();
        getPlayerModel();
        getPlayerAttackImage();
    }

    @Override
    public void update() {
        // Client-side movement
        if (keyH == null) {
            
            return;
        }

        boolean moved = false;
        
        if (keyH.isePressed()) {
            attacking = true;
            
        }
        
        if (attacking==true) {
        	attacking();
        }

        else if(keyH.isUpPressed() || keyH.isDownPressed() || keyH.isLeftPressed() || keyH.isRightPressed()) {
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
            
            contactMonster(monsterIndex);
            
            
            if (collisionOn==false ) {
                switch (direction) {
                    case "up": worldY -= speed; break;
                    case "down": worldY += speed; break;
                    case "left": worldX -= speed; break;
                    case "right": worldX += speed; break;
                }
            }

            // Send the update to the server
            if (networkManager != null ) {
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
    
    public void attacking() {
    	spriteCounter++;
    	
    	if(spriteCounter <=5) {
    		
    		
    	}
    	
    	if(spriteCounter>5 && spriteCounter <= 25) {
    		
    		
    		
    	}
    	
    	if (spriteCounter >	25) {
    		
    		spriteCounter=0;
    		attacking=false;
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
    	else {
    		if(gp.keyH.isePressed()==true) {
            	attacking=true;
            }
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
	
	public void contactMonster(int i){
		if(i!=999) {
			if(Health>=0) {
				
				if(invincible==false) {
					this.Health-=gp.getMonster()[i].getAttack();
					invincible=true;
				}
				
			}
		}
	}


	public void draw(Graphics2D g2) {
	    BufferedImage image = null;

	    // Draw the player at the correct position
	    int drawX = screenX - gp.getPlayer().getWorldX() + worldX;
	    int drawY = screenY - gp.getPlayer().getWorldY() + worldY;
		
		switch(direction) {
		case "down":
			
		if (attacking==false){	
		if (spriteNum==1) {image=down1;}
		if (spriteNum==2) {image=down2;}
		}
		if(attacking==true) {
			drawY-=5;
			if (spriteNum==1) {image=attackDown1;}
			if (spriteNum==2) {image=attackDown2;}
		}
		break;
		
		case "up":
		if (attacking==false){
		if (spriteNum==1) {	image=up1;}
        if (spriteNum==2) {image=up2;}
		}
		if (attacking==true){
			drawY=screenY-gp.getTileSize();
			drawX-=3;
		if (spriteNum==1) {	image=attackUp1;}
	       if (spriteNum==2) {image=attackUp2;}
		}
		
		break;
		
		case "right":
		if (attacking==false){
			if (spriteNum==1) {	image=right1;}
			if (spriteNum==2) {image=right2;}
		}
		if (attacking==true){
			drawX-=3;
			if (spriteNum==1) {	image=attackRight1;}
			if (spriteNum==2) {image=attackRight2;}
		}
		break;
		case "left":
		if (attacking==false){
			
		if (spriteNum==1) {image=left1;}
        if (spriteNum==2) {image=left2;}
        }
        if (attacking==true){
        	drawX=screenX-gp.getTileSize()*2+13;
        	if (spriteNum==1) {image=attackLeft1;}
            if (spriteNum==2) {image=attackLeft2;}
        }
		break;
				}

		
	    g2.drawImage(image, drawX, drawY,null);
	    drawX = screenX - gp.getPlayer().getWorldX() + worldX;
	    drawY = screenY - gp.getPlayer().getWorldY() + worldY;
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
	    
	    //Change later for monster hit rate time
	    if(invincible==true) {
	    	invincibleCounter++;
	    	if(invincibleCounter>60) {
	    		invincible=false;
	    		invincibleCounter = 0;
	    	}
	    }
	}
	
	public void getPlayerAttackImage() {
		
		
	    	attackUp1= setup("/Attacks/sword_sprite_up.png",gp.getTileSize()+2,gp.getTileSize()*2);
	    	attackUp2= setup("/Attacks/sword_sprite_up.png",gp.getTileSize()+2,gp.getTileSize()*2);
	    	attackDown1= setup("/Attacks/sword_sprite_down.png",gp.getTileSize()+5,gp.getTileSize()*2);
	    	attackDown2= setup("/Attacks/sword_sprite_down.png",gp.getTileSize()+5,gp.getTileSize()*2);
	    	attackLeft1= setup("/Attacks/sword_sprite_left.png",gp.getTileSize()*3-9,gp.getTileSize());//Should be on the first one
	    	attackLeft2= setup("/Attacks/sword_sprite_left.png",gp.getTileSize()*3-9,gp.getTileSize());//Should be on the first one
	    	attackRight1= setup("/Attacks/sword_sprite_right.png",gp.getTileSize()*3-13,gp.getTileSize());//Should be on the first one
	    	attackRight2= setup("/Attacks/sword_sprite_right.png",gp.getTileSize()*3-13,gp.getTileSize());//Should be on the first one
	    	
		
	}


	public void getPlayerModel() {
		
			up1 = setup("/Player/SpritesJava(up).png",gp.getTileSize(),gp.getTileSize());
			up2 = setup("/Player/SpritesJava(up2).png",gp.getTileSize(),gp.getTileSize());
			down1 = setup("/Player/SpritesJava(down).png",gp.getTileSize(),gp.getTileSize());
			down2 = setup("/Player/SpritesJava(down2).png",gp.getTileSize(),gp.getTileSize());
			left1 = setup("/Player/SpritesJava(left).png",gp.getTileSize(),gp.getTileSize());
			left2 = setup("/Player/SpritesJava(left2).png",gp.getTileSize(),gp.getTileSize());
			right1 = setup("/Player/SpritesJava(right).png",gp.getTileSize(),gp.getTileSize());
			right2 = setup("/Player/SpritesJava(right2).png",gp.getTileSize(),gp.getTileSize());
		
	}
	
	public boolean isAttacking() {
		return attacking;
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
	
	public void setIsAttacking(boolean isAttacking) {
		// TODO Auto-generated method stub
		this.attacking=isAttacking;
	}

}
