package Networking;

import java.net.Socket;

public class PlayerData {
    private String playerId;
    private String username;
    private int x, y;
    private String direction;
    private int spriteNum;
    private long timestamp;
    private int level;
    private boolean isAttacking;
    private int spriteCounter; // Add spriteCounter field
    private int invincibleCounter; // Add invincibleCounter field
    private Socket socket;
    // Constructor with spriteCounter and invincibleCounter
    public PlayerData(String playerId, String username, int x, int y, String direction, int spriteNum, long timestamp, int level, boolean isAttacking, int spriteCounter, int invincibleCounter) {
        this.playerId = playerId;
        this.username = username;
        this.x = x;
        this.y = y;
        this.direction = direction;
        this.spriteNum = spriteNum;
        this.timestamp = timestamp;
        this.level = level;
        this.isAttacking = isAttacking;
        this.spriteCounter = spriteCounter; // Initialize spriteCounter
        this.invincibleCounter = invincibleCounter; // Initialize invincibleCounter
    }

    

    // Getters and setters
    public String getPlayerId() {
        return playerId;
    }

    public void setPlayerId(String playerId) {
        this.playerId = playerId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public String getDirection() {
        return direction;
    }

    public void setDirection(String direction) {
        this.direction = direction;
    }

    public int getSpriteNum() {
        return spriteNum;
    }

    public void setSpriteNum(int spriteNum) {
        this.spriteNum = spriteNum;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public boolean isAttacking() {
        return isAttacking;
    }

    public void setAttacking(boolean isAttacking) {
        this.isAttacking = isAttacking;
    }

    public int getSpriteCounter() {
        return spriteCounter;
    }

    public void setSpriteCounter(int spriteCounter) {
        this.spriteCounter = spriteCounter;
    }

    public int getInvincibleCounter() {
        return invincibleCounter;
    }

    public void setInvincibleCounter(int invincibleCounter) {
        this.invincibleCounter = invincibleCounter;
    }



	public Socket getSocket() {
		return socket;
	}



	public void setSocket(Socket socket) {
		this.socket = socket;
	}
}
