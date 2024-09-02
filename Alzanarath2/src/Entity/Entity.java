package Entity;

import java.awt.image.BufferedImage;

public class Entity {
	protected int worldX;
	protected int worldY;
	protected int speed;
	
	protected BufferedImage up1,up2,down1,down2,left1,left2,right1,right2;
	protected String direction;
	
	protected int spriteCounter=0;
	public int spriteNum=1;
	
}
