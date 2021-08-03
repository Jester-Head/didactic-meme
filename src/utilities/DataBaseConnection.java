package utilities;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Sets up database connection
 */
public class DataBaseConnection {

	// jdbc url parts
	private static final String protocol = "jdbc";
	private static final String vendorName = ":mysql:";
	private static final String ipAddress = "//wgudb.ucertify.com:3306/";
	private static final String databaseName = "WJ07NYX";

	//jdbc url
	private static final String jdbcURL = protocol + vendorName + ipAddress + databaseName;

	//Driver Interface
	private static final String mySQLJDBCDriver = "com.mysql.cj.jdbc.Driver";
	//Login
	private static final String userName = "U07NYX";
	private static final String password = "53689077432";
	private static Connection connection = null;

	//Set up database connection
	public static Connection startConnection() {
		//finds jdbc driver in project directory
		try {
			Class.forName(mySQLJDBCDriver);
			connection = DriverManager.getConnection(jdbcURL, userName, password);

		} catch (ClassNotFoundException | SQLException e) {
			e.printStackTrace();
		}
		return connection;
	}

	public static Connection getConnection() {
		return connection;
	}


	public static void closeConnection() {
		try {
			connection.close();
		} catch (SQLException throwables) {
			throwables.printStackTrace();
		}
	}
}
