package utilities;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Sets up database connection
 */
public class DataBaseConnection {

	// jdbc url parts
	private static final String protocol = "jdbc";
	private static final String vendorName = ":sqlserver:";
	//Driver Interface
	private static final String msSQLServerJDBCDriver = "com.microsoft.sqlserver.jdbc.SQLServerDriver";
	//Login
	private static Connection connection = null;


	//Set up database connection
	public static Connection startConnection() {
		//finds jdbc driver in project directory
		try {
			File configFile = new File("src/utilities/Config.properties");
			FileReader reader = new FileReader(configFile);
			Properties props = new Properties();
			props.load(reader);
			Class.forName(msSQLServerJDBCDriver);
			String jdbcURL = protocol + vendorName + "//" + props.getProperty("ipAddress") + ":" + props.getProperty(
					"port");
			String userName = props.getProperty("userName");
			String password = props.getProperty("password");
			connection = DriverManager.getConnection(jdbcURL, userName, password);

		} catch (ClassNotFoundException | SQLException | IOException e) {
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
