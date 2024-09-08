package main;

import Entity.NpcOldMan;
import Monster.MON_Slime;
import Networking.NetworkManager;

public class AssetSetter {
	GamePanel gp;
	NetworkManager networkManager;
	public AssetSetter(GamePanel gp,NetworkManager networkManager) {
		this.gp=gp;
		this.networkManager=networkManager;
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
		if(gp.networkManager!=null && networkManager.isServer()) {
		gp.monster[0] = new MON_Slime(gp);
		gp.monster[0].setWorldX(gp.getTileSize()*23);
		gp.monster[0].setWorldY(gp.getTileSize()*15);
		
		gp.monster[1] = new MON_Slime(gp);
		gp.monster[1].setWorldX(gp.getTileSize()*26);
		gp.monster[1].setWorldY(gp.getTileSize()*15);
		}
	}
}

