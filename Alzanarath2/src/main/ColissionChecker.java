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
        int index = 999;
        for (int i = 0; i < gp.getNpc().length; i++) {
            updateSolidArea(entity);
            updateSolidArea(gp.getNpc()[i]);

            switch (entity.getDirection()) {
                case "up": entity.solidArea.y -= entity.getSpeed(); break;
                case "down": entity.solidArea.y += entity.getSpeed(); break;
                case "left": entity.solidArea.x -= entity.getSpeed(); break;
                case "right": entity.solidArea.x += entity.getSpeed(); break;
            }

            if (entity.solidArea.intersects(gp.getNpc()[i].solidArea)) {
                System.out.println(entity.getDirection() + " collision");
            }

            resetSolidArea(entity);
            resetSolidArea(gp.getNpc()[i]);
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
