package controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import model.Appointment;
import utilities.AppointmentQueries;
import utilities.ContactQueries;
import utilities.ReportQueries;


import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.Month;
import java.util.ResourceBundle;

/**
 * Reports the total number of customer appointments by type and month.
 * Displays a schedule for each contact in the organization.
 * Tracks the number of appointments each contact participated in each month.
 */
public class Reports implements Initializable {
	@FXML
	private ComboBox<Integer> contactYearCb;
	@FXML
	private ComboBox<String> contactMonthCb;
	@FXML
	private TableView<Appointment> contactStatsTbl;
	@FXML
	private TableColumn<Appointment, String> contactCol;
	@FXML
	private TableColumn<Appointment, Integer> appointmentCountCol;
	@FXML
	private TableColumn<Appointment, Integer> countCol;
	@FXML
	private TableColumn<Appointment, String> typesCol;

	@FXML
	private Button backBtn;

	@FXML
	private ComboBox<Appointment> contactCB;

	@FXML
	private TableView<Appointment> contactScheduleTbl;

	@FXML
	private TableColumn<Appointment, Integer> contactIdCol;

	@FXML
	private TableColumn<Appointment, Integer> appointmentIdCol;

	@FXML
	private TableColumn<Appointment, String> titleCol;

	@FXML
	private TableColumn<Appointment, String> descriptionCol;

	@FXML
	private TableColumn<Appointment, String> typeCol;

	@FXML
	private TableColumn<Appointment, Timestamp> startCol;

	@FXML
	private TableColumn<Appointment, Timestamp> endCol;

	@FXML
	private TableColumn<Appointment, Integer> customerIdCol;

	@FXML
	private ComboBox<Integer> yearCb;

	@FXML
	private ComboBox<String> monthCb;

	@FXML
	private TableView<Appointment> meetingTypesTbl;

	private final ObservableList<String> months = FXCollections.observableArrayList("January", "February", "March", "April",
			"May", "June", "July", "August", "September", "October", "November", "December");


	@FXML
	public void onClickGoBack(MouseEvent event) throws IOException {
		FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/MainMenu.fxml"));
		Parent root = loader.load();
		Scene scene = new Scene(root);
		Stage stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
		stage.setScene(scene);
		stage.show();
	}

	/**
	 * Displays a schedule for each contact in the organization.
	 *
	 * @param event ComboBox selection.
	 * @throws SQLException
	 */
	public void onActionSelectContact(ActionEvent event) throws SQLException {
		int contactId = contactCB.getSelectionModel().getSelectedItem().getContactId();
		ObservableList schedule = ContactQueries.getContactAppointments(contactId);


		appointmentIdCol.setCellValueFactory(new PropertyValueFactory<>("appointmentId"));
		typeCol.setCellValueFactory(new PropertyValueFactory<>("appointmentType"));
		titleCol.setCellValueFactory(new PropertyValueFactory<>("title"));

		descriptionCol.setCellValueFactory(new PropertyValueFactory<>("description"));
		startCol.setCellValueFactory(new PropertyValueFactory<>("startDateTime"));
		endCol.setCellValueFactory(new PropertyValueFactory<>("endDateTime"));
		customerIdCol.setCellValueFactory(new PropertyValueFactory<>("customerId"));
		contactScheduleTbl.setItems(schedule);


	}

	/**
	 * Enables user to select a specific time frame and individual to examine.
	 */
	public void populateFields() {
		monthCb.setVisible(false);
		contactMonthCb.setVisible(false);
		ObservableList<Appointment> contacts = FXCollections.observableArrayList(ContactQueries.queryContacts());
		contactCB.setItems(contacts);
		ObservableList<Integer> localDateTimes = FXCollections.observableArrayList();
		for (int year = 2000; year <= LocalDate.now().getYear(); year++) {
			localDateTimes.add(year);
		}
		yearCb.setItems(localDateTimes);
		monthCb.setItems(months);
		contactYearCb.setItems(localDateTimes);
		contactMonthCb.setItems(months);


	}


	public void onActionSelectYear(ActionEvent event) {
		monthCb.setVisible(true);
	}

	/**
	 * Displays a count for each appointment type in a given year and month.
	 *
	 * @param event month ComboBox selection.
	 * @throws SQLException
	 */
	public void onActionSelectMonth(ActionEvent event) throws SQLException {
		int year = yearCb.getValue();
		String monthStr = monthCb.getValue().toUpperCase();
		Month month = Month.valueOf(monthStr);
		int monthInt = month.getValue();

		ObservableList<Appointment> typesCount = ReportQueries.getAppointmentTypeCount(year, monthInt);
		typesCol.setCellValueFactory(new PropertyValueFactory<>("AppointmentType"));
		countCol.setCellValueFactory(new PropertyValueFactory<>("TypeCount"));
		meetingTypesTbl.setItems(typesCount);
	}

	public void onActionSelectContactYear(ActionEvent event) {
		contactMonthCb.setVisible(true);
	}

	/**
	 * Displays a count for the number of contact appointments  in a given year and month.
	 *
	 * @param event month ComboBox selection for contacts section.
	 * @throws SQLException
	 */
	public void onActionSelectContactMonth(ActionEvent event) throws SQLException {
		int year = contactYearCb.getValue();
		String monthStr = contactMonthCb.getValue().toUpperCase();
		Month month = Month.valueOf(monthStr);
		int monthInt = month.getValue();

		ObservableList<Appointment> contactAppointmentCount = ContactQueries.getContactAppointmentCount(year, monthInt);
		contactCol.setCellValueFactory(new PropertyValueFactory<>("ContactName"));
		appointmentCountCol.setCellValueFactory(new PropertyValueFactory<>("AppointmentCount"));
		contactStatsTbl.setItems(contactAppointmentCount);
	}

	@Override
	public void initialize(URL url, ResourceBundle resourceBundle) {
		populateFields();

	}

}
