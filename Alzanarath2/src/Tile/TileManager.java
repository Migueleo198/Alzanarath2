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
	    // Get player position and screen dimensions
	    int playerX = gp.getPlayer().getWorldX();
	    int playerY = gp.getPlayer().getWorldY();
	    int tileSize = gp.getTileSize();
	    int screenWidth = gp.getScreenWidth();
	    int screenHeight = gp.getScreenHeight();
	    
	    // Calculate the number of tiles needed to cover the screen horizontally and vertically
	    int tilesAcross = (int) Math.ceil((double) screenWidth / tileSize) + 2;
	    int tilesDown = (int) Math.ceil((double) screenHeight / tileSize) + 2;

	    // Calculate the starting tile positions to draw
	    int startCol = (playerX / tileSize) - (tilesAcross / 2);
	    int startRow = (playerY / tileSize) - (tilesDown / 2);

	    // Ensure starting positions are within bounds
	    startCol = Math.max(0, startCol);
	    startRow = Math.max(0, startRow);
	    
	    int endCol = Math.min(gp.getMaxWorldCol(), startCol + tilesAcross);
	    int endRow = Math.min(gp.getMaxWorldRow(), startRow + tilesDown);

	    // Draw tiles from startCol, startRow to endCol, endRow
	    for (int row = startRow; row < endRow; row++) {
	        for (int col = startCol; col < endCol; col++) {
	            int tileNum = mapTileNum[col][row];
	            int drawX = (col * tileSize) - (playerX - (screenWidth / 2));
	            int drawY = (row * tileSize) - (playerY - (screenHeight / 2));

	            g2.drawImage(tile[tileNum].getImage(), drawX, drawY, null);
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
