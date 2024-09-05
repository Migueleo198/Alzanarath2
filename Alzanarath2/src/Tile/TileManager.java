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
		int worldCol=0;
		int worldRow=0;

		
		//SCREEN X IS WHERE ON THE SCREEN WE DRAW THE TILES AND WORLD X IS THE POSITION OF THE CURRENT DRAWING
		//SO WHEN THE PLAYER MOVES WE SUBSTRACT THAT TO WORLD X IF HE MOVES TO THE SIDES OR TO WORLD Y IF HE MOVES UP AND DOWN
		
		while(worldRow<gp.getMaxWorldRow()){
			
			setTileNum(getMapTileNum()[worldCol][worldRow]);
			
			int checkCurrentWorldX = worldCol * gp.getTileSize();
			int checkCurrentWorldY = worldRow * gp.getTileSize();
			//WE SUBSTRACT THE POSITION OF THE PLAYER X AND Y TO THE 
			// MAP DRAWING AND DRAW THE TILE X AND Y POSITION TAKING THAT INTO ACCOUNT
			screenX = checkCurrentWorldX - gp.getPlayer().getWorldX() + gp.getPlayer().getScreenX();
			screenY = checkCurrentWorldY - gp.getPlayer().getWorldY() + gp.getPlayer().getScreenY();
			
			if(checkCurrentWorldX + gp.getTileSize() > gp.getPlayer().getWorldX()-gp.getPlayer().getScreenX()
					&& checkCurrentWorldX - gp.getTileSize() < gp.getPlayer().getWorldX()+gp.getPlayer().getScreenX()
					&& checkCurrentWorldY + gp.getTileSize()> gp.getPlayer().getWorldY()-gp.getPlayer().getScreenY()
					&& checkCurrentWorldY - gp.getTileSize() < gp.getPlayer().getWorldY()+gp.getPlayer().getScreenY()) {
			g2.drawImage(tile[getTileNum()].getImage(),screenX,screenY,null);
			}
			
			
			worldCol+=1;
		
		
		if(worldCol==gp.getMaxWorldCol()) {
			
			worldRow+=1;
			
			worldCol=0;
		}
		
		
		
		setTileNum(0);
		
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
