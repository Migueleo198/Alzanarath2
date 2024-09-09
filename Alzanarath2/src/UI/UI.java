package UI;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.List;

import main.GamePanel;
import main.main;

public class UI {
    private int commandNum = 0;
    private List<String> chatMessages = new ArrayList<>();
    private String currentMessage = "";
    private Graphics2D g2;
    private boolean chatVisible = false;
    private boolean globalChatVisible = false; // Flag to control global chat visibility
    private ArrayList<String> message = new ArrayList<>();
    ArrayList<Integer> messageCounter = new ArrayList<>();
    private List<String> globalChatMessages = new ArrayList<>(); // To store all chat messages
    private String truncatedText;
    
    public int getCommandNum() {
        return commandNum;
    }

    public void setCommandNum(int commandNum) {
        this.commandNum = commandNum;
    }

    GamePanel gp;

    public UI(GamePanel gp) {
        this.gp = gp;
        
    }

    public void drawUI(Graphics2D g2) {
    	 this.g2=g2;
        if (gp.getGameState() == gp.getTitleState() && !gp.isServer==true) {
            drawTitleScreen(g2);
        }

        if (gp.getGameState() == gp.getPlayState() && gp.getPlayer()!=null) {
            g2.setFont(new Font("Comic Sans", Font.BOLD, 30));
            g2.setColor(Color.white);

            g2.setColor(new Color(0, 0, 0, 120));
            g2.fillRoundRect(56, 26, 180, 75, 5, 5);
            g2.setColor(new Color(0, 0, 0, 255));
            g2.fillRoundRect(50, 20, 180, 75, 5, 5);

            g2.setColor(new Color(255, 255, 255, 200));
            g2.drawString("Level " + gp.getPlayer().getLevel(), 55, 50);
            drawHealthBar(g2);

            if (globalChatVisible) { // Draw global chat if visible
                drawGlobalChat(g2);
            }

            if (chatVisible) {
                drawChat(g2);
            }
            
            drawBattleNotification();
        }
        
        if(gp.getGameState()==gp.getCharacterState()) {
        	drawStatusScreen();
        }
        
        if(gp.getNetworkManager()!=null && gp.getNetworkManager().isServer()==true) {
        	drawServerScreen(g2);
        }
       
    }
    
    private void drawBattleNotification() {
    	
    	getG2().setFont(new Font("Comic Sans", Font.BOLD, 20));
    	
		int messageX = gp.getTileSize();
		int messageY = gp.getTileSize()*4;
		
		for(int i=0; i<message.size();i++) {
			if(message.get(i)!=null) {
				getG2().setColor(Color.white);
				getG2().drawString(message.get(i), messageX+2, messageY+2);
				
				int counter = messageCounter.get(i) +1;
				messageCounter.set(i, counter);
				messageY+=50;
			}
			
			if(messageCounter.get(i)>90) {
				message.remove(i);
				messageCounter.remove(i);
			}
		}
		
	}

