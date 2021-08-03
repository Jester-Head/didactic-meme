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
import model.Appointment;
import utilities.QueryClass;

import java.io.*;
import java.net.URL;
import java.sql.*;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

/**
 * Controller class for adding appointments.
 * Text fields are used to collect the appointment name, customer ID,user ID,location,and description.DatePickers and Spinners are used to get the event times.
 * Appointment IDs are auto-generated, and first-level division,country data and contact information are collected
 * using separate combo boxes.
 */
public class AddAppointment implements Initializable {

	@FXML
	private Spinner<Integer> selectEndMinsSpn;

	@FXML
	private Spinner<Integer> selectStartMinsSpn;

	@FXML
	private TextField locationTxt;

	@FXML
	private ComboBox<Appointment> contactCb;

	@FXML
	private TextField appointmentNameTxt;

	@FXML
	private TextField customerIdTxt;

	@FXML
	private TextField userIdTxt;

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
	private String appointmentType;
	private Timestamp startDateTime;
	private Timestamp endDateTime;
	private int contactId;
	private int customerIdInt;
	private int userIdInt;


	public AddAppointment() {
	}

	/**
	 * Redirects to Appointments screen.
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


			Appointment appointment = new Appointment(appointmentId, title, description, location, appointmentType
					, startDateTime, endDateTime, createDate, createdBy, lastUpdate, updatedBy, contactId,
					customerIdInt, userIdInt);
			//Inserts appointment if date selection is valid and appointment doesn't overlap with an existing one.
			if (valiDate(appointment) && checkOverlaps(appointment)) {

				QueryClass.insertAppointment(appointment);
				onClickGoBack(event);
			}

		}

	}

	/**
	 * Enables user to select appointment end date.
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
	 * @param appointment model of an appointment
	 * @return true if appointment passes all logical checks. Otherwise, returns false.
	 */
	public boolean valiDate(Appointment appointment) {
		Alert alert = new Alert(Alert.AlertType.ERROR);
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
		if(startDate.isEqual(ZonedDateTime.now().toLocalDate()) || endDate.isEqual(ZonedDateTime.now().toLocalDate())){
			if (startDT.isBefore(ZonedDateTime.now().toLocalDateTime()) || endDT.isBefore(ZonedDateTime.now()
					.toLocalDateTime()) | endDT.isBefore(startDT)) {
				alert.setHeaderText("Invalid Date/Time Selection");
				alert.setContentText("Appointment must be scheduled after "+ LocalDateTime.now().format(DateTimeFormatter.ofPattern("MM-dd hh:mm")));
				alert.showAndWait();
				return false;
			}
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
		String contactIdStr = String.valueOf(contactCb.getValue());
		String userIdStr = userIdTxt.getText();
		String type = String.valueOf(appointmentTypeCb.getValue());
		String customerIdStr = customerIdTxt.getText();
		String name = appointmentNameTxt.getText();
		String desc = descriptionTxt.getText();
		String loc = locationTxt.getText();

		//Assign values as strings to avoid multiple try/catch blocks
		String startDateStr = String.valueOf(startDateDp.getValue());
		String startHoursStr = String.valueOf(selectStartHoursSpn.getValue());
		String startMinsStr = String.valueOf(selectStartMinsSpn.getValue());
		String endDateStr = String.valueOf(endDateDp.getValue());
		String endHoursStr = String.valueOf(selectEndHoursSpn.getValue());
		String endMinsStr = String.valueOf(selectEndMinsSpn.getValue());

		//Checks for missing start times
		if (startDateStr.isBlank() | startHoursStr.isBlank() | startMinsStr.isBlank()) {
			alert.setHeaderText("Missing Times");
			alert.setContentText("Please select a start date and time.");
			alert.showAndWait();
			return false;
		}
		//Checks for missing end times
		if (endDateStr.isBlank() | endHoursStr.isBlank() | endMinsStr.isBlank()) {
			alert.setHeaderText("Missing Times");
			alert.setContentText("Please select an end date and time.");
			alert.showAndWait();
			return false;
		}

		//Checking for blank fields
		if (name.isBlank() | desc.isBlank() | loc.isBlank() | type.isBlank() | customerIdStr.isBlank() | userIdStr.isBlank() | contactIdStr.isBlank()) {
			alert.setContentText("Please fill out all text fields.");
			alert.showAndWait();
			return false;
		}

		//User ID must only contain integers
		if (userIdStr.matches("\\D")) {
			alert.setHeaderText("Invalid User ID");
			alert.setContentText("User ID must only contain numerical values.");
			alert.showAndWait();
			return false;
		}
		//Customer ID must only contain integers
		if (customerIdStr.matches("\\D")) {
			alert.setHeaderText("Invalid Customer ID");
			alert.setContentText("Customer ID must only contain numerical values.");
			alert.showAndWait();
			return false;
		}

		//Stores data as the appropriate type
		LocalDate startDate = startDateDp.getValue();
		LocalDate endDate = endDateDp.getValue();
		int startHours = selectStartHoursSpn.getValue();
		int startMins = selectStartMinsSpn.getValue();
		LocalTime startTime = LocalTime.of(startHours, startMins);
		this.startDateTime = Timestamp.valueOf(LocalDateTime.of(startDate, startTime));
		int endHours = selectEndHoursSpn.getValue();
		int endMins = selectEndHoursSpn.getValue();
		LocalTime endTime = LocalTime.of(endHours, endMins);
		this.endDateTime = Timestamp.valueOf(LocalDateTime.of(endDate, endTime));
		this.customerIdInt = Integer.parseInt(customerIdTxt.getText());
		this.userIdInt = Integer.parseInt(userIdTxt.getText());
		this.contactId = contactCb.getSelectionModel().getSelectedItem().getContactId();
		this.appointmentType = type;
		this.title = name;
		this.description = desc;
		this.location = loc;

		return true;
	}

	/**
	 * Prevents user from double booking customers.
	 * @param appointment time input.
	 * @return  true if the new appointment doesn't overlap with an existing appointment.
	 * @throws SQLException
	 */
	public Boolean checkOverlaps(Appointment appointment) throws SQLException {
		Alert alert = new Alert(Alert.AlertType.ERROR);
		ObservableList<Appointment> allAppointments = QueryClass.getAllAppointments();
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
			if (overlapFlag == true && (appointment.getCustomerId() == overlap.getCustomerId())) {
				alert.showAndWait();
				return false;
			}
		}
		return true;
	}

