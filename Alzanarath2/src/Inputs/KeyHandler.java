package Inputs;

import java.awt.Color;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import main.GamePanel;

public class KeyHandler implements KeyListener {
    private GamePanel gp;
    private boolean upPressed, downPressed, leftPressed, rightPressed, ePressed, tPressed;
    private boolean enterKeyPressed = false;
    private boolean escKeyPressed = false;
    private boolean cPressed;
    public int attackDelay = 0;
    
    public String username="";

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

			if (gp.getGameState() == gp.getScreenState()) {
				screenState(code);
			} else if (gp.getGameState() == gp.getLoginState()) {
		        handleLoginAccount(code);

		        if (gp.ui.getEmailFocused() || gp.ui.getPasswordFocused()) {
		            InputAuth(e);
		        }
			} else if(gp.getGameState() == gp.getRegisterState()) {
		        handleRegisterAccount(code);

		        if (gp.ui.getEmailFocused() || gp.ui.getPasswordFocused() || gp.ui.getUsernameFocused()) {
		            InputAuthReg(e);
		        }
			} else if (gp.getGameState() == gp.getTitleState()) {
				titleState(code);
			} else if (gp.getGameState() == gp.getPlayState()) {
				playState(code, e);

			} else if (gp.getGameState() == gp.getCharacterState()) {
				characterState(code);
			}
			
