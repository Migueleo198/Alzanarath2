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
	String name= "OLD MAN";
	public NpcOldMan(GamePanel gp) {
		super(gp);
		
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
	
		
	
	
}
