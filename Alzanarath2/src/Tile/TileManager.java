package Tile;

import java.awt.Graphics2D;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import javax.imageio.ImageIO;

import main.GamePanel;
import main.Utility;

public class TileManager {
	GamePanel gp;
	public Tile[] tile;
	private int mapTileNum[][];
	private int tileNum;
	public int screenX;
	public int screenY;
	public TileManager(GamePanel gp) {
		this.gp=gp;
		
		tile= new Tile[20];
		setMapTileNum(new int[gp.getMaxWorldCol()][gp.getMaxWorldRow()]);
		getTileImage();
		loadMap("/Maps/map01.txt");
		
		
	}
	
	public void getTileImage() {
		setup(0, "5Grass",false);
		setup(1, "0WallTile",true);
		setup(2, "8WoodenFloor",false);
		setup(3, "7Tree",true);
		
		
	}
	
	public void loadMap(String filePath) {
		try {
			InputStream is = getClass().getResourceAsStream(filePath);
			BufferedReader br = new BufferedReader(new InputStreamReader(is));
			
			int col = 0;
			int row = 0;
			
			while(row<gp.getMaxWorldRow() && col<gp.getMaxWorldCol()){
				
				String line = br.readLine();
				
				while(col<gp.getMaxWorldCol()) {
					String numbers[] = line.split(" ");
					
					int num = Integer.parseInt(numbers[col]);
					
					getMapTileNum()[col][row] = num;
					
					col++;
				}
				
				if(col==gp.getMaxWorldCol()) {
					col=0;
					row++;
				}
				
			
			}
			
			br.close();
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public void draw(Graphics2D g2) {
	    int tileSize = gp.getTileSize();
	    int maxWorldCol = gp.getMaxWorldCol();
	    int maxWorldRow = gp.getMaxWorldRow();
	    
	    // Get player's current position and screen position
	    int playerWorldX = gp.getPlayer().getWorldX();
	    int playerWorldY = gp.getPlayer().getWorldY();
	    int playerScreenX = gp.getPlayer().getScreenX();
	    int playerScreenY = gp.getPlayer().getScreenY();
	    
	    for (int worldRow = 0; worldRow < maxWorldRow; worldRow++) {
	        for (int worldCol = 0; worldCol < maxWorldCol; worldCol++) {
	            
	            // Calculate the world position of the current tile
	            int tileWorldX = worldCol * tileSize;
	            int tileWorldY = worldRow * tileSize;
	            
	            // Calculate the screen position of the tile based on the player's position
	            int screenX = tileWorldX - playerWorldX + playerScreenX;
	            int screenY = tileWorldY - playerWorldY + playerScreenY;
	            
	            // Check if the tile is within the visible screen bounds
	            if (tileWorldX + tileSize > playerWorldX - playerScreenX &&
	                tileWorldX - tileSize < playerWorldX + playerScreenX &&
	                tileWorldY + tileSize > playerWorldY - playerScreenY &&
	                tileWorldY - tileSize < playerWorldY + playerScreenY) {
	                
	                // Draw the tile if it's within the visible screen bounds
	                int tileNum = getMapTileNum()[worldCol][worldRow];
	                g2.drawImage(tile[tileNum].getImage(), screenX, screenY, null);
	            }
	        }
	    }
	}

	
	public void setup(int index, String imageName, boolean collision) {
        Utility uTool = new Utility();

        try {
            tile[index] = new Tile();
            tile[index].image = ImageIO.read(getClass().getResourceAsStream("/tiles/" + imageName + ".png"));
            tile[index].image = uTool.scaleImage(tile[index].image, gp.getTileSize(), gp.getTileSize());
            tile[index].collision = collision;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }	

	public int[][] getMapTileNum() {
		return mapTileNum;
	}

	public void setMapTileNum(int mapTileNum[][]) {
		this.mapTileNum = mapTileNum;
	}

	public int getTileNum() {
		return tileNum;
	}

	public void setTileNum(int tileNum) {
		this.tileNum = tileNum;
	}
	
	
}
