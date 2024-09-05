package Networking;
public class PlayerData {
    private String playerId;
    private String username; // New field to store the player's username
    private int x, y;
    private String direction;
    private int spriteNum;
    private long timestamp;
    private int level;

    // Constructor with username
    public PlayerData(String playerId, String username, int x, int y, String direction, int spriteNum, long timestamp, int level) {
        this.playerId = playerId;
        this.username = username; // Initialize the username
        this.x = x;
        this.y = y;
        this.direction = direction;
        this.spriteNum = spriteNum;
        this.timestamp = timestamp;
        this.level = level;
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
}