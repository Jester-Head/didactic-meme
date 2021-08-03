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
import model.User;
import utilities.AppointmentQueries;
import utilities.ContactQueries;


import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.*;
import java.time.chrono.ChronoZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;
/**
 * Controller class for updating appointments.All original information is displayed.
 * Text fields are used to collect the appointment name, customer ID,user ID,location,and description.DatePickers and Spinners are used to get the event times.
 * Appointment IDs are disabled and first-level division,country data and contact information are collected
 * using separate combo boxes.
 */
public class UpdateAppointment implements Initializable {

	@FXML
	private TextField appointmentIdTxt;

	@FXML
	private TextField appointmentNameTxt;

	@FXML
	private DatePicker startDateDp;

	@FXML
	private DatePicker endDateDp;

	@FXML
	private Spinner<Integer> selectStartHoursSpn;

	@FXML
	private Spinner<Integer> selectStartMinsSpn;

	@FXML
	private Spinner<Integer> selectEndHoursSpn;

	@FXML
	private Spinner<Integer> selectEndMinsSpn;

	@FXML
	private TextField locationTxt;

	@FXML
	private ComboBox<Appointment> contactCb;

	@FXML
	private TextField descriptionTxt;

	@FXML
	private ComboBox<String> appointmentTypeCb;

	@FXML
	private TextField userIdTxt;

	@FXML
	private TextField customerIdTxt;

	@FXML
	private Button backBtn;

	@FXML
	private Button saveBtn;

	private String title;
	private String description;
	private String location;
	private String appointmentType;
	private Timestamp startDateTime;
	private Timestamp endDateTime;
	private int contactId;
	private int customerIdInt;
	private int userIdInt;
	private User activeUser;


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
	 * Collects modified appointment information to add to the database.
	 * @param event save button clicked.
	 * @throws IOException
	 * @throws SQLException
	 */
	@FXML
	public void onClickSaveAppointment(MouseEvent event) throws IOException, SQLException {

		if (validateFields()) {
			int appointmentId = Integer.parseInt(appointmentIdTxt.getText());

			Timestamp lastUpdate = Timestamp.valueOf(LocalDateTime.now());
			String updatedBy = SignInScreen.getActiveUser().getUserName();


			Appointment appointment = new Appointment(appointmentId, title, description, location, appointmentType
					, startDateTime, endDateTime, lastUpdate, updatedBy, contactId,
					customerIdInt, userIdInt);

			if (valiDate(appointment) && checkOverlaps(appointment)) {

				Appointment validAppointment = new Appointment(appointmentId, title, description, location,
						appointmentType
						, startDateTime, endDateTime, lastUpdate, updatedBy, contactId,
						customerIdInt, userIdInt);
				boolean success = AppointmentQueries.updateAppointment(validAppointment);

				if (success) {
					Alert alert = new Alert(Alert.AlertType.INFORMATION);
					alert.setHeaderText("Appointment Updated");
					alert.showAndWait();
					onClickGoBack(event);
				}

			}
		}


	}

