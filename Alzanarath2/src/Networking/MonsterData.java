package Networking;

public class MonsterData {
	private String monsterId;
	private String name;
	private int speed;
	private int Health;
	private int maxHealth;
	private int attack;
	private String direction;
	private int spriteNum;
	private int worldX;
	private int worldY;
	public MonsterData(String monsterId,int worldX, int worldY, String name, int speed, int health, int maxHealth, int attack, String direction, int spriteNum) {
		super();
		this.worldY=worldY;
		this.worldX=worldX;
		this.name = name;
		this.speed = speed;
		Health = health;
		this.maxHealth = maxHealth;
		this.attack = attack;
		this.direction = direction;
		this.spriteNum = spriteNum;
	}
	
	public MonsterData(int health) {
		super();
		
		this.Health = health;
		
	}
	
	public String getName() {
		return name;
	}
	private void setName(String name) {
		this.name = name;
	}
	public int getSpeed() {
		return speed;
	}
	private void setSpeed(int speed) {
		this.speed = speed;
	}
	public int getHealth() {
		return Health;
	}
	private void setHealth(int health) {
		Health = health;
	}
	public int getMaxHealth() {
		return maxHealth;
	}
	private void setMaxHealth(int maxHealth) {
		this.maxHealth = maxHealth;
	}
	public int getAttack() {
		return attack;
	}
	private void setAttack(int attack) {
		this.attack = attack;
	}
	public String getDirection() {
		return direction;
	}
	private void setDirection(String direction) {
		this.direction = direction;
	}
	public int getSpriteNum() {
		return spriteNum;
	}
	private void setSpriteNum(int spriteNum) {
		this.spriteNum = spriteNum;
	}

	public int getWorldY() {
		return worldY;
	}

	public void setWorldY(int worldY) {
		this.worldY = worldY;
	}

	public int getWorldX() {
		return worldX;
	}

	public void setWorldX(int worldX) {
		this.worldX = worldX;
	}

	public String getMonsterId() {
		return monsterId;
	}

	public void setMonsterId(String monsterId) {
		this.monsterId = monsterId;
	}
	
	
	
	
}
