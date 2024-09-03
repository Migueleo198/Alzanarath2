package Inputs;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import main.GamePanel;

public class KeyHandler implements KeyListener {
    private GamePanel gp;
    private boolean upPressed, downPressed, leftPressed, rightPressed;

    public KeyHandler(GamePanel gp) {
        this.gp = gp;
    }

    @Override
    public void keyTyped(KeyEvent e) {
        // Not used
    }

    @Override
    public void keyPressed(KeyEvent e) {
    	synchronized (gp.keyH) {
        int code = e.getKeyCode();

        if (gp.getGameState() == gp.getTitleState()) {
            handleMenuNavigation(code);
        } else {
            handlePlayerMovement(code, true);
        }
    	}
    }

    @Override
    public void keyReleased(KeyEvent e) {
    	synchronized (gp.keyH) {
        int code = e.getKeyCode();
        handlePlayerMovement(code, false);
    	}
    }

    private void handleMenuNavigation(int code) {
        if (code == KeyEvent.VK_UP) {
            if (gp.ui.getCommandNum() > 0) {
                gp.ui.setCommandNum(gp.ui.getCommandNum() - 1);
            }
        } else if (code == KeyEvent.VK_DOWN) {
            if (gp.ui.getCommandNum() < 1) {
                gp.ui.setCommandNum(gp.ui.getCommandNum() + 1);
            }
        } else if (code == KeyEvent.VK_ENTER) {
            if (gp.ui.getCommandNum() == 0) {
                gp.setGameState(gp.getPlayState());
                gp.isServer = true;
               
                gp.initializeGame();  // Ensure this is called before the game loop
            } else if (gp.ui.getCommandNum() == 1) {
                gp.setGameState(gp.getPlayState());
                gp.initializeGame();  // Ensure this is called before the game loop
                
                gp.isServer = false;
            }
        }
    
    }

    private void handlePlayerMovement(int code, boolean pressed) {
        if (code == KeyEvent.VK_W) {
            upPressed = pressed;
        } else if (code == KeyEvent.VK_S) {
            downPressed = pressed;
        } else if (code == KeyEvent.VK_A) {
            leftPressed = pressed;
        } else if (code == KeyEvent.VK_D) {
            rightPressed = pressed;
        }
    }

    public boolean isUpPressed() {
        return upPressed;
    }

    public boolean isDownPressed() {
        return downPressed;
    }

    public boolean isLeftPressed() {
        return leftPressed;
    }

    public boolean isRightPressed() {
        return rightPressed;
    }
}

