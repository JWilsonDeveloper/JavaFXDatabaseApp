package com.company;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import DAO.JDBC;


/** This class creates an application for scheduling appointments.
 * @author James Wilson
 */
public class Main extends Application {
    // This boolean is used to determine whether or not an alert should be generated when the user opens the main menu
    public static boolean beenLoggedIn = false;

    /** This is the main method.
     * It opens a connection with the MySQL database and launches the program.
     * @param args an array storing command-line arguments
     */
    public static void main(String[] args) {
        JDBC.openConnection();
        launch(args);
        JDBC.closeConnection();
    }

    /** This is the start method. It loads and displays the login screen.
     * @param primaryStage the stage, a javafx container for scenes
     * @throws Exception scene unable to launch; error loading file
     */
    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("/view/LoginMenu.fxml"));
        primaryStage.setTitle("Scheduling Application");
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
    }
}