	public void drawStatusScreen() {
   	 
   	 final int frameX = gp.getTileSize();
   	 final int frameY = gp.getTileSize()-20;
   	 final int frameWidth = gp.getTileSize()*6;
   	 final int frameHeight = gp.getTileSize()*11+5;
   	 drawSubWindow(frameX,frameY,frameWidth,frameHeight);
   	 
   	 //text
   	 
   	 int textX= frameX+20;
   	 int textY= frameY+gp.getTileSize();
   	 int lineHeight=38;
   	 
   	 
   	 //Names
   	 getG2().setFont(new Font("Comic Sans", Font.BOLD, 30));
   	 String valueName;
   	 valueName =String.valueOf(gp.getPlayer().getUsernamePlayer());
   	 getG2().drawString(gp.getPlayer().getUsernamePlayer(), textX, textY);
   	 textY+=lineHeight+10;
   	 
   	 getG2().setColor(Color.white);
   	 getG2().setFont(getG2().getFont().deriveFont(23F));
   	 
   	 getG2().drawString("Level", textX, textY);
   	 textY+=lineHeight;
   	 getG2().drawString("Health", textX, textY);
   	 textY+=lineHeight;
   	 getG2().drawString("Strength", textX, textY);
   	 textY+=lineHeight;
   	 getG2().drawString("Dexterity", textX, textY);
   	 textY+=lineHeight;
   	 getG2().drawString("Attack", textX, textY);
   	 textY+=lineHeight;
   	 getG2().drawString("Defense", textX, textY);
   	 textY+=lineHeight;
   	 getG2().drawString("Exp", textX, textY);
   	 textY+=lineHeight;
   	 getG2().drawString("Next Level", textX, textY);
   	 textY+=lineHeight;
   	 getG2().drawString("Gold", textX, textY);
   	 textY+=lineHeight;
   	 getG2().drawString("Weapon", textX, textY);
   	 textY+=lineHeight;
   	 getG2().drawString("Shield", textX, textY);
   	 textY+=lineHeight;
   	 
   	 //STAT VALUES
   	 int tailX = (frameX + frameWidth)-30;
   	 //Reset Text Y
   	 textY = frameY + gp.getTileSize();
   	 String value;
   	 
   	
   	 
   	
   	 
   	 value =String.valueOf(gp.getPlayer().getLevel());
   	 textX = getXforAlignToRightText(value, tailX);
   	 getG2().drawString(value, tailX-75, textY+45);
   	 textY+=lineHeight;
   	 
   	 value =String.valueOf(gp.getPlayer().getHealth() + "/" + gp.getPlayer().getMaxHealth());
   	 textX = getXforAlignToRightText(value, tailX);
   	 getG2().drawString(value, tailX-75, textY+45);
   	 textY+=lineHeight;
   	 
   	 value =String.valueOf(gp.getPlayer().getStrength());
   	 textX = getXforAlignToRightText(value, tailX);
   	 getG2().drawString(value, tailX-75, textY+45);
   	 textY+=lineHeight;
   	 
   	 
   	 value =String.valueOf(gp.getPlayer().getDexterity());
   	 textX = getXforAlignToRightText(value, tailX);
   	 getG2().drawString(value, tailX-75, textY+45);
   	 textY+=lineHeight;
   	 
   	 value =String.valueOf(gp.getPlayer().getAttack());
   	 textX = getXforAlignToRightText(value, tailX);
   	 getG2().drawString(value, tailX-75, textY+45);
   	 textY+=lineHeight;
   	 
   	 value =String.valueOf(gp.getPlayer().getDefense());
   	 textX = getXforAlignToRightText(value, tailX);
   	 getG2().drawString(value, tailX-75, textY+45);
   	 textY+=lineHeight;
   	 
   	 value =String.valueOf(gp.getPlayer().getExp());
   	 textX = getXforAlignToRightText(value, tailX);
   	 getG2().drawString(value, tailX-75, textY+45);
   	 textY+=lineHeight;
   	 
   	 value =String.valueOf(gp.getPlayer().getNextLevelExp());
   	 textX = getXforAlignToRightText(value, tailX);
   	 getG2().drawString(value, tailX-75, textY+45);
   	 textY+=lineHeight;
   	 
   	 value =String.valueOf(gp.getPlayer().getGold());
   	 textX = getXforAlignToRightText(value, tailX);
   	 getG2().drawString(value, tailX-75, textY+45);
   	 textY+=lineHeight +5;
   	 
   	 //values
   	 
   	 
   	 
   	 
   	 getG2().drawImage(gp.getPlayer().currentWeapon.getDown1(), tailX-gp.getTileSize()-20,textY,null);
   	 textY += gp.getTileSize();
   	 
   	 getG2().drawImage(gp.getPlayer().currentShield.getDown1(), tailX-gp.getTileSize()-20,textY,null);
   	 textY += gp.getTileSize();
   	 
    }
    
    public void drawSubWindow(int x, int y, int width, int height) {
   	 //Color one, color two, color three, opacity(transparency)
   	 Color color = new Color(0,0,0,220);
   	 getG2().setColor(color);
   	 getG2().fillRect(x, y, width, height);
   	 
   	 color = new Color(255,255,255);
   	 getG2().setColor(color);
   	 getG2().setStroke(new BasicStroke(5));
   	 getG2().drawRect(x+5, y+5, width-10, height-10);
   	 
   	 
   	 
    }
    
