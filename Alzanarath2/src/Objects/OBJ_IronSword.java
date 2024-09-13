package Objects;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import Entity.Entity;
import main.GamePanel;

public class OBJ_IronSword extends Entity{
	public static final String objName = "Iron Sword";
	public OBJ_IronSword(GamePanel GamePanel) {
		super(GamePanel);
		
		
		name = objName;
		down1 = setup("/Object/Sword.png",GamePanel.getTileSize(),GamePanel.getTileSize());
		type=type_Sword;
		attackArea.width=36;
		attackArea.height=36;
		attackValue=8;
		direction="down";
		level=1;
		
		
		setDescription("["+ name +"]\n"  + " A normal basic sword,\n perfect for newbie adventurers" +
				"Attack:" + attackValue 
						+ "     Level:" + level);
		
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
		g2.setColor(Color.white);
		int textWidth = g2.getFontMetrics().stringWidth(name);
		int textX = screenX + (gp.getTileSize() / 2) - (textWidth / 2);
		int textY = screenY - 5;
		
		g2.drawString(name, textX, textY);
		g2.drawImage(image, screenX, screenY, null);
		
		g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,1f));
		}
	}

	public int getAttackValue() {
	    return attackValue;
	}

	public void setAttackValue(int attackValue) {
	    this.attackValue = attackValue;
	}

	public int getLevel() {
	    return level;
	}

	public void setLevel(int level) {
	    this.level = level;
	}
	
	public static void changeDescription() {
	
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
