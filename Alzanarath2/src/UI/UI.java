package UI;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.List;

import main.GamePanel;

public class UI {
    private int commandNum = 0;
    private List<String> chatMessages = new ArrayList<>();
    private String currentMessage = "";

    private boolean chatVisible = false;
    private boolean globalChatVisible = false; // Flag to control global chat visibility

    private List<String> globalChatMessages = new ArrayList<>(); // To store all chat messages

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
        if (gp.getGameState() == gp.getTitleState()) {
            drawTitleScreen(g2);
        }

        if (gp.getGameState() == gp.getPlayState()) {
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
        }
    }

    private void drawChat(Graphics2D g2) {
        int chatX = 50;
        int chatY = gp.getScreenHeight() - 70;
        int chatWidth = 400;
        int chatHeight = 50;

        g2.setFont(new Font("Comic Sans", Font.PLAIN, 20));
        g2.setColor(new Color(0, 0, 0));
        g2.fillRoundRect(chatX, chatY, chatWidth, chatHeight, 5, 5);
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

        g2.setFont(new Font("Comic Sans", Font.PLAIN, 20));
        g2.setColor(new Color(0, 0, 0, 150));
        g2.fillRoundRect(chatX, chatY, chatWidth, chatHeight, 5, 5);
        g2.setColor(Color.white);

        int y = chatY + 20;
        for (String message : globalChatMessages) {
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
        if (globalChatMessages.size() >= 10) { // Limit the number of messages
            globalChatMessages.remove(0); // Remove the oldest message
        }
        globalChatMessages.add(message); // Add the new message
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
}


