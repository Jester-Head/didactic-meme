package model;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.Timestamp;
import java.time.*;
import java.time.format.DateTimeFormatter;

/**
 *
 */
public class Appointment {

	private final String pattern = "yyyy-MM-dd hh:mm a";
	private final DateTimeFormatter format = DateTimeFormatter.ofPattern(pattern);
	ObservableList<Customer> customerObservableList = FXCollections.observableArrayList();
	private String customerName;
	//Customer_ID INT(10) (FK)
	private int customerId;
	//Appointment_ID INT(10) (PK)
	private int appointmentId;
	//Title VARCHAR(50)
	private String title;
	// Description VARCHAR(50)
	private String description;
	//Location VARCHAR(50)
	private String location;
	//Type VARCHAR(50)
	private String appointmentType;
	//Start DATE
	private Timestamp startDateTime;
	//Create_Date DATETIME
	private Timestamp createDate;
	//Created_By VARCHAR(50)
	private String createdBy;
	//Last_Update TIMESTAMP
	private Timestamp lastUpdate;
	//Last_Updated_By VARCHAR(50)
	private String updatedBy;
	//End DATE
	private Timestamp endDateTime;
	//User_ID INT(10) (FK)
	private int userId;
	//Contact_ID INT(10) (FK)
	private int contactId;
	// Contact_Name VARCHAR(50)
	private String contactName;
	private String contactEmail;
	private final ObservableList<String> allAppointmentTypes = FXCollections.observableArrayList();
	private int typeCount;
	private int appointmentCount;

	public Appointment() {

	}

	//Used for updating customer.
	public Appointment(int appointmentId, String title, String description, String location, String appointmentType,
	                   Timestamp startDateTime, Timestamp endDateTime, Timestamp lastUpdate,
	                   String updatedBy, int contactId,
	                   int customerId, int userId) {
		this.appointmentId = appointmentId;
		this.title = title;
		this.description = description;
		this.location = location;
		this.appointmentType = appointmentType;
		this.startDateTime = startDateTime;
		this.endDateTime = endDateTime;
		this.lastUpdate = lastUpdate;
		this.updatedBy = updatedBy;
		this.contactId = contactId;
		this.customerId = customerId;
		this.userId = userId;

	}

	public Appointment(int contactId, String contactName, String contactEmail) {
		this.contactId = contactId;
		this.contactName = contactName;
		this.contactEmail = contactEmail;
	}

	public Appointment(int appointmentId, String title, String description, String location, String type,
	                   Timestamp start, Timestamp end, Timestamp createDate, String createdBy,
	                   Timestamp lastUpdate,
	                   String updatedBy, int contactId, int customerId, int userId) {
		this.appointmentId = appointmentId;
		this.title = title;
		this.description = description;
		this.location = location;
		this.appointmentType = type;
		this.startDateTime = start;
		this.endDateTime = end;
		this.createDate = createDate;
		this.createdBy = createdBy;
		this.lastUpdate = lastUpdate;
		this.updatedBy = updatedBy;
		this.contactId = contactId;
		this.customerId = customerId;
		this.userId = userId;
	}

	public Appointment(int appointmentId, String title, String description, String location, String appointmentType,
	                   Timestamp startDateTime, Timestamp endDateTime, Timestamp createDate, String createdBy,
	                   Timestamp lastUpdate,
	                   String updatedBy, int contactId, String contactName, int customerId, int userId) {
		this.appointmentId = appointmentId;
		this.title = title;
		this.description = description;
		this.location = location;
		this.appointmentType = appointmentType;
		this.startDateTime = startDateTime;
		this.endDateTime = endDateTime;
		this.createDate = createDate;
		this.createdBy = createdBy;
		this.lastUpdate = lastUpdate;
		this.updatedBy = updatedBy;
		this.contactId = contactId;
		this.contactName = contactName;
		this.customerId = customerId;
		this.userId = userId;
	}

	public Appointment(int appointmentId, String title, String description, String appointmentType,
	                   Timestamp startDateTime, Timestamp endDateTime, int customerId) {
		this.appointmentId = appointmentId;
		this.title = title;
		this.description = description;
		this.appointmentType = appointmentType;
		this.customerId = customerId;
		this.startDateTime = startDateTime;
		this.endDateTime = endDateTime;

	}

	public int getTypeCount() {
		return typeCount;
	}

	public void setTypeCount(int typeCount) {
		this.typeCount = typeCount;
	}

	public int getAppointmentCount() {
		return appointmentCount;
	}

	public void setAppointmentCount(int appointmentCount) {
		this.appointmentCount = appointmentCount;
	}

	public int getCustomerId() {
		return customerId;
	}

	private void addCustomer(Customer customer) {
		customerObservableList.add(customer);
	}

	public int getAppointmentId() {
		return appointmentId;
	}

	public int getUserId() {
		return userId;
	}

	public String getTitle() {
		return title;
	}

	public String getDescription() {
		return description;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public String getAppointmentType() {
		return appointmentType;
	}

	public void setAppointmentType(String appointmentType) {
		this.appointmentType = appointmentType;
	}

	public Timestamp getStartDateTime() {

		return startDateTime;
	}


	public Timestamp getEndDateTime() {
		return endDateTime;
	}


	public int getContactId() {
		return contactId;
	}

	public String getCustomerName() {
		return customerName;
	}

	public String getContactName() {
		return contactName;
	}


	public ObservableList<Customer> getCustomerObservableList() {
		return customerObservableList;
	}

	public void setCustomerObservableList(ObservableList<Customer> customerObservableList) {
		this.customerObservableList = customerObservableList;
	}

	public Timestamp getCreateDate() {
		return createDate;
	}

	public Timestamp getLastUpdate() {
		return lastUpdate;
	}

	public ObservableList<String> getAllAppointmentTypes() {

		return allAppointmentTypes;
	}

	public void setAllAppointmentTypes(String type) {

		allAppointmentTypes.add(type);
	}

	@Override
	public String toString() {
		return (contactId + ". " + contactName + ", " + contactEmail);
	}

	public void setContactEmail(String contactEmail) {
		this.contactEmail = contactEmail;
	}

	public String getCreatedBy() {
		return createdBy;
	}

	public String getUpdatedBy() {
		return updatedBy;
	}
}
