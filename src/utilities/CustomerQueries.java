package utilities;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import model.Customer;

import java.sql.*;

public class CustomerQueries {
	private static final ObservableList<Customer> customerObservableList = FXCollections.observableArrayList();

	/**
	 * Queries all countries.
	 *
	 * @return ObservableList Of country names in the countries table.
	 */
	public static ObservableList<String> populateCountry() {

		ObservableList<String> countryNames = FXCollections.observableArrayList();

		try {
			String selectCountry = "SELECT Country FROM didactic_meme.dbo.countries ";

			PreparedStatement ps = DataBaseConnection.getConnection().prepareStatement(selectCountry);
			ResultSet country = ps.executeQuery();
			while (country.next()) {
				countryNames.add(country.getString("Country"));
			}

		} catch (SQLException throwables) {
			throwables.printStackTrace();
		}
		return countryNames;
	}

	/**
	 * Queries first level divisions.
	 *
	 * @return ObservableList of region names located in the divisions table.
	 */
	public static ObservableList<String> populateDivision() {

		ObservableList<String> divisionNames = FXCollections.observableArrayList();

		try {
			String selectDivision = "SELECT Division FROM didactic_meme.dbo.divisions ";

			PreparedStatement ps = DataBaseConnection.getConnection().prepareStatement(selectDivision);
			ResultSet division = ps.executeQuery();
			while (division.next()) {
				divisionNames.add(division.getString("Division"));
			}

		} catch (SQLException throwables) {
			throwables.printStackTrace();
		}
		return divisionNames;
	}

	/**
	 * Filters divisions by matching country.
	 *
	 * @param country user's selected country.
	 * @return ObservableList of filtered first_level_
	 * @throws SQLException
	 */
	public static ObservableList<String> filterDivision(String country) throws SQLException {

		String filter = "SELECT fd.Division FROM didactic_meme.dbo.divisions fd JOIN didactic_meme.dbo.countries c " +
				"ON" +
				" " +
				"c.Country_ID = fd" +
				".Country_ID WHERE Country = ?";
		ObservableList<String> divisions = FXCollections.observableArrayList();
		PreparedStatement ps = DataBaseConnection.getConnection().prepareStatement(filter);
		ps.setString(1, country);
		ResultSet rs = ps.executeQuery();
		while (rs.next()) {
			String division = rs.getString("Division");
			divisions.add(division);
		}
		return divisions;
	}

	/**
	 * Finds country name associated with specific region.
	 *
	 * @param divisions user's selected division.
	 * @return country name.
	 * @throws SQLException
	 */
	public static String filterCountry(String divisions) throws SQLException {
		String filter = "SELECT c.Country FROM didactic_meme.dbo.countries c JOIN didactic_meme.dbo.divisions fd ON " +
				"c" +
				".Country_ID=fd" +
				".Country_ID" +
				" WHERE Division=?";
		String country = "";
		PreparedStatement ps = DataBaseConnection.getConnection().prepareStatement(filter);
		ps.setString(1, divisions);
		ResultSet rs = ps.executeQuery();
		while (rs.next()) {
			country = rs.getString("Country");
		}
		return country;
	}


	public static void executeInsertWithKeys(Customer newCustomer) {
		String insertCustomer = "INSERT INTO didactic_meme.dbo.customers VALUES(NULL,?,?,?,?,?,?,?,?,?)";
		try (PreparedStatement ps = DataBaseConnection.getConnection().prepareStatement(insertCustomer)) {
			ps.executeUpdate(insertCustomer, PreparedStatement.RETURN_GENERATED_KEYS);

			String customerName = newCustomer.getCustomerName();
			String address = newCustomer.getCustomerAddress();
			String zipCode = newCustomer.getCustomerZip();
			String phone = newCustomer.getCustomerPhoneNumber();
			String createDate = String.valueOf(newCustomer.getCreateDate());
			String createdBy = newCustomer.getCreatedBy();
			String lastUpdate = String.valueOf(newCustomer.getUpdateTime());
			String updatedBy = newCustomer.getUpdatedBy();

			int divisionId = findDivisionId(newCustomer);

			ps.setString(1, customerName);
			ps.setString(2, address);
			ps.setString(3, zipCode);
			ps.setString(4, phone);
			ps.setString(5, createDate);
			ps.setString(6, createdBy);
			ps.setString(7, lastUpdate);
			ps.setString(8, updatedBy);
			ps.setInt(9, divisionId);

			ResultSet rs = ps.getGeneratedKeys();
			ResultSetMetaData rsmd = rs.getMetaData();
			int columnCount = rsmd.getColumnCount();
			if (rs.next()) {
				do {
					for (int i = 1; i <= columnCount; i++) {
						String key = rs.getString(i);
						System.out.println("KEY " + i + " = " + key);
					}
				} while (rs.next());
			} else {
				System.out.println("NO KEYS WERE GENERATED.");
			}

		}
		// Handle any errors that may have occurred.
		catch (SQLException e) {
			e.printStackTrace();
		}

	}


