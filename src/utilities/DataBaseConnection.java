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
	private static final String vendorName = ":sqlserver:";
	private static final String ipAddress = "//HOMELAB:1433";
	//private static final String databaseName = "didactic_meme";


	//jdbc url
	//private static final String jdbcURL = protocol + vendorName + ipAddress + databaseName;
	private static final String jdbcURL = protocol + vendorName + ipAddress;

	//Driver Interface
	private static final String msSQLServerJDBCDriver = "com.microsoft.sqlserver.jdbc.SQLServerDriver";

	//Login
	private static final String userName = "dbuser";
	private static final String password = "Ashtray_Glasses_27";
	private static Connection connection = null;

	//Set up database connection
	public static Connection startConnection() {
		//finds jdbc driver in project directory
		try {
			Class.forName(msSQLServerJDBCDriver);
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
