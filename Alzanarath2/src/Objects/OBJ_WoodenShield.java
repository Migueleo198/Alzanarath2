package Objects;

import Entity.Entity;
import main.GamePanel;

public class OBJ_WoodenShield extends Entity{
	
		public static final String objName = "Wooden Shield";
		public OBJ_WoodenShield(GamePanel GamePanel) {
			super(GamePanel);
			type=3;
			name=objName;
			down1=setup("/Object/WoodShield.png",GamePanel.getTileSize(),GamePanel.getTileSize());
			defenseValue=3;
			
			description="["+ name +"] \n" + "An old wodden Shield \n thrown  away  because of its \n low durability";
		
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
