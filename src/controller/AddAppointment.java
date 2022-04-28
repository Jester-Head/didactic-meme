package controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import model.Appointment;
import model.Customer;
import model.User;
import utilities.AppointmentQueries;
import utilities.CustomerQueries;
import utilities.DateTimeUtility;
import utilities.UserQueries;
//import utilities.QueryClass;

import java.io.*;
import java.net.URL;
import java.sql.*;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

/**
 * Controller class for adding appointments.
 * Text fields are used to collect the appointment name, customer ID,user ID,location,and description.DatePickers and
 * Spinners are used to get the event times.
 * Appointment IDs are auto-generated, and first-level division,country data and contact information are collected
 * using separate combo boxes.
 */
public class AddAppointment implements Initializable {
	@FXML
	public RadioButton endAmRb;
	@FXML
	public ToggleGroup endAmPmTg;
	@FXML
	public RadioButton endPmRb;
	@FXML
	public RadioButton startPmRb;
	@FXML
	public ToggleGroup startAmPmTg;
	@FXML
	public RadioButton startAmRb;
	@FXML
	public Label appointmentIdLbl;
	@FXML
	private ComboBox<User> userCb;
	@FXML
	private ComboBox<Customer> customerCb;
	@FXML
	private Spinner<Integer> selectEndMinutesSpn;

	@FXML
	private Spinner<Integer> selectStartMinutesSpn;

	@FXML
	private TextField locationTxt;

	@FXML
	private TextField appointmentNameTxt;

	@FXML
	private TextField appointmentIdTxt;

	@FXML
	private Button backBtn;

	@FXML
	private Button saveBtn;

	@FXML
	private DatePicker startDateDp;

	@FXML
	private DatePicker endDateDp;

	@FXML
	private Spinner<Integer> selectEndHoursSpn;

	@FXML
	private Spinner<Integer> selectStartHoursSpn;

	@FXML
	private TextField descriptionTxt;

	@FXML
	private ComboBox<String> appointmentTypeCb;
	private String title;
	private String description;
	private String location;
	private Timestamp startDateTime;
	private Timestamp endDateTime;
	private int customerIdInt;
	private int userIdInt;
	private StringConverter<String> stringConverter;


	public AddAppointment() {
	}

	/**
	 * Redirects to Appointments screen.
	 *
	 * @param event back button clicked.
	 * @throws IOException
	 */
	@FXML
	public void onClickGoBack(MouseEvent event) throws IOException {

		//Sets location
		FXMLLoader loader = new FXMLLoader();
		loader.setLocation(getClass().getResource("/view/Appointments.fxml"));
		loader.load();

		// Switches to Appointments view
		Stage stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
		Parent scene = loader.getRoot();
		stage.setScene((new Scene(scene)));
		stage.show();

	}

	/**
	 * Saves appointment information collected from form.
	 *
	 * @param event save button clicked
	 * @throws SQLException
	 * @throws IOException
	 */
	@FXML
	public void onClickSaveAppointment(MouseEvent event) throws SQLException, IOException {
		//Checking for valid input except date/time fields.
		if (validateFields()) {
			int appointmentId = 0;
			Timestamp lastUpdate = Timestamp.valueOf(LocalDateTime.now());
			String updatedBy = SignInScreen.getActiveUser().getUserName();


			Timestamp createDate = Timestamp.valueOf(LocalDateTime.now());
			String createdBy = SignInScreen.getActiveUser().getUserName();


			Appointment appointment = new Appointment(appointmentId, title, description, location
					, startDateTime, endDateTime, createDate, createdBy, lastUpdate, updatedBy,
					customerIdInt, userIdInt);
			//Inserts appointment if date selection is valid and appointment doesn't overlap with an existing one.
			if (valiDate() && checkOverlaps(appointment)) {

				AppointmentQueries.insertAppointment(appointment);
				onClickGoBack(event);
			}

		}

	}

	/**
	 * Enables user to select appointment end date.
	 *
	 * @param event ComboBox selection.
	 */
	@FXML
	public void selectStartDate(ActionEvent event) {
		endDateDp.setDisable(false);
	}

	/**
	 * Checks for logical errors to prevent scheduling an appointment outside business hours (8:00 a.m.
	 * to 10:00 p.m. EST, including weekends).
	 *
	 * @return true if appointment passes all logical checks. Otherwise, returns false.
	 */
	public boolean valiDate() {
		Alert alert = new Alert(Alert.AlertType.ERROR);
		alert.setHeaderText("Invalid Date/Time Selection");
		alert.setContentText("Appointment must be scheduled after " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("MM-dd-yyyy hh:mm a")));
		//Converts appointment times from Timestamp to LocalDateTime
		LocalDateTime startDT = startDateTime.toLocalDateTime();
		LocalDateTime endDT = endDateTime.toLocalDateTime();

