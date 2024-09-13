package Objects;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import Entity.Entity;
import main.GamePanel;

public class OBJ_WoodenShield extends Entity{
	
		public static final String objName = "Wooden Shield";
		public OBJ_WoodenShield(GamePanel GamePanel) {
			super(GamePanel);
			type=3;
			name=objName;
			down1=setup("/Object/WoodShield.png",GamePanel.getTileSize(),GamePanel.getTileSize());
			defenseValue=3;
			
			description="["+ name +"] \n" + "An old wodden Shield \n thrown  away  because of its \n low durability";
		
			
			
			
			
			
			
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
