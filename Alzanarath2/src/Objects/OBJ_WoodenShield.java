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
		}
}
