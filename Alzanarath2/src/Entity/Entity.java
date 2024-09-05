package Entity;

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
	protected String usernamePlayer;
	protected int worldX;
	protected int worldY;
	protected int speed;
	protected String name;
	protected BufferedImage up1,up2,down1,down2,left1,left2,right1,right2;
	protected BufferedImage attackUp1,attackUp2,attackDown1,attackDown2,attackLeft1,attackLeft2,attackRight1,attackRight2;
	protected String direction;
	protected int actionLockCounter=0;
	protected int spriteCounter=0;
	protected int spriteCounter2=0;
	protected int spriteNum=1;
	protected int solidAreaDefaultX;
	protected int solidAreaDefaultY;
	protected int level = 1;
	protected int attack=0;
	protected int type; //1= player 2 = slime
	
	//ATTACK ANIMATION VAR
	protected boolean attacking=false;
	
	public Rectangle solidArea = new Rectangle(0,0,48,48);
	
	//Hit params
	protected boolean invincible = false;
	protected int invincibleCounter = 0;
	
	
	
	
	public boolean collisionOn =false;
	
	GamePanel gp;
	
	protected int maxHealth;
	protected int Health;
	
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
	
	public void update(){
		setAction();
		
		collisionOn=false;
		gp.getcChecker().checkTile(this);
		gp.getcChecker().checkEntity(this,gp.getNpc());
		gp.getcChecker().checkEntity(this,gp.getMonster());
		boolean contactPlayer = gp.getcChecker().checkPlayer(this);
		
		
		
		if(this.type==2 && contactPlayer==true) {
			
			if(gp.getPlayer().Health>=0) {
				
				if(gp.getPlayer().invincible==false) {
					
					gp.getPlayer().Health-=this.getAttack();
					gp.getPlayer().invincible=true;
						
					
				}
				
			}
		}
		
		
		spriteCounter++;

		if (spriteCounter > 10) {
			if (spriteNum == 1) {
				spriteNum = 2;
			} else if (spriteNum == 2) {
				spriteNum = 1;
			}
			spriteCounter = 0;
		}
		
		// IF COLLISION IS FALSE THE NPC CAN MOVE
					if (collisionOn == false) {
						switch (direction) {
						case "up":
							worldY -= speed;
							break;
						case "down":
							worldY += speed;
							break;
						case "left":
							worldX -= speed;
							break;
						case "right":
							worldX += speed;
							break;
						}
					}
				
				
				
	}
	
	public void getNpcImage() {
		
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

				// THE ENTITY POSITION WILL NEVER BE STATIC
				Font customFont = new Font("Comic Sans", Font.BOLD, 16);
				g2.setFont(customFont);
				g2.setColor(Color.ORANGE);
				int textWidth = g2.getFontMetrics().stringWidth(gp.getNpc()[gp.getCurrentNpcNum()].getName());
				int textX = screenX + (gp.getTileSize() / 2) - (textWidth / 2);
				int textY = screenY - 5;
				
				g2.drawString(gp.getNpc()[gp.getCurrentNpcNum()].getName(), textX, textY);
				g2.drawImage(image, screenX, screenY, null);
				
				
			
	}
		}

	// [ This is the entity username ]
	
	public String getUsername() {
		return usernamePlayer;
	}
	
	public void setUsername(String usernamePlayer) {
		this.usernamePlayer = usernamePlayer;
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
	
}
