package main;

import Entity.Entity;

public class ColissionChecker {
	GamePanel gp;
	public ColissionChecker(GamePanel gp) {
		this.gp=gp;
	}
	
	public void checkTile(Entity entity) {
		int entityLeftWorldX = entity.getWorldX() + entity.solidArea.x;
		int entityRightWorldX = entity.getWorldX() + entity.solidArea.x + entity.solidArea.width;
		int entityTopWorldY = entity.getWorldY() + entity.solidArea.y;
		int entityBottomWorldY = entity.getWorldY() + entity.solidArea.y + entity.solidArea.height;
		
		int entityLeftCol = entityLeftWorldX/gp.getTileSize();
		int entityRightCol = entityRightWorldX/gp.getTileSize();
		int entityTopRow = entityTopWorldY/gp.getTileSize();
		int entityBottomRow = entityBottomWorldY/gp.getTileSize();
		
		//Only two tilenums since the entity will only be hitting collision with two borders or the rectangle at the same time
		int tileNum1, tileNum2;
		
		switch(entity.getDirection()) {
		case "up": entityTopRow = (entityTopWorldY - entity.getSpeed())/gp.getTileSize();
				   tileNum1 = gp.tileM.getMapTileNum()[entityLeftCol][entityTopRow];
				   tileNum2 = gp.tileM.getMapTileNum()[entityRightCol][entityTopRow];
				   if(gp.tileM.tile[tileNum1].isCollision() || gp.tileM.tile[tileNum2].isCollision()) {
					   entity.collisionOn=true;
				   }
		break;
		case "down": 
			entityBottomRow = (entityBottomWorldY + entity.getSpeed())/gp.getTileSize();
			   tileNum1 = gp.tileM.getMapTileNum()[entityLeftCol][entityBottomRow];
			   tileNum2 = gp.tileM.getMapTileNum()[entityRightCol][entityBottomRow];
			   if(gp.tileM.tile[tileNum1].isCollision() || gp.tileM.tile[tileNum2].isCollision()) {
				   entity.collisionOn=true;
			   }
			
			break;
		case "left": 
			entityLeftCol = (entityLeftWorldX - entity.getSpeed())/gp.getTileSize();
			   tileNum1 = gp.tileM.getMapTileNum()[entityLeftCol][entityTopRow];
			   tileNum2 = gp.tileM.getMapTileNum()[entityLeftCol][entityBottomRow];
			   if(gp.tileM.tile[tileNum1].isCollision() || gp.tileM.tile[tileNum2].isCollision()) {
				   entity.collisionOn=true;
			   }
			
			break;
		case "right": 
			
			entityRightCol = (entityRightWorldX + entity.getSpeed())/gp.getTileSize();
			   tileNum1 = gp.tileM.getMapTileNum()[entityRightCol][entityTopRow];
			   tileNum2 = gp.tileM.getMapTileNum()[entityRightCol][entityBottomRow];
			   if(gp.tileM.tile[tileNum1].isCollision() || gp.tileM.tile[tileNum2].isCollision()) {
				   entity.collisionOn=true;
			   }
			
			break;
		}
	}
}
