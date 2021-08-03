package model;

public class User {
	//User_ID INT(10) (PK)
	private int userID;
	//User_Name VARCHAR(50) (UNIQUE)
	private String userName;
	//Password TEXT
	private String password;

	public User(int userID, String userName, String password) {
		this.userID = userID;
		this.userName = userName;
		this.password = password;
	}

	public User() {

	}

	public User(int activeUserId, String userName) {
		this.userID = activeUserId;
		this.userName = userName;
	}


	public int getUserID() {
		return userID;
	}

	public void setUserID(int userID) {
		this.userID = userID;
	}

	public String getUserName() {
		return userName;
	}

	public String getPassword() {
		return password;
	}
}
