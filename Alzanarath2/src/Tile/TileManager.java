package Tile;

import java.awt.Graphics2D;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import javax.imageio.ImageIO;

import Entity.Player;
import main.GamePanel;
import main.Utility;

public class TileManager {
	GamePanel gp;
	public Tile[] tile;
	private int mapTileNum[][][];
	private int tileNum;
	public int screenX;
	public int screenY;
	ArrayList<String> fileNames = new ArrayList<>();
	ArrayList<String> collisionStatus = new ArrayList<>();
	
	public TileManager(GamePanel gp) {
		this.gp=gp;
		
		//READ TILE DATA FILE
		InputStream is = getClass().getResourceAsStream("/tileData/tileDataForImprovedMap01.txt");
		BufferedReader br = new BufferedReader(new InputStreamReader(is));
		
		//GETTING TILES AND COLLISION INFO FROM FILE
		String line;
		
		try {
			while((line =  br.readLine())!= null ) {
				fileNames.add(line);
				collisionStatus.add(br.readLine());
				
			}
			br.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		tile= new Tile[fileNames.size()];
		getTileImage();
		
		
		is= getClass().getResourceAsStream("/Maps/improvedMap01.txt");
		br = new BufferedReader(new InputStreamReader(is));
		
		try {
			String line2 = br.readLine();
			String maxTile[] = line2.split(" ");
			
			gp.setMaxWorldCol(maxTile.length);
			gp.setMaxWorldRow(maxTile.length);
			
			mapTileNum = new int[gp.maxMap][gp.getMaxWorldCol()][gp.getMaxWorldRow()];
			
			br.close();
			
		}catch(IOException e) {
			e.printStackTrace();
		}
		
		
		getTileImage();
		loadMap("/Maps/improvedMap01.txt",0);
		loadMap("/Maps/MapShop1.txt",1);
		
		
	}
	
	public void getTileImage() {
		for(int i=0; i< fileNames.size();i++) {
			String fileName;
			boolean collision;
		
			//get a file name
			fileName = fileNames.get(i);
			
			//get collision status
			
			if(collisionStatus.get(i).equals("true")) {
				collision=true;
			}
			else {
				collision=false;
			}
			setup(i,fileName,collision);
		}
			
		
		
		
		
		
	}
	
	public void loadMap(String filePath, int map) {
		try {
			InputStream is = getClass().getResourceAsStream(filePath);
			BufferedReader br = new BufferedReader(new InputStreamReader(is));
			
			int col = 0;
			int row = 0;
			
			while(row<gp.getMaxWorldRow() && col<gp.getMaxWorldCol()){
				
				String line = br.readLine();
				
				while(col<gp.getMaxWorldCol() ) {
					
					
					String numbers[] = line.split(" ");
					
					int num = Integer.parseInt(numbers[col]);
					
					getMapTileNum()[map][col][row] = num;
					
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
	    
	    Player player = gp.getPlayer();
	    
	    // Get player's current position and screen position
	    int playerWorldX = player.getWorldX();
	    int playerWorldY = player.getWorldY();
	    int playerScreenX = player.getScreenX();
	    int playerScreenY = player.getScreenY();
	    
	    int screenWidth = gp.getWidth();
	    int screenHeight = gp.getHeight();
	    
	    // Calculate visible tile bounds
	    int worldColStart = Math.max(0, (playerWorldX - playerScreenX) / tileSize);
	    int worldColEnd = Math.min(maxWorldCol, (playerWorldX - playerScreenX + screenWidth) / tileSize + 1);
	    int worldRowStart = Math.max(0, (playerWorldY - playerScreenY) / tileSize);
	    int worldRowEnd = Math.min(maxWorldRow, (playerWorldY - playerScreenY + screenHeight) / tileSize + 1);

	    for (int worldRow = worldRowStart; worldRow < worldRowEnd; worldRow++) {
	        for (int worldCol = worldColStart; worldCol < worldColEnd; worldCol++) {
	            int tileNum = mapTileNum[gp.currentMap][worldCol][worldRow];
	            int tileWorldX = worldCol * tileSize;
	            int tileWorldY = worldRow * tileSize;
	            int screenX = tileWorldX - playerWorldX + playerScreenX;
	            int screenY = tileWorldY - playerWorldY + playerScreenY;

	            // Draw the tile if it's within the visible screen bounds
	            g2.drawImage(tile[tileNum].getImage(), screenX, screenY, tileSize, tileSize, null);
	        }
	    }
	}


	
	public void setup(int index, String imageName, boolean collision) {
        Utility uTool = new Utility();

        try {
            tile[index] = new Tile();
            tile[index].image = ImageIO.read(getClass().getResourceAsStream("/tiles/" + imageName ));
            tile[index].image = uTool.scaleImage(tile[index].image, gp.getTileSize(), gp.getTileSize());
            tile[index].collision = collision;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }	

	public int[][][] getMapTileNum() {
		return mapTileNum;
	}

	public void setMapTileNum(int mapTileNum[][][]) {
		this.mapTileNum = mapTileNum;
	}

	public int getTileNum() {
		return tileNum;
	}

	public void setTileNum(int tileNum) {
		this.tileNum = tileNum;
	}
	
	
}
