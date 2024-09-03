package Networking;
public class PlayerData {
    private String playerId;
    private int x;
    private int y;
    private String direction;
    private int spriteNum; // Add this for animation state
    private long timestamp;

    public PlayerData(String playerId, int x, int y, String direction, int spriteNum, long timestamp) {
        this.playerId = playerId;
        this.x = x;
        this.y = y;
        this.direction = direction;
        this.spriteNum = spriteNum;
        this.timestamp = timestamp;
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