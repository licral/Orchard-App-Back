package db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnectionManager {

	private Connection connection;

	public DBConnectionManager() throws ClassNotFoundException, SQLException{
		Class.forName("org.postgresql.Driver");
		this.connection = DriverManager.getConnection(System.getenv("JDBC_DATABASE_URL"));
	}

	public Connection getConnection(){
		return this.connection;
	}
}