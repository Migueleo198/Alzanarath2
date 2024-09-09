package Inputs;

import java.awt.Color;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import main.GamePanel;

public class KeyHandler implements KeyListener {
    private GamePanel gp;
    private boolean upPressed, downPressed, leftPressed, rightPressed, ePressed;
    private boolean enterKeyPressed = false;
    private boolean escKeyPressed = false;
    private boolean cPressed;
    public int attackDelay = 0;

    private long lastMessageTime = 0; // Timestamp of the last sent message
    private static final long MESSAGE_DELAY = 5000; // 5 seconds in milliseconds
    private boolean warningColor;
    public KeyHandler(GamePanel gp) {
        this.gp = gp;
    }

    @Override
    public void keyPressed(KeyEvent e) {
        synchronized (gp.keyH) {
            int code = e.getKeyCode();

            if (gp.getGameState() == gp.getTitleState()) {
                titleState(code);
            }

            if (gp.getGameState() == gp.getPlayState()) {
                playState(code, e);
            } else if (gp.getGameState() == gp.getCharacterState()) {
                characterState(code);
            }
        }
    }

    public void titleState(int code) {
        handleMenuNavigation(code);
    }

    public void playState(int code, KeyEvent e) {
        // Handle chat input if chat is visible
        if (gp.ui.isChatVisible()) {
            if (code == KeyEvent.VK_ENTER) {
                // Send the message and clear the input
                String message = gp.ui.getCurrentMessage();
                if (System.currentTimeMillis() - lastMessageTime >= MESSAGE_DELAY) {
                    sendMessage(message);
                    gp.ui.appendGlobalChatMessage("You: " + message); // Add message to local chat
                    gp.ui.setCurrentMessage(""); // Clear the input
                    lastMessageTime = System.currentTimeMillis(); // Update last message time
                    setWarningColor(false);
                    gp.ui.getG2().setColor(Color.WHITE);
                } else {
                	
                    gp.ui.appendGlobalChatMessage("You are on cooldown"); // Optional feedback
                    setWarningColor(true);
                }
            } else if (code == KeyEvent.VK_ESCAPE) {
                // Hide chat
                gp.ui.hideChat();
            } else if (code == KeyEvent.VK_BACK_SPACE) {
                // Handle backspace to remove characters
                String currentMessage = gp.ui.getCurrentMessage();
                if (currentMessage.length() > 0) {
                    gp.ui.setCurrentMessage(currentMessage.substring(0, currentMessage.length() - 1));
                }
            } else {
                // Update current message while typing
                gp.ui.setCurrentMessage(gp.ui.getCurrentMessage() + e.getKeyChar());
            }
            return; // Skip player movement when chat is visible
        }

        // Handle chat visibility toggle
        if (code == KeyEvent.VK_ENTER && gp.getGameState() != gp.getTitleState()) {
            if (gp.ui.isChatVisible()) {
                // Start typing mode and reset the current message
                gp.ui.setCurrentMessage("");
            }
        } else if (code == KeyEvent.VK_ESCAPE) {
            gp.ui.toggleChatVisibility(); // Show or hide chat
            return; // Skip other actions if chat is being toggled
        } else {
            handlePlayerMovement(code, true);
        }

        // Open inventory
        if (code == KeyEvent.VK_C) {
            gp.setGameState(gp.getCharacterState());
        }
    }

    public void characterState(int code) {
        // Close Inventory
        if (gp.getGameState() == gp.getCharacterState()) {
            if (code == KeyEvent.VK_C) {
                gp.setGameState(gp.getPlayState());
            }
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {
        // Handle key typed for characters, no action needed here for this implementation
    }

    @Override
    public void keyReleased(KeyEvent e) {
        int code = e.getKeyCode();
        if (code == KeyEvent.VK_W) {
            upPressed = false;
        } else if (code == KeyEvent.VK_S) {
            downPressed = false;
        } else if (code == KeyEvent.VK_A) {
            leftPressed = false;
        } else if (code == KeyEvent.VK_D) {
            rightPressed = false;
        }

        if (code == KeyEvent.VK_E) {
            setePressed(false);
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

        if (code == KeyEvent.VK_E && attackDelay == 1) {
            ePressed = true;
            gp.playSE(3);
            attackDelay = 0;
        }
    }

    private void sendMessage(String message) {
        // Ensure message is sent to the server or the chat system
        if (gp.getNetworkManager() != null) {
            gp.getNetworkManager().sendChatMessage(message);
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

				gp.isServer = true;
				gp.initializeServer();
				gp.stopMusic();

			} else if (gp.ui.getCommandNum() == 1) {
				gp.setGameState(gp.getPlayState());
				gp.initializeGame();
				gp.isServer = false;
			}
		}
	}

	private void handleMenuAccount(int code) {
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
				System.out.println("Register Screen");
				gp.setGameState(gp.getRegisterState());
			} else if (gp.ui.getCommandNum() == 1) {
				System.out.println("You got clickbaited");
				System.exit(0);
			}
		}
	}

