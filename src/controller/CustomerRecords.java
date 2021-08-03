package controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
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
import utilities.CustomerQueries;



import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;

import java.util.ResourceBundle;

/**
 * Controller class for customer records. User can add,update,and delete records. Customer information is displayed in a table.
 */
public class CustomerRecords implements Initializable {

	private static final ObservableList<Customer> customerObservableList = FXCollections.observableArrayList();
	@FXML
	private Label customerRecordsLbl;
	@FXML
	private TableView<Customer> customerRecordsTbl;
	@FXML
	private TableColumn<Customer, LocalDate> createDateCol;
	@FXML
	private TableColumn<Customer, String> createdByCol;
	@FXML
	private TableColumn<Customer, LocalDate> lastUpdateCol;
	@FXML
	private TableColumn<Customer, String> updatedByCol;
	@FXML
	private TableColumn<Customer, Integer> customerIdCol;
	@FXML
	private TableColumn<Customer, String> customerNameCol;
	@FXML
	private TableColumn<Customer, String> customerPhoneCol;
	@FXML
	private TableColumn<Customer, String> customerAddressCol;
	@FXML
	private Button addCustomerBtn;
	@FXML
	private Button updateCustomerBtn;
	@FXML
	private Button deleteCustomerBtn;
	@FXML
	private Button customerDetailsBtn;
	@FXML
	private Button backBtn;
	private Stage stage;
	private Parent scene;
	private Customer customer;


	public CustomerRecords() {

	}

	/**
	 * Redirects to new customer form.
	 *
	 * @param event Add button clicked.
	 * @throws IOException
	 */
	@FXML
	public void onClickAddCustomer(MouseEvent event) throws IOException {
		stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
		scene = FXMLLoader.load(getClass().getResource("/view/AddCustomer.fxml"));
		stage.setScene((new Scene(scene)));
		stage.show();
	}

	/**
	 * Removes customer and all records of their appointments.
	 *
	 * @param event Delete button clicked.
	 */
	@FXML
	public void onClickDeleteCustomer(MouseEvent event) {

		if (customerRecordsTbl.getSelectionModel().getSelectedItem() == null) {
			Alert alert = new Alert(Alert.AlertType.ERROR);
			alert.setHeaderText("No customer selected.");
			alert.showAndWait();
		} else {
			Customer customer = customerRecordsTbl.getSelectionModel().getSelectedItem();
			int customerId = customer.getCustomerId();
			String customerName = customer.getCustomerName();

			Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
			alert.setHeaderText("Are you sure you want to delete customer " + customerId + ". " + customerName + "?");
			alert.setContentText("Warning: Deleting a customer will delete all records of their appointments.");
			alert.showAndWait().ifPresent(response -> {
				if (response == ButtonType.OK) {
					boolean deleteCustomer = CustomerQueries.deleteCustomer(customer);
					if (!deleteCustomer) {
						alert.setAlertType(Alert.AlertType.INFORMATION);
						alert.setHeaderText("Customer successfully deleted " + customerId + ". " + customerName);
						alert.setContentText("");
						alert.showAndWait();
					}
				}
			});
			displayCustomerRecords();
		}
	}

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
	 * Pass selected customer information to UpdateCustomer controller.
	 *
	 * @param event Update button clicked.
	 * @throws IOException
	 */
	@FXML
	public void onClickUpdateCustomer(MouseEvent event) throws IOException, SQLException {
		//Sets location
		FXMLLoader loader = new FXMLLoader();
		loader.setLocation(getClass().getResource("/view/UpdateCustomer.fxml"));
		loader.load();

		//Gets controller
		UpdateCustomer updateCustomer = loader.getController();

		if (customerRecordsTbl.getSelectionModel().getSelectedItem() == null) {
			Alert alert = new Alert(Alert.AlertType.ERROR);
			alert.setHeaderText("No Customer Selected");
			alert.showAndWait();

		} else {
			// Passes selected customer information to controller
			updateCustomer.transferInformation(customerRecordsTbl.getSelectionModel().getSelectedItem());

			// Switches to UpdateCustomer view
			stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
			Parent scene = loader.getRoot();
			stage.setScene((new Scene(scene)));
			stage.show();
		}
	}

	/**
	 * Intended for future implementation.
	 *
	 * @param event
	 * @throws IOException
	 */
	@FXML
	public void onClickViewCustomer(MouseEvent event) throws IOException {
		stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
		scene = FXMLLoader.load(getClass().getResource("/view/CustomerDetails.fxml"));
		stage.setScene((new Scene(scene)));
		stage.show();
	}

	/**
	 * Populates tableview with customer records found in the database.
	 */
	public void displayCustomerRecords() {
		customerObservableList.setAll(CustomerQueries.getAllCustomers());
		customerNameCol.setCellValueFactory(new PropertyValueFactory<>("customerName"));
		customerIdCol.setCellValueFactory(new PropertyValueFactory<>("customerId"));
		customerPhoneCol.setCellValueFactory(new PropertyValueFactory<>("customerPhoneNumber"));
		customerAddressCol.setCellValueFactory(new PropertyValueFactory<>("customerAddress"));
		createDateCol.setCellValueFactory(new PropertyValueFactory<>("createDate"));
		createdByCol.setCellValueFactory(new PropertyValueFactory<>("createdBy"));
		updatedByCol.setCellValueFactory(new PropertyValueFactory<>("updatedBy"));
		lastUpdateCol.setCellValueFactory(new PropertyValueFactory<>("updateTime"));
		customerRecordsTbl.setItems(customerObservableList);
	}


	@Override
	public void initialize(URL url, ResourceBundle resourceBundle) {
		displayCustomerRecords();
	}
}
