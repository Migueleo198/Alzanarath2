package Entity;

import java.io.IOException;

import javax.imageio.ImageIO;

import Inputs.KeyHandler;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import main.GamePanel;

public class Player extends Entity{
	GamePanel gp;
	KeyHandler keyH;
	
	private final int screenX;
	private final int screenY;

	public Player(GamePanel gp, KeyHandler keyH) {
		this.gp=gp;
		this.keyH=keyH;
		setDefaultParams();
		getPlayerModel();
		
		screenX = (gp.getScreenWidth()/2)-gp.getTileSize()/2;
		screenY = (gp.getScreenHeight()/2)-gp.getTileSize()/2;
	}
	
	public void setDefaultParams() {
		worldX =100;
		worldY= 200;
	    speed = 4;
	    direction = "down";
	}
	
	public void update() {
		if(keyH.isUpPressed()==true || keyH.isDownPressed()==true || keyH.isLeftPressed()==true || keyH.isRightPressed()==true) {
		
		if(keyH.isUpPressed()==true) {
			direction="up";
			worldY-=speed;
		}
		
		else if(keyH.isDownPressed()==true) {
			worldY+=speed;
			direction="down";
		}
		
		else if(keyH.isLeftPressed()==true) {
			worldX-=speed;
			direction="left";
		}
		
		else if(keyH.isRightPressed()==true) {
			worldX+=speed;
			direction="right";
			
			
		}
		
		spriteCounter++;
		
		if(spriteCounter>10) {
			if(spriteNum== 1) {
				spriteNum=2;
			}
			else if (spriteNum== 2) {
				spriteNum=1;
				}
			spriteCounter = 0;
			}
		}
	}
	
	public void draw(Graphics2D g2) {
		BufferedImage image = null;
		
		switch(direction) {
		case "up":
			if(spriteNum==1) {
			image = up1;}
			if(spriteNum==2) {
			image = up2;
			}
			break;
		case "down": 
			if(spriteNum==1) {
			image = down1;
			}
			if(spriteNum==2) {
			image = down2;
			}break;
			
		case "left": 
			if(spriteNum==1) {
			image = left1;}
			if(spriteNum==2) {
			image = left2;}
			break;
		case "right": 
			if(spriteNum==1) {
			image = right1;}
			if(spriteNum==2) {
			image = right2;}break;
		}
		
		
		
		//THE PLAYER POSITION WILL ALWAYS BE STATIC, ITS THE MAP WHICH MOVES NOT THE PLAYER!
		g2.drawImage(image,screenX,screenY,gp.getTileSize(),gp.getTileSize(),null);
		
		
	}
	
	public void getPlayerModel() {
		try {
			up1 = ImageIO.read(getClass().getResourceAsStream("/Player/SpritesJava(up).png"));
			up2 = ImageIO.read(getClass().getResourceAsStream("/Player/SpritesJava(up2).png"));
			down1 = ImageIO.read(getClass().getResourceAsStream("/Player/SpritesJava(down).png"));
			down2 = ImageIO.read(getClass().getResourceAsStream("/Player/SpritesJava(down2).png"));
			left1 = ImageIO.read(getClass().getResourceAsStream("/Player/SpritesJava(left).png"));
			left2 = ImageIO.read(getClass().getResourceAsStream("/Player/SpritesJava(left2).png"));
			right1= ImageIO.read(getClass().getResourceAsStream("/Player/SpritesJava(right).png"));
			right2 = ImageIO.read(getClass().getResourceAsStream("/Player/SpritesJava(right2).png"));
		}catch(IOException e) {
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