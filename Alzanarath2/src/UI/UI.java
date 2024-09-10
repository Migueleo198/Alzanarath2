package UI;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
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

    private List<String> globalChatMessages = new ArrayList<>(); // To store all chat messages

    GamePanel gp;
    
    private ArrayList<String> message = new ArrayList<>();
    private ArrayList<Integer> messageCounter = new ArrayList<>();
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
            g2.setColor(new Color(0, 0, 0, 225));
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
            
            
        } else if (gp.getGameState()==gp.getCharacterState()) {
        	drawStatusScreen();
        } else if(gp.getNetworkManager()!=null && gp.getNetworkManager().isServer()==true) {
        	drawServerScreen(g2);
        }
       
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
   	 g2.setFont(new Font("Comic Sans", Font.BOLD, 30));
   	 String valueName;
   	 valueName =String.valueOf(gp.getPlayer().getUsernamePlayer());
   	 g2.drawString(gp.getPlayer().getUsernamePlayer(), textX, textY);
   	 textY+=lineHeight+10;
   	 
   	 g2.setColor(Color.white);
   	 g2.setFont(g2.getFont().deriveFont(23F));
   	 
   	 g2.drawString("Level", textX, textY);
   	 textY+=lineHeight;
   	 g2.drawString("Health", textX, textY);
   	 textY+=lineHeight;
   	 g2.drawString("Strength", textX, textY);
   	 textY+=lineHeight;
   	 g2.drawString("Dexterity", textX, textY);
   	 textY+=lineHeight;
   	 g2.drawString("Attack", textX, textY);
   	 textY+=lineHeight;
   	 g2.drawString("Defense", textX, textY);
   	 textY+=lineHeight;
   	 g2.drawString("Exp", textX, textY);
   	 textY+=lineHeight;
   	 g2.drawString("Next Level", textX, textY);
   	 textY+=lineHeight;
   	 g2.drawString("Gold", textX, textY);
   	 textY+=lineHeight;
   	 g2.drawString("Weapon", textX, textY);
   	 textY+=lineHeight;
   	 g2.drawString("Shield", textX, textY);
   	 textY+=lineHeight;
   	 
   	 //STAT VALUES
   	 int tailX = (frameX + frameWidth)-30;
   	 //Reset Text Y
   	 textY = frameY + gp.getTileSize();
   	 String value;
   	 
   	 value =String.valueOf(gp.getPlayer().getLevel());
   	 textX = getXforAlignToRightText(value, tailX);
   	 g2.drawString(value, tailX-75, textY+45);
   	 textY+=lineHeight;
   	 
   	 value =String.valueOf(gp.getPlayer().getHealth() + "/" + gp.getPlayer().getMaxHealth());
   	 textX = getXforAlignToRightText(value, tailX);
   	 g2.drawString(value, tailX-75, textY+45);
   	 textY+=lineHeight;
   	 
   	 value =String.valueOf(gp.getPlayer().getStrength());
   	 textX = getXforAlignToRightText(value, tailX);
   	 g2.drawString(value, tailX-75, textY+45);
   	 textY+=lineHeight;
   	 
   	 
   	 value =String.valueOf(gp.getPlayer().getDexterity());
   	 textX = getXforAlignToRightText(value, tailX);
   	 g2.drawString(value, tailX-75, textY+45);
   	 textY+=lineHeight;
   	 
   	 value =String.valueOf(gp.getPlayer().getAttack());
   	 textX = getXforAlignToRightText(value, tailX);
   	 g2.drawString(value, tailX-75, textY+45);
   	 textY+=lineHeight;
   	 
   	 value =String.valueOf(gp.getPlayer().getDefense());
   	 textX = getXforAlignToRightText(value, tailX);
   	 g2.drawString(value, tailX-75, textY+45);
   	 textY+=lineHeight;
   	 
   	 value =String.valueOf(gp.getPlayer().getExp());
   	 textX = getXforAlignToRightText(value, tailX);
   	 g2.drawString(value, tailX-75, textY+45);
   	 textY+=lineHeight;
   	 
   	 value =String.valueOf(gp.getPlayer().getNextLevelExp());
   	 textX = getXforAlignToRightText(value, tailX);
   	 g2.drawString(value, tailX-75, textY+45);
   	 textY+=lineHeight;
   	 
   	 value =String.valueOf(gp.getPlayer().getGold());
   	 textX = getXforAlignToRightText(value, tailX);
   	 g2.drawString(value, tailX-75, textY+45);
   	 textY+=lineHeight +5;
   	 
   	 //values
   	
   	 g2.drawImage(gp.getPlayer().currentWeapon.getDown1(), tailX-gp.getTileSize()-20,textY,null);
   	 textY += gp.getTileSize();
   	 
   	 g2.drawImage(gp.getPlayer().currentShield.getDown1(), tailX-gp.getTileSize()-20,textY,null);
   	 textY += gp.getTileSize();
   	 
    }
    
    public void drawSubWindow(int x, int y, int width, int height) {
   	 //Color one, color two, color three, opacity(transparency)
   	 Color color = new Color(0,0,0,220);
   	 g2.setColor(color);
   	 g2.fillRect(x, y, width, height);
   	 
   	 color = new Color(255,255,255);
   	 g2.setColor(color);
   	 g2.setStroke(new BasicStroke(5));
   	 g2.drawRect(x+5, y+5, width-10, height-10);
   	 
   	 
   	 
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

        g2.setFont(new Font("Comic Sans", Font.PLAIN, 20));
        g2.setColor(new Color(0, 0, 0));
       
        g2.fillRect(chatX, chatY, chatWidth, chatHeight);
        g2.setColor(Color.white);

        int y = chatY + 20;
        for (String message : chatMessages) {
            g2.drawString(message, chatX + 10, y);
            y += 20; // Move down for each new line
        }

        g2.drawString(currentMessage, chatX + 10, chatY + 40); // Draw the current message being typed
    }

    private void drawGlobalChat(Graphics2D g2) {
        int chatX = 50;
        int chatY = gp.getScreenHeight() - (globalChatVisible ? 200 : 20); // Position depends on visibility
        int chatWidth = 400;
        int chatHeight = globalChatVisible ? 180 : 100; // Adjust height based on visibility

        g2.setColor(new Color(0, 0, 0, 150));
        g2.fillRect(chatX, chatY, chatWidth, chatHeight);
        g2.setColor(Color.black);
        g2.setStroke(new BasicStroke(5));
        g2.drawRect(chatX, chatY, chatWidth + 2, chatHeight + 2);
        g2.setFont(new Font("Comic Sans", Font.PLAIN, 20));
        g2.setColor(Color.white);

        int y = chatY + 20;
        // Display only the last 5 messages
        List<String> lastMessages = getGlobalChatMessages().size() > 5 
                                    ? getGlobalChatMessages().subList(getGlobalChatMessages().size() - 5, getGlobalChatMessages().size()) 
                                    : getGlobalChatMessages();

        for (String message : lastMessages) {
            g2.drawString(message, chatX + 10, y);
            y += 20; // Move down for each new line
            if (y > chatY + chatHeight - 20) { // Prevent drawing outside the visible area
                break;
            }
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
        g2.drawString("Email", middleX - 245, middleY - 25);
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
        String Health = " HP: ";
        g2.setFont(g2.getFont().deriveFont(Font.PLAIN, 20));
        g2.drawString(Health, 50, 82);
        g2.setColor(Color.DARK_GRAY);
        g2.fillRect(95, 65, gp.getPlayer().getMaxHealth(), 20);

        g2.setColor(Color.white);
        g2.setStroke(new BasicStroke(1));

        g2.fillRect(93, 63, gp.getPlayer().getHealth() + 4, 20 + 4);

        g2.setColor(Color.red);
        g2.fillRect(95, 65, gp.getPlayer().getHealth(), 20);
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

	
}


