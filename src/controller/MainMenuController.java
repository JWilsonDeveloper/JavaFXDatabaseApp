package controller;

import DAO.DBAppointments;
import DAO.DBCustomers;
import com.company.Main;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.util.Callback;
import model.Appointment;
import model.Customer;
import utility.DateTimeFormatCell;
import utility.SceneInterface;
import utility.Time;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ResourceBundle;
import java.util.concurrent.atomic.AtomicBoolean;

/** This class is a controller for the main menu.
 * @author James Wilson
 */
public class MainMenuController implements Initializable {

    // Declaration of variables
    Stage stage;
    Parent scene;
    // Implementation of SceneInterface
    SceneInterface sceneChange = (event, address) -> {
        stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
        scene = FXMLLoader.load(getClass().getResource(address));
        stage.setScene(new Scene(scene));
        stage.show();
    };

    // GUI Controls
    @FXML
    private TableColumn<Customer, String> fldCol;

    @FXML
    private TableColumn<Customer, String> addressCol;

    @FXML
    private TableColumn<Appointment, Integer> appointmentCustomerIdCol;

    @FXML
    private TableColumn<Appointment, Integer> appointmentIdCol;

    @FXML
    private TableView<Appointment> appointmentsTableView;

    @FXML
    private TableColumn<Appointment, String> contactCol;

    @FXML
    private TableColumn<Customer, String> countryCol;

    @FXML
    private TableColumn<Customer, Integer> customerCustomerIdCol;

    @FXML
    private TableView<Customer> customersTableView;

    @FXML
    private TableColumn<Appointment, String> descriptionCol;

    @FXML
    private TableColumn endDTCol;

    @FXML
    private TableColumn<Appointment, String> locationCol;

    @FXML
    private TextArea mainMenuMsgTxt;

    @FXML
    private TextArea mainMenuMsgTxt1;

    @FXML
    private TableColumn<Customer, String> nameCol;

    @FXML
    private TableColumn<Customer, String> phoneCol;

    @FXML
    private TableColumn<Customer, String> postalCodeCol;

    @FXML
    private TableColumn startDTCol;

    @FXML
    private TableColumn<Appointment, String> titleCol;

    @FXML
    private TableColumn<Appointment, String> typeCol;

    @FXML
    private TableColumn<Appointment, Integer> userIdCol;

    @FXML
    private RadioButton allRbtn;

    @FXML
    private RadioButton monthRBtn;

    @FXML
    private RadioButton weekRBtn;

    @FXML
    private ToggleGroup appointmentsTG;

    /** This method sets the appointment tableview to display all appointments.
     * @param event All Appointments button clicked.
     */
    @FXML
    void onMouseClickedAll(MouseEvent event) throws SQLException {
        appointmentsTableView.setItems(DBAppointments.getAllAppointments());
    }

    /** This method sets the appointment tableview to display all appointments from this month.
     * It gets all appointments and goes through each one to see if it starts this month this year.
     * @param event This Month button clicked.
     */
    @FXML
    void onMouseClickedMonth(MouseEvent event) throws SQLException {
        ObservableList<Appointment> allMonthAppointments = FXCollections.observableArrayList();;
        ObservableList<Appointment> allAppointments = DBAppointments.getAllAppointments();

        // Go through each appointment
        for(Appointment a : allAppointments){
            LocalDate localDate = Time.utcToLocal(a.getUtcStart()).toLocalDate();
            LocalDate today = LocalDate.now();
            // Check if appointment is this month
            if(localDate.getMonth() == today.getMonth() && localDate.getYear() == today.getYear()){
                // Add appointment to a list
                allMonthAppointments.add(a);
            }
        }
        appointmentsTableView.setItems(allMonthAppointments);
    }

    /** This method sets the appointment tableview to display all appointments from this week.
     * It gets all appointments and then goes through each one to see if it starts this week.
     * @param event This Week button clicked.
     */
    @FXML
    void onMouseClickedWeek(MouseEvent event) throws SQLException {
        ObservableList<Appointment> allWeekAppointments = FXCollections.observableArrayList();;
        ObservableList<Appointment> allAppointments = DBAppointments.getAllAppointments();

        // Go through each appointment
        for(Appointment a : allAppointments){
            LocalDate localDate = Time.utcToLocal(a.getUtcStart()).toLocalDate();
            LocalDate today = LocalDate.now();
            LocalDate thisSun = today.minusDays(today.getDayOfWeek().getValue());
            LocalDate thisSat = thisSun.plusDays(6);
            // Check if the appointment is this week
            if(!(localDate.isBefore(thisSun) || localDate.isAfter(thisSat))) {
                // Add appointment to a list
                allWeekAppointments.add(a);
            }
        }
        appointmentsTableView.setItems(allWeekAppointments);
    }


