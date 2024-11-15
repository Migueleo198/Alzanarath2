package Entity;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

import main.GamePanel;
import main.Utility;

public abstract class Entity {
	public String usernamePlayer;
	protected int worldX;
	protected int worldY;
	protected int speed;
	protected String name;
	protected BufferedImage up1,up2,down1,down2,left1,left2,right1,right2;
	protected BufferedImage attackUp1,attackUp2,attackDown1,attackDown2,attackLeft1,attackLeft2,attackRight1,attackRight2;
	protected String direction;
	protected int actionLockCounter=0;
	public int spriteCounter=0;
	protected int spriteCounter2=0;
	public int spriteNum=1;
	protected int solidAreaDefaultX;
	protected int solidAreaDefaultY;
	protected int level = 1;
	protected int strength=0;
	protected int dexterity=0;
	protected int attack=0;
	protected int defense=0;
	protected int exp=0;
	protected int nextLevelExp;
	private int gold;
	private Entity currentWeapon;
	private Entity currentShield;
	protected boolean isMonster=false;
	protected boolean hasChanged=false;
	
	
	
	
	
	
	
	
	protected Rectangle attackArea = new Rectangle(0,0,0,0);
	
	//ATTACK ANIMATION VAR
	protected boolean attacking=false;
	
	public Rectangle solidArea = new Rectangle(0,0,48,48);
	
	//Hit params
	private boolean invincible = false;
	public int invincibleCounter = 0;
	
	
	private boolean alive=true;
	
	public boolean collisionOn =false;
	
	protected GamePanel gp;
	
	//monster death time counter
	protected long deathTime;

	protected int maxHealth;
	public int Health;
	
	//ITEM ATTRIBUTES
	protected int attackValue;
	protected int defenseValue;
	
	protected String monsterId;
	
	protected long removalTime=0;
	
	protected String description = "";
	
	//TYPE
	protected int type; //1= player 2 = slime
	
	protected final int type_Player=0;
	protected final int type_Npc=1;
	protected final int type_Monster=2;
	protected final int type_Sword=3;
	protected final int type_Shield=4;
	protected final int type_BloodSword=5;
	protected final int type_Consumable=6;
	
	public int getAttack() {
		
		return attack;
	}

	public void setAttack(int attack) {
		this.attack = attack;
	}
	
	public int getMaxHealth() {
		return maxHealth;
	}

	public void setMaxHealth(int maxHealth) {
		this.maxHealth = maxHealth;
	}

	public Entity(GamePanel gp) {
		this.gp=gp;
		
	
	}
	
	public void setAction() {}
	
	public BufferedImage setup(String imageName, int width, int height) {
    	Utility uTool = new Utility();
    	BufferedImage scaledImage = null;
    	
    	try {
    	scaledImage = ImageIO.read(getClass().getResourceAsStream(imageName));
    	scaledImage = uTool.scaleImage(scaledImage, width, height);
    	}catch(IOException e){
    		e.printStackTrace();
    	}
    	return scaledImage;
    }
	
	public int getLeftX() {
		return worldX +solidArea.x;
	}
	
	public int getRightX() {
		return worldX + solidArea.x + solidArea.width;
	}
	
	public int getTopY() {
		return worldY +solidArea.y;
	}
	
	public int getBottomY() {
		return worldY + solidArea.y + solidArea.height;
	}
	
	public int getCol() {
		return(worldX + solidArea.x)/gp.getTileSize();
	}
	
	public int getRow() {
		return(worldY + solidArea.y)/gp.getTileSize();
	}
	