		//Converts appointment times to ZoneDateTime using the system's default timezone
		ZonedDateTime startZdt = startDT.atZone(ZoneId.of(ZoneId.systemDefault().toString()));
		ZonedDateTime endZdt = endDT.atZone(ZoneId.of(ZoneId.systemDefault().toString()));

		//Converts timezone to EST
		ZonedDateTime localToEstStart = startZdt.withZoneSameInstant(ZoneId.of("America/New_York"));
		ZonedDateTime localToEstEnd = endZdt.withZoneSameInstant(ZoneId.of("America/New_York"));

		//Sets business hours to EST
		LocalDate startDate = startDT.toLocalDate();
		ZonedDateTime estStartDateTime = ZonedDateTime.of(startDate, LocalTime.of(8, 0),
				ZoneId.of("America/New_York"));

		LocalDate endDate = endDT.toLocalDate();
		ZonedDateTime estEndDateTime = ZonedDateTime.of(endDate, LocalTime.of(22, 0),
				ZoneId.of("America/New_York"));


		//Checks if the appointment is scheduled before the current time
		LocalDateTime currentTime = LocalDateTime.now();
		if (startDT.isEqual(currentTime) || endDT.isEqual(currentTime)) {
			alert.showAndWait();
			return false;
		}
		if (startDT.isBefore(currentTime) || (endDT.isBefore(currentTime))) {
			alert.showAndWait();
			return false;
		}
		if (endDT.isBefore(startDT) || endDT.isEqual(startDT)) {
			alert.showAndWait();
			return false;
		}

