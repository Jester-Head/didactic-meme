package controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
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
import utilities.QueryClass;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Locale;
import java.util.ResourceBundle;
/**
 * Enables user to view customer records,appointments ,and reports.
 */
public class MainMenu implements Initializable {

	@FXML
	private Label mainMenuLbl;

	@FXML
	private Button appointmentsBtn;

	@FXML
	private Button recordsBtn;

	@FXML
	private Button reportsBtn;

	@FXML
	private Button signOutBtn;

	@FXML
	private Label welcomeLbl;

	private Stage stage;



	public MainMenu() {
	}


	@FXML
	public void onClickSignOut(MouseEvent event) throws IOException {
		FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/SignInScreen.fxml"));
		Parent root = loader.load();
		Scene scene = new Scene(root);
		Stage stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
		stage.setScene(scene);
		stage.show();

	}


	@FXML
	public void onClickViewAppointments(MouseEvent event) throws IOException {
		FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/Appointments.fxml"));
		Parent root = loader.load();
		Scene scene = new Scene(root);
		Stage stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
		stage.setScene(scene);
		stage.show();


	}


	@FXML
	public void onClickViewRecords(MouseEvent event) throws IOException {

		FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/CustomerRecords.fxml"));
		Parent root = loader.load();
		Scene scene = new Scene(root);
		Stage stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
		stage.setScene(scene);
		stage.show();


	}

	@FXML
	public void onClickViewReports(MouseEvent event) throws IOException {
		FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/Reports.fxml"));
		Parent root = loader.load();
		Scene scene = new Scene(root);
		Stage stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
		stage.setScene(scene);
		stage.show();
	}

	@Override
	public void initialize(URL url, ResourceBundle resourceBundle) {
		welcomeLbl.setText("Welcome " + SignInScreen.getActiveUser().getUserName());
	}

}
