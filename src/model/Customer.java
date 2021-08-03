package model;

import java.sql.Timestamp;
import java.time.LocalDate;

public class Customer {
	// Customer_ID INT(10) (PK)
	private final int customerId;
	// Customer_Name VARCHAR(50)
	private final String customerName;
	//Address VARCHAR(100)
	private final String customerAddress;

	// Postal_Code VARCHAR(50)
	private final String customerZip;
	// Phone VARCHAR(50)
	private final String customerPhoneNumber;
	//   Create_Date DATETIME
	private Timestamp createDate;
	//    Created_By VARCHAR(50)
	private String createdBy;
	//    Last_Update TIMESTAMP
	private final Timestamp updateTime;
	//    Last_Updated_By VARCHAR(50)
	private final String updatedBy;
	//      Division_ID INT(10)
	private int divisionId;
	private String customerCountry;
	private String customerDivision;

	public Customer(int customerId, String customerName, String customerAddress, String customerZip,
	                String customerPhoneNumber, Timestamp createDate, String createdBy, Timestamp updateTime,
	                String updatedBy, String customerCountry, String customerDivision) {
		this.customerId = customerId;
		this.customerName = customerName;
		this.customerAddress = customerAddress;
		this.customerZip = customerZip;
		this.customerPhoneNumber = customerPhoneNumber;
		this.createDate = createDate;
		this.createdBy = createdBy;
		this.updateTime = updateTime;
		this.updatedBy = updatedBy;
		this.customerCountry = customerCountry;
		this.customerDivision = customerDivision;
	}

	public Customer(int customerId, String customerName, String customerAddress, String customerZip,
	                String customerPhoneNumber, Timestamp updateTime,
	                String updatedBy, String customerCountry, String customerDivision) {
		this.customerId = customerId;
		this.customerName = customerName;
		this.customerAddress = customerAddress;
		this.customerZip = customerZip;
		this.customerPhoneNumber = customerPhoneNumber;
		this.updateTime = updateTime;
		this.updatedBy = updatedBy;
		this.customerCountry = customerCountry;
		this.customerDivision = customerDivision;
	}

	public Customer(int customerId, String customerName, String customerAddress, String customerZip,
	                String customerPhoneNumber, Timestamp createDate, String createdBy, Timestamp updateTime,
	                String updatedBy) {
		this.customerId = customerId;
		this.customerName = customerName;
		this.customerAddress = customerAddress;
		this.customerZip = customerZip;
		this.customerPhoneNumber = customerPhoneNumber;
		this.createDate = createDate;
		this.createdBy = createdBy;
		this.updateTime = updateTime;
		this.updatedBy = updatedBy;
	}

	public String getCustomerPhoneNumber() {
		return customerPhoneNumber;
	}

	public String getCustomerZip() {
		return customerZip;
	}

	public int getCustomerId() {
		return customerId;
	}

	public String getCustomerName() {
		return customerName;
	}

	public String getCustomerAddress() {
		return customerAddress;
	}

	public Timestamp getCreateDate() {
		return createDate;
	}

	public String getCreatedBy() {
		return createdBy;
	}

	public Timestamp getUpdateTime() {
		return updateTime;
	}

	public String getUpdatedBy() {
		return updatedBy;
	}

	public String getCustomerCountry() {
		return customerCountry;
	}

	public void setCustomerCountry(String customerCountry) {
		this.customerCountry = customerCountry;
	}

	public String getCustomerDivision() {
		return customerDivision;
	}

	public void setCustomerDivision(String customerDivision) {
		this.customerDivision = customerDivision;
	}

	public int getDivisionId() {
		return divisionId;
	}

	public void setDivisionId(int divisionId) {
		this.divisionId = divisionId;
	}

	@Override
	public String toString() {

		return customerAddress + "," + customerDivision;
	}
}
