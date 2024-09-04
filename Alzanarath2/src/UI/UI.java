package UI;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;

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
	    	  g2.drawString("Player lvl = " + gp.getPlayer().getLevel(), 50 ,50);
	    	  
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
}