	public void update(){
		setAction();
		
		collisionOn=false;
		gp.getcChecker().checkTile(this);
		gp.getcChecker().checkEntity(this,gp.getNpc());
		gp.getcChecker().checkEntity(this,gp.getMonster());
		boolean contactPlayer = gp.getcChecker().checkPlayer(this);
		
		
		
		if(this.type==type_Monster && contactPlayer==true) {
			
			if(gp.getPlayer().Health>=0) {
				
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
		
		// IF COLLISION IS FALSE THE NPC CAN MOVE
					if (collisionOn == false) {
						switch (direction) {
						case "up":
							worldY -= speed;
							 hasChanged=true; // Sprite changed
							
							break;
						case "down":
							worldY += speed;
							 hasChanged=true; // Sprite changed
							
						case "left":
							worldX -= speed;
							 hasChanged=true; // Sprite changed
							
							break;
						case "right":
							worldX += speed;
							 hasChanged=true; // Sprite changed
							 
							break;
						}
					}
					
					
				
				
	}
	
	public void getNpcImage() {
		
	}
	
	public void use(Entity entity) {}
	
	public void getDetected(Entity user, Entity target[], String targetName) {
		int index = 999;
		
		//Check the surrounding object
		int nextWorldX = user.getLeftX();
		int nextWorldY = user.getTopY();
		
		switch(direction) {
		case "up": nextWorldY = user.getTopY()-1;break;
		case "down": nextWorldY = user.getBottomY()+1;break;
		case "left": nextWorldX = user.getLeftX()-1; break;
		case "right": nextWorldX = user.getRightX()+1;break;
		}
		
		int col = nextWorldX/gp.getTileSize();
		int row = nextWorldY/gp.getTileSize();
		
		for(int i=0; i<target.length; i++) {
			if(target[i]!=null) {
				if(target[i].getCol()==col &&
						target[i].getRow()==row
						&& target[i].name.equals(targetName)) {
					index = 1;
					break;
				}
			}
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

				
				
				
				 // Handle invincibility effect
			    if (isInvincible()) {
			        invincibleCounter++;
			        if (invincibleCounter > 40) {
			            setInvincible(false);
			            invincibleCounter = 0;
			        }
			    }
			if(isInvincible()==true) {
				g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,0.4f));
			}
			
			// THE ENTITY POSITION WILL NEVER BE STATIC
			Font customFont = new Font("Comic Sans", Font.BOLD, 16);
			g2.setFont(customFont);
			
			
			
			
			g2.drawImage(image, screenX, screenY, null);
			
			g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,1f));
			
	}
		}

	// [ This is the entity username ]
	
	public String getUsername() {
		return getUsernamePlayer();
	}
	
	public void setUsername(String usernamePlayer) {
		this.setUsernamePlayer(usernamePlayer);
	}
	
	// 
	
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

	public int getSpeed() {
		return speed;
	}

	public void setSpeed(int speed) {
		this.speed = speed;
	}

	public BufferedImage getUp1() {
		return up1;
	}

	public void setUp1(BufferedImage up1) {
		this.up1 = up1;
	}

	public BufferedImage getUp2() {
		return up2;
	}

	public void setUp2(BufferedImage up2) {
		this.up2 = up2;
	}

	public BufferedImage getDown1() {
		return down1;
	}

	public void setDown1(BufferedImage down1) {
		this.down1 = down1;
	}

	public BufferedImage getDown2() {
		return down2;
	}

	public void setDown2(BufferedImage down2) {
		this.down2 = down2;
	}

	public BufferedImage getLeft1() {
		return left1;
	}

	public void setLeft1(BufferedImage left1) {
		this.left1 = left1;
	}

	public BufferedImage getLeft2() {
		return left2;
	}

	public void setLeft2(BufferedImage left2) {
		this.left2 = left2;
	}

	public BufferedImage getRight1() {
		return right1;
	}

	public void setRight1(BufferedImage right1) {
		this.right1 = right1;
	}

	public BufferedImage getRight2() {
		return right2;
	}

	public void setRight2(BufferedImage right2) {
		this.right2 = right2;
	}

	public String getDirection() {
		return direction;
	}

	public void setDirection(String direction) {
		this.direction = direction;
	}

	public int getSpriteCounter() {
		return spriteCounter;
	}

	public void setSpriteCounter(int spriteCounter) {
		this.spriteCounter = spriteCounter;
	}

	public int getSpriteNum() {
		return spriteNum;
	}

	public void setSpriteNum(int spriteNum) {
		this.spriteNum = spriteNum;
	}

	public Rectangle getSolidArea() {
		return solidArea;
	}

	public void setSolidArea(Rectangle solidArea) {
		this.solidArea = solidArea;
	}

	public boolean isCollisionOn() {
		return collisionOn;
	}

	public void setCollisionOn(boolean collisionOn) {
		this.collisionOn = collisionOn;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getSolidAreaDefaultX() {
		return solidAreaDefaultX;
	}

	public void setSolidAreaDefaultX(int solidAreaDefaultX) {
		this.solidAreaDefaultX = solidAreaDefaultX;
	}

	public int getSolidAreaDefaultY() {
		return solidAreaDefaultY;
	}

	public void setSolidAreaDefaultY(int solidAreaDefaultY) {
		this.solidAreaDefaultY = solidAreaDefaultY;
	}

	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	public int getHealth() {
		return Health;
	}

	public void setHealth(int health) {
		Health = health;
	}

	public int getGold() {
		return gold;
	}

	public void setGold(int gold) {
		this.gold = gold;
	}

	public int getNextLevelExp() {
		return nextLevelExp;
	}

	public void setNextLevelExp(int nextLevelExp) {
		this.nextLevelExp = nextLevelExp;
	}

	public int getExp() {
		return exp;
	}

	public void setExp(int exp) {
		this.exp = exp;
	}

	public int getDefense() {
		return defense;
	}

	public void setDefense(int defense) {
		this.defense = defense;
	}

	public int getDexterity() {
		return dexterity;
	}

	public void setDexterity(int dexterity) {
		this.dexterity = dexterity;
	}

	public int getStrength() {
		return strength;
	}

	public void setStrength(int strength) {
		this.strength = strength;
	}

	public String getUsernamePlayer() {
		return usernamePlayer;
	}

	public void setUsernamePlayer(String usernamePlayer) {
		this.usernamePlayer = usernamePlayer;
	}

	public abstract String getMonsterId();

	public void setMonsterId(String monsterId2) {
		
		
	}

	public void updateSprite() {
		// TODO Auto-generated method stub
		
	}

	public boolean isInvincible() {
		return invincible;
	}

	public void setInvincible(boolean invincible) {
		this.invincible = invincible;
	}
	
	

	public void takeDamage(String monsterId, int damage) {
		
		System.out.println("hi");
	}

	public abstract void hitMonster(String monsterId2, int attack2, int health2);

	public boolean isAlive() {
		return alive;
	}

	public void setAlive(boolean alive) {
		this.alive = alive;
	}
	public long getDeathTime() {
        return deathTime;
    }

    public void setDeathTime(long deathTime) {
        this.deathTime = deathTime;
    }

	public long getRemovalTime() {
		// TODO Auto-generated method stub
		return removalTime;
	}

	public void setRemovalTime(long removalTime) {
		this.removalTime = removalTime;
	}

	public Entity getCurrentWeapon() {
		return currentWeapon;
	}

	public void setCurrentWeapon(Entity currentWeapon) {
		this.currentWeapon = currentWeapon;
	}

	public Entity getCurrentShield() {
		return currentShield;
	}

	public void setCurrentShield(Entity currentShield) {
		this.currentShield = currentShield;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
	
	
}