	/**
	 * Inserts new customer data into database.
	 *
	 * @param newCustomer customer information collected from AddCustomer form.
	 */
	public static void insertCustomer(Customer newCustomer) {
		try {

			String insertCustomer = "INSERT INTO didactic_meme.dbo.customers VALUES(?,?,?,?,?,?,?,?,?)";
			//Gets database connection and retrieves autogenerated primary key for new customer

			PreparedStatement ps = DataBaseConnection.getConnection().prepareStatement(insertCustomer,
					PreparedStatement.RETURN_GENERATED_KEYS);


			String customerName = newCustomer.getCustomerName();
			String address = newCustomer.getCustomerAddress();
			String zipCode = newCustomer.getCustomerZip();
			String phone = newCustomer.getCustomerPhoneNumber();
			String createDate = String.valueOf(newCustomer.getCreateDate());
			String createdBy = newCustomer.getCreatedBy();
			String lastUpdate = String.valueOf(newCustomer.getUpdateTime());
			String updatedBy = newCustomer.getUpdatedBy();

			int divisionId = findDivisionId(newCustomer);

			ps.setString(1, customerName);
			ps.setString(2, address);
			ps.setString(3, zipCode);
			ps.setString(4, phone);
			ps.setString(5, createDate);
			ps.setString(6, createdBy);
			ps.setString(7, lastUpdate);
			ps.setString(8, updatedBy);
			ps.setInt(9, divisionId);


			//Executes Update
			ps.execute();
		} catch (SQLException throwables) {
			throwables.getMessage();
			//throwables.printStackTrace();
		}
	}

	/**
	 * Updates existing customer in database.
	 *
	 * @param customer information collected from UpdateCustomer form,
	 * @return number of rows affected.
	 * @throws SQLException
	 */
	public static int updateCustomer(Customer customer) throws SQLException {
		int customerId = customer.getCustomerId();

		int divisionId = findDivisionId(customer);
		String updateCustomer = " UPDATE didactic_meme.dbo.customers SET Customer_Name = ?,Address = ?,Postal_Code =" +
				" " +
				"?, Phone = ?," +
				"Last_Update = ?, Last_Updated_By = ?,Division_ID = ? WHERE Customer_ID" +
				" =?";
		PreparedStatement ps = DataBaseConnection.getConnection().prepareStatement(updateCustomer);
		String customerName = customer.getCustomerName();
		String address = customer.getCustomerAddress();
		String zipCode = customer.getCustomerZip();
		String phone = customer.getCustomerPhoneNumber();
		String createDate = String.valueOf(customer.getCreateDate());
		String createdBy = customer.getCreatedBy();
		String lastUpdate = String.valueOf(customer.getUpdateTime());
		String updatedBy = customer.getUpdatedBy();
		ps.setString(1, customerName);
		ps.setString(2, address);
		ps.setString(3, zipCode);
		ps.setString(4, phone);
		ps.setString(5, lastUpdate);
		ps.setString(6, updatedBy);
		ps.setInt(7, divisionId);
		ps.setInt(8, customerId);


		return ps.executeUpdate();
	}

	/**
	 * Deletes customer and all of their appointments from the database.
	 *
	 * @param customer item selected from table.
	 */
	public static boolean deleteCustomer(Customer customer) {
		boolean results = true;
		int customerId = customer.getCustomerId();
		String deleteCustomer = "DELETE from didactic_meme.dbo.customers WHERE Customer_ID = ?";
		String deleteAppointments = "DELETE from didactic_meme.dbo.appointments WHERE Customer_ID = ?";
		try {
			PreparedStatement psa = DataBaseConnection.getConnection().prepareStatement(deleteAppointments);
			PreparedStatement ps = DataBaseConnection.getConnection().prepareStatement(deleteCustomer);
			ps.setInt(1, customerId);
			psa.setInt(1, customerId);
			psa.execute();
			results = ps.execute();

		} catch (SQLException throwables) {
			throwables.printStackTrace();
		}
		return results;
	}

	/**
	 * Queries all customers and first level divisions.
	 *
	 * @return ObservableList of customers and their location.
	 */
	public static ObservableList<Customer> getAllCustomers() {

		customerObservableList.clear();
		try {
			String queryCustomer = "SELECT c.Customer_ID, c.Customer_Name,CONCAT(c.Address,', ', fd.Division)As " +
					"'Address', c.Postal_Code,c.Phone, c.Create_Date,c.Created_By,c.Last_Update,c.Last_Updated_By,c" +
					".Division_ID  FROM didactic_meme.dbo.customers c JOIN didactic_meme.dbo.divisions fd ON c" +
					".Division_ID = fd.Division_ID;";
			//Gets database connection
			PreparedStatement ps = DataBaseConnection.getConnection().prepareStatement(queryCustomer);
			//Executes Query
			ResultSet customerResults = ps.executeQuery();


			//Loops through results and adds Customer to the observable list
			while (customerResults.next()) {
				int customerId = customerResults.getInt("Customer_ID");
				String customerName = customerResults.getString("Customer_Name");
				String customerAddress = customerResults.getString("Address");
				String customerZip = customerResults.getString("Postal_Code");
				String customerPhoneNumber = customerResults.getString("Phone");
				Timestamp createDate = customerResults.getTimestamp("Create_Date");
				String createdBy = customerResults.getString("Created_By");
				Timestamp lastUpdate = customerResults.getTimestamp("Last_Update");
				String lastUpdatedBy = customerResults.getString("Last_Updated_By");
				//int divisionId = customerResults.getInt("Division_ID");
				Customer customer = new Customer(customerId, customerName, customerAddress, customerZip,
						customerPhoneNumber, createDate, createdBy, lastUpdate, lastUpdatedBy);
				addCustomer(customer);
			}

		} catch (SQLException throwable) {
			throwable.printStackTrace();
		}
		return customerObservableList;
	}

