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
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import model.Appointment;
import model.Customer;
import utilities.QueryClass;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.ResourceBundle;
/**
 * Controller class for appointments. User can add,update,and delete appointments. Appointment  information is displayed in a table.
 */
public class Appointments implements Initializable {

	Stage stage;
	Parent scene;
	ObservableList<Appointment> filteredAppointments = FXCollections.observableArrayList();
	@FXML
	private TableView<Appointment> appointmentsTbl;
	@FXML
	private TableColumn<Appointment, Integer> appointmentIdCol;
	@FXML
	private TableColumn<Appointment, String> titleCol;
	@FXML
	private TableColumn<Appointment, String> descriptionCol;
	@FXML
	private TableColumn<Appointment, String> locationCol;
	@FXML
	private TableColumn<Appointment, String> contactCol;
	@FXML
	private TableColumn<Appointment, String> appointmentTypeCol;
	@FXML
	private TableColumn<Appointment, LocalDateTime> startDateCol;
	@FXML
	private TableColumn<Appointment, LocalDateTime> endDateCol;
	@FXML
	private TableColumn<Appointment, LocalDateTime> startTimeCol;
	@FXML
	private TableColumn<Appointment, LocalDateTime> endTimeCol;
	@FXML
	private TableColumn<Customer, String> customerIdCol;
	@FXML
	private TextField searchAppointmentsTxt;
	@FXML
	private Button backBtn;
	@FXML
	private Button customerDetailsBtn;
	@FXML
	private Button addAppointmentBtn;
	@FXML
	private Button updateAppointmentBtn;
	@FXML
	private Button deleteAppointmentBtn;
	@FXML
	private Label appointmentsViewLbl;
	@FXML
	private RadioButton defaultViewRbtn;
	@FXML
	private ToggleGroup view;
	@FXML
	private RadioButton monthViewRbtn;
	@FXML
	private RadioButton weeklyViewRbtn;
	private final ObservableList<Appointment> appointments = FXCollections.observableArrayList();

	/**
	 * Displays all appointments in the TableView.
	 *
	 * @param event default RadioButton selected.
	 */
	@FXML
	public void onActionSelectDefault(ActionEvent event) {
		displayAppointments();
	}

	/**
	 * Displays appointment for the current month.
	 *
	 * @param event monthly RadioButton selected.
	 */
	@FXML
	public void onActionSelectMonthly(ActionEvent event) {
		//Show current month's appointments
		appointmentsTbl.setItems(QueryClass.getMonthlyAppointments());
		appointmentsTbl.setPlaceholder(new Label("No appointments this month"));
	}

	/**
	 * Displays appointments for the current week.
	 *
	 * @param event weekly RadioButton selected.
	 */
	@FXML
	public void onActionSelectWeekly(ActionEvent event) {
		appointmentsTbl.setItems(QueryClass.getWeeklyAppointments());
		appointmentsTbl.setPlaceholder(new Label("No appointments this week"));
	}

	@FXML
	public void onClickAddAppointment(MouseEvent event) throws IOException {
		stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
		scene = FXMLLoader.load(getClass().getResource("/view/AddAppointment.fxml"));
		stage.setScene(new Scene(scene));
		stage.show();
	}

	/**
	 * Cancels an appointment.
	 * Lambda expression is used here to help change the default alert response instead of instantiating a new alerts.
	 *
	 * @param event cancel button clicked.
	 */
	@FXML
	public void onClickDeleteAppointment(MouseEvent event) {
		Alert alert = new Alert(Alert.AlertType.ERROR);
		Appointment appointment = appointmentsTbl.getSelectionModel().getSelectedItem();
		if (appointment == null) {
			alert.setHeaderText("No Appointment Selected");
			alert.showAndWait();
		} else {
			alert.setAlertType(Alert.AlertType.CONFIRMATION);
			alert.setHeaderText("Are you sure you want to cancel appointment");
			alert.setContentText(appointment.getAppointmentId() + ". " + appointment.getAppointmentType() + " at: " + appointment.getStartDateTime());
			alert.showAndWait().ifPresent(response -> {
				if (response == ButtonType.OK) {
					boolean canceled = QueryClass.deleteAppointment(appointment);
					if (!canceled) {
						alert.setAlertType(Alert.AlertType.INFORMATION);
						alert.setHeaderText(appointment.getAppointmentType() + " Canceled");
						alert.showAndWait();
					}

					displayAppointments();
				}
			});

		}
	}

	@FXML
	public void onClickGoBack(MouseEvent event) throws IOException {
		stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
		scene = FXMLLoader.load(getClass().getResource("/view/MainMenu.fxml"));
		stage.setScene(new Scene(scene));
		stage.show();
	}

	/**
	 * Pass selected appointment information to UpdateAppointment controller.
	 *
	 * @param event Update button clicked.
	 * @throws IOException
	 */
	@FXML
	public void onClickUpdateAppointment(MouseEvent event) throws IOException {
		//Sets location
		FXMLLoader loader = new FXMLLoader();
		loader.setLocation(getClass().getResource("/view/UpdateAppointment.fxml"));
		loader.load();

		// Get UpdateAppointment controller
		UpdateAppointment updateAppointment = loader.getController();

		if (appointmentsTbl.getSelectionModel().getSelectedItem() == null) {
			Alert alert = new Alert(Alert.AlertType.ERROR);
			alert.setHeaderText("No Appointment Selected");
			alert.showAndWait();
		} else {
			// Pass selected appointment information
			updateAppointment.transferInformation(appointmentsTbl.getSelectionModel().getSelectedItem());

			// Switches view to UpdateAppointment
			stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
			scene = loader.getRoot();
			stage.setScene(new Scene(scene));
			stage.show();
		}

	}

	/**
	 * Intended for future implementation.
	 */
	@FXML
	public void onClickViewCustomer(MouseEvent event) {
		// TODO: Code here

	}

	/**
	 * Populates tableview with appointment information found in the database.
	 */
	public void displayAppointments() {
		appointments.setAll(QueryClass.getAllAppointments());
		appointmentIdCol.setCellValueFactory(new PropertyValueFactory<>("appointmentId"));
		appointmentTypeCol.setCellValueFactory(new PropertyValueFactory<>("appointmentType"));
		titleCol.setCellValueFactory(new PropertyValueFactory<>("title"));
		locationCol.setCellValueFactory(new PropertyValueFactory<>("location"));
		descriptionCol.setCellValueFactory(new PropertyValueFactory<>("description"));
		startDateCol.setCellValueFactory(new PropertyValueFactory<>("startDateTime"));
		endDateCol.setCellValueFactory(new PropertyValueFactory<>("endDateTime"));
		contactCol.setCellValueFactory(new PropertyValueFactory<>("contactName"));
		customerIdCol.setCellValueFactory(new PropertyValueFactory<>("customerId"));
		appointmentsTbl.setItems(appointments);

	}

	@Override
	public void initialize(URL url, ResourceBundle resourceBundle) {
		displayAppointments();

		/*
		Button disabled because it's not currently functional.
		Will implement this feature in the future.
		 */
		customerDetailsBtn.setDisable(true);
		customerDetailsBtn.setVisible(false);
	}

}