	/**

	 * Displays error messages if input is incorrect.
	 */
	/**
	 * Prevents scheduling appointment outside business hours in EST.
	 * Displays error messages if input is incorrect.
	 * @param appointment information from text fields.
	 * @return true if all fields are valid.
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


		//Prevents scheduling appointment before 8am EST
		if (localToEstStart.isBefore(estStartDateTime) | localToEstEnd.isBefore(estStartDateTime)) {
			alert.setHeaderText("Office is closed ");
			alert.setContentText("Please schedule appointments after 8:00 a.m. EST");
			alert.showAndWait();
			return false;
		}
		//Prevents scheduling appointment after 10 pm EST
		if (localToEstEnd.isAfter(estEndDateTime) | localToEstEnd.isAfter(estEndDateTime)) {
			alert.setHeaderText("Office closing ");
			alert.setContentText("Please schedule appointments before 10:00 p.m. EST");
			alert.showAndWait();
			return false;
		}
		return true;
	}

	/**
	 * Provides input validation for text fields.
	 * @return true if all input passes error checks,Otherwise, false.
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

		//Assigns values as strings for convenient error checks
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
		int endMins = selectEndMinsSpn.getValue();
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
	 * Prevents user from double booking contacts and customers.
	 *
	 * @return true if the new appointment doesn't overlap with an existing appointment.
	 * @throws SQLException
	 */
	public Boolean checkOverlaps(Appointment appointment) throws SQLException {
		Alert alert = new Alert(Alert.AlertType.ERROR);
		ObservableList<Appointment> allAppointments = AppointmentQueries.getAllAppointments();
		LocalDateTime newAppointmentStart = appointment.getStartDateTime().toLocalDateTime();
		LocalDateTime newAppointmentEnd = appointment.getEndDateTime().toLocalDateTime();
		alert.setHeaderText("Timeslot unavailable");
		alert.setContentText("Please change the time of the appointment");

		for (Appointment overlap : allAppointments) {
			LocalDateTime overlapStart = overlap.getStartDateTime().toLocalDateTime();
			LocalDateTime overlapEnd = overlap.getEndDateTime().toLocalDateTime();

			boolean overlapFlag = newAppointmentStart.isAfter(overlapStart) && newAppointmentStart.isBefore(overlapEnd);

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


	@FXML
	public void selectEndDate(ActionEvent event) {

	}

	@FXML
	public void selectStartDate(ActionEvent event) {

	}

	/**
	 * Sets texts fields with selected appointment information.
	 * @param selectedItem Tableview selection.
	 */
	public void transferInformation(Appointment selectedItem) {

		customerIdTxt.setText(String.valueOf(selectedItem.getCustomerId()));
		appointmentIdTxt.setText(String.valueOf(selectedItem.getAppointmentId()));
		appointmentTypeCb.setValue(selectedItem.getAppointmentType());
		appointmentNameTxt.setText(selectedItem.getTitle());
		userIdTxt.setText(String.valueOf(selectedItem.getUserId()));
		descriptionTxt.setText(selectedItem.getDescription());
		locationTxt.setText(selectedItem.getLocation());
		Appointment appointment = ContactQueries.getContactInfo(selectedItem.getContactId());
		contactCb.setValue(appointment);

		//Disables past dates for start and end times
		startDateDp.setDayCellFactory(picker -> new DateCell() {
			public void updateItem(LocalDate date, boolean empty) {
				super.updateItem(date, empty);
				LocalDate today = LocalDate.now();
				setDisable(empty || date.isBefore(today));
			}
		});
		endDateDp.setDayCellFactory(picker -> new DateCell() {
			public void updateItem(LocalDate date, boolean empty) {
				super.updateItem(date, empty);
				LocalDate today = LocalDate.now();
				LocalDate start = startDateDp.getValue();
				setDisable(empty || date.isBefore(today) | date.isBefore(start));
			}
		});

		startDateDp.setValue(selectedItem.getStartDateTime().toLocalDateTime().toLocalDate());
		endDateDp.setValue(selectedItem.getEndDateTime().toLocalDateTime().toLocalDate());


		//Start Times
		SpinnerValueFactory<Integer> startHours = new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 23);
		SpinnerValueFactory<Integer> startMinutes = new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 59);
		startHours.setWrapAround(true);
		startMinutes.setWrapAround(true);
		startHours.setValue(selectedItem.getStartDateTime().toLocalDateTime().toLocalTime().getHour());
		startMinutes.setValue(selectedItem.getStartDateTime().toLocalDateTime().toLocalTime().getMinute());
		selectStartHoursSpn.setValueFactory(startHours);
		selectStartMinsSpn.setValueFactory(startMinutes);

		//End Times
		SpinnerValueFactory<Integer> endHours = new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 23);
		SpinnerValueFactory<Integer> endMinutes = new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 59);
		endHours.setWrapAround(true);
		endMinutes.setWrapAround(true);
		endHours.setValue(selectedItem.getEndDateTime().toLocalDateTime().toLocalTime().getHour());
		endMinutes.setValue(selectedItem.getEndDateTime().toLocalDateTime().toLocalTime().getMinute());
		selectEndHoursSpn.setValueFactory(endHours);
		selectEndMinsSpn.setValueFactory(endMinutes);


	}

	@Override
	public void initialize(URL url, ResourceBundle resourceBundle) {
		try {
			populateFields();
		} catch (SQLException throwables) {
			throwables.printStackTrace();
		}

	}

	public void populateFields() throws SQLException {
		// Hours set to business hours
		//Start Times
		SpinnerValueFactory<Integer> startHours = new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 23);
		SpinnerValueFactory<Integer> startMinutes = new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 59);
		startHours.setWrapAround(true);
		startMinutes.setWrapAround(true);
		selectStartHoursSpn.setValueFactory(startHours);
		selectStartMinsSpn.setValueFactory(startMinutes);

		//End Times
		SpinnerValueFactory<Integer> endHours = new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 23);
		SpinnerValueFactory<Integer> endMinutes = new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 59);
		endHours.setWrapAround(true);
		endMinutes.setWrapAround(true);
		selectEndHoursSpn.setValueFactory(endHours);
		selectEndMinsSpn.setValueFactory(endMinutes);

		//Appointment Types
		ObservableList<String> types = FXCollections.observableArrayList("Planning Session", "De-Briefing", "Lunch " +
				"Meeting", "Other");
		appointmentTypeCb.setItems(types);

		//Contacts
		ObservableList<Appointment> contacts = FXCollections.observableArrayList(ContactQueries.queryContacts());
		contactCb.setItems(contacts);
	}


}

