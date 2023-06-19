package controller;

import DAO.*;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import model.Appointment;
import model.Customer;
import utility.ErrorMessageInterface;
import utility.Time;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ResourceBundle;

/** This class is the controller for the update appointment menu.
 * @author James Wilson
 */
public class UpdateAppointmentController implements Initializable {

    // Declare variables
    Stage stage;
    Parent scene;
    Customer selectedCustomer;
    DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("M/dd/yyyy");

    // This variable is needed for Time.getAvailablesTimes().
    // When updating an appointment, the previously chosen start time needs to be recorded.
    // Otherwise, it would be seen as an overlapping existing appointment time and considered unavailable.
    // The user would not be able to re-select the previously chosen start time.
    public static LocalTime previousStartTime;

    // GUI Controls
    @FXML
    private TextField appointmentIDTxt;

    @FXML
    private ComboBox<String> contactComboBox;

    @FXML
    private TextArea updateAppointmentMsgTxt;

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
        int appointmentId = Integer.parseInt(appointmentIDTxt.getText());
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
            rowsAffected = DBAppointments.updateAppointment(appointmentId, title, description, location, contact, type, startLocalZdt, endLocalZdt, customerIDComboBox.getValue(), userIDComboBox.getValue());
        }
        // If update operation didn't fail load main menu
        if(rowsAffected > 0) {
            insertMsg = "Update successful";
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
            updateAppointmentMsgTxt.setText("Update failed\n" + eMsg);
        }
    }

    /** This method takes the appointment and fills in the update appointment fields with the appointment's existing information.
     * @param appointment The appointment selected from the main menu.
     */
    public void sendAppointment(Appointment appointment) throws SQLException {
        // Load fields with pre-existing appointment information
        appointmentIDTxt.setText(String.valueOf(appointment.getAppointmentId()));
        titleTxt.setText(appointment.getTitle());
        descriptionTxt.setText(appointment.getDescription());
        locationTxt.setText(appointment.getLocation());
        contactComboBox.setItems(DBContacts.getAllContactNames());
        contactComboBox.getSelectionModel().select(appointment.getContact());
        typeTxt.setText(appointment.getType());
        userIDComboBox.setItems(DBUsers.getAllUserIds());
        userIDComboBox.getSelectionModel().select(Integer.valueOf((appointment.getUserId())));
        customerIDComboBox.setItems(DBCustomers.getAllCustomerIds());
        customerIDComboBox.getSelectionModel().select(Integer.valueOf(String.valueOf(appointment.getCustomerId())));
        selectedCustomer = DBCustomers.getCustomer(customerIDComboBox.getValue());
        startDatePicker.setValue(Time.utcToLocal(appointment.getUtcStart()).toLocalDate());
        previousStartTime = Time.utcToLocal(appointment.getUtcStart()).toLocalTime();
        startTimeComboBox.setItems(Time.getAvailableTimes(selectedCustomer, startDatePicker.getValue(), true));
        startTimeComboBox.getSelectionModel().select(Time.utcToLocal(appointment.getUtcStart()).toLocalTime());

        if (startTimeComboBox.getValue() != null) {
            endTimeTxt.setText(String.valueOf(startTimeComboBox.getValue().plus(1, ChronoUnit.HOURS)));
            // An end time at midnight means the end date is one day later than the start date
            if(endTimeTxt.getText().equals("00:00")){
                endDateTxt.setText(startDatePicker.getValue().plus(1, ChronoUnit.DAYS).format(dateTimeFormatter));;
            }
            // If the end time isn't midnight, the end date is the same as the start date
            else{
                endDateTxt.setText(startDatePicker.getValue().format(dateTimeFormatter));
            }
        }
        startTimeComboBox.setDisable(false);
        startDatePicker.setDisable(false);
    }

    /** This method displays the end date/time for the appointment based on the chosen start date/time.
     * @param event Start time selected.
     */
    @FXML
    void onActionDisplayEndDateTime(ActionEvent event) {
        // Check if there's a start time
        if (startTimeComboBox.getValue() != null) {
            // End time is one hour after start time
            endTimeTxt.setText(String.valueOf(startTimeComboBox.getValue().plus(1, ChronoUnit.HOURS)));
            // An end time at midnight means the end date is one day later than the start date
            if(endTimeTxt.getText().equals("00:00")){
                endDateTxt.setText(startDatePicker.getValue().plus(1, ChronoUnit.DAYS).format(dateTimeFormatter));;
            }
            // If the end time isn't midnight, the end date is the same as the start date
            else{
                endDateTxt.setText(startDatePicker.getValue().format(dateTimeFormatter));
            }
        }
    }

    /** This method enables the startTimeComboBox and populates it with available times based on the date in startDatePicker.
     * It also resets the fields after the startTimeComboBox to null.
     * @param event Start date selected.
     */
    @FXML
    void onActionDisplayStartTimes(ActionEvent event) throws SQLException {
        // Start times should only be selectable if there is a customer id and a start date selected
        if(customerIDComboBox.getValue() != null && startDatePicker.getValue() != null) {
            // Allow the user to select from available start times
            startTimeComboBox.setDisable(false);
            startTimeComboBox.setValue(null);
            startTimeComboBox.setItems(Time.getAvailableTimes(selectedCustomer, startDatePicker.getValue(), true));

            // Reset end date and time
            endDateTxt.setText(null);
            endTimeTxt.setText(null);
            endDateTxt.setPromptText("Select a start time");
            endTimeTxt.setPromptText("Select a start time");
        }
    }

    /** This method enables the startDatePicker.
     * It also resets the fields after the startDatePicker to null.
     * @param event Customer Id selected.
     */
    @FXML
    void onActionEnableStartDate(ActionEvent event) throws SQLException {
        // Start dates should only be selectable if there is a customer selected
        if (customerIDComboBox.getValue() != null) {
            // Allow the user to select a start date
            startDatePicker.setValue(null);
            startDatePicker.setDisable(false);
            startDatePicker.setPromptText("");

            // Reset the following fields
            startTimeComboBox.setValue(null);
            endDateTxt.setText(null);
            endTimeTxt.setText(null);
            startTimeComboBox.setDisable(true);
            startTimeComboBox.setPromptText("Select a start date");
            endDateTxt.setPromptText("Select a start date");
            endTimeTxt.setPromptText("Select a start date");
         }
    }

    /** This method initializes the update appointment controller class.
     * @param url file path
     * @param rb null
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {}
}
