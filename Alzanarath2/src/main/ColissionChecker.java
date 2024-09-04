package main;

import Entity.Entity;

public class ColissionChecker {
	GamePanel gp;

	public ColissionChecker(GamePanel gp) {
		this.gp = gp;
	}
	
	public void checkTile(Entity entity) {
		
		int entityLeftWorldX = entity.getWorldX() + entity.solidArea.x;
		int entityRightWorldX = entity.getWorldX() + entity.solidArea.x + entity.solidArea.width;
		int entityTopWorldY = entity.getWorldY() + entity.solidArea.y;
		int entityBottomWorldY = entity.getWorldY() + entity.solidArea.y + entity.solidArea.height;

		int entityLeftCol = entityLeftWorldX / gp.getTileSize();
		int entityRightCol = entityRightWorldX / gp.getTileSize();
		int entityTopRow = entityTopWorldY / gp.getTileSize();
		int entityBottomRow = entityBottomWorldY / gp.getTileSize();

		// Only two tilenums since the entity will only be hitting collision with two
		// borders or the rectangle at the same time
		int tileNum1, tileNum2;

		switch (entity.getDirection()) {
		case "up":
			entityTopRow = (entityTopWorldY - entity.getSpeed()) / gp.getTileSize();
			tileNum1 = gp.getTileM().getMapTileNum()[entityLeftCol][entityTopRow];
			tileNum2 = gp.getTileM().getMapTileNum()[entityRightCol][entityTopRow];
			if (gp.getTileM().tile[tileNum1].isCollision() || gp.getTileM().tile[tileNum2].isCollision()) {
				entity.collisionOn = true;
			}
			break;
		case "down":
			entityBottomRow = (entityBottomWorldY + entity.getSpeed()) / gp.getTileSize();
			tileNum1 = gp.getTileM().getMapTileNum()[entityLeftCol][entityBottomRow];
			tileNum2 = gp.getTileM().getMapTileNum()[entityRightCol][entityBottomRow];
			if (gp.getTileM().tile[tileNum1].isCollision() || gp.getTileM().tile[tileNum2].isCollision()) {
				entity.collisionOn = true;
			}

			break;
		case "left":
			entityLeftCol = (entityLeftWorldX - entity.getSpeed()) / gp.getTileSize();
			tileNum1 = gp.getTileM().getMapTileNum()[entityLeftCol][entityTopRow];
			tileNum2 = gp.getTileM().getMapTileNum()[entityLeftCol][entityBottomRow];
			if (gp.getTileM().tile[tileNum1].isCollision() || gp.getTileM().tile[tileNum2].isCollision()) {
				entity.collisionOn = true;
			}

			break;
		case "right":

			entityRightCol = (entityRightWorldX + entity.getSpeed()) / gp.getTileSize();
			tileNum1 = gp.getTileM().getMapTileNum()[entityRightCol][entityTopRow];
			tileNum2 = gp.getTileM().getMapTileNum()[entityRightCol][entityBottomRow];
			if (gp.getTileM().tile[tileNum1].isCollision() || gp.getTileM().tile[tileNum2].isCollision()) {
				entity.collisionOn = true;
			}

			break;
		}
	}

	
	
