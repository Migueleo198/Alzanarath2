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

    public final int screenX;
    public final int screenY;
    public boolean moved;
    // Client-side prediction variables
    private int predictedX, predictedY;
    private boolean isPredicting;
    
    private PlayerData lastReceivedData; // Store the last received data
    
    private int drawX;
    private int drawY;

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
        
        //attack collision area //can be changed based on the players weapon size
        
        attackArea.width = 36;
        attackArea.height = 36;
        
        setLevel(1);
        setDefaultParams();
        getPlayerModel();
        getPlayerAttackImage();
    }
    
    public void setDefaultParams() {
		worldX = 270;
		worldY = 270;
		usernamePlayer = networkManager != null ? (networkManager.isServer() ? networkManager.getNameServer() : networkManager.getNameClient()) : "SinglePlayer";
		speed = 4;
		attack=5;
		direction = "down";
		maxHealth=100;
		setHealth(maxHealth);
	}

    @Override
    public void update() {
        // Client-side movement
        if (keyH == null) {
            
            return;
        }

         moved = false;
        
        if (keyH.isePressed()) {
            attacking = true;
            
            
        }
        
        
        
        if (attacking==true) {
        	networkManager.sendPlayerUpdate(this);
        	attacking();
        	networkManager.sendPlayerUpdate(this);
        	spriteNum=1;
        	
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

        if (spriteCounter <= 5) {
            spriteNum = 1;
        } else if (spriteCounter <= 25) {
            spriteNum = 2;
            
            //Save the original player parameters
            int currentWorldX=worldX;
            int currentWorldY=worldY;
            int solidAreaWidth = solidArea.width;
            int solidAreaHeight = solidArea.height;
            
            //Adjust player parameters for the attack area collision
            
            switch(direction) {
    		case "up": worldY -= attackArea.height; break;
    		case "down": worldY += attackArea.height; break;
    		case "left": worldX -= attackArea.height; break;
    		case "right": worldX += attackArea.height; break;
    		}
    		
    		//attackArea become solidArea
    		solidArea.width = attackArea.width;
    		solidArea.height = attackArea.height;
    		
    		//check monster collision with the updated worldX, worldY and solidArea
    		int monsterIndex = gp.getcChecker().checkEntity(this,gp.getMonster());
    		damageMonster(monsterIndex);
    		//After checking collision, restore the original data
    		worldX=currentWorldX;
    		worldY=currentWorldY;
    		solidArea.width=solidAreaWidth;
    		solidArea.height=solidAreaHeight;
            
            
        } else {
            spriteNum = 1;
            spriteCounter = 0;
            attacking = false;
        }
        
        
    }
    
    public void damageMonster(int i) {
    	if(i!=999) {
    		
    		if (gp.monster[i].invincible == false){
    			
    			gp.monster[i].Health-=this.attack;
    		}
    		
    		if(gp.monster[i].getHealth()<=0  ) {
    			gp.monster[i]=null;
    		}
    	}
    	else {
    		System.out.println("miss");
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

	    // Calculate draw position
	    int drawX = screenX - gp.getPlayer().getWorldX() + worldX;
	    int drawY = screenY - gp.getPlayer().getWorldY() + worldY;

	    // Adjust draw position based on attack state
	    switch (direction) {
	        case "down":
	            if (!attacking) {
	                image = (spriteNum == 1) ? down1 : down2;
	            } else {
	                image = (spriteNum == 1) ? attackDown1 : attackDown2;
	                // No need to adjust drawX or drawY here
	            }
	            break;

	        case "up":
	            if (!attacking) {
	                image = (spriteNum == 1) ? up1 : up2;
	            } else {
	                image = (spriteNum == 1) ? attackUp1 : attackUp2;
	                drawY -= (gp.getTileSize()); // Move up to align with attack image
	            }
	            break;

	        case "right":
	            if (!attacking) {
	                image = (spriteNum == 1) ? right1 : right2;
	            } else {
	                image = (spriteNum == 1) ? attackRight1 : attackRight2;
	                
	            }
	            break;

	        case "left":
	            if (!attacking) {
	                image = (spriteNum == 1) ? left1 : left2;
	            } else {
	                image = (spriteNum == 1) ? attackLeft1 : attackLeft2;
	                drawX -= (gp.getTileSize()); // Move left to align with attack image
	            }
	            break;
	    }

	    g2.drawImage(image, drawX, drawY, null);

	    // Draw the player's username
	    if (usernamePlayer != null && !usernamePlayer.isEmpty()) {
	        Font customFont = new Font("Comic Sans", Font.BOLD, 16);
	        g2.setFont(customFont);
	        g2.setColor(Color.white);

	        int textWidth = g2.getFontMetrics().stringWidth(usernamePlayer + " Lvl " + level);
	        int textX = drawX + (gp.getTileSize() / 2) - (textWidth / 2);
	        int textY = drawY - 5;

	        g2.drawString(usernamePlayer + " Lvl " + level, textX, textY);
	    }

	    // Handle invincibility effect
	    if (invincible) {
	        invincibleCounter++;
	        if (invincibleCounter > 60) {
	            invincible = false;
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

	public int getDrawX() {
		return drawX;
	}

	public void setDrawX(int drawX) {
		this.drawX = drawX;
	}

	public int getDrawY() {
		return drawY;
	}

	public void setDrawY(int drawY) {
		this.drawY = drawY;
	}

}
