package controller;

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
import model.Customer;
import utilities.CustomerQueries;


import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ResourceBundle;
import java.util.StringTokenizer;

/**
 * Controller class for adding updating customers.All original information is displayed.
 * Text fields are used to collect customer name, address, postal code, and phone number.
 * Customer IDs are disabled,and first-level division and country data are collected
 * using separate combo boxes.
 */
public class UpdateCustomer implements Initializable {
	@FXML
	public TextField phone1txt;
	@FXML
	public TextField phone2txt;
	@FXML
	public TextField phone3txt;
	@FXML
	public Label customerIdLbl;
	@FXML
	private Button backBtn;
	@FXML
	private Button saveBtn;
	@FXML
	private Button mainMenuBtn;
	@FXML
	private TextField customerIdTxt;
	@FXML
	private TextField customerPhoneTxt;
	@FXML
	private TextField customerNameTxt;
	@FXML
	private TextField customerAddressTxt;
	@FXML
	private TextField customerZipCodeTxt;
	@FXML
	private ComboBox<String> selectDivisionCbx;
	@FXML
	private ComboBox<String> selectCountryCbx;
	private Stage stage;
	private Parent scene;
	private String[] phone;


	@FXML
	public void onClickGoBack(MouseEvent event) throws IOException {
		stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
		scene = FXMLLoader.load(getClass().getResource("/view/CustomerRecords.fxml"));
		stage.setScene(new Scene(scene));
		stage.show();
	}

	@FXML
	public void onClickGoHome(MouseEvent event) throws IOException {
		stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
		scene = FXMLLoader.load(getClass().getResource("/view/MainMenu.fxml"));
		stage.setScene(new Scene(scene));
		stage.show();
	}

	@FXML
	public void onClickSaveCustomer(MouseEvent event) throws IOException {
		int customerId = Integer.parseInt(customerIdTxt.getText());
		String customerName = customerNameTxt.getText();
		String customerZipCode = customerZipCodeTxt.getText();
		String customerAddress = customerAddressTxt.getText();
		String customerPhone = phone1txt.getText() + "-" + phone2txt.getText() + "-" + phone3txt.getText().replaceAll(
				"^0", "").stripLeading();
		String customerCountry = String.valueOf(selectCountryCbx.getSelectionModel().getSelectedItem());
		String customerDivision = String.valueOf(selectDivisionCbx.getSelectionModel().getSelectedItem());


		String createdBy = SignInScreen.getActiveUser().getUserName();
		Timestamp updateTime = Timestamp.valueOf(LocalDateTime.now());
		Timestamp createDate = Timestamp.valueOf(LocalDateTime.now());
		String updatedBy = SignInScreen.getActiveUser().getUserName();

		Customer customer = new Customer(customerId, customerName, customerAddress, customerZipCode, customerPhone,
				createDate, createdBy, updateTime, updatedBy, customerCountry, customerDivision);
		if (validateCustomerInfo(customer)) {
			try {
				CustomerQueries.updateCustomer(customer);
				//CustomerQueries.setCustomerLocation(customer);
			} catch (SQLException throwables) {
				throwables.printStackTrace();
			}
		}
		onClickGoBack(event);

	}


	/**
	 * Checks if customer zipcode and phone number have the correct amount of characters.
	 *
	 * @param customer information entered in form.
	 */
	public boolean validateCustomerInfo(Customer customer) {
		Alert alert = new Alert(Alert.AlertType.ERROR);
		alert.setHeaderText("Invalid input");
		String customerPhone = customer.getCustomerPhoneNumber().replace("-", "").replaceAll("^0", "").stripLeading();
		if (customer.getCustomerZip().length() != 5) {
			alert.setContentText("Postal code must contain 5 characters.");
			alert.showAndWait();
			return false;
		}

		if (customerPhone.length() != 12 && customer.getCustomerCountry().equals("UK")) {
			alert.showAndWait();
			return false;
		} else if (customerPhone.length() != 11 && !customer.getCustomerCountry().equals("UK")) {
			alert.showAndWait();
			return false;
		}
		return true;
	}

