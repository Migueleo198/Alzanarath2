package Database;

import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DBConnection {
	protected Connection connection;
	public DBConnection() {
		
	}
	
	public void initializeConnection() {
		 String url = "jdbc:mysql://127.0.0.1:3306/?user=root";
		
		 System.out.println("Connecting to database ...");

		 try {
			 connection = DriverManager.getConnection(url);
		     System.out.println("Connected to Database!");
		 } catch (SQLException e) {
		     throw new IllegalStateException("Cannot establish connection to the database!", e);
		 }
	}
	
	
	
}
