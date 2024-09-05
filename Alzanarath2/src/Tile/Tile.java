package Tile;

import java.awt.image.BufferedImage;

public class Tile {
	//_*THIS CLASS REPRESENTS THE TILE*_
	
	BufferedImage image;
    boolean collision=false;
	
	
	public BufferedImage getImage() {
		return image;
	}
	public void setImage(BufferedImage image) {
		this.image = image;
	}
	public boolean isCollision() {
		return collision;
	}
	public void setCollision(boolean collision) {
		this.collision = collision;
	}
	
	
}
