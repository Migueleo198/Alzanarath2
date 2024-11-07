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

public class UI {
    private int commandNum = 0;
    private List<String> chatMessages = new ArrayList<>();
    private String currentMessage = "";
    Graphics2D g2;
    private boolean chatVisible = false;
    private boolean globalChatVisible = false; // Flag to control global chat visibility
    
    private boolean emailFieldFocused = false;
    private boolean passwordFieldFocused = false;
    private boolean usernameFieldFocused = false;
    private String usernameInput = "";
    private String emailInput = "";
    private String passwordInput = "";
    protected boolean isDead;
    protected boolean test;
    private List<String> globalChatMessages = new ArrayList<>(); // To store all chat messages

    GamePanel gp;
    
    	// In your UI class
    private int selectedSkillIndex = 0; // Index of the currently selected skill
    public boolean[] unlockedSkills = {false, false, false}; // Track whether skills are unlocked
    
    private ArrayList<String> message = new ArrayList<>();
    private ArrayList<Integer> messageCounter = new ArrayList<>();
	public String playerUsername;
	
	//INVENTORY SLOTS VARS
	
	private int slotCol=0;
	private int slotRow=0;
	
	
	//Alerts
	 ArrayList<String> Alert = new ArrayList<>();
     @SuppressWarnings("unchecked")
	  ArrayList<Integer> AlertCounter = new ArrayList<>();
	
	
    public UI(GamePanel gp) {
        this.gp = gp;
    }
    
    
    
    
    
    public void drawUI(Graphics2D g2) {
    	 this.g2 = g2;
    	 
    	 if(gp.getGameState() == gp.getScreenState()) {
    		 drawJoinWorld(g2);
    	 }
    	 if(gp.getGameState() == gp.getTitleState()) {
    		 drawTitleScreen(g2);
    	 }
    	 
    	 if (gp.getGameState() == gp.getSkillTreeState()) {
    		    drawSkillTree(g2);
    		}
    	 
    	 
    	 else if(gp.getGameState() == gp.getRegisterState()) {
    		 drawRegisterAccount(g2);
    	 } else if(gp.getGameState() == gp.getLoginState()){
    		 drawLoginAccount(g2);
    	 }else if (gp.getGameState() == gp.getTitleState() && !gp.isServer==true) {
            drawTitleScreen(g2);
        } else if (gp.getGameState() == gp.getPlayState() && gp.getPlayer()!=null) {
            g2.setFont(new Font("Comic Sans", Font.BOLD, 30));
            g2.setColor(Color.white);

            g2.setColor(new Color(0, 0, 0, 120));
            g2.fillRoundRect(56, 26, 180, 75, 5, 5);
            g2.setColor(new Color(0, 0, 0, 230));
            g2.fillRoundRect(50, 20, 180, 75, 5, 5);

            g2.setColor(new Color(255, 255, 255, 200));
            g2.drawString("Level " + gp.getPlayer().getLevel(), 55, 50);
            drawHealthBar(g2);
            drawAlert();
            if (globalChatVisible) { // Draw global chat if visible
                drawGlobalChat(g2);
            }

            if (chatVisible) {
                drawChat(g2);
            }
            
            
        } else if (gp.getGameState()==gp.getCharacterState()) {
        	drawStatusScreen();
        	drawInventory();
        } else if(gp.getNetworkManager()!=null && gp.getNetworkManager().isServer()==true) {
        	drawServerScreen(g2);
        }
    	 
    	 if(gp.getNetworkManager()!=null && gp.getGameState()==gp.getPlayState() && gp.getNetworkManager().isServer()) {
    		 drawServerScreen(g2);
    	 }
       
    }
    
   
    public void drawAlert() {
    	  
    	  int messageX= gp.getTileSize();
    	  int messageY=  gp.getTileSize()*5;
    	  g2.setFont(g2.getFont().deriveFont(Font.BOLD, 20f));
    	  
    	  for (int i= 0; i < Alert.size(); i++) {
    		  if(Alert.get(i)!=null) {
    			  g2.setColor(Color.black);
    			  g2.drawString(Alert.get(i), messageX+2, messageY+2);
    			  
    			  
    			  g2.setColor(Color.white);
    			  g2.drawString(Alert.get(i), messageX, messageY);
    			  int counter = AlertCounter.get(i)+1;
    			  AlertCounter.set(i,counter);
    			  messageY+=50;
    			  
    			  if (AlertCounter.get(i)>180) {
    				  Alert.remove(i);
    				  AlertCounter.remove(i);
    			  }
    		  }
    	  }
      }
    
