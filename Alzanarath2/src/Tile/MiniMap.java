package Tile;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import main.GamePanel;



public class MiniMap extends TileManager {
    private GamePanel gamePanel;
    private BufferedImage worldMap[];
    public boolean miniMapOn = false;

    public MiniMap(GamePanel gamePanel) {
        super(gamePanel);
        this.gamePanel = gamePanel;
        createWorldMap();
    }

    public void createWorldMap() {
        worldMap = new BufferedImage[1];
        int worldMapWidth = gamePanel.getTileSize() * gamePanel.getMaxWorldCol();
        int worldMapHeight = gamePanel.getTileSize() * gamePanel.getMaxWorldRow();

      
            worldMap[0] = new BufferedImage(worldMapWidth, worldMapHeight, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2 = worldMap[0].createGraphics();

            for (int col = 0, row = 0; col < gamePanel.getMaxWorldCol() && row < gamePanel.getMaxWorldRow(); ) {
                int tileNum = getMapTileNum()[col][row];
                int x = gamePanel.getTileSize() * col;
                int y = gamePanel.getTileSize() * row;
                g2.drawImage(tile[tileNum].image, x, y, null);

                col++;
                if (col == gamePanel.getMaxWorldCol()) {
                    col = 0;
                    row++;
                }
            }
            g2.dispose();
        }
    

    public void drawFullMapScreen(Graphics2D g2) {
        // SET BACKGROUND COLOR
        g2.setColor(new Color(0, 0, 0, 210));
        g2.fillRect(0, 0, gamePanel.getScreenWidth(), gamePanel.getScreenHeight());

        // DRAW MAP
        int width = 500;
        int height = 500;
        int x = gamePanel.getScreenWidth() / 2 - width / 2;
        int y = gamePanel.getScreenHeight() / 2 - height / 2;
        g2.drawImage(worldMap[0], x, y, width, height, null);

        // DRAW PLAYER
        double scale = (double) (gamePanel.getTileSize() * gamePanel.getMaxWorldCol()) / width;
        int playerX = (int) (x + gamePanel.getPlayer().getWorldX() / scale);
        int playerY = (int) (y + gamePanel.getPlayer().getWorldY() / scale);
        int playerSize = (int) (gamePanel.getTileSize() / scale);
        g2.drawImage(gamePanel.getPlayer().getDown1(), playerX, playerY, playerSize, playerSize, null);

        // HINT
        g2.setFont(new Font("COMIC SANS", Font.BOLD,20));
        g2.setColor(Color.white);
        g2.drawString("Press M to close Map", 750, 550);
    }

    public void drawMiniMap(Graphics2D g2) {
        if (miniMapOn) {
            // DRAW MAP BACKGROUND with transparency
            int diameter = 150; // Diameter of the circular minimap
            int x = gamePanel.getScreenWidth() - diameter - 50;
            int y = 50;

            // Set the Alpha Composite for transparency
            AlphaComposite originalComposite = (AlphaComposite) g2.getComposite();
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.85f));

            // Dark frame with glowing edge
            g2.setColor(new Color(20, 20, 20, 220)); // Dark semi-transparent background

            // Draw a circular background
            g2.fillOval(x, y, diameter, diameter);

            // Glowing border effect
            g2.setColor(new Color(0, 0, 0, 150)); // Subtle glow effect
            g2.setStroke(new BasicStroke(3));
            g2.drawOval(x - 2, y - 2, diameter + 4, diameter + 4); // Glowing circular frame

            // Clip to a circular region to make the map round
            g2.setClip(new java.awt.geom.Ellipse2D.Float(x, y, diameter, diameter));

            // DRAW MAP inside the circular clipped area
            g2.drawImage(worldMap[0], x, y, diameter, diameter, null);

            // Reset the clipping area to the original so other drawings are not clipped
            g2.setClip(null);

            // Gothic border for minimap
            g2.setColor(new Color(80, 80, 80)); // Dark iron frame
            g2.setStroke(new BasicStroke(5));
            g2.drawOval(x, y, diameter, diameter); // Circular Gothic border

            // Player icon with shadow
            double scale = (double) (gamePanel.getTileSize() * gamePanel.getMaxWorldCol()) / diameter;
            int playerX = (int) (x + gamePanel.getPlayer().getWorldX() / scale);
            int playerY = (int) (y + gamePanel.getPlayer().getWorldY() / scale);
            int playerSize = (int) (gamePanel.getTileSize() / 4);

            // Draw shadow under player
            g2.setColor(new Color(0, 0, 0, 100)); // Slightly transparent shadow
            g2.fillOval(playerX - playerSize / 2 - 2, playerY - playerSize / 2 - 2, playerSize + 4, playerSize + 4);

            // Draw player icon
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
            g2.drawImage(gamePanel.getPlayer().getDown1(), playerX - playerSize / 2, playerY - playerSize / 2, playerSize, playerSize, null);

            // Draw coordinates with medieval font
            g2.setFont(new Font("Garamond", Font.PLAIN, 14));
            g2.setColor(new Color(0, 0, 0)); // Off-white for readability
            g2.drawString("X: " + gamePanel.getPlayer().getWorldX(), x + 20, y + diameter + 15);
            g2.drawString("Y: " + gamePanel.getPlayer().getWorldY(), x + 80, y + diameter + 15);

            // Restore the original composite
            g2.setComposite(originalComposite);
        }
    }


}
