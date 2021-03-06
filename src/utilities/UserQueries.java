package utilities;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import model.User;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserQueries {

	private static ObservableList<User> userObservableList = FXCollections.observableArrayList();

	/**
	 * Queries all users in the database.
	 *
	 * @return ObservableList users.
	 */
	public static ObservableList<User> getAllUsers() throws SQLException {
		String queryUser = "SELECT * FROM didactic_meme.dbo.users";
		try (
				PreparedStatement ps = DataBaseConnection.getConnection().prepareStatement(queryUser)) {
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				String userName = rs.getString("User_Name");
				int userId = rs.getInt("User_ID");
				String password = rs.getString("Password");

				User user = new User(userId, userName, password);
				userObservableList.add(user);
			}
			return userObservableList;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Queries the user who's currently logged in.
	 *
	 * @param activeUserId user currently logged in.
	 * @return User logged in user.
	 */
	public static User lookupUser(int activeUserId) throws SQLException {
		String userSearch = "SELECT * FROM didactic_meme.dbo.users Where User_ID = ?";
		try (PreparedStatement ps = DataBaseConnection.getConnection().prepareStatement(userSearch)) {

			ps.setInt(1, activeUserId);
			ResultSet rs = ps.executeQuery();
			String userName = rs.getString("User_Name");
			int userId = rs.getInt("User_ID");
			String password = rs.getString("Password");
			return new User(userId, userName);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

		/**
		 * Queries the user who's attempting to log-in.
		 *
		 * @param userName active user
		 * @param password user password
		 * @return User logged in user.
		 */
		public static User lookupUser (String userName, String password) {
			String userSearch = " SELECT * FROM didactic_meme.dbo.users Where User_Name = ?" +
					" " +
					"And Password = ?";


			try (PreparedStatement ps = DataBaseConnection.getConnection().prepareStatement(userSearch)) {


				ps.setString(1, userName);
				ps.setString(2, password);

				ResultSet rs = ps.executeQuery();
				if (rs.next()) {
					int userID = rs.getInt("User_ID");
					return new User(userID, userName, password);
				}

				return new User(0, null, null);
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}
	}

