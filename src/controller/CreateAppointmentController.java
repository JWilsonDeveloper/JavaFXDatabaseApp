package controller;

import DAO.DBAppointments;
import DAO.DBContacts;
import DAO.DBCustomers;
import DAO.DBUsers;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import model.Customer;
import utility.ErrorMessageInterface;
import utility.Time;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ResourceBundle;

/** This class is the controller for the create appointment menu.
 * @author James Wilson
 */
public class CreateAppointmentController implements Initializable {

    // Declare variables
    Stage stage;
    Parent scene;
    Customer selectedCustomer;

    // GUI Controls
    @FXML
    private TextField appointmentIDTxt;

    @FXML
    private ComboBox<String> contactComboBox;

    @FXML
    private TextArea createAppointmentMsgTxt;

    @FXML
    private ComboBox<Integer> customerIDComboBox;

    @FXML
    private TextField descriptionTxt;

    @FXML
    private TextField endDateTxt;

    @FXML
    private TextField endTimeTxt;

    @FXML
    private TextField locationTxt;

    @FXML
    private DatePicker startDatePicker;

    @FXML
    private ComboBox<LocalTime> startTimeComboBox;

    @FXML
    private TextField titleTxt;

    @FXML
    private TextField typeTxt;

    @FXML
    private ComboBox<Integer> userIDComboBox;


    /** This method enables the startDatePicker.
     * It also resets the fields after the startDatePicker to null.
     * @param event Customer Id selected.
     */
    @FXML
    void onActionEnableStartDate(ActionEvent event) throws SQLException {
        // Set the selected customer
        selectedCustomer = DBCustomers.getCustomer(customerIDComboBox.getValue());

        // Reset all following fields.
        // A new customer may have been selected, and the appointment date/time that had been available
        // for the previously selected customer may clash with the new customer's pre-existing appointments.
        startDatePicker.setPromptText("");
        startTimeComboBox.setPromptText("Select a start date");
        endDateTxt.setPromptText("Select a start date");
        endTimeTxt.setPromptText("Select a start date");
        startDatePicker.setValue(null);
        startTimeComboBox.setValue(null);
        endDateTxt.setText(null);
        endTimeTxt.setText(null);
        startTimeComboBox.setDisable(true);

        // Allow the user to select a date for the selected customer's appointment.
        startDatePicker.setDisable(false);
    }


    /** This method enables the startTimeComboBox and populates it with available times based on the date in startDatePicker.
     * It also resets the fields after the startTimeComboBox to null.
     * @param event Start date selected.
     */
    @FXML
    void onActionDisplayStartTimes(ActionEvent event) throws SQLException {
        // Clear and enable start time selector
        startTimeComboBox.setDisable(false);
        startTimeComboBox.setValue(null);
        // Get available start times for the selected customer.
        // Times are unavailable if they are outside business hours,
        // or if the selected customer already has an appointment that hour.
        startTimeComboBox.setItems(Time.getAvailableTimes(selectedCustomer, startDatePicker.getValue(), false));

        // Reset all following fields
        endDateTxt.setText(null);
        endTimeTxt.setText(null);
        startTimeComboBox.setPromptText("");
    }


    /** This method displays the end date/time for the appointment based on the chosen start date/time.
     * @param event Start time selected.
     */
    @FXML
    void onActionDisplayEndDateTime(ActionEvent event) {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("M/dd/yyyy");
        // Check if the start time has been chosen
        if (startTimeComboBox.getValue() != null) {
            // The end time is always one hour after the start time
            endTimeTxt.setText(String.valueOf(startTimeComboBox.getValue().plus(1, ChronoUnit.HOURS)));
            // Check if the appointment ends at midnight
            if(endTimeTxt.getText().equals("00:00")){
                // Appointments that end at midnight have an end date one day after their starting date
                endDateTxt.setText(startDatePicker.getValue().plus(1, ChronoUnit.DAYS).format(dateTimeFormatter));;
            }
            else{
                // Appointments that don't end at midnight start and end on the same date
                endDateTxt.setText(startDatePicker.getValue().format(dateTimeFormatter));
            }
        }
    }


