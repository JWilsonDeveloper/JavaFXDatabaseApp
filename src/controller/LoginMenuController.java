package controller;

import DAO.DBUsers;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import DAO.JDBC;
import model.User;
import utility.SceneInterface;
import utility.Time;
import java.io.*;
import java.net.URL;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Locale;
import java.util.ResourceBundle;

/** This class is the controller for the login menu.
 * @author James Wilson
 */
public class LoginMenuController implements Initializable {

    // Declaration of variables
    Stage stage;
    Parent scene;
    ResourceBundle myRB;

    // Implementation of SceneInterface
    SceneInterface sceneChange = (event, address) -> {
        stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
        scene = FXMLLoader.load(getClass().getResource(address));
        stage.setScene(new Scene(scene));
        stage.show();
    };

    // GUI Controls
    @FXML
    private Button exitBtn;

    @FXML
    private TextField loginMsgTxt;

    @FXML
    private Label passwordLbl;

    @FXML
    private TextField passwordTxt;

    @FXML
    private Button submitBtn;

    @FXML
    private Label usernameLbl;

    @FXML
    private TextField usernameTxt;

    @FXML
    private TextField zoneIdTxt;


    /** This method closes the connection to the database and the program.
     * @param event Exit button clicked.
     */
    @FXML
    void onActionExit(ActionEvent event) {
        JDBC.closeConnection();
        System.exit(0);
    }


    /** This method validates the user's username and login.
     * It displays an error message if the credentials are invalid, and it records successful/unsuccessful attempts to a file.
     * It utilizes the sceneChange lambda expression.
     * @param event Submit button clicked.
     */
    @FXML
    void onActionSubmit(ActionEvent event) {
        // The login menu error messages may be translated to French based on the user's locale.
        // Try/catch necessary for attempting to write to a file.
        try {
            // Check if the username or password fields are empty.
            String username = usernameTxt.getText();
            String password = passwordTxt.getText();
            if("".equals(username) || "".equals(password)) {
                loginMsgTxt.setText(myRB.getString("PleaseEnterAUsernameAndPassword"));
            }
            else {
                // Output file name
                String filename = "login_activity.txt";
                // The message that will be written to the file
                String outputLine;
                // Get username
                User user = DBUsers.getUser(username);
                // Create fileWriter object
                FileWriter fWriter = new FileWriter(filename, true);
                // Create and open file
                PrintWriter outputFile = new PrintWriter(fWriter);

                // If username and password are valid...
                if (user.getPassword().equals(password)) {
                    outputLine = "User " + username + " successfully logged in at " + Time.localToUtc(ZonedDateTime.now());
                    outputFile.println(outputLine);
                    outputFile.close();
                    System.out.println("login_activity.txt updated");
                    DBUsers.activeUser = user;
                    sceneChange.loadScene(event, "/view/MainMenu.fxml");
                }
                // If username and password are invalid...
                else {
                    outputLine = "User " + username + " gave invalid login at " + Time.localToUtc(ZonedDateTime.now());
                    outputFile.println(outputLine);
                    outputFile.close();
                    System.out.println("login_activity.txt updated");
                    loginMsgTxt.setText(myRB.getString("InvalidUsername/Password"));
                }
            }
        }
        catch (Exception e){
            System.out.println(e.getMessage());
        }
    }

    /** This method initializes the login controller class.
     * It changes the language displayed on the login screen based on the user's locale.
     * @param url file path
     * @param rb null
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        zoneIdTxt.setText(String.valueOf(ZoneId.systemDefault()));

        // The utility package contains resource bundles for French and English.
        // The login menu will be translated to English or French based on the user's locale.
        try {
            myRB = ResourceBundle.getBundle("utility/Nat", Locale.getDefault());
        }
        catch (Exception e) {
            myRB = ResourceBundle.getBundle("utility/Nat", Locale.ENGLISH);
        }
        usernameLbl.setText(myRB.getString("Username"));
        passwordLbl.setText(myRB.getString("Password"));
        submitBtn.setText(myRB.getString("Submit"));
        exitBtn.setText(myRB.getString("Exit"));
    }
}