	/**
	 * Adds customer to an ObservableList that's used to populate customer records table.
	 *
	 * @param customer customer object.
	 */
	public static void addCustomer(Customer customer) {
		customerObservableList.add(customer);
	}

	/**
	 * Finds the division ID using the name of the division and country.
	 *
	 * @param customer division name field in customer class.
	 * @return division ID associated with division name.
	 */
	public static int findDivisionId(Customer customer) {
		String division = customer.getCustomerDivision();
		String country = customer.getCustomerCountry();
		String statement = "SELECT Division_ID FROM didactic_meme.dbo.divisions fd JOIN didactic_meme.dbo.countries " +
				"c" +
				" " +
				"ON c.Country_ID = fd" +
				".Country_ID WHERE fd.Division = ? AND c.Country = ?";

		try {
			int divisionId = 0;
			PreparedStatement ps = DataBaseConnection.getConnection().prepareStatement(statement);
			ps.setString(1, division);
			ps.setString(2, country);
			//Executes Query
			ResultSet resultSet = ps.executeQuery();
			if (resultSet.next()) {
				divisionId = resultSet.getInt("Division_ID");
			}
			return divisionId;
		} catch (SQLException throwables) {
			//throwables.printStackTrace();
			throwables.getMessage();
		}
		return 0;
	}

	/**
	 * Queries and sets customer's Division and Country.
	 *
	 * @param customer customer information necessary to perform table joins.
	 */
	public static void setCustomerLocation(Customer customer) {

		int customerId = customer.getCustomerId();
		String queryDivision = "SELECT d.Division FROM didactic_meme.dbo.divisions d JOIN didactic_meme.dbo" +
				".customers" +
				" " +
				"cu on cu.Division_ID " +
				"=" +
				" " +
				"d.Division_ID JOIN didactic_meme.dbo.countries co ON co.Country_ID = d.Country_ID WHERE Customer_ID" +
				" " +
				"=" +
				" ?";
		String queryCountry = "SELECT co.Country FROM didactic_meme.dbo.countries co JOIN didactic_meme.dbo" +
				".divisions" +
				" " +
				"d ON co.Country_ID = " +
				"d" +
				".Country_ID JOIN didactic_meme.dbo.customers cu ON cu.Division_ID = d.Division_ID WHERE Customer_ID" +
				" " +
				"=" +
				" ?";

		try {
			PreparedStatement psd = DataBaseConnection.getConnection().prepareStatement(queryDivision);
			psd.setInt(1, customerId);
			ResultSet resultSetD = psd.executeQuery();
			resultSetD.next();
			customer.setCustomerDivision(resultSetD.getString("Division"));


			PreparedStatement psc = DataBaseConnection.getConnection().prepareStatement(queryCountry);
			psc.setInt(1, customerId);
			ResultSet resultSetC = psc.executeQuery();
			resultSetC.next();
			customer.setCustomerCountry(resultSetC.getString("Country"));

		} catch (SQLException throwables) {
			System.out.println(throwables.getMessage());
		}
	}

	public static Customer lookUpCustomer(int customerID) throws SQLException {
		String search = "SELECT cu.Customer_Name,cu.Address,cu.Postal_Code,cu.Phone,cu.Create_Date,cu.Created_By,cu" +
				".Last_Update,cu" +
				".Last_Updated_By,d.Division,co.Country FROM  didactic_meme.dbo.customers cu JOIN  didactic_meme.dbo" +
				".divisions d ON cu.Division_ID = d" +
				".Division_ID JOIN  didactic_meme.dbo.countries co ON d.Country_ID = co.Country_ID WHERE Customer_ID" +
				" " +
				"=" +
				" ?";
		PreparedStatement ps = DataBaseConnection.getConnection().prepareStatement(search);
		ps.setInt(1, customerID);
		ResultSet rs = ps.executeQuery();
		rs.next();
		String name = rs.getString("Customer_Name");
		String address = rs.getString("Address");
		String zip = rs.getString("Postal_Code");
		String phoneNumber = rs.getString("Phone");
		Timestamp createDate = rs.getTimestamp("Create_Date");
		String createdBy = rs.getString("Created_By");
		Timestamp updateTime = rs.getTimestamp("Last_Update");
		String updatedBy = rs.getString("Last_Updated_By");
		String division = rs.getString("Division");
		String country = rs.getString("Country");


		return new Customer(customerID, name, address, zip, phoneNumber,
				createDate, createdBy, updateTime, updatedBy, country, division);
	}
}
