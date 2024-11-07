package Entity;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Random;

import javax.imageio.ImageIO;

import main.GamePanel;

public class NpcOldMan extends Entity{
	String name= "OLD MAN(Lvl24)";
	public NpcOldMan(GamePanel gp) {
		super(gp);
		this.gp=gp;
		direction ="down";
		speed=1;
		
		
		
		
		
		collisionOn=true;
		
		getNpcImage();
		
	}
	
	
	
	public void getNpcImage() {
		try {
			up1 = ImageIO.read(getClass().getResourceAsStream("/Npcs/OldManUp1.png"));
			up2 = ImageIO.read(getClass().getResourceAsStream("/Npcs/OldManUp2.png"));
			down1 = ImageIO.read(getClass().getResourceAsStream("/Npcs/OldManDown1.png"));
			down2 = ImageIO.read(getClass().getResourceAsStream("/Npcs/OldManDown2.png"));
			left1 = ImageIO.read(getClass().getResourceAsStream("/Npcs/OldManLeft1.png"));
			left2 = ImageIO.read(getClass().getResourceAsStream("/Npcs/OldManLeft2.png"));
			right1 = ImageIO.read(getClass().getResourceAsStream("/Npcs/OldManRight1.png"));
			right2 = ImageIO.read(getClass().getResourceAsStream("/Npcs/OldManRight2.png"));
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
	
	public void draw(Graphics2D g2){
		
		int screenX = worldX - gp.getPlayer().getWorldX() + gp.getPlayer().getScreenX();
		int screenY = worldY - gp.getPlayer().getWorldY() + gp.getPlayer().getScreenY();
		
		if(worldX + gp.getTileSize() > gp.getPlayer().getWorldX()-gp.getPlayer().getScreenX()
				&& worldX - gp.getTileSize() < gp.getPlayer().getWorldX()+gp.getPlayer().getScreenX()
				&& worldY + gp.getTileSize()> gp.getPlayer().getWorldY()-gp.getPlayer().getScreenY()
				&& worldY - gp.getTileSize() < gp.getPlayer().getWorldY()+gp.getPlayer().getScreenY()) {
		
		// THE ENTITY POSITION WILL NEVER BE STATIC
		Font customFont = new Font("Comic Sans", Font.BOLD, 16);
		g2.setFont(customFont);
		g2.setColor(Color.ORANGE);
		int textWidth = g2.getFontMetrics().stringWidth(gp.getNpc()[gp.getCurrentNpcNum()].getName());
		int textX = screenX + (gp.getTileSize() / 2) - (textWidth / 2);
		int textY = screenY - 5;
		
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
		g2.setColor(new Color(0,0,0));
		g2.drawString(gp.getNpc()[gp.getCurrentNpcNum()].getName(), textX+1, textY+1);
		g2.setColor(Color.ORANGE);
		g2.drawString(gp.getNpc()[gp.getCurrentNpcNum()].getName(), textX, textY);
		g2.drawImage(image, screenX, screenY, gp.getTileSize(), gp.getTileSize(), null);
		}
	}



	@Override
	public String getMonsterId() {
		// TODO Auto-generated method stub
		return null;
	}



	@Override
	public void hitMonster(String monsterId2, int attack2, int health2) {
		// TODO Auto-generated method stub
		
	}
		
	
	
}