	/**
	 *
	 *  Sets the DatePicker,and Spinners for time selection. Also sets ComboBoxes with appointment types and contacts.
	 *  Lambda expression is used to condense the code that adds restrictions to the DatePicker.
	 * @throws SQLException
	 */
	public void populateFields() throws SQLException {
		LocalDate today = LocalDate.now();

		// Disables past calendar dates for start and end times.
		startDateDp.setDayCellFactory(picker -> new DateCell() {
			public void updateItem(LocalDate date, boolean empty) {
				super.updateItem(date, empty);
				setDisable(empty || date.isBefore(today));
			}
		});
		endDateDp.setDayCellFactory(picker -> new DateCell() {
			public void updateItem(LocalDate date, boolean empty) {
				super.updateItem(date, empty);
				LocalDate start = startDateDp.getValue();
				setDisable(empty || date.isBefore(today) || date.isBefore(start));
			}
		});


		//Start Times
		SpinnerValueFactory<Integer> startHours = new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 23,
				LocalTime.now().getHour());
		SpinnerValueFactory<Integer> startMinutes = new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 59,
				LocalTime.now().plusMinutes(5).getMinute());
		startHours.setWrapAround(true);
		startMinutes.setWrapAround(true);

		selectStartHoursSpn.setValueFactory(startHours);
		selectStartMinsSpn.setValueFactory(startMinutes);

		//End Times
		SpinnerValueFactory<Integer> endHours = new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 22,
				LocalTime.now().plusHours(1).getHour());
		SpinnerValueFactory<Integer> endMinutes = new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 59,
				LocalTime.now().plusMinutes(5).getMinute());
		endHours.setWrapAround(true);
		endMinutes.setWrapAround(true);
		selectEndHoursSpn.setValueFactory(endHours);
		selectEndMinsSpn.setValueFactory(endMinutes);

		//Appointment Types
		ObservableList<String> types = FXCollections.observableArrayList("De-Briefing", "Planning Session", "Lunch " +
				"Meeting", "Other");
		appointmentTypeCb.setItems(types);

		//Contacts
		ObservableList<Appointment> contacts = FXCollections.observableArrayList(QueryClass.queryContacts());
		contactCb.setItems(contacts);

	}

	@Override
	public void initialize(URL url, ResourceBundle resourceBundle) {
		try {
			// Prevents error cause by selecting an end date before selecting a start date.
			endDateDp.setDisable(true);
			populateFields();
		} catch (SQLException throwables) {
			throwables.printStackTrace();
		}

	}
}