    /** This method loads the create appointment screen.
     * It utilizes the sceneChange lambda expression.
     * @param event Create(appointment) button clicked.
     */
    @FXML
    void onActionCreateAppointment(ActionEvent event) throws IOException {
        sceneChange.loadScene(event, "/view/CreateAppointment.fxml");
    }

    /** This method loads the create customer screen.
     * It utilizes the sceneChange lambda expression.
     * @param event Create(customer) button clicked.
     */
    @FXML
    void onActionCreateCustomer(ActionEvent event) throws IOException {
        sceneChange.loadScene(event, "/view/CreateCustomer.fxml");
    }

    /** This method deletes the selected appointment from the appointment tableview.
     * First, it alerts the user if an appointment is not selected.
     * If one is selected, it confirms whether or not the user would like to delete it.
     * Finally, it attempts to delete the appointment and then alerts the user of the outcome.
     * It utilizes the sceneChange lambda expression.
     * @param event Create(appointment) button clicked.
     */
    @FXML
    void onActionDeleteAppointment(ActionEvent event) throws SQLException {
        // Check if no appointment has been selected
        if(appointmentsTableView.getSelectionModel().getSelectedItem() == null) {
            mainMenuMsgTxt.setText("Select an appointment from the table to delete");
        }
        else {
            // Get selected appointment
            Appointment appointment = appointmentsTableView.getSelectionModel().getSelectedItem();

            // Display confirmation message
            AtomicBoolean confirmBool = new AtomicBoolean(false);
            int appointmentId = appointment.getAppointmentId();
            String appointmentType = appointment.getType();
            Alert deleteConfirm = new Alert(Alert.AlertType.CONFIRMATION);
            deleteConfirm.setContentText("Are you sure you want to delete this appointment?\nAppointment Id: " + appointmentId + "\nAppointment Type: " + appointmentType);
            deleteConfirm.showAndWait().filter(response -> response == ButtonType.OK).ifPresent(response -> confirmBool.set(true));
            // If the user wants to delete the appointment...
            if(confirmBool.get()){
                // Delete appointment
                int rowsAffected = DBAppointments.deleteAppointment(appointment.getAppointmentId());
                if (rowsAffected > 0) {
                    mainMenuMsgTxt.setText("Delete successful. An appointment has been removed from the table.\nAppointment Id " + appointmentId + "\nAppointment Type: " + appointmentType);
                } else {
                    mainMenuMsgTxt.setText("Delete failed");
                }

                // Reset appointments table in case an appointment has been deleted
                appointmentsTableView.setItems(DBAppointments.getAllAppointments());
            }
        }
    }

    /** This method deletes the selected customer from the customer tableview.
     * First, it alerts the user if a customer is not selected.
     * If one is selected, it confirms whether or not the user would like to delete the customer.
     * Finally, it attempts to delete the customer and then alerts the user of the outcome.
     * Any appointments in the appointment tableview for this customer will also be deleted.
     * It utilizes the sceneChange lambda expression.
     * @param event Create(customer) button clicked.
     */
    @FXML
    void onActionDeleteCustomer(ActionEvent event) throws SQLException {
        // Check if no appointment has been selected
        if (customersTableView.getSelectionModel().getSelectedItem() == null) {
            mainMenuMsgTxt1.setText("Select a customer from the table to delete");
        }
        else {
            // Get selected customer
            Customer customer = customersTableView.getSelectionModel().getSelectedItem();

            // Display confirmation message
            AtomicBoolean confirmBool = new AtomicBoolean(false);
            String customerName = customer.getName();
            Alert deleteConfirm = new Alert(Alert.AlertType.CONFIRMATION);
            deleteConfirm.setContentText("Are you sure you want to delete this customer?\nCustomer Name: " + customerName);
            deleteConfirm.showAndWait().filter(response -> response == ButtonType.OK).ifPresent(response -> confirmBool.set(true));
            // If the user wants to delete the customer...
            if(confirmBool.get()){
                // Delete customer
                int rowsAffected = DBCustomers.deleteCustomer(customer.getCustomerId());
                if(rowsAffected > 0) {
                    mainMenuMsgTxt1.setText("Delete successful. A customer has been removed from the table.\nCustomer Name: " + customerName);
                }
                else {
                    mainMenuMsgTxt1.setText("Delete failed");
                }

                // Reset customers table in case a customer has been deleted
                customersTableView.setItems(DBCustomers.getAllCustomers());
                // Reset appointments table in case an appointment has been deleted.
                // Deleting a customer may affect the appointments table.
                appointmentsTableView.setItems(DBAppointments.getAllAppointments());
                mainMenuMsgTxt.setText("Customer " + customerName + " deleted.\nAs a result, if " + customerName + " had any appointments scheduled, they have been deleted.");
            }
        }
    }

