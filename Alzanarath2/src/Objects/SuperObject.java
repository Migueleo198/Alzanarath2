package Objects;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import main.GamePanel;

public class SuperObject {
	GamePanel gp;
	public BufferedImage image;
	protected int worldX,worldY;
	public void draw(Graphics2D g2, GamePanel gp) {
		int screenX = worldX - gp.getPlayer().getWorldX() + gp.getPlayer().getScreenX();
		int screenY = worldY - gp.getPlayer().getWorldY() + gp.getPlayer().getScreenY();
		
		if(worldX + gp.getTileSize() > gp.getPlayer().getWorldX()-gp.getPlayer().getScreenX()
				&& worldX - gp.getTileSize() < gp.getPlayer().getWorldX()+gp.getPlayer().getScreenX()
				&& worldY + gp.getTileSize()> gp.getPlayer().getWorldY()-gp.getPlayer().getScreenY()
				&& worldY - gp.getTileSize() < gp.getPlayer().getWorldY()+gp.getPlayer().getScreenY()) {
		g2.drawImage(image,screenX,screenY,gp.getTileSize(),gp.getTileSize(),null);
		}
	}
}
