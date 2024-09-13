package Objects;

import Entity.Entity;
import main.GamePanel;

public class OBJ_WoodenChest extends Entity {
	GamePanel gp;
	public OBJ_WoodenChest(GamePanel gp) {
		super(gp);
		
		name = "Wooden Chest";
		down1= setup("/Object/WoodenTreasureChestClosed.png", gp.getTileSize(), gp.getTileSize());
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
