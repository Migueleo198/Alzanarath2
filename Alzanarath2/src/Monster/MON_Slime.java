package Monster;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Random;

import javax.imageio.ImageIO;

import Entity.Entity;
import main.GamePanel;

public class MON_Slime extends Entity{
	private int healthBarcounter;
	private boolean gotHit=false;
	public MON_Slime(GamePanel gp){
		super(gp);
		name = "Blue_Slime";
		speed=1;
		maxHealth = 60;
		Health = maxHealth;
		attack=5;
		type=2;
		exp = 100;
		solidArea.x=3;
		solidArea.y=18;
		solidArea.width=42;
		solidArea.height=30;
		solidAreaDefaultX = solidArea.x;
		solidAreaDefaultY = solidArea.y;
		direction="down";
		getModel();
		if(gp.getNetworkManager().isServer()) {
		monsterId = "" + System.currentTimeMillis();
		}
		spriteNum=1;
		
	}
	
	public void getModel() {
		try {
			up1 = ImageIO.read(getClass().getResourceAsStream("/Monsters/SlimeDown1.png"));
			up2 = ImageIO.read(getClass().getResourceAsStream("/Monsters/SlimeDown2.png"));
			down1 = ImageIO.read(getClass().getResourceAsStream("/Monsters/SlimeDown1.png"));
			down2 = ImageIO.read(getClass().getResourceAsStream("/Monsters/SlimeDown2.png"));
			left1 = ImageIO.read(getClass().getResourceAsStream("/Monsters/SlimeDown1.png"));
			left2 = ImageIO.read(getClass().getResourceAsStream("/Monsters/SlimeDown2.png"));
			right1 = ImageIO.read(getClass().getResourceAsStream("/Monsters/SlimeDown1.png"));
			right2 = ImageIO.read(getClass().getResourceAsStream("/Monsters/SlimeDown2.png"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void setAction() {
		actionLockCounter++;
		
		if(actionLockCounter==120) {
		Random random = new Random();
		
		int i = random.nextInt(100)+1; //Picks a number from 1 to 100
		
		if(i<=25) {
			direction="up";
			
		}
		
		if(i>25 && i<=50) {
			direction="down";
			
		}
		
		if(i>50 && i<=75) {
			direction="left";
			
		}
		
		if(i>75 && i<=100) {
			direction="right";
			
		}
		actionLockCounter=0;
		}
	}
	
	public void draw(Graphics2D g2) {
		
		int screenX = worldX - gp.getPlayer().getWorldX() + gp.getPlayer().getScreenX();
		int screenY = worldY - gp.getPlayer().getWorldY() + gp.getPlayer().getScreenY();
		
		if(worldX + gp.getTileSize() > gp.getPlayer().getWorldX()-gp.getPlayer().getScreenX()
				&& worldX - gp.getTileSize() < gp.getPlayer().getWorldX()+gp.getPlayer().getScreenX()
				&& worldY + gp.getTileSize()> gp.getPlayer().getWorldY()-gp.getPlayer().getScreenY()
				&& worldY - gp.getTileSize() < gp.getPlayer().getWorldY()+gp.getPlayer().getScreenY()) {
		
		
		
		BufferedImage image = null;

		switch (direction) {
		case "up":
			if (spriteNum == 1) {
				image = up1;
			}
			if (spriteNum == 2) {
				image = up2;
			}
			break;
		case "down":
			if (spriteNum == 1) {
				image = down1;
			}
			if (spriteNum == 2) {
				image = down2;
			}
			break;

		case "left":
			if (spriteNum == 1) {
				image = left1;
			}
			if (spriteNum == 2) {
				image = left2;
			}
			break;
		case "right":
			if (spriteNum == 1) {
				image = right1;
			}
			if (spriteNum == 2) {
				image = right2;
			}
			break;
		}
		
		 if (isInvincible()) {
		        invincibleCounter++;
		        if (invincibleCounter > 40) {
		            setInvincible(false);
		            invincibleCounter = 0;
		        }
		    }
		if(isInvincible()==true) {
			g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,0.4f));
			gotHit=true;
		}
		
		// THE ENTITY POSITION WILL NEVER BE STATIC
		Font customFont = new Font("Comic Sans", Font.BOLD, 16);
		g2.setFont(customFont);
		
		
		
		int textWidth = g2.getFontMetrics().stringWidth(this.getName());
		int textX = screenX + (gp.getTileSize() / 2) - (textWidth / 2);
		int textY = screenY - 5;
		
		g2.setColor(new Color(0,0,0));
		
		g2.drawString(this.getName(), textX+1, textY+1);
		
		
		g2.setColor(Color.RED);
		g2.drawString(this.getName(), textX, textY);
		g2.drawImage(image, screenX, screenY, gp.getTileSize(), gp.getTileSize(), null);
		
		g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,1f));
		
		}
		healthBarcounter++;
		drawMonsterHealthBar(g2);
	}
	
