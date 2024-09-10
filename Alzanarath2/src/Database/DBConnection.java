package Database;

import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DBConnection {
	public Connection connection;
	public DBConnection() {
		
	}
	
	public void initializeConnection() {
		 String url = "jdbc:mysql://192.168.0.105:3306/alzanarath2";
		 String user = "root";
		 String password = "Miguel4.0";
		 System.out.println("Connecting to database ...");

		 try {
			 setConnection(DriverManager.getConnection(url,user,password));
		     System.out.println("Connected to Database!");
		 } catch (SQLException e) {
		     throw new IllegalStateException("Cannot establish connection to the database!", e);
		 }
	}

	public Connection getConnection() {
		return connection;
	}

	public void setConnection(Connection connection) {
		this.connection = connection;
	}
	
	
	
}
