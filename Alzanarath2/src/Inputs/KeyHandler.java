package Inputs;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class KeyHandler implements KeyListener{
	private int code;
	
	private boolean upPressed,downPressed,leftPressed,rightPressed;
	@Override
	public void keyTyped(KeyEvent e) {
		
		
	}

	@Override
	public void keyPressed(KeyEvent e) {
		code = e.getKeyCode();
		
		if(code == KeyEvent.VK_W) {
			upPressed=true;
		}
		
		if(code == KeyEvent.VK_S) {
			downPressed=true;
		}
		
		if(code == KeyEvent.VK_A) {
			leftPressed=true;
		}
		
		if(code == KeyEvent.VK_D) {
			rightPressed=true;
		}
		
	}

	@Override
	public void keyReleased(KeyEvent e) {
		code = e.getKeyCode();
		
		if(code == KeyEvent.VK_W) {
			upPressed=false;
		}
		
		if(code == KeyEvent.VK_S) {
			downPressed=false;
		}
		
		if(code == KeyEvent.VK_A) {
			leftPressed=false;
		}
		
		if(code == KeyEvent.VK_D) {
			rightPressed=false;
		}
		
	}
	
	public boolean isUpPressed() {
		return upPressed;
	}

	public void setUpPressed(boolean upPressed) {
		this.upPressed = upPressed;
	}

	public boolean isDownPressed() {
		return downPressed;
	}

	public void setDownPressed(boolean downPressed) {
		this.downPressed = downPressed;
	}

	public boolean isLeftPressed() {
		return leftPressed;
	}

	public void setLeftPressed(boolean leftPressed) {
		this.leftPressed = leftPressed;
	}

	public boolean isRightPressed() {
		return rightPressed;
	}

	public void setRightPressed(boolean rightPressed) {
		this.rightPressed = rightPressed;
	}

}