	private void handleLoginAccount(int code) {
		if (code == KeyEvent.VK_UP) {
			if (gp.ui.getCommandNum() > 0) {
				gp.ui.setCommandNum(gp.ui.getCommandNum() - 1);
			}
		} else if (code == KeyEvent.VK_DOWN) {
			if (gp.ui.getCommandNum() < 3) {
				gp.ui.setCommandNum(gp.ui.getCommandNum() + 1);
			}
		} else if (code == KeyEvent.VK_ENTER) {
			if (gp.ui.getCommandNum() == 0) {
				gp.ui.setEmailFocused(true);
				gp.ui.setPasswordFocused(false);
			} else if (gp.ui.getCommandNum() == 1) {
				gp.ui.setEmailFocused(false);
				gp.ui.setPasswordFocused(true);
			} else if (gp.ui.getCommandNum() == 2) {

			} else if (gp.ui.getCommandNum() == 3) {

			}
		} else if (code == KeyEvent.VK_ESCAPE) {
			if (gp.ui.getCommandNum() == 0) {
				gp.ui.setEmailFocused(false);
				gp.ui.setPasswordFocused(false);
			} else if (gp.ui.getCommandNum() == 1) {
				gp.ui.setEmailFocused(false);
				gp.ui.setPasswordFocused(false);
			}
		}
	}

	private void InputAuth(KeyEvent e) {
		char keyChar = e.getKeyChar();

		if (gp.ui.getEmailFocused()) {
			if (Character.isLetterOrDigit(keyChar) || isSpecialCharacter(keyChar) || keyChar == KeyEvent.VK_BACK_SPACE) {
				String currentEmail = gp.ui.getEmailInput();

				if (keyChar == KeyEvent.VK_BACK_SPACE && currentEmail.length() > 0) {
					gp.ui.setEmailInput(currentEmail.substring(0, currentEmail.length() - 1));
				} else if (Character.isLetterOrDigit(keyChar) || isSpecialCharacter(keyChar)) {
					gp.ui.setEmailInput(currentEmail + keyChar);
				}
				System.out.println(gp.ui.getEmailInput());
			}
		} else if (gp.ui.getPasswordFocused()) {
			if (Character.isLetterOrDigit(keyChar) || isSpecialCharacter(keyChar) || keyChar == KeyEvent.VK_BACK_SPACE) {
				String currentPassword = gp.ui.getPasswordInput();

				if (keyChar == KeyEvent.VK_BACK_SPACE && currentPassword.length() > 0) {
					gp.ui.setPasswordInput(currentPassword.substring(0, currentPassword.length() - 1));
				} else if (Character.isLetterOrDigit(keyChar) || isSpecialCharacter(keyChar)) {
					gp.ui.setPasswordInput(currentPassword + keyChar);
				}
	            System.out.println(gp.ui.getPasswordInput());
			}
		}
	}
	
