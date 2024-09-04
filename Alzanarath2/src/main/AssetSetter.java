package main;

import Entity.NpcOldMan;
import Monster.MON_Slime;

public class AssetSetter {
	GamePanel gp;
	
	public AssetSetter(GamePanel gp) {
		this.gp=gp;
		
	}
	
	public void setObject() {
		
	}
	
	public void setNpc() {
		
		gp.getNpc()[0] = new NpcOldMan(gp);
		gp.getNpc()[0].setWorldX(gp.getTileSize()*21);
		gp.getNpc()[0].setWorldY(gp.getTileSize()*21);
		gp.getNpc()[0].setName("Old Man");
	}
	
	public void setMonster() {
		gp.getMonster()[0] = new MON_Slime(gp);
		gp.getMonster()[0].setWorldX(gp.getTileSize()*23);
		gp.getMonster()[0].setWorldY(gp.getTileSize()*15);
		
		gp.getMonster()[1] = new MON_Slime(gp);
		gp.getMonster()[1].setWorldX(gp.getTileSize()*26);
		gp.getMonster()[1].setWorldY(gp.getTileSize()*15);
		
	}
}