		//Prevents scheduling appointments before 8am EST
		if (localToEstStart.isBefore(estStartDateTime) | localToEstEnd.isBefore(estStartDateTime)) {
			alert.setHeaderText("Office is closed ");
			alert.setContentText("Please schedule appointments after 8:00 a.m. EST");
			alert.showAndWait();
			return false;
		}
		//Prevents scheduling appointments after 10 pm EST
		if (localToEstEnd.isAfter(estEndDateTime) | localToEstEnd.isAfter(estEndDateTime)) {
			alert.setHeaderText("Office closing ");
			alert.setContentText("Please schedule appointments before 10:00 p.m. EST");
			alert.showAndWait();
			return false;
		}
		return true;
	}

	/**
	 * Pulls information from texts fields and checks for valid input.
	 *
	 * @return true if all fields have the proper input. Otherwise, returns false.
	 */

	public boolean validateFields() {
		Alert alert = new Alert(Alert.AlertType.ERROR);
		try {
			String userIdStr = String.valueOf(userCb.getValue());
			String customerIdStr = String.valueOf(customerCb.getValue());
			String name = appointmentNameTxt.getText();
			String desc = descriptionTxt.getText();
			String loc = locationTxt.getText();

			String startDateStr = String.valueOf(startDateDp.getValue());

			String startHoursStr = String.valueOf(selectStartHoursSpn.getValue());
			String startMinutesStr = String.valueOf(selectStartMinutesSpn.getValue());


			//Checks for missing start times
			if (startDateStr.isBlank() | startHoursStr.isBlank() | startMinutesStr.isBlank()) {
				alert.setHeaderText("Missing Times");
				alert.setContentText("Please select a start date and time.");
				alert.showAndWait();
				return false;
			}
			String endDateStr = String.valueOf(endDateDp.getValue());
			String endHoursStr = String.valueOf(selectEndHoursSpn.getValue());
			String endMinutesStr = String.valueOf(selectEndMinutesSpn.getValue());

			if (endDateStr.isBlank() | endHoursStr.isBlank() | endMinutesStr.isBlank()) {
				alert.setHeaderText("Missing Times");
				alert.setContentText("Please select a end date and time.");
				alert.showAndWait();
				return false;
			}

			//Checking for blank fields
			if (name.isBlank() | desc.isBlank() | loc.isBlank() | customerIdStr.isBlank() | userIdStr.isBlank()) {
				alert.setContentText("Please fill out all text fields.");
				alert.showAndWait();
			}
				return false;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return true;
	}




	public LocalTime getTwelveHourTimes(Spinner<Integer> hoursSpin,Spinner<Integer> minutesSpin, ToggleGroup toggleGroup) {
		int value = hoursSpin.getValue();
		Toggle selectedToggle = toggleGroup.getSelectedToggle();
		LocalTime convertLocalTime;
		if (selectedToggle.equals(startAmRb) || selectedToggle.equals(endAmRb)) {
			if (value == 12){
				convertLocalTime = LocalTime.of(0,minutesSpin.getValue());
			}else {
				convertLocalTime = LocalTime.of(value,minutesSpin.getValue());
			}
		}else {
			convertLocalTime = LocalTime.of(value+12,minutesSpin.getValue());
		}
		return convertLocalTime;
	}


	/**
	 * Prevents user from double booking customers.
	 *
	 * @param appointment time input.
	 * @return true if the new appointment doesn't overlap with an existing appointment.
	 */
	public Boolean checkOverlaps(Appointment appointment) {
		Alert alert = new Alert(Alert.AlertType.ERROR);
		ObservableList<Appointment> allAppointments = AppointmentQueries.getAllAppointments();
		LocalDateTime newAppointmentStart = appointment.getStartDateTime().toLocalDateTime();
		LocalDateTime newAppointmentEnd = appointment.getEndDateTime().toLocalDateTime();
		alert.setHeaderText("Timeslot unavailable");
		alert.setContentText("Please change the time of the appointment");
		for (Appointment overlap : allAppointments) {
			LocalDateTime overlapStart = overlap.getStartDateTime().toLocalDateTime();
			LocalDateTime overlapEnd = overlap.getEndDateTime().toLocalDateTime();

			boolean overlapFlag =
					newAppointmentStart.isAfter(overlapStart) && newAppointmentStart.isBefore(overlapEnd);

			// Appointment A Starts between Appointment B Start-End
			//Appointment A Ends between Appointment B Start-End.
			if (newAppointmentEnd.isAfter(overlapStart) && newAppointmentEnd.isBefore(overlapEnd)) {
				overlapFlag = true;
			}
			/*
			 * Appointment A Start time = Appointment B Start time.
			 * Appointment A Start time = Appointment B End time.
			 * Appointment A End time = Appointment B End time
			 */
			if (newAppointmentStart.isEqual(overlapStart) || newAppointmentEnd.isEqual(overlapStart) || newAppointmentEnd.isEqual(overlapEnd)) {
				overlapFlag = true;
			}
			//Checks if Appointment A's customer is scheduled for Appointment B.
			if (overlapFlag && (appointment.getCustomerId() == overlap.getCustomerId())) {
				alert.showAndWait();
				return false;
			}
		}
		return true;
	}

	/**
	 * Sets the DatePicker,and Spinners for time selection. Also sets ComboBoxes with appointment types and contacts.
	 * Lambda expression is used to condense the code that adds restrictions to the DatePicker.
	 *
	 * @throws SQLException
	 */
	public void populateFields() throws SQLException {
		//Disables past calendar dates for start and end times.
		DateTimeUtility.setDate(startDateDp, endDateDp);
		//12hr format for start and end times
		String setSpinner = DateTimeUtility.populateHoursTwelve(selectStartHoursSpn);
		String dummyVar = DateTimeUtility.populateHoursTwelve(selectEndHoursSpn);
		if (setSpinner.equalsIgnoreCase("am")) {
			startAmRb.setSelected(true);
			endAmRb.setSelected(true);
		} else {
			startPmRb.setSelected(true);
			endPmRb.setSelected(true);
		}
		DateTimeUtility.populateMinutes(selectStartMinutesSpn);
		DateTimeUtility.populateMinutes(selectEndMinutesSpn);

		//TODO 24hr format

		ObservableList<User> users = FXCollections.observableArrayList(UserQueries.getAllUsers());
		userCb.setItems(users);

		//Set customers ComboBox
		ObservableList<Customer> customers = FXCollections.observableArrayList(CustomerQueries.getAllCustomers());
		customerCb.setItems(customers);


	}

	@Override
	public void initialize(URL url, ResourceBundle resourceBundle) {

		try {
			// Prevents user from selecting an end date before choosing starting date.
			endDateDp.setDisable(true);
			populateFields();
		} catch (SQLException throwables) {
			throwables.printStackTrace();
		}

	}

	public void testFunction() {

		String startHoursStr = String.valueOf(selectStartHoursSpn.getValue());
		String startMinutesStr = String.valueOf(selectStartMinutesSpn.getValue());
		if (!startHoursStr.matches("\\D") || !startMinutesStr.matches("\\D")) {
			Alert alert = new Alert(Alert.AlertType.ERROR);
			alert.setHeaderText("Invalid Format");
			alert.showAndWait();

		}
	}
}