    /** This method loads the main menu.
     * @param event Cancel button clicked.
     */
    @FXML
    void onActionCancel(ActionEvent event) throws IOException {
        stage = (Stage)((Button)event.getSource()).getScene().getWindow();
        scene = FXMLLoader.load(getClass().getResource("/view/MainMenu.fxml"));
        stage.setScene(new Scene(scene));
        stage.show();
    }


    /** This method saves the appointment information and loads the main menu.
     * First, it alerts the user if any fields still need to be filled in.
     * If all fields are complete, the appointment is saved and the main menu is loaded.
     * It utilizes the error lambda expression.
     * @param event Save button clicked.
     */
    @FXML
    void onActionSaveAppointment(ActionEvent event) throws IOException, SQLException {
        // Initialize variables
        int rowsAffected = 0;
        String eMsg = "";
        String insertMsg = "";

        // Read in values from the form
        String title = titleTxt.getText();
        String description = descriptionTxt.getText();
        String location = locationTxt.getText();
        String contact = contactComboBox.getValue();
        String type = typeTxt.getText();
        LocalDate startDate = startDatePicker.getValue();
        LocalTime startTime = startTimeComboBox.getValue();

        // Lambda expression for functional interface.
        // Collects errors regarding empty fields.
        ErrorMessageInterface error = (msg, field, fieldTitle) -> {
            if(field == null || field.equals("")) {
                msg += "----" + fieldTitle + " is required\n";
            }
            return msg;
        };
        // Use of implemented interface
        eMsg = error.appendMsg(eMsg, title, "Title");
        eMsg = error.appendMsg(eMsg, description, "Description");
        eMsg = error.appendMsg(eMsg, location, "Location");
        eMsg = error.appendMsg(eMsg, contact, "Contact");
        eMsg = error.appendMsg(eMsg, type, "Type");
        eMsg = error.appendMsg(eMsg, userIDComboBox.getValue(), "User Id");
        eMsg = error.appendMsg(eMsg, customerIDComboBox.getValue(), "Customer Id");
        eMsg = error.appendMsg(eMsg, startDatePicker.getValue(), "Start date");
        eMsg = error.appendMsg(eMsg, startTimeComboBox.getValue(), "Start time");

        // If there are no empty fields
        if (eMsg.equals("")){
            // Get start and end ZonedDateTimes from the start date and time fields
            ZonedDateTime startLocalZdt = ZonedDateTime.of(startDate, startTime, ZoneId.systemDefault());
            ZonedDateTime endLocalZdt = startLocalZdt.plus(1, ChronoUnit.HOURS);
            // Add appointment
            rowsAffected = DBAppointments.addAppointment(title, description, location, contact, type, startLocalZdt, endLocalZdt, customerIDComboBox.getValue(), userIDComboBox.getValue());
        }
        // If insert operation didn't fail load main menu
        if(rowsAffected > 0) {
            insertMsg = "Insert successful";
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/view/MainMenu.fxml"));
            loader.load();

            MainMenuController mainMenuController = loader.getController();
            mainMenuController.sendMsg(insertMsg);

            stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
            Parent scene = loader.getRoot();
            stage.setScene(new Scene(scene));
            stage.show();
        }
        // Display error message if appointment wasn't added
        else {
            createAppointmentMsgTxt.setText("Insert failed\n" + eMsg);
        }
    }


    /** This method initializes the create appointment controller class.
     * It sets the items in the customerID, the userID, and the contact comboBoxes.
     * @param url file path
     * @param rb null
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        try {
            customerIDComboBox.setItems(DBCustomers.getAllCustomerIds());
            userIDComboBox.setItems(DBUsers.getAllUserIds());
            contactComboBox.setItems(DBContacts.getAllContactNames());
        }
        catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }
}
