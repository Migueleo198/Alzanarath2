package Monster;

import Entity.Entity;
import main.GamePanel;

public class MON_Slime extends Entity{
	GamePanel gp;
	public MON_Slime(GamePanel gp){
		super(gp);
		name = "Blue Slime";
		speed=1;
		maxLife = 4;
		life=maxLife;
		
	}
}