    /** This method loads the login screen.
     * It also sets the loggedIn boolean to false.
     * It utilizes the sceneChange lambda expression.
     * @param event Logout button clicked.
     */
    @FXML
    void onActionLogOut(ActionEvent event) throws IOException {
        Main.beenLoggedIn = false;
        sceneChange.loadScene(event, "/view/LoginMenu.fxml");
    }

    /** This method loads the reports screen.
     * It utilizes the sceneChange lambda expression.
     * @param event Reports button clicked.
     */
    @FXML
    void onActionReports(ActionEvent event) throws IOException {
        sceneChange.loadScene(event, "/view/ReportsMenu.fxml");
    }

    /** This method loads the update appointment screen with the information of the selected appointment in the appointments tableview.
     * First, it alerts the user if there is no selected appointment.
     * If an appointment is selected, the appointment is passed to the UpdateAppointmentController.
     * Finally, the update appointment screen is loaded.
     * @param event Update(appointment) button clicked.
     */
    @FXML
    void onActionUpdateAppointment(ActionEvent event) throws IOException {
        // Utilizes the sendAppointment() method in UpdateAppointmentController to preload the appointment's existing information
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/view/UpdateAppointment.fxml"));
            loader.load();
            UpdateAppointmentController updateAppointmentController = loader.getController();
            updateAppointmentController.sendAppointment(appointmentsTableView.getSelectionModel().getSelectedItem());

            stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
            Parent scene = loader.getRoot();
            stage.setScene(new Scene(scene));
            stage.show();
        }
        catch (NullPointerException e) {
            mainMenuMsgTxt.setText("Select an appointment from the table to update.");
        }
        catch (SQLException sqlE) {
            mainMenuMsgTxt.setText(sqlE.getMessage());
        }
    }

    /** This method loads the update customer screen with the information of the selected customer in the customers tableview.
     * First, it alerts the user if there is no selected customer.
     * If a customer is selected, the customer is passed to the UpdateCustomerController.
     * Finally, the update customer screen is loaded.
     * @param event Update(customer) button clicked.
     */
    @FXML
    void onActionUpdateCustomer(ActionEvent event) throws IOException {
        // Utilizes the sendCustomer() method in UpdateCustomerController to preload the customer's existing information
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/view/UpdateCustomer.fxml"));
            loader.load();

            UpdateCustomerController updateCustomerController = loader.getController();
            updateCustomerController.sendCustomer(customersTableView.getSelectionModel().getSelectedItem());

            stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
            Parent scene = loader.getRoot();
            stage.setScene(new Scene(scene));
            stage.show();
        }
        catch (NullPointerException e) {
            mainMenuMsgTxt1.setText("Select a customer from the table to update.");
        }
        catch (SQLException sqlE) {
            mainMenuMsgTxt1.setText(sqlE.getMessage());
        }
    }

    /** This method displays a message from a controller class.
     * @param msg The message to be displayed below the appointments tableview
     */
    public void sendMsg(String msg){
        mainMenuMsgTxt.setText(msg);
    }

    /** This method displays a message from a controller class.
     * @param msg The message to be displayed below the customers tableview
     */
    public void sendMsg1(String msg){
        mainMenuMsgTxt1.setText(msg);
    }

    /** This method initializes the main menu controller class.
     * It sets the items in the customers and appointments tableviews.
     * It also alerts the user if there is an ongoing or upcoming appointment.
     * @param url file path
     * @param rb null
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        ObservableList<Appointment> imminentAppts = FXCollections.observableArrayList();
        try {
            // Initialize customer table
            customersTableView.setItems(DBCustomers.getAllCustomers());
            customerCustomerIdCol.setCellValueFactory(new PropertyValueFactory<>("customerId"));
            nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
            phoneCol.setCellValueFactory(new PropertyValueFactory<>("phone"));
            addressCol.setCellValueFactory(new PropertyValueFactory<>("address"));
            postalCodeCol.setCellValueFactory(new PropertyValueFactory<>("postalCode"));
            fldCol.setCellValueFactory(new PropertyValueFactory<>("fld"));
            countryCol.setCellValueFactory(new PropertyValueFactory<>("country"));

            // For formatting date/time in tableview cells
            Callback<TableColumn, TableCell> DateTimeCellFactory =
                    new Callback<TableColumn, TableCell>() {
                        public TableCell call(TableColumn p) {
                            return new DateTimeFormatCell();
                        }
                    };
            // Initialize appointment table
            appointmentsTableView.setItems(DBAppointments.getAllAppointments());
            appointmentIdCol.setCellValueFactory(new PropertyValueFactory<>("appointmentId"));
            titleCol.setCellValueFactory(new PropertyValueFactory<>("title"));
            descriptionCol.setCellValueFactory(new PropertyValueFactory<>("description"));
            locationCol.setCellValueFactory(new PropertyValueFactory<>("location"));
            contactCol.setCellValueFactory(new PropertyValueFactory<>("contact"));
            typeCol.setCellValueFactory(new PropertyValueFactory<>("type"));
            startDTCol.setCellFactory(DateTimeCellFactory);
            startDTCol.setCellValueFactory(new PropertyValueFactory<Appointment, ZonedDateTime>("utcStart"));
            endDTCol.setCellFactory(DateTimeCellFactory);
            endDTCol.setCellValueFactory(new PropertyValueFactory<Appointment, ZonedDateTime>("utcEnd"));
            appointmentCustomerIdCol.setCellValueFactory(new PropertyValueFactory<>("customerId"));
            userIdCol.setCellValueFactory(new PropertyValueFactory<>("userId"));

            // Check if there are any ongoing or upcoming appointments
            imminentAppts = DBAppointments.getImminentAppts();
        }
        catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        // If the user just logged in...
        if (Main.beenLoggedIn == false) {
            String imminentApptAlert = "";
            // If there are imminent appointments...
            if (!(imminentAppts.isEmpty())) {
                // Go through each imminent appointment
                for (Appointment imminentAppt : imminentAppts) {
                    long minutes = Math.abs(ChronoUnit.MINUTES.between(Time.utcToLocal(imminentAppt.getUtcStart()).toLocalTime(), LocalTime.now()));
                    ZonedDateTime apptLocalStart = Time.utcToLocal(imminentAppt.getUtcStart());
                    // Generate alert for imminent appointment depending on whether it's upcoming or ongoing
                    if (apptLocalStart.toLocalTime().isAfter(LocalTime.now())) {
                        imminentApptAlert += "\nUPCOMING APPOINTMENT: An appointment is starting in " + minutes + " minute(s)!\nAppointment Id: " + imminentAppt.getAppointmentId() + "   Date: " + apptLocalStart.toLocalDate() + "   Time: " + apptLocalStart.toLocalTime();
                    } else {
                        imminentApptAlert = "\nONGOING APPOINTMENT: An appointment started " + minutes + " minute(s) ago.\nAppointment Id: " + imminentAppt.getAppointmentId() + "   Date: " + apptLocalStart.toLocalDate() + "   Time: " + apptLocalStart.toLocalTime() + imminentApptAlert;
                    }
                }
            }
            // If there are no imminent appointments...
            else {
                imminentApptAlert = "There are no upcoming appointments";
            }

            // Display imminent appointment alert
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Ongoing/Upcoming Appointments");
            alert.setContentText(imminentApptAlert);
            alert.show();

            // Set beenLoggedIn to true so that the imminent appointment alert
            // won't appear every time the user returns to the main menu
            Main.beenLoggedIn = true;
        }
    }
}