			else if(gp.getGameState() == gp.getSkillTreeState()) {
				skillTreeState(code);
				
				
				if (code == KeyEvent.VK_UP) {
	        	    gp.ui.setSelectedSkillIndex(Math.max(gp.ui.getSelectedSkillIndex() - 1, 0));  // Move up, prevent going negative
	        	}
	        	if (code == KeyEvent.VK_DOWN) {
	        	    gp.ui.setSelectedSkillIndex(Math.min(gp.ui.getSelectedSkillIndex() + 1, gp.ui.getSkillCount() - 1));  // Move down, prevent out-of-bound
	        	}
	        	if (code == KeyEvent.VK_ENTER) {
	        	    gp.ui.unlockSelectedSkill();  // Unlock the selected skill
	        	}    
			}
		}
	}

	public void titleState(int code) {
		handleMenuNavigation(code);
	}

	public void screenState(int code) {
		handleMenuAccount(code);
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
	                } else {
	                    gp.ui.appendGlobalChatMessage("Please wait before sending another message"); // Optional feedback
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
	        
	        if (code == KeyEvent.VK_T) {
	            // Check if the current state is the skill tree state
	        	gp.setGameState(gp.getSkillTreeState());
	                // If the skill tree is already open, close it and revert to the previous state
	        	// In KeyHandler.java, within the keyPressed method
	        	
	                
	        }
	 }
	 
	 public void skillTreeState(int code) {
		 if (gp.getGameState() == gp.getSkillTreeState()) {
	            if (code == KeyEvent.VK_T) {
	                gp.setGameState(gp.getPlayState());
	            }
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
        
        if (code == KeyEvent.VK_T) {
            // Check if the current state is the skill tree state
            tPressed=false;
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
			if (gp.ui.getCommandNum() < 4) {
				gp.ui.setCommandNum(gp.ui.getCommandNum() + 1);
			}
		} else if (code == KeyEvent.VK_ENTER) {
			if (gp.ui.getCommandNum() == -1) {
				gp.ui.setUsernameFocused(true);
				gp.ui.setEmailFocused(false);
				gp.ui.setPasswordFocused(false);
			} else if (gp.ui.getCommandNum() == 0) {
				gp.ui.setUsernameFocused(false);
				gp.ui.setEmailFocused(true);
				gp.ui.setPasswordFocused(false);
			} else if (gp.ui.getCommandNum() == 1) {
				gp.ui.setUsernameFocused(false);
				gp.ui.setEmailFocused(false);
				gp.ui.setPasswordFocused(true);
			} else if (gp.ui.getCommandNum() == 2) {
				gp.ui.setCommandNum(0);
				handleLogin(gp.ui.getEmailInput(),gp.ui.getPasswordInput());
				
				
				
			} else if (gp.ui.getCommandNum() == 3) {
				gp.ui.setCommandNum(0);
				gp.setGameState(gp.getRegisterState());
				handleRegisterAccount(code);
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
	
	private void handleLogin(String username, String password) {
	    Connection conn = gp.connection.connection;
	    PreparedStatement pstmt = null;
	    ResultSet rs = null;

	    if (conn == null) {
	        System.out.println("Database connection is not initialized.");
	        return;
	    }

	    try {
	        // Prepare the SQL query to check login credentials
	        String query = "SELECT * FROM accounts WHERE username = ? AND password = ?";
	        pstmt = conn.prepareStatement(query);
	        pstmt.setString(1, username);
	        pstmt.setString(2, password);

	        // Debug print statements
	        System.out.println("Executing query: " + query);
	        this.username=username;
	        System.out.println("With username: " + username);
	        System.out.println("With password: " + password);

	        // Execute the query
	        rs = pstmt.executeQuery();

	        // Check if the login was successful
	        if (rs.next()) {
	            // Login successful
	            System.out.println("Login successful!");
	            gp.setGameState(gp.getTitleState());
	        } else {
	            // Login failed
	            System.out.println("Invalid username or password.");
	            gp.setGameState(gp.getLoginState());
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	        System.out.println("An error occurred while trying to log in. Please try again.");
	    } finally {
	        // Close resources
	        try {
	            if (rs != null) rs.close();
	            if (pstmt != null) pstmt.close();
	            // Uncomment if connection is not managed elsewhere
	            // if (conn != null) conn.close();
	        } catch (SQLException e) {
	            e.printStackTrace();
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
			} else if (gp.ui.getCommandNum() == 2) {
				gp.ui.setUsernameFocused(false);
				gp.ui.setEmailFocused(false);
				gp.ui.setPasswordFocused(true);
			} else if (gp.ui.getCommandNum() == 3) {
				gp.ui.setCommandNum(0);
				 gp.setGameState(gp.getLoginState()); 
		}// Proceed to the Login
				 else if (gp.ui.getCommandNum() == 4) {
				
				    String username = gp.ui.getUsernameInput();
				    String email = gp.ui.getEmailInput();
				    String password = gp.ui.getPasswordInput();
				    gp.ui.setCommandNum(0);
				    // Validate the inputs
				    
				     
				        if (registerUser(username, password, email)) {
				        	this.username=username;
				        	gp.setGameState(gp.getPlayState());
				        }
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
	
	
	private boolean registerUser(String username, String password, String email) {
	    try {
	        // Assuming you have a connection object to your MySQL database
	        String query = "INSERT INTO accounts (username, password, gmail) VALUES (?, ?, ?)";
	        PreparedStatement pstmt = gp.connection.connection.prepareStatement(query);
	        pstmt.setString(1, username);
	        pstmt.setString(2, password);
	        pstmt.setString(3, email);
	        
	        int result = pstmt.executeUpdate(); // Execute the query
	        return result > 0; // Return true if inserted
	    } catch (SQLException e) {
	        e.printStackTrace();
	        return false;
	    }
	}
	
	private boolean validateLogin(String username, String password) {
	    try {
	        // Query to check if the username and password match
	        String query = "SELECT * FROM accounts WHERE username = ? AND password = ?";
	        PreparedStatement pstmt = gp.connection.connection.prepareStatement(query);
	        pstmt.setString(1, username);
	        pstmt.setString(2, password);
	        
	        ResultSet rs = pstmt.executeQuery();
	        
	        return rs.next(); // Return true if a match is found
	    } catch (SQLException e) {
	        e.printStackTrace();
	        return false;
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


	public boolean istPressed() {
		return tPressed;
	}


	public void settPressed(boolean tPressed) {
		this.tPressed = tPressed;
	}
}