	private void handleRegisterAccount(int code) {
		if (code == KeyEvent.VK_UP) {
			if (gp.ui.getCommandNum() > 0) {
				gp.ui.setCommandNum(gp.ui.getCommandNum() - 1);
			}
		} else if (code == KeyEvent.VK_DOWN) {
			if (gp.ui.getCommandNum() < 4) {
				gp.ui.setCommandNum(gp.ui.getCommandNum() + 1);
			}
		} else if (code == KeyEvent.VK_ENTER) {
			if (gp.ui.getCommandNum() == 0) {
				gp.ui.setUsernameFocused(true);
				gp.ui.setEmailFocused(false);
				gp.ui.setPasswordFocused(false);
			} else if (gp.ui.getCommandNum() == 1) {
				gp.ui.setUsernameFocused(false);
				gp.ui.setEmailFocused(true);
				gp.ui.setPasswordFocused(false);
			} else if (gp.ui.getCommandNum() == 4) {
				gp.ui.setUsernameFocused(false);
				gp.ui.setEmailFocused(false);
				gp.ui.setPasswordFocused(true);
			} else if (gp.ui.getCommandNum() == 3) {

			} else if (gp.ui.getCommandNum() == 4) {

			}
		} else if (code == KeyEvent.VK_ESCAPE) {
			if (gp.ui.getCommandNum() == 0) {
				gp.ui.setUsernameFocused(false);
				gp.ui.setEmailFocused(false);
				gp.ui.setPasswordFocused(false);
			} else if (gp.ui.getCommandNum() == 1) {
				gp.ui.setUsernameFocused(false);
				gp.ui.setEmailFocused(false);
				gp.ui.setPasswordFocused(false);
			} else if (gp.ui.getCommandNum() == 2) {
				gp.ui.setUsernameFocused(false);
				gp.ui.setEmailFocused(false);
				gp.ui.setPasswordFocused(false);
			}
		}
	}
	
	private void InputAuthReg(KeyEvent e) {
		char keyChar = e.getKeyChar();
		
		if (gp.ui.getUsernameFocused()) {
			if (Character.isLetterOrDigit(keyChar) || isSpecialCharacter(keyChar) || keyChar == KeyEvent.VK_BACK_SPACE) {
				String currentUsername = gp.ui.getUsernameInput();

				if (keyChar == KeyEvent.VK_BACK_SPACE && currentUsername.length() > 0) {
					gp.ui.setUsernameInput(currentUsername.substring(0, currentUsername.length() - 1));
				} else if (Character.isLetterOrDigit(keyChar) || isSpecialCharacter(keyChar)) {
					gp.ui.setUsernameInput(currentUsername + keyChar);
				}
				System.out.println( "Username: " + gp.ui.getUsernameInput());
			}
		} else if (gp.ui.getEmailFocused()) {
			if (Character.isLetterOrDigit(keyChar) || isSpecialCharacter(keyChar) || keyChar == KeyEvent.VK_BACK_SPACE) {
				String currentEmail = gp.ui.getEmailInput();

				if (keyChar == KeyEvent.VK_BACK_SPACE && currentEmail.length() > 0) {
					gp.ui.setEmailInput(currentEmail.substring(0, currentEmail.length() - 1));
				} else if (Character.isLetterOrDigit(keyChar) || isSpecialCharacter(keyChar)) {
					gp.ui.setEmailInput(currentEmail + keyChar);
				}
				System.out.println("Email: " + gp.ui.getEmailInput());
			}
		} else if (gp.ui.getPasswordFocused()) {
			if (Character.isLetterOrDigit(keyChar) || isSpecialCharacter(keyChar) || keyChar == KeyEvent.VK_BACK_SPACE) {
				String currentPassword = gp.ui.getPasswordInput();

				if (keyChar == KeyEvent.VK_BACK_SPACE && currentPassword.length() > 0) {
					gp.ui.setPasswordInput(currentPassword.substring(0, currentPassword.length() - 1));
				} else if (Character.isLetterOrDigit(keyChar) || isSpecialCharacter(keyChar)) {
					gp.ui.setPasswordInput(currentPassword + keyChar);
				}
	            System.out.println( "Password: " + gp.ui.getPasswordInput());
			}
		}
	}
	
	private boolean isSpecialCharacter(char c) {
	    // List of special characters to be included
	    return "@.".indexOf(c) >= 0;
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

    public boolean isEnterKeyPressed() {
        return enterKeyPressed;
    }

    public void setEnterKeyPressed(boolean enterKeyPressed) {
        this.enterKeyPressed = enterKeyPressed;
    }

    public boolean isePressed() {
        return ePressed;
    }

    public void setePressed(boolean ePressed) {
        this.ePressed = ePressed;
    }

	public boolean isWarningColor() {
		return warningColor;
	}

	public void setWarningColor(boolean warningColor) {
		this.warningColor = warningColor;
	}
}