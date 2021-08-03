package controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import model.Appointment;
import model.Customer;


import java.io.IOException;

/**
 * Intended for future implementation. Please Disregard.
 */
public class CustomerDetails {


	@FXML
	private TableView<Appointment> appointmentsTbl;
	@FXML
	private TableColumn<Appointment, Integer> appointmentIdCol;
	@FXML
	private TableColumn<Appointment, Integer> appointmentTimeCol;
	@FXML
	private TableColumn<Appointment, ?> appointmentTypeCol;
	@FXML
	private TableColumn<?, ?> appointmentDateCol;
	@FXML
	private TableColumn<?, ?> contactCol;
	@FXML
	private TableColumn<?, ?> descriptionCol;
	@FXML
	private Label userIdLbl;
	@FXML
	private Label nameLbl;
	@FXML
	private Label addressLbl;
	@FXML
	private Label zipLbl;
	@FXML
	private Label phoneLbl;
	@FXML
	private Label countryLbl;
	@FXML
	private Label stateLbl;
	@FXML
	private TextField userIdTxt;
	@FXML
	private TextField nameTxt;
	@FXML
	private TextField addressTxt;
	@FXML
	private TextField zipTxt;
	@FXML
	private TextField phoneTxt;
	@FXML
	private TextField countryTxt;
	@FXML
	private TextField stateTxt;
	@FXML
	private Label detailsPageTitleLbl;
	@FXML
	private Button backBtn;

	@FXML
	public void onClickGoBack(MouseEvent event) throws IOException {
		FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/CustomerRecords.fxml"));

		Parent root = loader.load();
		Scene scene = new Scene(root);
		Stage stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
		stage.setScene(scene);
		stage.show();
	}

	/**
	 * Receives existing customer information and displays in corresponding fields.
	 *
	 * @param customer customer model
	 */
	public void transferInformation(Customer customer) {

		//placeholder - should be customerId and not userId
		userIdTxt.setText(String.valueOf(customer.getCustomerId()));
		nameTxt.setText(customer.getCustomerName());
		addressTxt.setText(customer.getCustomerAddress());
		zipTxt.setText(String.valueOf(customer.getCustomerZip()));
		phoneTxt.setText(customer.getCustomerPhoneNumber());


	}


}
