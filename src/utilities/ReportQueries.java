package utilities;

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



public class ReportQueries {

	private static ObservableList<Appointment> appointmentObservableList = FXCollections.observableArrayList();

	/**
	 * Queries selected contact's appointments.
	 * @param contactId contact ID associated with appointment ID.
	 * @return ObservableList appointments
	 * @throws SQLException
	 */
	public static ObservableList<Appointment> getContactAppointments(int contactId) throws SQLException {
		appointmentObservableList.clear();
		String contactSchedule = "Select c.Contact_ID,c.Contact_Name,a.Appointment_ID,a.Title,a.Description,a.Type,a" +
				".Start,a.End,a.Customer_ID From appointments a Right Outer Join contacts c on a.Contact_ID = c" +
				".Contact_ID Where c.Contact_ID = ?";

		PreparedStatement ps = DataBaseConnection.getConnection().prepareStatement(contactSchedule);
		ps.setInt(1, contactId);
		ResultSet rs = ps.executeQuery();
		while (rs.next()) {
			int appointmentId = rs.getInt("Appointment_ID");
			String title = rs.getString("Title");
			String description = rs.getString("Description");
			String appointmentType = rs.getString("Type");
			int customerId = rs.getInt("Customer_ID");


			LocalDateTime startDateTime = rs.getTimestamp("Start").toLocalDateTime();
			ZonedDateTime zdtStart = startDateTime.atZone(ZoneId.of("UTC"));
			ZonedDateTime newLocalStart = zdtStart.withZoneSameInstant(ZoneId.systemDefault());
			startDateTime = newLocalStart.toLocalDateTime();
			Timestamp startTS = Timestamp.valueOf(startDateTime);


			LocalDateTime endDateTime = rs.getTimestamp("End").toLocalDateTime();
			ZonedDateTime zdtEnd = endDateTime.atZone(ZoneId.of("UTC"));
			ZonedDateTime newLocalEnd = zdtEnd.withZoneSameInstant(ZoneId.systemDefault());
			Timestamp endTS = Timestamp.valueOf(newLocalEnd.toLocalDateTime());

			Appointment appointment = new Appointment(appointmentId, title, description, appointmentType
					, startTS, endTS, customerId);
			appointmentObservableList.add(appointment);

		}
		return appointmentObservableList;
	}

	/**
	 * Queries appointments in the given time frame and counts how many there are of each type.
	 *
	 * @param year  selected year.
	 * @param month selected month
	 * @return ObservableList appointments
	 */
	public static ObservableList<Appointment> getAppointmentTypeCount(int year, int month) throws SQLException {
		ObservableList<Appointment> typeCount = FXCollections.observableArrayList();
		String test = "Select Monthname(Start) AS Month, Type, Count(Type) AS Count From appointments Where Month" +
				"(Start)=? And year(Start)=? group by Month, Type ";


		PreparedStatement ps = DataBaseConnection.getConnection().prepareStatement(test);
		ps.setInt(1, month);
		ps.setInt(2, year);

		ResultSet rs = ps.executeQuery();
		while (rs.next()) {
			String monthName = rs.getString("Month");
			String type = rs.getString("Type");
			int count = rs.getInt("Count");
			Appointment typeAppointment = new Appointment();
			typeAppointment.setAppointmentType(type);
			typeAppointment.setTypeCount(count);
			typeCount.add(typeAppointment);

		}

		return typeCount;
	}

	/**
	 * Queries each the number of appointments each contact has or had in a given time frame.
	 *
	 * @param year  selected year.
	 * @param month selected month.
	 * @return ObservableList appointments
	 */
	public static ObservableList<Appointment> getContactAppointmentCount(int year, int month) throws SQLException {
		ObservableList<Appointment> contactCount = FXCollections.observableArrayList();

		String test = "Select Monthname(Start) AS Month,c.Contact_ID,c.Contact_Name,c.Email,COUNT(a.Appointment_ID) " +
				"As" +
				" Count From appointments a Left Outer Join contacts c on c.Contact_ID=a.Contact_ID Where Month " +
				"(Start)=? And year(Start)=? group by Month,Contact_ID; ";


		PreparedStatement ps = DataBaseConnection.getConnection().prepareStatement(test);
		ps.setInt(1, month);
		ps.setInt(2, year);

		ResultSet rs = ps.executeQuery();
		while (rs.next()) {
			String monthName = rs.getString("Month");
			int contactId = rs.getInt("Contact_ID");
			String contactName = rs.getString("Contact_Name");
			String email = rs.getString("Email");
			int count = rs.getInt("Count");
			Appointment contactAppointment = new Appointment(contactId, contactName, email);
			contactAppointment.setAppointmentCount(count);
			contactCount.add(contactAppointment);

		}
		return contactCount;
	}

}