	/**
	 * Displays divisions appropriate for selected country.
	 *
	 * @param event ComboBox selection.
	 */
	@FXML
	public void onActionSelectCountry(ActionEvent event) throws SQLException {
		String customerCountry = String.valueOf(selectCountryCbx.getSelectionModel().getSelectedItem());
		switch (customerCountry) {
			case "U.S":
				if (selectDivisionCbx.getSelectionModel().isEmpty()) {
					selectDivisionCbx.setDisable(false);

				} else {
					selectDivisionCbx.getSelectionModel().clearSelection();
				}
				selectDivisionCbx.setPromptText("Select State");
				phone[0] = "1";

				break;
			case "Canada":
				if (selectDivisionCbx.getSelectionModel().isEmpty()) {
					selectDivisionCbx.setDisable(false);

				} else {
					selectDivisionCbx.getSelectionModel().clearSelection();
				}
				selectDivisionCbx.setPromptText("Select Province");
				phone[0] = "1";
				break;
			case "UK":
				if (selectDivisionCbx.getSelectionModel().isEmpty()) {
					selectDivisionCbx.setDisable(false);
				} else {
					selectDivisionCbx.getSelectionModel().clearSelection();
				}
				selectDivisionCbx.setPromptText("Select Region");
				phone[0] = "44";

				break;
			default:
				selectDivisionCbx.getSelectionModel().clearSelection();
				selectDivisionCbx.setPromptText("-----");
				selectDivisionCbx.setDisable(true);
		}
		ObservableList<String> states = CustomerQueries.filterDivision(customerCountry);
		selectDivisionCbx.setItems(states);
	}


	/**
	 * Receives existing customer information and displays in corresponding fields.
	 *
	 * @param customer item selected from table.
	 */
	public void transferInformation(Customer customer) throws SQLException {

		Customer foundCustomer = CustomerQueries.lookUpCustomer(customer.getCustomerId());
		customerIdLbl.setText("Customer ID: " + foundCustomer.getCustomerId());
		customerNameTxt.setText(foundCustomer.getCustomerName());
		phone = String.valueOf(foundCustomer.getCustomerPhoneNumber()).split("-");

		phone1txt.setText(phone[0]);
		phone2txt.setText(phone[1]);
		phone3txt.setText(phone[2]);
		customerZipCodeTxt.setText(String.valueOf(foundCustomer.getCustomerZip()));
		customerAddressTxt.setText(foundCustomer.getCustomerAddress());


		selectCountryCbx.setItems(CustomerQueries.populateCountry());
		selectDivisionCbx.setItems(CustomerQueries.populateDivision());
		CustomerQueries.setCustomerLocation(customer);
		selectCountryCbx.setValue(foundCustomer.getCustomerCountry());
		selectDivisionCbx.setValue(foundCustomer.getCustomerDivision());

		//Holds original country and division information for resetting the combo boxes
		String country = customer.getCustomerCountry();
		String division = customer.getCustomerDivision();

		//Filters division list based on the country . This will reset the division list
		locationHelper(country);
		//Sets the combo box to show the division again
		for (String s : selectDivisionCbx.getItems()) {
			if (division.equals(s)) {
				selectDivisionCbx.setValue(division);
				break;
			}
		}


	}


	/**
	 * Determines appropriate region name and display regions which belong to the selected country based.
	 *
	 * @param location country selected by user.
	 * @throws SQLException
	 */
	public void locationHelper(String location) throws SQLException {
		switch (location) {
			case "U.S":
				if (selectDivisionCbx.getSelectionModel().isEmpty()) {
					selectDivisionCbx.setDisable(false);
					selectDivisionCbx.setPromptText("Select State");
				} else {
					selectDivisionCbx.getSelectionModel().clearSelection();
				}
				break;
			case "Canada":
				if (selectDivisionCbx.getSelectionModel().isEmpty()) {
					selectDivisionCbx.setDisable(false);
					selectDivisionCbx.setPromptText("Select Province");
				} else {
					selectDivisionCbx.getSelectionModel().clearSelection();
				}
				break;

			case "UK":
				if (selectDivisionCbx.getSelectionModel().isEmpty()) {
					selectDivisionCbx.setDisable(false);
					selectDivisionCbx.setPromptText("Select Region");
				} else {
					selectDivisionCbx.getSelectionModel().clearSelection();
				}
				break;
			default:
				selectDivisionCbx.setPromptText("-----");
				selectDivisionCbx.setDisable(true);
		}
		ObservableList<String> region = CustomerQueries.filterDivision(location);
		selectDivisionCbx.setItems(region);
	}

	@Override
	public void initialize(URL url, ResourceBundle resourceBundle) {
	}

}