	public void drawMonsterHealthBar(Graphics2D g2) {
		
		if(gotHit==true && healthBarcounter<=180) {
	    int screenX = worldX - gp.getPlayer().getWorldX() + gp.getPlayer().getScreenX();
	    int screenY = worldY - gp.getPlayer().getWorldY() + gp.getPlayer().getScreenY();
	    int textWidth = g2.getFontMetrics().stringWidth(this.getName());
	    int textX = screenX + (gp.getTileSize() / 2) - (textWidth / 2);
	    int textY = screenY - 5;

	    // Set a fixed length for the health bar
	    int barWidth = 80; 
	    int barHeight = 12; // Height of the health bar

	    // Calculate the proportion of health (currentHealth / maxHealth) to determine bar length
	    int currentHealth = this.Health; // Monster's current health
	    int maxHealth = this.maxHealth;  // Monster's maximum health
	    int healthBarWidth = (int) ((double) currentHealth / maxHealth * barWidth);

	    // Draw the background of the health bar (dark gray)
	    g2.setFont(g2.getFont().deriveFont(Font.PLAIN, 20));
	    g2.setColor(Color.DARK_GRAY);
	    g2.fillRect(textX, textY + 10, barWidth, barHeight); // Full-length background

	    // Draw the actual health bar (red) based on current health
	    g2.setColor(Color.red);
	    g2.fillRect(textX, textY + 10, healthBarWidth, barHeight); // Scaled by current health
		}
		if(healthBarcounter>=180){
			healthBarcounter=0;
			gotHit=false;
			
			
		}
	}
	
	public String getMonsterId() {
		return monsterId;
	}

	public void setMonsterId(String id) {
		monsterId=id;
		
	}
	
	public void updateSprite() {
		collisionOn=false;
		gp.getcChecker().checkTile(this);
		gp.getcChecker().checkEntity(this,gp.getNpc());
		gp.getcChecker().checkEntity(this,gp.getMonster());
		boolean contactPlayer = gp.getcChecker().checkPlayer(this);
		
		
		
		if(this.type==2 && contactPlayer==true) {
			
			if(gp.getPlayer().getHealth()>=0) {
				
				if(gp.getPlayer().isInvincible()==false) {
					
					gp.getPlayer().Health-=this.getAttack();
					gp.getPlayer().setInvincible(true);
						
					
				}
				
			}
		}
		
		
		spriteCounter++;
        if (spriteCounter > 10) {
            spriteNum = (spriteNum == 1) ? 2 : 1;
            spriteCounter = 0;
            hasChanged=true; // Sprite changed
           
        }
		
		
					
	}
	
	public void hitMonster(String monsterId2, int attack2, int health2) {
	    // Iterate through the list of monsters in the game panel
	    for (int i = 0; i < gp.monster.length; i++) {
	        // Debug log to confirm the method is being called
	        System.out.println("Checking monster at index " + i);
	        
	        // Check if the monster ID matches
	        if (gp.monster[i]!=null && gp.monster[i].getMonsterId().equals(monsterId2)) {
	            // Apply damage and update health
	            int newHealth = gp.monster[i].getHealth() - attack2;
	            gp.monster[i].setHealth(newHealth); // Ensure health does not go below zero
	            
	            // Log the updated health for debugging
	            System.out.println("Monster " + monsterId2 + " health updated to " + gp.monster[i].getHealth());
	            
	            // Check if the monster is dead
	            if (gp.monster[i].getHealth() <= 0) {
	                // Handle monster death
	            	gp.getPlayer().setExp(gp.getPlayer().getExp()+this.exp);
	            	gp.setStopUpdatingMonstersOnDeath(true);
	            	gp.getNetworkManager().sendMonsterDeathToAllClients(gp.monster[i].getMonsterId());
	            	
	                return;
	                
	                
	                
	                // Optionally set the monster to null if you want to remove it from the game
	                // gp.monster[i] = null; // Uncomment if you want to set the monster to null
	            }
	            
	            // Send updated monster data to the server
	            gp.getNetworkManager().sendMonsterDataToServer(gp.getPlayer().getUsername(), monsterId2, gp.monster[i]);
	            
	            // Optionally handle additional logic for reactions or effects
	            // ...
	            
	            return; // Exit once the monster is found and updated
	        }
	    }
	    
	    // Optional: Log if the monster ID was not found
	    System.out.println("Monster with ID " + monsterId2 + " not found.");
	}
	
	
	
	public void takeDamage(String monsterId, int damage) {
	   
	        
	            // Subtract the damage from the monster's health
	            this.Health -= damage;

	            // Check if the monster's health falls to or below zero
	            if (this.Health <= 0) {
	            	this.Health = 0; // Ensure no negative health
	            	
	            }
	    
	        
	}

	
}
