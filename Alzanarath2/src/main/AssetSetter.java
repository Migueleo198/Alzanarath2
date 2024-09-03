package main;

import Entity.NpcOldMan;

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
}
