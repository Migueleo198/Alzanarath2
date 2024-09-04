package UI;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Rectangle;

import main.GamePanel;

public class UI {
	//Command nums for cursor
	private int commandNum=0;
	public int getCommandNum() {
		return commandNum;
	}

	public void setCommandNum(int commandNum) {
		this.commandNum = commandNum;
	}

	GamePanel gp;
	public UI (GamePanel gp) {
		this.gp = gp;
	}
	
	public void drawUI(Graphics2D g2) {
		if (gp.getGameState() == gp.getTitleState()) {
			drawTitleScreen(g2);
		}
			
	    if (gp.getGameState() == gp.getPlayState()) {
	    	  g2.setFont(new Font("Comic Sans", Font.BOLD,30));
	    	  g2.setColor(Color.white);
	    	  
	    	  g2.setColor(new Color(0,0,0,120));
	    	  g2.fillRoundRect(56, 26, 180, 75, 5, 5);
	    	  g2.setColor(new Color(0,0,0,225));
	    	  g2.fillRoundRect(50, 20, 180, 75, 5, 5);
	    	  
	    	  g2.setColor(new Color(255,255,255,200));
	    	  g2.drawString("Level " + gp.getPlayer().getLevel(), 55 ,50);
	    	  drawHealthBar(g2);
	    	  }
		}
	
	
	public void drawTitleScreen(Graphics2D g2) {
		int middleX=gp.getScreenWidth()/2;
		int middleY=gp.getScreenHeight()/2;
		
		g2.setFont(new Font("Comic Sans", Font.BOLD,30));
		
		g2.setColor(Color.white);
		
		g2.drawString("Host Game", middleX-70,middleY-50);
		g2.drawString("Join Game", middleX-70,middleY);
		if(commandNum==0) {
		g2.drawString(">",middleX-90,middleY-50);
		}
		if (commandNum==1) {
		g2.drawString(">",middleX-90,middleY);
		}
	}
	
	 public void drawHealthBar(Graphics2D g2) {
	    	String Health = " HP: ";
	    	g2.setFont(g2.getFont().deriveFont(Font.PLAIN,20));
	 		g2.drawString(Health, 50, 82);
	 		g2.setColor(Color.DARK_GRAY);
	 		g2.fillRect(95, 65,gp.getPlayer().getMaxHealth(), 20);
	 		
	 		
	 		g2.setColor(Color.white);
	 		g2.setStroke(new BasicStroke(1));
	 		
	 		g2.fillRect(93,63,gp.getPlayer().getHealth()+4,20+4);
	 		
	 		g2.setColor(Color.red);
	 		g2.fillRect(95,65,gp.getPlayer().getHealth(),20);
	 		
	 		
	 		
	 		
	 	}
}
