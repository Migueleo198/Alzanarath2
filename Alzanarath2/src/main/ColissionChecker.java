package main;

import Entity.Entity;

public class ColissionChecker {
    private GamePanel gp;

    public ColissionChecker(GamePanel gp) {
        this.gp = gp;
    }

    private void updateSolidArea(Entity entity) {
        entity.solidArea.x = entity.getWorldX() + entity.solidArea.x;
        entity.solidArea.y = entity.getWorldY() + entity.solidArea.y;
    }

    private void resetSolidArea(Entity entity) {
        entity.solidArea.x = entity.getSolidAreaDefaultX();
        entity.solidArea.y = entity.getSolidAreaDefaultY();
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

        int tileNum1, tileNum2;

        switch (entity.getDirection()) {
            case "up":
                entityTopRow = (entityTopWorldY - entity.getSpeed()) / gp.getTileSize();
                tileNum1 = gp.getTileM().getMapTileNum()[gp.currentMap][entityLeftCol][entityTopRow];
                tileNum2 = gp.getTileM().getMapTileNum()[gp.currentMap][entityRightCol][entityTopRow];
                if (gp.getTileM().tile[tileNum1].isCollision() || gp.getTileM().tile[tileNum2].isCollision()) {
                    entity.collisionOn = true;
                }
                break;
            case "down":
                entityBottomRow = (entityBottomWorldY + entity.getSpeed()) / gp.getTileSize();
                tileNum1 = gp.getTileM().getMapTileNum()[gp.currentMap][entityLeftCol][entityBottomRow];
                tileNum2 = gp.getTileM().getMapTileNum()[gp.currentMap][entityRightCol][entityBottomRow];
                if (gp.getTileM().tile[tileNum1].isCollision() || gp.getTileM().tile[tileNum2].isCollision()) {
                    entity.collisionOn = true;
                }
                break;
            case "left":
                entityLeftCol = (entityLeftWorldX - entity.getSpeed()) / gp.getTileSize();
                tileNum1 = gp.getTileM().getMapTileNum()[gp.currentMap][entityLeftCol][entityTopRow];
                tileNum2 = gp.getTileM().getMapTileNum()[gp.currentMap][entityLeftCol][entityBottomRow];
                if (gp.getTileM().tile[tileNum1].isCollision() || gp.getTileM().tile[tileNum2].isCollision()) {
                    entity.collisionOn = true;
                }
                break;
            case "right":
                entityRightCol = (entityRightWorldX + entity.getSpeed()) / gp.getTileSize();
                tileNum1 = gp.getTileM().getMapTileNum()[gp.currentMap][entityRightCol][entityTopRow];
                tileNum2 = gp.getTileM().getMapTileNum()[gp.currentMap][entityRightCol][entityBottomRow];
                if (gp.getTileM().tile[tileNum1].isCollision() || gp.getTileM().tile[tileNum2].isCollision()) {
                    entity.collisionOn = true;
                }
                break;
        }
    }

public int checkObject(Entity entity, boolean player){
		
		
		int index = 999;
		
		for(int i = 0; i < gp.Objects.length; i++) {
			
			if (gp.Objects[i] != null) {
				//entity solid area pos
			entity.solidArea.x = entity.getWorldX() + entity.solidArea.x;
			entity.solidArea.y = entity.getWorldY() + entity.solidArea.y;
			
			gp.Objects[i].solidArea.x = gp.Objects[i].getWorldX() + gp.Objects[i].solidArea.x;
			gp.Objects[i].solidArea.y =gp.Objects[i].getWorldY() +gp.Objects[i].solidArea.y;
			
			switch(entity.getDirection()) {
			case "up":
				entity.solidArea.y = entity.solidArea.y - entity.getSpeed();
				if(entity.solidArea.intersects(gp.Objects[i].solidArea)){
					if (gp.Objects[i].collisionOn==true) {
						entity.collisionOn=true;
						if (player == true) {
							index=i;
							
						}
						
					}
				}
				break;
				
			case "down":
				entity.solidArea.y = entity.solidArea.y + entity.getSpeed(); break;
				
			case "left":
				entity.solidArea.x = entity.solidArea.x - entity.getSpeed(); break;
			
			case "right":
				entity.solidArea.x = entity.solidArea.x + entity.getSpeed(); break;
				
			}
			
			
			if(entity.solidArea.intersects(gp.Objects[i].solidArea)){
				if (gp.Objects[i].collisionOn==true) {
					entity.collisionOn=true;
				}
				
				
				if (player == true) {
					index=i;
					
			    }
					
			}
			
			entity.solidArea.x = entity.getSolidAreaDefaultX();
			entity.solidArea.y = entity.getSolidAreaDefaultY();
			gp.Objects[i].solidArea.x = gp.Objects[i].getSolidAreaDefaultX();
			gp.Objects[i].solidArea.y = gp.Objects[i].getSolidAreaDefaultY();
			
			
			}
			}
		
		
		return index;
			
		
	}

    public int checkEntity(Entity entity, Entity[] target) {
        int index = 999;
        for (int i = 0; i < target.length; i++) {
            if (target[i] != null) {
                updateSolidArea(entity);
                updateSolidArea(target[i]);

                switch (entity.getDirection()) {
                    case "up": entity.solidArea.y -= entity.getSpeed(); break;
                    case "down": entity.solidArea.y += entity.getSpeed(); break;
                    case "left": entity.solidArea.x -= entity.getSpeed(); break;
                    case "right": entity.solidArea.x += entity.getSpeed(); break;
                }

                if (entity.solidArea.intersects(target[i].solidArea) && target[i] != entity) {
                    entity.collisionOn = true;
                    index = i;
                }

                resetSolidArea(entity);
                resetSolidArea(target[i]);
            }
        }
        return index;
    }

    public boolean checkPlayer(Entity entity) {
        boolean contactPlayer = false;
        if (gp.player != null) {
            updateSolidArea(entity);
            updateSolidArea(gp.player);

            switch (entity.getDirection()) {
                case "up": entity.solidArea.y -= entity.getSpeed(); break;
                case "down": entity.solidArea.y += entity.getSpeed(); break;
                case "left": entity.solidArea.x -= entity.getSpeed(); break;
                case "right": entity.solidArea.x += entity.getSpeed(); break;
            }

            if (entity.solidArea.intersects(gp.player.solidArea)) {
                entity.collisionOn = true;
                contactPlayer = true;
            }

            resetSolidArea(entity);
            resetSolidArea(gp.player);
        }
        return contactPlayer;
    }
}