    public void addAlert(String text) {
    	
  	  Alert.add(text);
  	  AlertCounter.add(0);
    }
    
    
    public void drawMessage() {
    	int messageX = gp.getTileSize();
    	int messageY = gp.getTileSize()*4;
    	
    	g2.setFont(new Font("Comic Sans", Font.BOLD, 32));
    	
    	for(int i=0; i< message.size();i++) {
    		if(message.get(i)!=null ) {
    			g2.setColor(Color.white);
    			g2.drawString(message.get(i), messageX, messageY);
    			
    			int counter = messageCounter.get(i)+1;
    			messageCounter.set(i, counter);
    		}
    	}
    	
    }
    
    public void addBattleNotification(String text) {
		message.add(text);
		
		messageCounter.add(0);
		
		drawMessage();
		
	}
    
    public void drawInventory() {
        
        // FRAME
        int frameX = gp.getTileSize() * 9;
        int frameY = gp.getTileSize() * 3 - 10;
        int frameWidth = gp.getTileSize() * 6;
        int frameHeight = gp.getTileSize() * 5;

        // Draw Inventory title box
        drawSubWindow(frameX, frameY - 100, frameWidth, gp.getTileSize() * 2);
        
        // Set medieval-style font and color for the "Inventory" label
        g2.setFont(new Font("Serif", Font.BOLD, 30)); // Gothic/serif font for medieval feel
        g2.setColor(new Color(200, 200, 200)); // Off-white text
        g2.drawString("Inventory", frameX + 75, frameY - 50);

        // Draw main inventory window
        drawSubWindow(frameX, frameY, frameWidth, frameHeight);

        // SLOTS
        final int slotXStart = frameX + 20;
        final int slotYStart = frameY + 20;
        int slotX = slotXStart;
        int slotY = slotYStart;
        int slotSize = gp.getTileSize()+3;

        // DRAW PLAYER ITEMS
        for (int i = 0; i < gp.getPlayer().getInventory().size(); i++) {
        	
        	
        	//EQUIP CURSOR
        	if(gp.getPlayer().getInventory().get(i)==gp.getPlayer().getCurrentWeapon() 
        			|| gp.getPlayer().getInventory().get(i)==gp.getPlayer().getCurrentShield()) {
        		g2.setColor(new Color(240,190,90));
        		g2.fillRoundRect(slotX,slotY,gp.getTileSize(),gp.getTileSize(),10,10);
        	}
        	
            g2.drawImage(gp.getPlayer().getInventory().get(i).getDown1(), slotX, slotY, null);
            slotX += gp.getTileSize()+2;

            if (i == 4 || i == 9 || i == 14) {
                slotX = slotXStart;
                slotY += slotSize;
            }
        }

        // CURSOR
        int cursorX = slotXStart + (slotSize * getSlotCol())-1;
        int cursorY = slotYStart + (slotSize * getSlotRow());
        int cursorWidth = gp.getTileSize();
        int cursorHeight = gp.getTileSize();

        // Outer border of the cursor: Dark gray to give it a medieval, iron look
        g2.setColor(new Color(50, 50, 50)); // Dark gray
        g2.setStroke(new BasicStroke(3));
        g2.drawRoundRect(cursorX, cursorY, cursorWidth, cursorHeight, 10, 10); // Rounded corners

        // Inner border for glow effect: subtle dark glow to give the cursor some depth
        g2.setColor(new Color(100, 100, 100, 120)); // Dark gray with transparency
        g2.setStroke(new BasicStroke(2));
        g2.drawRoundRect(cursorX + 2, cursorY + 2, cursorWidth - 4, cursorHeight - 4, 8, 8);

        // Optional: Add a glowing effect around the cursor for emphasis
        g2.setColor(new Color(150, 0, 0, 150)); // Dark red glow for a mysterious, medieval look
        g2.setStroke(new BasicStroke(1));
        g2.drawRoundRect(cursorX - 2, cursorY - 2, cursorWidth + 4, cursorHeight + 4, 12, 12);

        // ITEM DESCRIPTION FRAME
        int dFrameX = frameX;
        int dFrameY = frameY + frameHeight + 10;
        int dFrameWidth = frameWidth;
        int dFrameHeight = gp.getTileSize() * 3 + 20;

        // DRAW ITEM DESCRIPTION TEXT
        int textX = dFrameX + 20;
        int textY = dFrameY + gp.getTileSize() - 10;

        g2.setFont(g2.getFont().deriveFont(14F)); // Smaller font for descriptions

        int itemIndex = getItemIndexOnSlot();

        if (itemIndex < gp.getPlayer().getInventory().size()) {
            // Draw description window
            drawSubWindow(dFrameX, dFrameY, dFrameWidth, dFrameHeight);

            // Draw each line of item description
            for (String line : gp.getPlayer().getInventory().get(itemIndex).getDescription().split("\n")) {
                g2.setColor(new Color(200, 200, 200)); // Off-white text for readability
                g2.drawString(line, textX, textY);
                textY += 28;
            }
        }
    }

    
    public void drawStatusScreen() {
        
        final int frameX = gp.getTileSize();
        final int frameY = gp.getTileSize() - 20;
        final int frameWidth = gp.getTileSize() * 6;
        final int frameHeight = gp.getTileSize() * 11 + 5;
        
        // Draw dark, medieval-style sub-window
        drawSubWindow(frameX, frameY, frameWidth, frameHeight);

        // Text settings
        int textX = frameX + 20;
        int textY = frameY + gp.getTileSize();
        int lineHeight = 38;
        
        // Names
        g2.setFont(new Font("Serif", Font.BOLD, 30)); // Gothic/Serif font for medieval feel
        String valueName;
        g2.setColor(new Color(200, 200, 200)); // Off-white for text to contrast with the dark background
        valueName = String.valueOf(gp.getPlayer().getUsernamePlayer());
        g2.drawString(gp.getPlayer().getUsernamePlayer(), textX, textY);
        textY += lineHeight + 10;

        // Set smaller font for other stats
        g2.setFont(g2.getFont().deriveFont(23F));

        // Draw stat names in a muted, dark color
        g2.setColor(new Color(150, 150, 150)); // Dark gray color for stat labels
        g2.drawString("Level", textX, textY);
        textY += lineHeight;
        g2.drawString("Health", textX, textY);
        textY += lineHeight;
        g2.drawString("Strength", textX, textY);
        textY += lineHeight;
        g2.drawString("Dexterity", textX, textY);
        textY += lineHeight;
        g2.drawString("Attack", textX, textY);
        textY += lineHeight;
        g2.drawString("Defense", textX, textY);
        textY += lineHeight;
        g2.drawString("Exp", textX, textY);
        textY += lineHeight;
        g2.drawString("Next Level", textX, textY);
        textY += lineHeight;
        g2.drawString("Gold", textX, textY);
        textY += lineHeight;
        g2.drawString("Weapon", textX, textY);
        textY += lineHeight;
        g2.drawString("Shield", textX, textY);
        textY += lineHeight;

        // STAT VALUES
        int tailX = (frameX + frameWidth) - 30;
        textY = frameY + gp.getTileSize(); // Reset textY

        String value;

        g2.setColor(new Color(200, 200, 200)); // Off-white for the stat values

        value = String.valueOf(gp.getPlayer().getLevel());
        textX = getXforAlignToRightText(value, tailX);
        g2.drawString(value, tailX - 75, textY + 45);
        textY += lineHeight;

        value = String.valueOf(gp.getPlayer().getHealth() + "/" + gp.getPlayer().getMaxHealth());
        textX = getXforAlignToRightText(value, tailX);
        g2.drawString(value, tailX - 75, textY + 45);
        textY += lineHeight;

        value = String.valueOf(gp.getPlayer().getStrength());
        textX = getXforAlignToRightText(value, tailX);
        g2.drawString(value, tailX - 75, textY + 45);
        textY += lineHeight;

        value = String.valueOf(gp.getPlayer().getDexterity());
        textX = getXforAlignToRightText(value, tailX);
        g2.drawString(value, tailX - 75, textY + 45);
        textY += lineHeight;

        value = String.valueOf(gp.getPlayer().getAttack());
        textX = getXforAlignToRightText(value, tailX);
        g2.drawString(value, tailX - 75, textY + 45);
        textY += lineHeight;

        value = String.valueOf(gp.getPlayer().getDefense());
        textX = getXforAlignToRightText(value, tailX);
        g2.drawString(value, tailX - 75, textY + 45);
        textY += lineHeight;

        value = String.valueOf(gp.getPlayer().getExp());
        textX = getXforAlignToRightText(value, tailX);
        g2.drawString(value, tailX - 75, textY + 45);
        textY += lineHeight;

        value = String.valueOf(gp.getPlayer().getNextLevelExp());
        textX = getXforAlignToRightText(value, tailX);
        g2.drawString(value, tailX - 75, textY + 45);
        textY += lineHeight;

        value = String.valueOf(gp.getPlayer().getGold());
        textX = getXforAlignToRightText(value, tailX);
        g2.drawString(value, tailX - 75, textY + 45);
        textY += lineHeight + 5;

        // Draw Weapon and Shield Icons
        g2.drawImage(gp.getPlayer().getCurrentWeapon().getDown1(), tailX - gp.getTileSize() - 20, textY, null);
        textY += gp.getTileSize();
        g2.drawImage(gp.getPlayer().getCurrentShield().getDown1(), tailX - gp.getTileSize() - 20, textY, null);
        textY += gp.getTileSize();
    }

    
    public void drawSubWindow(int x, int y, int width, int height) {
        // Dark background for window body (slightly transparent for depth)
        Color backgroundColor = new Color(20, 20, 20, 220); // Dark gray, 220 transparency
        g2.setColor(backgroundColor);
        g2.fillRoundRect(x, y, width, height, 25, 25); // Rounded corners for a refined medieval look
        
        // Dark border color, resembling aged iron or dark stone
        Color borderColor = new Color(50, 50, 50); // Dark gray/iron-like tone
        g2.setColor(borderColor);
        g2.setStroke(new BasicStroke(5));
        g2.drawRoundRect(x+5, y+5, width-10, height-10, 20, 20); // Matching rounded corners
        
        // Subtle inner glow effect for highlighting the edges (still dark but provides depth)
        Color innerGlow = new Color(100, 100, 100, 120); // Dark gray, subtle glow
        g2.setColor(innerGlow);
        g2.setStroke(new BasicStroke(3));
        g2.drawRoundRect(x+8, y+8, width-16, height-16, 15, 15); // Slightly smaller inner border
        
        // Optional: add subtle dark decorative patterns or corner elements for a gothic touch
        // You can implement textures or patterns for more detailing if desired
    }
    
