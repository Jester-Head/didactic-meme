package main;


import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import utilities.DataBaseConnection;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Application driver.
 */
public class SchedulerMain extends Application {


    public static void main(String[] args) {
        // Driver mysql:mysql-connector-java:8.0.23
        Connection connection = DataBaseConnection.startConnection();
        try (connection){
            launch(args);
            DataBaseConnection.closeConnection();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }


    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/SignInScreen.fxml"));
        Parent root = loader.load();
        primaryStage.setScene(new Scene(root));
        primaryStage.show();

    }


}


