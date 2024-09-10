package Entity;

import java.io.IOException;
import java.net.Socket;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.imageio.ImageIO;

import Inputs.KeyHandler;
import Monster.MON_Slime;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import main.GamePanel;
import javax.swing.JOptionPane;

import Networking.NetworkManager;
import Networking.PlayerData;
import Objects.OBJ_BloodSword;
import Objects.OBJ_WoodenShield;

public class Player extends Entity {
    private GamePanel gp;
    private KeyHandler keyH;
    private NetworkManager networkManager;
    private Socket socket;
    public final int screenX;
    public final int screenY;
    public boolean moved;
    // Client-side prediction variables
    private int predictedX, predictedY;
    private boolean isPredicting;
    
    private PlayerData lastReceivedData; // Store the last received data
    
    private int drawX;
    private int drawY;
    private int countAttackDelayTime=0;
    private boolean hasData=false;
    
    private int skillPoints;
    private boolean isAtkUp1Unlocked() {
		return atkUp1Unlocked;
	}



	private void setAtkUp1Unlocked(boolean atkUp1Unlocked) {
		this.atkUp1Unlocked = atkUp1Unlocked;
	}



	private boolean isDefUp1Unlocked() {
		return defUp1Unlocked;
	}



	private void setDefUp1Unlocked(boolean defUp1Unlocked) {
		this.defUp1Unlocked = defUp1Unlocked;
	}



