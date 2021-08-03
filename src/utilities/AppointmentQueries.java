package utilities;

import controller.SignInScreen;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import model.Appointment;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class AppointmentQueries {
	private static final ObservableList<Appointment> appointmentObservableList = FXCollections.observableArrayList();

	/**
	 * Converts time to UTC for uniform database storage.
	 *
	 * @param ts system's default time.
	 * @return Time in UTC
	 */
	public static Timestamp timeConversionUTC(Timestamp ts) {

		LocalDateTime ldt = ts.toLocalDateTime();
		ZonedDateTime zdt = ldt.atZone(ZoneId.of(ZoneId.systemDefault().toString()));
		ZonedDateTime utczdt = zdt.withZoneSameInstant(ZoneId.of("UTC"));
		LocalDateTime ldtIn = utczdt.toLocalDateTime();

		return Timestamp.valueOf(ldtIn);

	}

	/**
	 * Converts from UTC to the system's default timezone.
	 *
	 * @param ts time in UTC
	 * @return time in system's default time.
	 */
	public static Timestamp timeConversionLocal(Timestamp ts) {
		LocalDateTime ldtIn = ts.toLocalDateTime();
		ZonedDateTime zdtOut = ldtIn.atZone(ZoneId.of("UTC"));
		ZonedDateTime zdtToLocalTZ = zdtOut.withZoneSameInstant(ZoneId.of(ZoneId.systemDefault().toString()));
		LocalDateTime ldtOut = zdtToLocalTZ.toLocalDateTime();

		return Timestamp.valueOf(ldtOut);
	}

	/**
	 * Queries appointments within 15 minutes of login-in.
	 *
	 * @return ObservableList of upcoming appointments.
	 * @throws SQLException
	 */
	public static ObservableList<Appointment> getUpcomingAppointments() throws SQLException {

		ObservableList<Appointment> allAppointments = FXCollections.observableArrayList();
		int activeUser = SignInScreen.getActiveUser().getUserID();
		Timestamp soon =
				Timestamp.valueOf(String.valueOf(timeConversionUTC(Timestamp.valueOf(LocalDateTime.now().plusMinutes(15)))));
		Timestamp now = Timestamp.valueOf(String.valueOf(timeConversionUTC(Timestamp.valueOf(LocalDateTime.now()))));

		PreparedStatement ps = DataBaseConnection.getConnection().prepareStatement("Select * From appointments Where" +
				" " +
				"Start Between ? And ? And User_ID = ? ");

		ps.setTimestamp(1, now);
		ps.setTimestamp(2, soon);
		ps.setInt(3, activeUser);

		ResultSet rs = ps.executeQuery();

		while (rs.next()) {
			// get data from the returned rows
			int appointmentID = rs.getInt("Appointment_ID");
			String title = rs.getString("Title");
			String description = rs.getString("Description");
			String location = rs.getString("Location");
			String type = rs.getString("Type");
			Timestamp startDateTime = rs.getTimestamp("Start");
			Timestamp endDateTime = rs.getTimestamp("End");
			Timestamp lastUpdated = rs.getTimestamp("Last_Update");
			String lastUpdatedBy = rs.getString("Last_Updated_By");
			int customerID = rs.getInt("Customer_ID");
			int userID = rs.getInt("User_ID");
			int contactID = rs.getInt("Contact_ID");

			Appointment appointment = new Appointment(appointmentID, title, description, location, type, startDateTime
					, endDateTime, lastUpdated, lastUpdatedBy, contactID, customerID, userID);

			allAppointments.add(appointment);

		}
		return allAppointments;

	}

	/**
	 * Queries all appointments
	 *
	 * @return ObservableList of appointments.
	 */
	public static ObservableList<Appointment> getAllAppointments() {
		appointmentObservableList.clear();
		String findApp = "SELECT * FROM appointments a Left Outer Join contacts as c ON a.Contact_ID = c.Contact_ID";

		try {
			PreparedStatement ps = DataBaseConnection.getConnection().prepareStatement(findApp);
			ResultSet rs = ps.executeQuery();

			while (rs.next()) {
				int appointmentId = rs.getInt("Appointment_ID");
				String title = rs.getString("Title");
				String description = rs.getString("Description");
				String location = rs.getString("Location");
				String appointmentType = rs.getString("Type");
				int customerId = rs.getInt("Customer_ID");

				Timestamp startDateTime = rs.getTimestamp("Start");
				Timestamp startTS = timeConversionLocal(startDateTime);


				Timestamp endDateTime = rs.getTimestamp("End");
				Timestamp endTS = timeConversionLocal(endDateTime);


				Timestamp createDate = timeConversionLocal(rs.getTimestamp("Create_Date"));

				String createdBy = rs.getString("Created_By");
				Timestamp lastUpdate = timeConversionLocal(rs.getTimestamp("Last_Update"));
				String updatedBy = rs.getString("Last_Updated_By");


				int contactId = rs.getInt("Contact_ID");
				int userId = rs.getInt("User_ID");
				String contactName = rs.getString("Contact_Name");
				String contactEmail = rs.getString("Email");


				Appointment appointment = new Appointment(appointmentId, title, description, location, appointmentType
						, startTS, endTS, createDate, createdBy, lastUpdate, updatedBy, contactId, contactName,
						customerId, userId);
				appointment.setContactEmail(contactEmail);
				addAppointment(appointment);


			}

		} catch (SQLException throwables) {
			throwables.printStackTrace();
		}
		return appointmentObservableList;
	}

	/**
	 * Adds appointment to an ObservableList which is used to populate the appointments TableView.
	 *
	 * @param newAppointment appointment information from controller class.
	 */
	public static void addAppointment(Appointment newAppointment) {
		appointmentObservableList.add(newAppointment);
	}

	/**
	 * Updates existing appointment in the database.
	 *
	 * @param appointment appointment information gathered from controller class.
	 */
	public static boolean updateAppointment(Appointment appointment) {
		//appointmentObservableList.clear();


		String updateAppointment = "Update appointments Set Title=?,Description=?,Location=?," +
				"Type=?,Start=?,End=?,Last_Update=?,Last_Updated_By=?,Customer_ID=?," +
				"User_ID=?,Contact_ID=? Where Appointment_ID = ?";

		try {
			PreparedStatement ps = DataBaseConnection.getConnection().prepareStatement(updateAppointment);
			int appointmentID = appointment.getAppointmentId();
			String appointmentTitle = appointment.getTitle();
			String description = appointment.getDescription();
			String location = appointment.getLocation();
			String appointmentType = appointment.getAppointmentType();

			Timestamp startTime = timeConversionUTC(appointment.getStartDateTime());

			Timestamp endTime = timeConversionUTC(appointment.getEndDateTime());

			Timestamp lastUpDate = timeConversionUTC(appointment.getLastUpdate());
			int userId = appointment.getUserId();
			int customerId = appointment.getCustomerId();
			int contactId = appointment.getContactId();
			String updatedBy = appointment.getUpdatedBy();


			ps.setString(1, appointmentTitle);
			ps.setString(2, description);
			ps.setString(3, location);
			ps.setString(4, appointmentType);
			ps.setTimestamp(5, startTime);
			ps.setTimestamp(6, endTime);
			ps.setTimestamp(7, lastUpDate);
			ps.setString(8, updatedBy);
			ps.setInt(9, customerId);
			ps.setInt(10, userId);
			ps.setInt(11, contactId);
			ps.setInt(12, appointmentID);

			ps.execute();
			return true;
		} catch (SQLException throwables) {
			throwables.printStackTrace();
			return false;
		}


	}

	/**
	 * Inserts new appointment in the database.
	 *
	 * @param appointment appointment information from controller class.
	 */
	public static void insertAppointment(Appointment appointment) {
		appointmentObservableList.clear();
		String addAppointment = "Insert Into appointments VALUES(NULL,?,?,?,?,?,?,?,?,?,?,?,?,?)";
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

		try {
			PreparedStatement ps = DataBaseConnection.getConnection().prepareStatement(addAppointment,
					PreparedStatement.RETURN_GENERATED_KEYS);
			String appointmentTitle = appointment.getTitle();
			String description = appointment.getDescription();
			String location = appointment.getLocation();
			String appointmentType = appointment.getAppointmentType();

			Timestamp startTime = timeConversionUTC(appointment.getStartDateTime());
			Timestamp endTime = timeConversionUTC(appointment.getEndDateTime());
			Timestamp createDate = timeConversionUTC(appointment.getCreateDate());
			Timestamp lastUpDate = timeConversionUTC(appointment.getLastUpdate());


			int userId = appointment.getUserId();
			int customerId = appointment.getCustomerId();
			int contactId = appointment.getContactId();

			String createdBy = appointment.getCreatedBy();
			String updatedBy = appointment.getUpdatedBy();

			ps.setString(1, appointmentTitle);
			ps.setString(2, description);
			ps.setString(3, location);
			ps.setString(4, appointmentType);
			ps.setTimestamp(5, startTime);
			ps.setTimestamp(6, endTime);
			ps.setTimestamp(7, createDate);
			ps.setString(8, createdBy);
			ps.setTimestamp(9, lastUpDate);
			ps.setString(10, updatedBy);
			ps.setInt(11, customerId);
			ps.setInt(12, userId);
			ps.setInt(13, contactId);

			ps.execute();


		} catch (SQLException throwables) {
			throwables.printStackTrace();
		}


	}

	/**
	 * Deletes appointment from the database.
	 *
	 * @param appointment item selected from table.
	 */
	public static boolean deleteAppointment(Appointment appointment) {
		boolean results = true;
		int appointmentId = appointment.getAppointmentId();
		String deleteAppointments = "Delete from appointments Where Appointment_ID = ?";
		try {
			PreparedStatement psa = DataBaseConnection.getConnection().prepareStatement(deleteAppointments);
			psa.setInt(1, appointmentId);
			results = psa.execute();


		} catch (SQLException throwables) {
			throwables.printStackTrace();
		}


		return results;
	}

	/**
	 * Queries all appointments scheduled within the current month.
	 *
	 * @return ObservableList appointments.
	 */
	public static ObservableList<Appointment> getMonthlyAppointments() {
		appointmentObservableList.clear();
		String monthly = "Select * From appointments a Left Outer Join contacts c On a.Contact_ID = c.Contact_ID " +
				"Where" +
				" (MONTH(Start)=MONTH(now()) and YEAR(Start)=YEAR(now()))";
		try {
			PreparedStatement ps = DataBaseConnection.getConnection().prepareStatement(monthly);
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				int appointmentId = rs.getInt("Appointment_ID");
				String title = rs.getString("Title");
				String description = rs.getString("Description");
				String location = rs.getString("Location");
				String appointmentType = rs.getString("Type");
				int customerId = rs.getInt("Customer_ID");


				ZoneId systemDefault = ZoneId.systemDefault();

				LocalDateTime startDateTime = rs.getTimestamp("Start").toLocalDateTime();
				ZonedDateTime zdtStart = startDateTime.atZone(ZoneId.of("UTC"));
				ZonedDateTime newLocalStart = zdtStart.withZoneSameInstant(systemDefault);
				Timestamp startTS = Timestamp.valueOf(newLocalStart.toLocalDateTime());


				LocalDateTime endDateTime = rs.getTimestamp("End").toLocalDateTime();
				ZonedDateTime zdtEnd = endDateTime.atZone(ZoneId.of("UTC"));
				ZonedDateTime newLocalEnd = zdtEnd.withZoneSameInstant(systemDefault);
				Timestamp endTS = Timestamp.valueOf(newLocalEnd.toLocalDateTime());

				Timestamp createDate = rs.getTimestamp("Create_Date");
				String createdBy = rs.getString("Created_By");
				Timestamp lastUpdate = rs.getTimestamp("Last_Update");
				String updatedBy = rs.getString("Last_Updated_By");


				int contactId = rs.getInt("Contact_ID");
				int userId = rs.getInt("User_ID");
				String contactName = rs.getString("Contact_Name");
				String contactEmail = rs.getString("Email");

				Appointment appointment = new Appointment(appointmentId, title, description, location, appointmentType
						, startTS, endTS, createDate, createdBy, lastUpdate, updatedBy, contactId, contactName,
						customerId, userId);
				appointment.setContactEmail(contactEmail);
				addAppointment(appointment);

			}
		} catch (SQLException throwables) {
			throwables.printStackTrace();
		}

		return appointmentObservableList;
	}

	/**
	 * Queries all appointments scheduled within the current week.
	 *
	 * @return ObservableList appointments.
	 */
	public static ObservableList<Appointment> getWeeklyAppointments() {
		appointmentObservableList.clear();
		String monthly = "Select * From appointments a Left Outer Join contacts c On a.Contact_ID = c.Contact_ID " +
				"Where" +
				" (Week(Start)=Week(now()) and YEAR(Start)=YEAR(now()))";
		try {
			PreparedStatement ps = DataBaseConnection.getConnection().prepareStatement(monthly);
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				int appointmentId = rs.getInt("Appointment_ID");
				String title = rs.getString("Title");
				String description = rs.getString("Description");
				String location = rs.getString("Location");
				String appointmentType = rs.getString("Type");
				int customerId = rs.getInt("Customer_ID");


				ZoneId systemDefault = ZoneId.systemDefault();

				LocalDateTime startDateTime = rs.getTimestamp("Start").toLocalDateTime();
				ZonedDateTime zdtStart = startDateTime.atZone(ZoneId.of("UTC"));
				ZonedDateTime newLocalStart = zdtStart.withZoneSameInstant(systemDefault);
				Timestamp startTS = Timestamp.valueOf(newLocalStart.toLocalDateTime());


				LocalDateTime endDateTime = rs.getTimestamp("End").toLocalDateTime();
				ZonedDateTime zdtEnd = endDateTime.atZone(ZoneId.of("UTC"));
				ZonedDateTime newLocalEnd = zdtEnd.withZoneSameInstant(systemDefault);
				Timestamp endTS = Timestamp.valueOf(newLocalEnd.toLocalDateTime());

				Timestamp createDate = rs.getTimestamp("Create_Date");
				String createdBy = rs.getString("Created_By");
				Timestamp lastUpdate = rs.getTimestamp("Last_Update");
				String updatedBy = rs.getString("Last_Updated_By");


				int contactId = rs.getInt("Contact_ID");
				int userId = rs.getInt("User_ID");
				String contactName = rs.getString("Contact_Name");
				String contactEmail = rs.getString("Email");

				Appointment appointment = new Appointment(appointmentId, title, description, location, appointmentType
						, startTS, endTS, createDate, createdBy, lastUpdate, updatedBy, contactId, contactName,
						customerId, userId);
				appointment.setContactEmail(contactEmail);
				addAppointment(appointment);

			}
		} catch (SQLException throwables) {
			throwables.printStackTrace();
		}
		return appointmentObservableList;
	}
}