	public int checkObject(Entity entity, boolean player) {
		int index=999;
		for(int i = 0; i < gp.getNpc().length;i++) {
			
			//Get entity solid area position
			entity.solidArea.x = entity.getWorldX() + entity.solidArea.x;
			entity.solidArea.y = entity.getWorldY() + entity.solidArea.y;
			
			//get the npc solid area position
			gp.getNpc()[i].solidArea.x = gp.getNpc()[i].getWorldX() + gp.getNpc()[i].solidArea.x;
			gp.getNpc()[i].solidArea.y = gp.getNpc()[i].getWorldY() + gp.getNpc()[i].solidArea.y;
			
			switch(entity.getDirection()) {
			case "up": entity.solidArea.y -= entity.getSpeed(); 
			if(entity.solidArea.intersects(gp.getNpc()[i].solidArea)) {
				System.out.println("up collision");
			}
			break;
			case "down": entity.solidArea.y += entity.getSpeed(); 
			if(entity.solidArea.intersects(gp.getNpc()[i].solidArea)) {
				System.out.println("down collision");
			}break;
			case "left": entity.solidArea.x -= entity.getSpeed();
			if(entity.solidArea.intersects(gp.getNpc()[i].solidArea)) {
				System.out.println("left collision");
			}break;
			case "right":  entity.solidArea.x += entity.getSpeed(); 
			if(entity.solidArea.intersects(gp.getNpc()[i].solidArea)) {
				System.out.println("right collision");
			}break;
			}
			
			entity.solidArea.x = entity.getSolidAreaDefaultX();
			entity.solidArea.y = entity.getSolidAreaDefaultY();
			
			gp.getNpc()[i].solidArea.x = gp.getNpc()[i].getSolidAreaDefaultX();
			gp.getNpc()[i].solidArea.y = gp.getNpc()[i].getSolidAreaDefaultY();
		}
		
		
		return index;
	}
	
	public int checkEntity(Entity entity, Entity[] target) {
		int index=999;
		for(int i = 0; i < target.length;i++) {
			if(target[i]!=null) {
			//Get entity solid area position
			entity.solidArea.x = entity.getWorldX() + entity.solidArea.x;
			entity.solidArea.y = entity.getWorldY() + entity.solidArea.y;
			
			//get the npc solid area position
			target[i].solidArea.x = target[i].getWorldX() + target[i].solidArea.x;
			target[i].solidArea.y = target[i].getWorldY() + target[i].solidArea.y;
			
			switch(entity.getDirection()) {
			case "up": entity.solidArea.y -= entity.getSpeed(); 
			break;
			case "down": entity.solidArea.y += entity.getSpeed(); 
			
			break;
			case "left": entity.solidArea.x -= entity.getSpeed();
			
			break;
			case "right":  entity.solidArea.x += entity.getSpeed(); 
			
			break;
			}
	
		if(entity.solidArea.intersects(target[i].solidArea)) {
			if(target[i]!=entity) {
			entity.collisionOn=true;
			index = i;
			}
		}
			
			entity.solidArea.x = entity.getSolidAreaDefaultX();
			entity.solidArea.y = entity.getSolidAreaDefaultY();
			
			target[i].solidArea.x = target[i].getSolidAreaDefaultX();
			target[i].solidArea.y = target[i].getSolidAreaDefaultY();
		}
		}
		
		
		return index;
	}
	
	public boolean checkPlayer(Entity entity) {
		
		boolean contactPlayer = false;
	
		if(gp.player!=null) {
			//Get entity solid area position
			entity.solidArea.x = entity.getWorldX() + entity.solidArea.x;
			entity.solidArea.y = entity.getWorldY() + entity.solidArea.y;
			
			//get the npc solid area position
			gp.player.solidArea.x = gp.player.getWorldX() + gp.player.solidArea.x;
			gp.player.solidArea.y = gp.player.getWorldY() + gp.player.solidArea.y;
			
			switch(entity.getDirection()) {
			case "up": entity.solidArea.y -= entity.getSpeed(); 
			break;
			case "down": entity.solidArea.y += entity.getSpeed(); 
			break;
			case "left": entity.solidArea.x -= entity.getSpeed();
			break;
			case "right":  entity.solidArea.x += entity.getSpeed(); 
			break;
			}
			
			if(entity.solidArea.intersects(gp.player.solidArea)) {
				entity.collisionOn=true;
				contactPlayer = true;
				
			}
			
			entity.solidArea.x = entity.getSolidAreaDefaultX();
			entity.solidArea.y = entity.getSolidAreaDefaultY();
			
			gp.player.solidArea.x = gp.player.getSolidAreaDefaultX();
			gp.player.solidArea.y = gp.player.getSolidAreaDefaultY();
			
			
	}
		return contactPlayer;
	}
}