	private boolean atkUp1Unlocked;
    private boolean defUp1Unlocked;
    private boolean speedUpUnlocked;
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
        
       
        setDefaultParams();
        getPlayerModel();
        getPlayerAttackImage();
        
        
    }
    
    
    
    public void setDefaultParams() {
        // Load player data first
        loadPlayerData(usernamePlayer);

        // Default position
        worldX = 600;
        worldY = 600;

        // Set username depending on the game mode
        setUsernamePlayer(networkManager != null ? 
            (networkManager.isServer() ? networkManager.getNameServer() : networkManager.getNameClient()) 
            : "SinglePlayer");

        // Default speed and direction
        speed = 4;
        direction = "down";

        // Ensure loaded level is not overridden by default stats
        if (level == 0) {
            level = 1;
        }

        // Ensure other stats are set based on loaded data
        if (maxHealth == 0) {
            maxHealth = 100;
        }
        
        if (strength == 0) {
            setStrength(1);
        }
        
        if (dexterity == 0) {
            setDexterity(1);
        }

        // Load skill points based on loaded level
        setSkillPoints(level);

        // Health is set based on loaded or default max health
        setHealth(maxHealth);

        invincibleCounter = 0;
        setExp(0);
        setGold(0);

        // Equip default weapon and shield
        currentWeapon = new OBJ_BloodSword(gp);
        attack = getAttack(); // Calculate total attack based on weapon and strength
        currentShield = new OBJ_WoodenShield(gp);
        setDefense(getDefense()); // Calculate total defense based on dexterity and equipment
    }
    
    public void setSkillStats() {
    	 if(defUp1Unlocked) {
         	dexterity+=1;
         }
         
         if(atkUp1Unlocked) {
         	strength+=1;
         }
    }

    @Override
    public void update() {
        // Client-side movement
        if (keyH == null) {
            
            return;
        }
        
        //Check if level up happened and reset exp needed to level up
        checkLevelUp();
        
        countAttackDelayTime++;
      //DELAY BETWEEN EACH PLAYER ATTCK SO YOU CANT KEEP THE KEY PRESSED AND ATTACK INFINITELY
        if( countAttackDelayTime>=30) {
        	gp.keyH.attackDelay=1;
        	countAttackDelayTime=0;
        }

         moved = false;
        
        if (keyH.isePressed()) {
            attacking = true;
            
            
        }
        
        //ON DATABASE
        
        
        
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
    	
    	//FIRST WE REGISTER THE ATTACK TO THE MONSTER SO IT DOESNT TAKE THE DELAY
    	//OF THE ATTACK INTO ACCOUNT FOR THE MONSTERS TO RECEIVE DAMAGE SINCE THAT IS
    	//HANDLED BY THE INVENCIBILITY TIME OF THE MONSTER
    	
    	
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
		
		
		
    	if(gp.keyH.attackDelay==1) {
        spriteCounter++;
        
        if (spriteCounter <= 5) {
            spriteNum = 1;
            
        } else if (spriteCounter <= 25) {
            spriteNum = 2;
            
           
    		
    		
        
            
        } else {
            
        	spriteNum = 1;
            spriteCounter = 0;
            attacking = false;
        }
        
        
        spriteNum = 1;
        spriteCounter = 0;
        attacking = false;
    	}
        
    }
    
    public void damageMonster(int i) {
        if (i != 999) {
            Entity monster = gp.monster[i];
            
            if (monster != null && !monster.isInvincible()) {
                synchronized (monster) {
                    monster.setInvincible(true); // Set monster to invincible to avoid multiple hits
                    gp.playSE(2); // Play sound effect for the hit
                    
                    if ( gp.monster[i].getHealth() <= 0 ) {
                            // Notify server and other clients about the monster's death
                    		exp+=gp.monster[i].exp;
                    	   
                        	
                        	
                        }

                        // Optionally set the monster to null in the local game panel
                        
                    }
                   
                    // Apply damage to the monster
                    monster.hitMonster(monster.getMonsterId(), this.attack, monster.getHealth());
                    gp.ui.addBattleNotification("You dealt " + attack + "damage!");
                    // Check if monster is dead
                   
                }
            
        } else {
            // Handle case where index is 999 (if applicable)
            System.out.println("Invalid monster index: " + i);
        }
    }
    
    public void checkLevelUp(){
    	int exp2= exp-nextLevelExp;
    	if(exp>=nextLevelExp) {
    		level++;
    		
    		nextLevelExp = (int) ((double) +  1.5*(((Math.pow(level,2)*2)*20)) + Math.pow(level, 3));
    	    
    		    		
    		
    			
    		exp=exp2;
    		maxHealth +=10;
    		strength++;
    		dexterity++;
    		attack=getAttack();
    		defense= getDefense();
    		Health=maxHealth;
    		skillPoints+=1;
    		//DIALOGUES AFTER LEVELING UP(NOT YET IMPLEMENTED)!
    		savePlayerData(usernamePlayer);
    		//gp.playSE(6);
    		//gp.setGameState(gp.dialogueState);
    		
    		//startDialogue(this,0);
    		//setDialogue();
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
				
				if(isInvincible()==false) {
					this.Health-=gp.getMonster()[i].getAttack();
					setInvincible(true);
				}
				
			}
		}
	}


	public void draw(Graphics2D g2) {
	    BufferedImage image = null;
	   
	    // Calculate draw position
	    int drawX = screenX - gp.getPlayer().getWorldX() + worldX;
	    int drawY = screenY - gp.getPlayer().getWorldY() + worldY;
	    
	    if (getUsernamePlayer() != null && !getUsernamePlayer().isEmpty()) {
            Font customFont = new Font("Comic Sans", Font.BOLD, 16);
            g2.setFont(customFont);
            g2.setColor(Color.white);

            int textWidth = g2.getFontMetrics().stringWidth(getUsernamePlayer() + " Lvl " + level);
            int textX = drawX + (gp.getTileSize() / 2) - (textWidth / 2);
            int textY = drawY - 5;

            g2.drawString(getUsernamePlayer() + " Lvl " + level, textX, textY);
        }

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
	                drawX -= (gp.getTileSize()*2-7); // Move left to align with attack image
	            }
	            break;
	    }

	    g2.drawImage(image, drawX, drawY, null);

	   

	    // Handle invincibility effect
	    if (isInvincible()) {
	        invincibleCounter++;
	        if (invincibleCounter > 60) {
	            setInvincible(false);
	            invincibleCounter = 0;
	        }
	    }
	}



	
	public void getPlayerAttackImage() {
		
		if(currentWeapon.name.equals("Blood Sword")) {

	    	attackUp1= setup("/Attacks/BloodSwordAtkUp.png",gp.getTileSize()+2,gp.getTileSize()*2+10);
	    	attackUp2= setup("/Attacks/BloodSwordAtkUp.png",gp.getTileSize()+2,gp.getTileSize()*2+10);
	    	attackDown1= setup("/Attacks/BloodSwordAtk_Down.png",gp.getTileSize()+5,gp.getTileSize()*2+10);
	    	attackDown2= setup("/Attacks/BloodSwordAtk_Down.png",gp.getTileSize()+5,gp.getTileSize()*2+10);
	    	attackLeft1= setup("/Attacks/BloodSwordAtkLeft.png",gp.getTileSize()*3-9,gp.getTileSize());//Should be on the first one
	    	attackLeft2= setup("/Attacks/BloodSwordAtkLeft.png",gp.getTileSize()*3-9,gp.getTileSize());//Should be on the first one
	    	attackRight1= setup("/Attacks/BloodSwordAtkRight.png",gp.getTileSize()*3-13,gp.getTileSize());//Should be on the first one
	    	attackRight2= setup("/Attacks/BloodSwordAtkRight.png",gp.getTileSize()*3-13,gp.getTileSize());//Should be on the first one
		}
		else {
		
		
	    	attackUp1= setup("/Attacks/sword_sprite_up.png",gp.getTileSize()+2,gp.getTileSize()*2);
	    	attackUp2= setup("/Attacks/sword_sprite_up.png",gp.getTileSize()+2,gp.getTileSize()*2);
	    	attackDown1= setup("/Attacks/sword_sprite_down.png",gp.getTileSize()+5,gp.getTileSize()*2);
	    	attackDown2= setup("/Attacks/sword_sprite_down.png",gp.getTileSize()+5,gp.getTileSize()*2);
	    	attackLeft1= setup("/Attacks/sword_sprite_left.png",gp.getTileSize()*3-9,gp.getTileSize());//Should be on the first one
	    	attackLeft2= setup("/Attacks/sword_sprite_left.png",gp.getTileSize()*3-9,gp.getTileSize());//Should be on the first one
	    	attackRight1= setup("/Attacks/sword_sprite_right.png",gp.getTileSize()*3-13,gp.getTileSize());//Should be on the first one
	    	attackRight2= setup("/Attacks/sword_sprite_right.png",gp.getTileSize()*3-13,gp.getTileSize());//Should be on the first one
	    	
		}
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
	
	//SKILL TREE
	
	// Method to unlock the selected skill if enough skill points are available
	public void unlockSelectedSkill() {
	    if (this.getSkillPoints() > 0) {
	        switch (gp.ui.getSelectedSkillIndex()) {
	            case 0:  // Atk up
	                if (!this.isAtkUp1Unlocked()) {
	                	this.setAtkUp1Unlocked(true);
	                	this.setSkillPoints(this.getSkillPoints() - 1);
	                    savePlayerData(usernamePlayer);
	                }
	                break;
	            case 1:  // Def up
	                if (!this.isDefUp1Unlocked()) {
	                	this.setDefUp1Unlocked(true);
	                	this.setSkillPoints(this.getSkillPoints() - 1);
	                    savePlayerData(usernamePlayer);
	                }
	                break;
	            case 2:  // Speed up
	               
	                break;
	        }
	    }
	}
	
	public void savePlayerData(String username) {
	    try {
	        // Ensure you are saving data for the correct player by using the username in WHERE
	        String query = "UPDATE PlayerData SET level=?, dexterity=?, strength=?, maxHealth=?, skillPoints=?, atkUp1Unlocked=?, defUp1Unlocked=?, speedUp1Unlocked=? WHERE username=?";
	        PreparedStatement ps = gp.connection.connection.prepareStatement(query);
	        
	        // Set player stats
	        ps.setInt(1, level);           // Level
	        ps.setInt(2, dexterity);       // Dexterity
	        ps.setInt(3, strength);        // Strength
	        ps.setInt(4, maxHealth);       // Max Health

	        // Set skill-related attributes
	        ps.setInt(5, skillPoints);         // Skill Points
	        ps.setBoolean(6, this.isAtkUp1Unlocked());   // Attack skill unlocked
	        ps.setBoolean(7, this.isDefUp1Unlocked());   // Defense skill unlocked
	        ps.setBoolean(8, this.isSpeedUp1Unlocked()); // Speed skill unlocked

	        // Set the correct username for WHERE condition
	        ps.setString(9, usernamePlayer);

	        // Execute the update query
	        ps.executeUpdate();
	        
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	}

	public void loadPlayerData(String username) {
	    try {
	        // Make sure to load the correct player data by selecting based on the username
	        String query = "SELECT * FROM PlayerData WHERE username=?";
	        PreparedStatement ps = gp.connection.connection.prepareStatement(query);
	        ps.setString(1, usernamePlayer);
	        ResultSet rs = ps.executeQuery();

	        if (rs.next()) {
	            // Load player stats
	            level =(rs.getInt("level"));
	            dexterity = (rs.getInt("dexterity"));
	            strength = (rs.getInt("strength"));
	            maxHealth = (rs.getInt("maxHealth"));
	            skillPoints = (rs.getInt("skillPoints"));

	            // Load unlocked skills
	            this.atkUp1Unlocked = rs.getBoolean("atkUp1Unlocked");
	            this.defUp1Unlocked = rs.getBoolean("defUp1Unlocked");
	            this.speedUpUnlocked = rs.getBoolean("speedUp1Unlocked");

	            // Mark that data has been loaded
	            hasData = true;
	        }
	        
	    } catch (SQLException e) {
	        e.printStackTrace();
	        hasData = false;
	    }
	}
	
	
    // Method to check if a skill is unlocked
    public boolean isSkillUnlocked(String skillName) {
        switch (skillName) {
            case "Skill 1":
                return atkUp1Unlocked;
            case "Skill 2":
                return defUp1Unlocked;
            default:
                return false;
        }
    }
    
    
    

	
	public int getAttack() {
		return attack = getStrength() * currentWeapon.attackValue;
	}
	
	public int getDefense() {
		return defense = getDexterity() * currentShield.defenseValue;
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
	
	public int getInvincibleCounter() {
		return this.invincibleCounter;
	}
	
	public void setInvincibleCounter(int invincibleCounter) {
		this.invincibleCounter=invincibleCounter;
	}

	@Override
	public String getMonsterId() {
		// TODO Auto-generated method stub
		return "" + System.currentTimeMillis();
	}

	@Override
	public void hitMonster(String monsterId2, int attack2, int health2) {
		// TODO Auto-generated method stub
		
	}

	public Socket getSocket() {
		return socket;
	}

	public void setSocket(Socket socket) {
		this.socket = socket;
	}



	public int getSkillPoints() {
		return skillPoints;
	}



	public void setSkillPoints(int skillPoints) {
		this.skillPoints = skillPoints;
	}



	public boolean isSpeedUp1Unlocked() {
		return speedUpUnlocked;
	}



	public void setSpeedUpUnlocked(boolean speedUpUnlocked) {
		this.speedUpUnlocked = speedUpUnlocked;
	}

}
