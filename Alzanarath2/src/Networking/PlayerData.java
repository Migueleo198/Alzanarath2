package Networking;

public class PlayerData {
    private String playerId;
    private String username;
    private int x, y;
    private String direction;
    private int spriteNum;
    private long timestamp;
    private int level;
    private boolean isAttacking; // New field to store the attacking state

    // Constructor with attacking state
    public PlayerData(String playerId, String username, int x, int y, String direction, int spriteNum, long timestamp, int level, boolean isAttacking) {
        this.playerId = playerId;
        this.username = username;
        this.x = x;
        this.y = y;
        this.direction = direction;
        this.spriteNum = spriteNum;
        this.timestamp = timestamp;
        this.level = level;
        this.isAttacking = isAttacking; // Initialize the attacking state
    }

    // Getter for username
    public String getUsername() {
        return username;
    }

    // Add getter for the level
    public int getLevel() {
        return level;
    }

    // Getters and setters
    public int getSpriteNum() {
        return spriteNum;
    }

    public void setSpriteNum(int spriteNum) {
        this.spriteNum = spriteNum;
    }

    public String getPlayerId() {
        return playerId;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public String getDirection() {
        return direction;
    }

    public long getTimestamp() {
        return timestamp;
    }

    // Getter and setter for attacking state
    public boolean isAttacking() {
        return isAttacking;
    }

    public void setAttacking(boolean isAttacking) {
        this.isAttacking = isAttacking;
    }

	public boolean getIsAttacking() {
		// TODO Auto-generated method stub
		return isAttacking;
	}
	
	public void setIsAttacking(boolean isAttacking) {
		// TODO Auto-generated method stub
		this.isAttacking=isAttacking;
	}
}