    public int getXforCenteredText(String text) {
    	int length = (int)getG2().getFontMetrics().getStringBounds(text,getG2()).getWidth();
    	int x = gp.getScreenWidth()/2 - length/2;
    	
    	return x;
    }
    
    public int getXforAlignToRightText(String text,int tailX) {
    	int length = (int)getG2().getFontMetrics().getStringBounds(text,getG2()).getWidth();
    	int x = tailX - length;
    	
    	return x;
    }

    private void drawChat(Graphics2D g2) {
        int chatX = 50;
        int chatY = gp.getScreenHeight() - 70;
        int chatWidth = 400;
        int chatHeight = 50;

        // Dark semi-transparent background for input area
        g2.setColor(new Color(30, 30, 30));
        g2.fillRoundRect(chatX, chatY - 150, chatWidth - 200, chatHeight - 10, 20, 20);

        // White border around the input area
        g2.setColor(new Color(50,50,50,220));
        g2.setStroke(new BasicStroke(4));
        g2.drawRoundRect(chatX, chatY - 150, chatWidth - 200, chatHeight - 10, 20, 20);

        // Global Chat header in white
        g2.setColor(Color.WHITE);
        g2.setFont(new Font("Roboto", Font.BOLD, 18));
        g2.drawString("Global Chat", chatX + 30, chatY - 125);

        // Dark background for message area
        g2.setColor(new Color(20, 20, 20, 200));
        g2.fillRect(chatX, chatY, chatWidth, chatHeight-300);

        // White border around the message area
        g2.setColor(new Color(50,50,50,220));
        g2.setStroke(new BasicStroke(4));
        g2.drawRect(chatX, chatY, chatWidth, chatHeight);
        
        // Draw each message with off-white text
        g2.setFont(new Font("Roboto", Font.PLAIN, 16));
        g2.setColor(new Color(100, 100, 100));
        
        g2.drawString("Type message: ", chatX+10, chatY+30);
        g2.setColor(new Color(220, 220, 220));
        int y = chatY + 20;
        for (String message : chatMessages) {
            g2.drawString(message, chatX + 30, y);
            y += 20;
        }

        // Display the current message being typed
        g2.setColor(Color.WHITE);
        
        FontMetrics fm = g2.getFontMetrics();
        int textWidth = fm.stringWidth(currentMessage);
        
        // Truncate text if it exceeds the width of the input field
        
            truncatedText = currentMessage;
            while (textWidth > chatWidth-100 && truncatedText.length() > 0) {
                truncatedText = truncatedText.substring(1);
                textWidth = fm.stringWidth(truncatedText);
            }
            g2.drawString(truncatedText, chatX +120, chatY + chatHeight - 20);
        
        	
        
    }
       
        
    

    private void drawGlobalChat(Graphics2D g2) {
        int chatX = 50;
        int chatY = gp.getScreenHeight() - (globalChatVisible ? 200 : 20);
        int chatWidth = 400;
        int chatHeight = globalChatVisible ? 180 : 100;

        // Dark background for the chat area
        g2.setColor(new Color(25, 25, 25, 240));
        g2.fillRect(chatX, chatY, chatWidth, chatHeight);

        // White border around the chat window
        g2.setColor(new Color(50, 50, 50, 220));
        g2.setStroke(new BasicStroke(4));
        g2.drawRect(chatX, chatY, chatWidth, chatHeight);

        // Set font and color for chat messages
        g2.setFont(new Font("Roboto", Font.PLAIN, 16));
        g2.setColor(new Color(220, 220, 220));

        // Get the last few chat messages based on the space available in the chat box
        List<String> lastMessages = getGlobalChatMessages().size() > 4
                ? getGlobalChatMessages().subList(getGlobalChatMessages().size() - 4, getGlobalChatMessages().size())
                : getGlobalChatMessages();

        // Adjust the y-position of each message and cut them if they exceed the visible height
        int y = chatY + 40;
        int lineHeight = g2.getFontMetrics().getHeight();
        for (String message : lastMessages) {
            if (message.length()>35) {
            	
               message=message.substring(0, 36);
            }
            if(gp.keyH.isWarningColor()==true) {
            g2.setColor(Color.RED);
            
            }
            else {
            	
            }
           
            g2.drawString(message, chatX + 20, y);
            
            y += 25;
        }
    }



