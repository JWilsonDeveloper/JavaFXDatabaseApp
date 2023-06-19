package controller;

import DAO.DBAppointments;
import DAO.DBContacts;
import DAO.DBCountries;
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
import javafx.stage.Stage;
import javafx.util.Callback;
import model.Appointment;
import model.Country;
import utility.DateTimeFormatCell;
import utility.Time;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.time.Month;
import java.time.ZonedDateTime;
import java.util.ResourceBundle;

/** This class is a controller for the login menu.
 * @author James Wilson
 */
public class ReportsMenuController implements Initializable {

    // Declaration of variables
    Stage stage;
    Parent scene;

    // GUI Controls
    @FXML
    private TableColumn<?, ?> appointmentIdCol;

    @FXML
    private ComboBox<String> contactComboBox;

    @FXML
    private TableColumn<?, ?> countryCol;

    @FXML
    private TableView<Country> countryCustomersTableView;

    @FXML
    private TableColumn<?, ?> customerIdCol;

    @FXML
    private TableView<Appointment> contactTableView;

    @FXML
    private TableColumn<?, ?> descriptionCol;

    @FXML
    private TableColumn<Appointment, ZonedDateTime> endDTCol;

    @FXML
    private ComboBox<Month> monthComboBox;

    @FXML
    private TextField numAppointmentsTxt;

    @FXML
    private TableColumn<?, ?> numCustomersCol;

    @FXML
    private TableColumn<Appointment, ZonedDateTime> startDTCol;

    @FXML
    private TableColumn<?, ?> titleCol;

    @FXML
    private TableColumn<?, ?> typeCol;

    @FXML
    private ComboBox<String> typeComboBox;

    /** This method loads the main menu.
     * @param event Back button clicked.
     */
    @FXML
    void onActionMainMenu(ActionEvent event) throws IOException {
        stage = (Stage)((Button)event.getSource()).getScene().getWindow();
        scene = FXMLLoader.load(getClass().getResource("/view/MainMenu.fxml"));
        stage.setScene(new Scene(scene));
        stage.show();
    }

    /** This method displays the number of existing appointments of a given type in a given month of this year.
     * @param event Month selected.
     */
    @FXML
    void onActionMonthGenAppts(ActionEvent event) throws SQLException {
        // If type has also been selected, in addition to month...
        if(typeComboBox.getSelectionModel().getSelectedItem() != null) {
            // Find and display number of appointments with the selected month and type
            int numAppts = DBAppointments.getNumAppts(monthComboBox.getSelectionModel().getSelectedItem(), typeComboBox.getSelectionModel().getSelectedItem());
            numAppointmentsTxt.setText(String.valueOf(numAppts));
        }
    }

    /** This method displays the number of existing appointments of a given type in a given month of this year.
     * @param event Type selected.
     */
    @FXML
    void onActionTypeGenAppts(ActionEvent event) throws SQLException {
        // If month has also been selected, in addition to type...
        if(monthComboBox.getSelectionModel().getSelectedItem() != null) {
            // Find and display number of appointments with the selected month and type
            int numAppts = DBAppointments.getNumAppts(monthComboBox.getSelectionModel().getSelectedItem(), typeComboBox.getSelectionModel().getSelectedItem());
            numAppointmentsTxt.setText(String.valueOf(numAppts));
        }
    }

    /** This method displays the existing appointments associated with a selected contact on the Schedule tableview.
     * @param event Contact selected.
     */
    @FXML
    void onActionGenSchedule(ActionEvent event) throws SQLException {
        // For formatting date/time in tableview cells
        Callback<TableColumn<Appointment, ZonedDateTime>, TableCell<Appointment, ZonedDateTime>> DateTimeCellFactory =
                new Callback<>() {
                    public TableCell call(TableColumn p) {
                        return new DateTimeFormatCell();
                    }
                };
        // Initialize contact schedule table
        ObservableList<Appointment> contactAppts = FXCollections.observableArrayList();
        contactAppts = DBAppointments.getApptsByContact(contactComboBox.getSelectionModel().getSelectedItem());
        contactTableView.setItems(contactAppts);
        appointmentIdCol.setCellValueFactory(new PropertyValueFactory<>("appointmentId"));
        titleCol.setCellValueFactory(new PropertyValueFactory<>("title"));
        descriptionCol.setCellValueFactory(new PropertyValueFactory<>("description"));
        typeCol.setCellValueFactory(new PropertyValueFactory<>("type"));
        startDTCol.setCellFactory(DateTimeCellFactory);
        startDTCol.setCellValueFactory(new PropertyValueFactory<Appointment, ZonedDateTime>("utcStart"));
        endDTCol.setCellFactory(DateTimeCellFactory);
        endDTCol.setCellValueFactory(new PropertyValueFactory<Appointment, ZonedDateTime>("utcEnd"));
        customerIdCol.setCellValueFactory(new PropertyValueFactory<>("customerId"));
    }

    /** This method initializes the reports controller class.
     * It sets the items in the type, month, and contact comboBoxes and it sets the valueFactory for the Schedule tableview.
     * @param url file path
     * @param rb null
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        try {
            typeComboBox.setItems(DBAppointments.getAllTypes());
            monthComboBox.setItems(Time.getMonths());
            countryCustomersTableView.setItems(DBCountries.getAllCountryObjects());
            countryCol.setCellValueFactory(new PropertyValueFactory<>("name"));
            numCustomersCol.setCellValueFactory(new PropertyValueFactory<>("numCustomers"));
            contactComboBox.setItems(DBContacts.getAllContactNames());
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

    }
}
