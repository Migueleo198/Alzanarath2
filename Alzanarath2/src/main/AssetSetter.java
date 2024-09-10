package main;

import Entity.Entity;
import Entity.NpcOldMan;
import Monster.MON_Slime;
import Networking.NetworkManager;

public class AssetSetter {
	GamePanel gp;
	NetworkManager networkManager;
	int respawnCounter;
	private int monsterCounter=0;
	int i=0;
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
		gp.monster[0].setWorldX(gp.getTileSize()*30);
		gp.monster[0].setWorldY(gp.getTileSize()*20);
		
		gp.monster[1] = new MON_Slime(gp);
		gp.monster[1].setWorldX(gp.getTileSize()*30);
		gp.monster[1].setWorldY(gp.getTileSize()*15);
		
		
		}
		
		
		
		
	}
	
	public void respawnMonsters() {
	    if (gp.networkManager != null && gp.networkManager.isServer()) {
	        long currentTime = System.currentTimeMillis();
	        
	        // Iterate through the list of removed monsters
	        for (int i = 0; i < gp.getRemovedMonsters().size(); i++) {
	            Entity removedMonster = gp.getRemovedMonsters().get(i); // Assuming RemovedMonster contains monster and time

	            long removalTime = removedMonster.getRemovalTime();

	            // Check if enough time (e.g., 20 seconds or 20000 ms) has passed since the monster was removed
	            if (currentTime - removalTime >= 20000) {
	                
	                // Find an empty slot in the monster array to respawn the monster
	                for (int j = 0; j < gp.monster.length; j++) {
	                    if (gp.monster[j] == null) {
	                        // Respawn the monster in the empty slot
	                        MON_Slime respawnedMonster = new MON_Slime(gp); // Or the appropriate monster class
	                        respawnedMonster.setMonsterId(removedMonster.getMonsterId()); // Restore original monster ID
	                        respawnedMonster.setWorldX(gp.getTileSize() * 30); // Set respawn position
	                        respawnedMonster.setWorldY(gp.getTileSize() * 15);

	                        gp.monster[j] = respawnedMonster; // Place the respawned monster into the game

	                        // Remove the monster from the removed list after respawning
	                        gp.getRemovedMonsters().remove(i);
	                        i--; // Adjust the index since we removed an element from the list

	                        System.out.println("Monster respawned with ID: " + respawnedMonster.getMonsterId() + " at index: " + j);
	                        break; // Exit the inner loop after respawning
	                    }
	                }
	            }
	        }

	        // Reset the respawn flag if no monsters are left to respawn
	        if (gp.getRemovedMonsters().isEmpty()) {
	            gp.respawn = false;
	        }
	    }
	}

	public int getMonsterCounter() {
		return monsterCounter;
	}

	public void setMonsterCounter(int monsterCounter) {
		this.monsterCounter = monsterCounter;
	}
}

