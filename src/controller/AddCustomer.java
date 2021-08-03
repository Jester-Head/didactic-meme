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
import utilities.DataBaseConnection;
import utilities.QueryClass;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.*;
import java.util.ResourceBundle;

/**
 * Controller class for adding new customers..
 * Text fields are used to collect customer name, address, postal code, and phone number.
 * Customer IDs are auto-generated, and first-level division and country data are collected
 * using separate combo boxes.
 */
public class AddCustomer implements Initializable {

	@FXML
	private Button backBtn;

	@FXML
	private Button saveBtn;

	@FXML
	private Button mainMenuBtn;

	@FXML
	private TextField customerIdTxt;

	@FXML
	private TextField customerNameTxt;

	@FXML
	private TextField customerAddressTxt;

	@FXML
	private TextField customerZipCodeTxt;

	@FXML
	private TextField customerPhoneTxt;

	@FXML
	private ComboBox<String> selectDivisionCbx;

	@FXML
	private ComboBox<String> selectCountryCbx;

	/**
	 * Uses selected country to find customer's region.
	 * @param event ComboBox selection.
	 * @throws SQLException
	 */
	@FXML
	public void onActionSelectCountry(ActionEvent event) throws SQLException {
		String customerCountry = String.valueOf(selectCountryCbx.getSelectionModel().getSelectedItem());
		locationHelper(customerCountry);
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
		ObservableList<String> region = QueryClass.filterDivision(location);
		selectDivisionCbx.setItems(region);
	}

	/**
	 *
	 * @param event
	 * @throws IOException
	 */
	@FXML
	public void onClickGoBack(MouseEvent event) throws IOException {

		//Sets location
		FXMLLoader loader = new FXMLLoader();
		loader.setLocation(getClass().getResource("/view/CustomerRecords.fxml"));
		loader.load();

		// Switches to CustomerRecords view
		Stage stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
		Parent scene = loader.getRoot();
		stage.setScene((new Scene(scene)));
		stage.show();
	}

	@FXML
	public void onClickGoHome(MouseEvent event) throws IOException {
		Stage stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
		Parent scene = FXMLLoader.load(getClass().getResource("/view/MainMenu.fxml"));
		stage.setScene(new Scene(scene));
		stage.show();
	}

	/**
	 * Collects the following data: customer name, address, postal code, and phone number.
	 *
	 * @param event save button clicked.
	 * @throws IOException
	 */
	@FXML
	public void onClickSaveCustomer(MouseEvent event) throws IOException {
		//placeholder value for primary key
		int customerId = 0;
		String customerName = customerNameTxt.getText();
		String customerZipCode = customerZipCodeTxt.getText();
		String customerAddress = customerAddressTxt.getText();
		String customerPhone = customerPhoneTxt.getText();
		String customerCountry = String.valueOf(selectCountryCbx.getSelectionModel().getSelectedItem());
		String customerDivision = String.valueOf(selectDivisionCbx.getSelectionModel().getSelectedItem());


		String createdBy = SignInScreen.getActiveUser().getUserName();
		Timestamp updateTime = Timestamp.valueOf(LocalDateTime.now());
		Timestamp createDate = Timestamp.valueOf(LocalDateTime.now());
		String updatedBy = SignInScreen.getActiveUser().getUserName();

		Customer customer = new Customer(customerId, customerName, customerAddress, customerZipCode, customerPhone,
				createDate, createdBy, updateTime, updatedBy, customerCountry, customerDivision);
		if (validateCustomerInfo(customer)) {
			QueryClass.insertCustomer(customer);
		}

		onClickGoBack(event);

	}

	/**
	 * Checks if customer zipcode and phone number have the correct amount of characters.
	 * @param customer data gathered from form.
	 * @return true if information passes error checks. Otherwise returns false.
	 */
	public boolean validateCustomerInfo(Customer customer) {
		Alert alert = new Alert(Alert.AlertType.ERROR);
		alert.setHeaderText("Invalid input");
		String customerPhone = customerPhoneTxt.getText().replaceAll("-", "");
		if (customer.getCustomerZip().length() != 5) {
			alert.setContentText("Postal code must contain 5 characters.");
			alert.showAndWait();
			return false;
		}
		if (customer.getCustomerCountry() == "U.K" && customerPhone.length() != 12) {
			alert.setContentText("Phone number must contain 12 digits.");
			alert.showAndWait();
			return false;
		} else if (customerPhone.length() != 10) {
			alert.setContentText("Phone number must contain 10 digits.");
			alert.showAndWait();
			return false;
		}
		return true;
	}

	/**
	 * Sets the ComboBoxes with the names of countries and regions in the database
	 *
	 * @throws SQLException
	 */
	public void populateComboBox() throws SQLException {
		selectCountryCbx.setItems(QueryClass.populateCountry());
		// Determines country selection and Sets the combo box with the names of divisions in the database;
		ActionEvent event = new ActionEvent();
		onActionSelectCountry(event);
		selectDivisionCbx.setItems(QueryClass.populateDivision());
	}

	@Override
	public void initialize(URL url, ResourceBundle resourceBundle) {
		try {
			populateComboBox();
		} catch (SQLException throwables) {
			throwables.printStackTrace();
		}
	}


}
