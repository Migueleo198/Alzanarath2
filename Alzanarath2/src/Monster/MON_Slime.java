package Monster;

import java.awt.AlphaComposite;
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
	
	public MON_Slime(GamePanel gp){
		super(gp);
		name = "Blue Slime";
		speed=1;
		maxHealth = 120;
		setHealth(maxHealth);
		attack=5;
		type=2;
		solidArea.x=3;
		solidArea.y=18;
		solidArea.width=42;
		solidArea.height=30;
		solidAreaDefaultX = solidArea.x;
		solidAreaDefaultY = solidArea.y;
		direction="down";
		getModel();
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
		
		 if (invincible) {
		        invincibleCounter++;
		        if (invincibleCounter > 40) {
		            invincible = false;
		            invincibleCounter = 0;
		        }
		    }
		if(invincible==true) {
			g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,0.4f));
		}
		
		// THE ENTITY POSITION WILL NEVER BE STATIC
		Font customFont = new Font("Comic Sans", Font.BOLD, 16);
		g2.setFont(customFont);
		
		
		
		int textWidth = g2.getFontMetrics().stringWidth(this.getName());
		int textX = screenX + (gp.getTileSize() / 2) - (textWidth / 2);
		int textY = screenY - 5;
		
		g2.setColor(new Color(0,0,0));
		if(gp.getMonster()[gp.getCurrentMonsterNum()]!=null) {
		g2.drawString(gp.getMonster()[gp.getCurrentMonsterNum()].getName(), textX+1, textY+1);
		
		
		g2.setColor(Color.RED);
		g2.drawString(gp.getMonster()[gp.getCurrentMonsterNum()].getName(), textX, textY);
		g2.drawImage(image, screenX, screenY, gp.getTileSize(), gp.getTileSize(), null);
		
		g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,1f));
		}
		}
	}
}