    public void showChat() {
        if (gp.getGameState() == gp.getPlayState()) {
            chatVisible = true;
            globalChatVisible = true; // Ensure global chat is visible when opening chat
        }
    }

    public void hideChat() {
        chatVisible = false;
        globalChatVisible = false; // Hide global chat when chat is hidden
    }

    public void toggleChatVisibility() {
        if (gp.getGameState() == gp.getPlayState()) {
            chatVisible = !chatVisible;
            if (chatVisible) {
                globalChatVisible = true; // Show global chat when toggling chat visibility
            } else {
                globalChatVisible = false; // Hide global chat when toggling chat visibility
            }
        }
    }

    public boolean isChatVisible() {
        return chatVisible;
    }

    public void appendGlobalChatMessage(String message) {
        if (getGlobalChatMessages().size() >= 10) { // Limit the number of messages
            getGlobalChatMessages().remove(0); // Remove the oldest message
        }
        getGlobalChatMessages().add(message); // Add the new message
    }

    private void drawTitleScreen(Graphics2D g2) {
        int middleX = gp.getScreenWidth() / 2;
        int middleY = gp.getScreenHeight() / 2;

        g2.setFont(new Font("Comic Sans", Font.BOLD, 30));

        g2.setColor(Color.white);

        g2.drawString("Host Game", middleX - 70, middleY - 50);
        g2.drawString("Join Game", middleX - 70, middleY);
        if (commandNum == 0) {
            g2.drawString(">", middleX - 90, middleY - 50);
        }
        if (commandNum == 1) {
            g2.drawString(">", middleX - 90, middleY);
        }
    }
    
    private void drawServerScreen(Graphics2D g2) {
        int middleX = gp.getScreenWidth() / 2;
        int middleY = gp.getScreenHeight() / 2;

        g2.setFont(new Font("Comic Sans", Font.BOLD, 30));

        g2.setColor(Color.white);

        g2.drawString("Hosting Server", middleX - 70, middleY - 50);
        
    }

    public void drawHealthBar(Graphics2D g2) {
        String Health = " HP: ";
        
        
        int barWidth = 100; 
	    int barHeight = 12; // Height of the health bar
        int currentHealth = gp.getPlayer().Health; // Monster's current health
	    int maxHealth = gp.getPlayer().getMaxHealth();  // Monster's maximum health
	    int healthBarWidth = (int) ((double) currentHealth / maxHealth * barWidth);

	    // Draw the background of the health bar (dark gray)
	    
	    
	    
        g2.setFont(g2.getFont().deriveFont(Font.PLAIN, 20));
        g2.drawString(Health, 50, 82);
        g2.setColor(Color.DARK_GRAY);
        g2.fillRect(95, 65, barWidth, 20);

        g2.setColor(Color.white);
        g2.setStroke(new BasicStroke(1));

        g2.fillRect(93, 63, healthBarWidth + 4, 20 + 4);

        g2.setColor(Color.red);
        g2.fillRect(95, 65, healthBarWidth, 20);
    }

    public void setCurrentMessage(String message) {
        currentMessage = message;
    }

    public String getCurrentMessage() {
        return currentMessage;
    }
    
    public void addBattleNotification(String text) {
    	message.add(text);
    	
    	messageCounter.add(0);
    }
    
    public void setChatVisible(boolean chatVisible) {
        this.chatVisible = chatVisible;
    }
    
    public boolean isGlobalChatVisible() {
        return globalChatVisible;
    }

    public void setGlobalChatVisible(boolean globalChatVisible) {
        this.globalChatVisible = globalChatVisible;
    }

	public List<String> getGlobalChatMessages() {
		return globalChatMessages;
	}

	public void setGlobalChatMessages(List<String> globalChatMessages) {
		this.globalChatMessages = globalChatMessages;
	}

	public Graphics2D getG2() {
		return g2;
	}

	public void setG2(Graphics2D g2) {
		this.g2 = g2;
	}
}


