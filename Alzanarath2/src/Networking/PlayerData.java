package Networking;
public class PlayerData {
    private String playerId;
    private int x;
    private int y;
    private String direction;
    private int spriteNum;
    private long timestamp;
    private int level; // New field to store the player's level

    public PlayerData(String playerId, int x, int y, String direction, int spriteNum, long timestamp, int level) {
        this.playerId = playerId;
        this.x = x;
        this.y = y;
        this.direction = direction;
        this.spriteNum = spriteNum;
        this.timestamp = timestamp;
        this.level = level; // Initialize the new level field
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
}