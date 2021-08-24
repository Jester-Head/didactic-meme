package controller;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.NumberAxis;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.TilePane;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.stage.Popup;
import javafx.stage.Stage;
import model.Appointment;
import model.User;
import utilities.AppointmentQueries;
import utilities.UserQueries;


import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.time.format.TextStyle;
import java.util.*;


public class SignInScreen implements Initializable {

	private static User activeUser;
	private ResourceBundle rb = ResourceBundle.getBundle("language_files/Nat");
	private Alert alert;
	@FXML
	private PasswordField passwordPwf;
	@FXML
	private TextField userIdTxt;
	@FXML
	private Label userIdLbl;
	@FXML
	private Label passwordLbl;
	@FXML
	private Button signInBtn;
	@FXML
	private Button exitBtn;
	@FXML
	private Button clearBtn;
	@FXML
	private Label timezoneLbl;
	@FXML
	private Label languageLbl;

	public static User getActiveUser() {
		return activeUser;
	}

	@FXML
	public void onClickClearForm(MouseEvent event) {
		userIdTxt.setText("");
		passwordPwf.setText("");
	}

	@FXML
	public void onClickExit(MouseEvent event) {
		Platform.exit();
	}

	/**
	 * Records time of login,alerts user of upcoming appointments and redirects to the main menu.
	 * @param event Submit button clicked.
	 */
	@FXML
	public void onClickSignIn(MouseEvent event) throws SQLException, IOException {

		//Tests input fields
		if (handleInput()) {
			String userName = userIdTxt.getText().trim();
			String password = passwordPwf.getText().trim();

			//Checks user credentials
			User user = UserQueries.lookupUser(userName, password);

			/*The lookupUser method sets the userID to 0 by default.
			 * If the query was a success, the userID will not be 0
			 */
			if (user.getUserID() != 0) {
				activeUser = user;
				//Logs user session.
				logUser(user, "Success");

				try {
					Alert alert = new Alert(Alert.AlertType.INFORMATION);
					ObservableList<Appointment> upcomingAppointments = AppointmentQueries.getUpcomingAppointments();
					ObservableList<String> stringObservableList = FXCollections.observableArrayList();
					DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm a");

					//user has appointments in the next 15 minutes
					if (!upcomingAppointments.isEmpty()) {
						for (Appointment appointment : upcomingAppointments) {
							LocalDateTime time = AppointmentQueries.timeConversionLocal(appointment.getStartDateTime()).toLocalDateTime();
							String appointmentInfo =
									appointment.getAppointmentType() + " " + rb.getString("at") + " " + time.format(formatter) + "\n";
							stringObservableList.add(appointmentInfo);
							alert.setHeaderText("Upcoming Appointments");
							alert.setContentText(stringObservableList.toString());

						}
					} else {
						alert.setHeaderText(rb.getString("NoUpcomingAppointments"));
					}
					((Button) alert.getDialogPane().lookupButton(ButtonType.OK)).setText(rb.getString("OK"));
					alert.showAndWait();
					//Proceeds to main menu
					Stage stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
					Parent scene = FXMLLoader.load(getClass().getResource("/view/MainMenu.fxml"));
					stage.setScene(new Scene(scene));
					stage.show();

				} catch (Exception e) {
					e.printStackTrace();
				}
			} else {
				user.setUserID(Integer.parseInt(userIdTxt.getText().trim()));
				String failed =  rb.getString("FailedAttempt") + ".";
				logUser(user, failed);
				Alert alert = new Alert(Alert.AlertType.ERROR);
				alert.setContentText(rb.getString("InvalidUsernameOrPassword")+".");
				((Button) alert.getDialogPane().lookupButton(ButtonType.OK)).setText(rb.getString("OK"));
				alert.showAndWait();
			}

		}
	}

	/**
	 * Checks for empty fields,and valid formatting.
	 *
	 * @return true if information passes error checks. Otherwise, returns false.
	 */
	public  boolean handleInput() {
		try {
			String testUserId = userIdTxt.getText().trim();
			String password = passwordPwf.getText().trim();
			alert = new Alert(Alert.AlertType.ERROR);

			//Checks for empty fields.
			if (testUserId.isBlank() || password.isBlank()) {
				alert.setHeaderText(rb.getString("InvalidInput"));
				alert.setContentText(rb.getString("PleaseFillOutAllFields"));
				((Button) alert.getDialogPane().lookupButton(ButtonType.OK)).setText(rb.getString("OK"));
				alert.showAndWait();
				return false;
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return true;
	}

	/**
	 * Records user log-in attempts,time stamps, and whether each attempt was successful in a file.
	 *
	 * @param validUser user attempting to log in.
	 * @param attempt "Success" if user exists in database.Else "Failed Attempt"
	 * @throws IOException
	 */
	public void logUser(User validUser, String attempt) throws IOException {
		String logger = "src/utilities/login_activity";
		LocalDateTime ldt = LocalDateTime.now();
		ZonedDateTime zdt = ldt.atZone(ZoneId.of(ZoneId.systemDefault().toString()));
		ZonedDateTime utczdt = zdt.withZoneSameInstant(ZoneId.of("UTC"));
		String dateTimePattern = "yyyy-MM-dd  HH:mm:ss z ";
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern(dateTimePattern);
		String userInfo =
				String.format("User ID: %d" + "\t\t" + "Username: %s" + "\t\t" + "Time: %s" + "\t\t" + attempt + "\n",
						validUser.getUserID(), validUser.getUserName(), utczdt.format(formatter));
		FileWriter fileWriter = new FileWriter(logger, true);
		fileWriter.append(userInfo);
		fileWriter.close();

	}

	/**
	 * Gets the user's default language and translates the text.
	 */
	public void setLanguage() {
		Locale locale = Locale.getDefault();
		languageLbl.setText(String.valueOf(locale.getDisplayLanguage()));
		userIdLbl.setText(rb.getString("UserName") + ":");
		passwordLbl.setText(rb.getString("Password") + ":");
		clearBtn.setText(rb.getString("Clear"));
		exitBtn.setText(rb.getString("Exit"));
		signInBtn.setText(rb.getString("Submit"));


	}


	@Override
	public void initialize(URL url, ResourceBundle resourceBundle) {
		setLanguage();
		ZoneId zoneId = ZoneId.systemDefault();
		//Translates timezone label to default language.
		if (Locale.getDefault().equals(Locale.FRANCE)) {
			timezoneLbl.setText(zoneId.getDisplayName(TextStyle.FULL, Locale.FRANCE));
		} else {
			timezoneLbl.setText(zoneId.getDisplayName(TextStyle.FULL, Locale.US));
		}
	}
}