    public int getXforCenteredText(String text) {
    	int length = (int)g2.getFontMetrics().getStringBounds(text,g2).getWidth();
    	int x = gp.getScreenWidth()/2 - length/2;
    	
    	return x;
    }
    
    public int getXforAlignToRightText(String text,int tailX) {
    	int length = (int)g2.getFontMetrics().getStringBounds(text,g2).getWidth();
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
        g2.drawString(currentMessage, chatX +120, chatY + chatHeight - 20);
    }

    private void drawGlobalChat(Graphics2D g2) {
        int chatX = 50;
        int chatY = gp.getScreenHeight() - (globalChatVisible ? 200 : 20);
        int chatWidth = 400;
        int chatHeight = globalChatVisible ? 180 : 100;

        // Dark background for the chat area
        g2.setColor(new Color(25, 25, 25,240));
        g2.fillRect(chatX, chatY, chatWidth, chatHeight);

        // White border around the chat window
        g2.setColor(new Color(50,50,50,220));
        g2.setStroke(new BasicStroke(4));
        g2.drawRect(chatX, chatY, chatWidth, chatHeight);

        // Display last 5 chat messages
        g2.setFont(new Font("Roboto", Font.PLAIN, 16));
        g2.setColor(new Color(220, 220, 220));
        List<String> lastMessages = getGlobalChatMessages().size() > 4
                ? getGlobalChatMessages().subList(getGlobalChatMessages().size() - 4, getGlobalChatMessages().size())
                : getGlobalChatMessages();

        int y = chatY + 40;
        for (String message : lastMessages) {
            g2.drawString(message, chatX + 20, y);
            y += 25;
            if (y > chatY + chatHeight - 20) break;
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
    
    private void drawJoinWorld(Graphics2D g2) {
        int middleX = gp.getScreenWidth() / 2;
        int middleY = gp.getScreenHeight() / 2;
        
        g2.setFont(new Font("Comic Sans", Font.BOLD, 48));    
        g2.setColor(Color.white);
        
        g2.drawString("Alzanarath 2", middleX - 135, middleY - 50);
        
        g2.setFont(new Font("Comic Sans", Font.BOLD, 30));

        if (commandNum == 0) {
            g2.setColor(Color.yellow);
            g2.drawString(">", middleX - 100, middleY + 75);
        } else {
            g2.setColor(Color.white);
        }
        g2.drawString("Join World", middleX - 70, middleY + 75);

        if (commandNum == 1) {
            g2.setColor(Color.yellow);
            g2.drawString(">", middleX - 145, middleY + 120);
        } else {
            g2.setColor(Color.white);
        }
        g2.drawString("Delete system32", middleX - 115, middleY + 120);
    }
    
    private void drawLoginAccount(Graphics2D g2) {
        int middleX = gp.getScreenWidth() / 2;
        int middleY = gp.getScreenHeight() / 2;
        
        g2.setFont(new Font("Comic Sans", Font.BOLD, 48));    
        g2.setColor(Color.white);
        
        g2.drawString("Login to your account", middleX - 240, middleY - 150);
        
        g2.setFont(new Font("Comic Sans", Font.BOLD, 30));
        
        // Draw "Email" label
        g2.setColor(commandNum == 0 ? Color.yellow : Color.white);
        g2.drawString("Username", middleX - 245, middleY - 25);
        g2.drawRect(middleX - 280, middleY - 55, 200, 40);

        // Draw rectangle for email input field
        g2.setColor(gp.ui.getEmailFocused() ? Color.yellow : Color.white);
        g2.drawRect(middleX - 70, middleY - 55, 325, 40);  // Draw next to "Email" label
        drawTextWithOverflow(g2, emailInput, middleX - 60, middleY - 25, 310, 40);

        // Draw "Password" label
        g2.setColor(commandNum == 1 ? Color.yellow : Color.white);
        g2.drawString("Password", middleX - 245, middleY + 25);
        g2.drawRect(middleX - 280, middleY - 5, 200, 40);

        // Draw rectangle for password input field
        g2.setColor(gp.ui.getPasswordFocused() ? Color.yellow : Color.white);
        g2.drawRect(middleX - 70, middleY - 5, 325, 40);  // Draw next to "Password" label
        drawTextWithOverflow(g2, passwordInput, middleX - 60, middleY + 25, 310, 40);

        // Draw "Login" button
        g2.setColor(commandNum == 2 ? Color.yellow : Color.white);
        g2.drawString("Login", middleX - 115, middleY + 95);

        // Draw "Register" button
        g2.setColor(commandNum == 3 ? Color.yellow : Color.white);
        g2.drawString("Register", middleX - 5, middleY + 95);

        // Draw additional instruction text
        g2.setFont(new Font("Comic Sans", Font.BOLD, 15));
        g2.setColor(Color.white);  // Ensure this text is always white
        g2.drawString("No account? Register a new account!", middleX + 95, middleY + 275);
    }
    
    private void drawRegisterAccount(Graphics2D g2) {
        int middleX = gp.getScreenWidth() / 2;
        int middleY = gp.getScreenHeight() / 2;
        
        
        
        g2.setFont(new Font("Comic Sans", Font.BOLD, 48));    
        g2.setColor(Color.white);
        
        g2.drawString("Register to your account", middleX - 300, middleY - 150);
        
        g2.setFont(new Font("Comic Sans", Font.BOLD, 30));

        // Draw "Username" label
        g2.setColor(commandNum == 0 ? Color.yellow : Color.white);
        g2.drawString("Username", middleX - 245, middleY - 25);
        g2.drawRect(middleX - 280, middleY - 55, 200, 40);

        // Draw rectangle for email input field
        g2.setColor(gp.ui.getUsernameFocused() ? Color.yellow : Color.white);
        g2.drawRect(middleX - 70, middleY - 55, 325, 40);  // Draw next to "Email" label
        drawTextWithOverflow(g2, usernameInput, middleX - 60, middleY - 25, 310, 40);
        
        // Draw "Email" label
        g2.setColor(commandNum == 1 ? Color.yellow : Color.white);
        g2.drawString("Email", middleX - 245, middleY + 25);
        g2.drawRect(middleX - 280, middleY - 5, 200, 40);

        // Draw rectangle for email input field
        g2.setColor(gp.ui.getEmailFocused() ? Color.yellow : Color.white);
        g2.drawRect(middleX - 70, middleY - 5, 325, 40);  // Draw next to "Email" label
        drawTextWithOverflow(g2, emailInput, middleX - 60, middleY + 25, 310, 40);

        g2.setColor(commandNum == 2 ? Color.yellow : Color.white);
        g2.drawString("Password", middleX - 245, middleY + 75);
        g2.drawRect(middleX - 280, middleY + 45, 200, 40);

        // Draw rectangle for password input field
        g2.setColor(gp.ui.getPasswordFocused() ? Color.yellow : Color.white);
        g2.drawRect(middleX - 70, middleY + 45, 325, 40);  // Draw next to "Password" label
        drawTextWithOverflow(g2, passwordInput, middleX - 60, middleY + 75, 310, 40);

        // Draw "Login" button
        g2.setColor(commandNum == 3 ? Color.yellow : Color.white);
        g2.drawString("Login", middleX - 115, middleY +125);

        // Draw "Register" button
        g2.setColor(commandNum == 4 ? Color.yellow : Color.white);
        g2.drawString("Register", middleX - 5, middleY + 125);

        // Draw additional instruction text
        g2.setFont(new Font("Comic Sans", Font.BOLD, 15));
        g2.setColor(Color.white);  // Ensure this text is always white
        g2.drawString("Already have an account? Login now!", middleX + 75, middleY + 275);
    }

    private void drawTextWithOverflow(Graphics2D g2, String text, int x, int y, int width, int height) {
        FontMetrics fm = g2.getFontMetrics();
        int textWidth = fm.stringWidth(text);
        
        // Truncate text if it exceeds the width of the input field
        if (textWidth > width) {
            String truncatedText = text;
            while (textWidth > width && truncatedText.length() > 0) {
                truncatedText = truncatedText.substring(1);
                textWidth = fm.stringWidth(truncatedText);
            }
            g2.drawString(truncatedText, x, y);
        } else {
            g2.drawString(text, x, y);
        }
    }

    public void drawHealthBar(Graphics2D g2) {
    	
    	int barWidth=100;
    	
    	 int currentHealth = gp.getPlayer().getHealth();
         int maxHealth = gp.getPlayer().getMaxHealth();

         // Calculate the percentage of health remaining
         float healthPercentage = (float) currentHealth / maxHealth;

         // Calculate the width of the health bar based on the percentage
         int healthBarWidth = (int) (barWidth * healthPercentage);
         
         
        String Health = " HP: ";
        g2.setFont(g2.getFont().deriveFont(Font.PLAIN, 20));
        g2.drawString(Health, 50, 82);
        
        g2.setColor(Color.DARK_GRAY);
        g2.fillRect(95, 65, 110, 20);

        g2.setColor(Color.white);
        g2.setStroke(new BasicStroke(1));

        g2.fillRect(93, 63, healthBarWidth + 4, 20 + 4);

        g2.setColor(Color.red);
        g2.fillRect(95, 65, healthBarWidth, 20);
    }
    
    //DRAWS THE SKILL TREE
    
    public void drawSkillTree(Graphics2D g2) {
        int screenWidth = gp.getWidth(); // Screen width
        int screenHeight = gp.getHeight(); // Screen height

        int treeWidth = 600;
        int treeHeight = 400;
        
        // Calculate the center of the screen for the skill tree
        int treeX = (screenWidth - treeWidth) / 6; // Centered horizontally
        int treeY = (screenHeight - treeHeight) / 6; // Centered vertically

        // Create a dark medieval-style background
        g2.setPaint(new GradientPaint(treeX, treeY, new Color(20, 20, 20, 200), 
                                       treeX + treeWidth, treeY + treeHeight, new Color(10, 10, 10, 255)));
        g2.fillRoundRect(treeX, treeY, treeWidth, treeHeight, 20, 20); // Rounded corners

        // Draw metallic border with emboss effect
        g2.setColor(new Color(150, 150, 150)); // Light gray for metallic look
        g2.setStroke(new BasicStroke(4)); // Thicker stroke for prominence
        g2.drawRoundRect(treeX, treeY, treeWidth, treeHeight, 20, 20);

        // Inner shadow for depth
        g2.setColor(new Color(0, 0, 0, 120)); // Darker shadow color
        g2.setStroke(new BasicStroke(1)); // Thin stroke for shadow
        g2.drawRoundRect(treeX + 2, treeY + 2, treeWidth - 4, treeHeight - 4, 20, 20);

        // Define positions of skill nodes relative to the tree's position
        int[][] skillPositions = {
            {treeX + 100, treeY + 100}, // Skill 1 position
            {treeX + 250, treeY + 150}, // Skill 2 position
            {treeX + 400, treeY + 200}  // Skill 3 position
        };

        // Draw lines connecting skills
        for (int i = 0; i < skillPositions.length - 1; i++) {
            int x1 = skillPositions[i][0] + 15; // Center x of skill i
            int y1 = skillPositions[i][1] + 15; // Center y of skill i
            int x2 = skillPositions[i + 1][0] + 15; // Center x of skill i+1
            int y2 = skillPositions[i + 1][1] + 15; // Center y of skill i+1

            // Change line color to green if the skill is unlocked
            g2.setColor(getUnlockedSkills()[i] ? new Color(0, 255, 0) : new Color(255, 255, 255)); // Green for unlocked, white for locked
            g2.setStroke(new BasicStroke(2));
            g2.drawLine(x1, y1, x2, y2);
        }

        // Draw skill nodes with medieval style
        for (int i = 0; i < skillPositions.length; i++) {
            int x = skillPositions[i][0];
            int y = skillPositions[i][1];

            // Shadow effect for nodes
            g2.setColor(new Color(0, 0, 0, 120)); // Darker shadow color
            g2.fillOval(x + 5, y + 5, 30, 30);

            // Skill node color
            g2.setColor(getUnlockedSkills()[i] ? new Color(0, 255, 0) : new Color(150, 150, 150)); // Green for unlocked, gray for locked
            g2.fillOval(x, y, 30, 30);

            // Draw selection highlight if skill is selected
            if (i == selectedSkillIndex) {
                g2.setColor(new Color(255, 215, 0)); // Gold for selected
                g2.setStroke(new BasicStroke(3));
                g2.drawOval(x, y, 30, 30);
            }
        }

        // Draw skill labels with medieval style
        g2.setColor(new Color(255, 255, 255)); // White for text
        g2.setFont(new Font("Garamond", Font.BOLD, 18)); // Gothic/Serif font
        g2.drawString("Atk Up +", skillPositions[0][0] - 15, skillPositions[0][1] - 10); // Skill 1 label
        g2.drawString("Def Up +", skillPositions[1][0] - 15, skillPositions[1][1] - 10); // Skill 2 label
        g2.drawString("Speed Up +", skillPositions[2][0] - 15, skillPositions[2][1] - 10); // Skill 3 label

        // Display skill points at the top of the tree
        g2.setColor(new Color(255, 255, 255)); // White for text
        g2.setFont(new Font("Garamond", Font.BOLD, 20)); // Gothic/Serif font
        g2.drawString("Skill Points: " + gp.getPlayer().getSkillPoints(), treeX + treeWidth / 2 - 60, treeY + 40);
    }
    
    


    
    public void unlockSelectedSkill() {
    	
    	
    	
        if (!getUnlockedSkills()[selectedSkillIndex]) {

            getUnlockedSkills()[selectedSkillIndex] = true;  // Unlock the selected skill 
        }
    }
    
    public int getItemIndexOnSlot() {
    	int itemIndex = slotCol + (slotRow*5);
    	return itemIndex;
    }
    
    public int getSelectedSkillIndex() {
        return selectedSkillIndex;
    }

    public void setSelectedSkillIndex(int selectedSkillIndex) {
        this.selectedSkillIndex = selectedSkillIndex;
    }

    public int getSkillCount() {
        return 2;  // Number of skill nodes
    }
    
    public int getCommandNum() {
        return commandNum;
    }

    public void setCommandNum(int commandNum) {
        this.commandNum = commandNum;
    }
    
    public boolean getUsernameFocused() {
        return usernameFieldFocused;
    }

    public void setUsernameFocused(boolean usernameFieldFocused) {
        this.usernameFieldFocused = usernameFieldFocused;
    }
    
    public boolean getEmailFocused() {
        return emailFieldFocused;
    }

    public void setEmailFocused(boolean emailFieldFocused) {
        this.emailFieldFocused = emailFieldFocused;
    }
    
    public boolean getPasswordFocused() {
        return passwordFieldFocused;
    }

    public void setPasswordFocused(boolean passwordFieldFocused) {
        this.passwordFieldFocused = passwordFieldFocused;
    }
    
    public void setUsernameInput(String usernameInput) {
        this.usernameInput = usernameInput;
    }

    public String getUsernameInput() {
        return usernameInput;
    }
    
    public void setEmailInput(String emailInput) {
        this.emailInput = emailInput;
    }

    public String getEmailInput() {
        return emailInput;
    }
    
    public void setPasswordInput(String passwordInput) {
        this.passwordInput = passwordInput;
    }

    public String getPasswordInput() {
        return passwordInput;
    }

    public void setCurrentMessage(String message) {
        currentMessage = message;
    }

    public String getCurrentMessage() {
        return currentMessage;
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

	public boolean[] getUnlockedSkills() {
		return unlockedSkills;
	}

	public void setUnlockedSkills(boolean[] unlockedSkills) {
		this.unlockedSkills = unlockedSkills;
	}
	public int getSlotRow() {
		return slotRow;
	}
	public void setSlotRow(int slotRow) {
		this.slotRow = slotRow;
	}
	public int getSlotCol() {
		return slotCol;
	}
	public void setSlotCol(int slotCol) {
		this.slotCol = slotCol;
	}

	
}


