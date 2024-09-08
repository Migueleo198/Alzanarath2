package Objects;

import Entity.Entity;
import main.GamePanel;

public class OBJ_BloodSword extends Entity{
	public static final String objName = "Blood Sword";
	public OBJ_BloodSword(GamePanel GamePanel) {
		super(GamePanel);
		
		type=3;
		name = objName;
		down1 = setup("/Object/BloodSword_Icon2.png",GamePanel.getTileSize(),GamePanel.getTileSize());
		attackValue=15;
		
		level=1;
		
		
		
		
	}

	public int getAttackValue() {
	    return attackValue;
	}

	public void setAttackValue(int attackValue) {
	    this.attackValue = attackValue;
	}

	public int getLevel() {
	    return level;
	}

	public void setLevel(int level) {
	    this.level = level;
	}
	
	public static void changeDescription() {
	
	}

	@Override
	public String getMonsterId() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void hitMonster(String monsterId2, int attack2, int health2) {
		// TODO Auto-generated method stub
		
	}
	


}
