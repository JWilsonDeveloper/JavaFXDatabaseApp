package controller;

import DAO.DBCountries;
import DAO.DBCustomers;
import DAO.DBFLDs;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import model.Customer;
import utility.ErrorMessageInterface;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.time.LocalTime;
import java.util.ResourceBundle;

/** This class is the controller for the update customer menu.
 * @author James Wilson
 */
public class UpdateCustomerController implements Initializable {

    // Declare variables
    Stage stage;
    Parent scene;

    // GUI Controls
    @FXML
    private ComboBox<String> firstLevelDivComboBox;

    @FXML
    private TextField addressTxt;

    @FXML
    private ComboBox<String> countryComboBox;

    @FXML
    private TextArea updateCustomerMsgTxt;

    @FXML
    private TextField customerIdTxt;

    @FXML
    private TextField nameTxt;

    @FXML
    private TextField phoneTxt;

    @FXML
    private TextField postalCodeTxt;

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

    /** This method saves the customer information and loads the main menu.
     * First, it alerts the user if any fields still need to be filled in.
     * If all fields are complete, the customer is saved and the main menu is loaded.
     * * It utilizes the error lambda expression.
     * @param event Save button clicked.
     */
    @FXML
    void onActionSaveCustomer(ActionEvent event) throws IOException, SQLException {
        // Initialize variables
        int rowsAffected = 0;
        String eMsg = "";
        String updateMsg = "";

        // Read in values from the form
        int customerId = Integer.parseInt(customerIdTxt.getText());
        String name = nameTxt.getText();
        String address = addressTxt.getText();
        String postal = postalCodeTxt.getText();
        String phone = phoneTxt.getText();
        String fld = firstLevelDivComboBox.getValue();

        // Lambda expression for functional interface.
        // Collects errors regarding empty fields.
        ErrorMessageInterface error = (msg, field, fieldTitle) -> {
            if(field == null || field.equals("")) {
                msg += "----" + fieldTitle + " is required\n";
            }
            return msg;
        };
        // Use of implemented interface
        eMsg = error.appendMsg(eMsg, name, "Name");
        eMsg = error.appendMsg(eMsg, phone, "Phone");
        eMsg = error.appendMsg(eMsg, address, "Address");
        eMsg = error.appendMsg(eMsg, postal, "Postal code");
        eMsg = error.appendMsg(eMsg, countryComboBox.getValue(), "Country");
        eMsg = error.appendMsg(eMsg, firstLevelDivComboBox.getValue(), "First level division");

        // If there are no empty fields...
        if (eMsg.equals("")){
            // Add customer
            rowsAffected = DBCustomers.updateCustomer(customerId, name, address, postal, phone, fld);
        }
        // If update operation didn't fail load main menu
        if(rowsAffected > 0) {
            updateMsg = "Update successful";
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/view/MainMenu.fxml"));
            loader.load();

            MainMenuController mainMenuController = loader.getController();
            mainMenuController.sendMsg1(updateMsg);

            stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
            Parent scene = loader.getRoot();
            stage.setScene(new Scene(scene));
            stage.show();
        }
        // Display error message if customer wasn't added
        else {
            updateCustomerMsgTxt.setText("Update failed\n" + eMsg);
        }
    }

    /** This method takes the customer and fills in the update customer fields with the customer's existing information.
     * @param customer The customer selected from the main menu.
     */
    public void sendCustomer(Customer customer) throws SQLException {
        // Load fields with pre-existing customer information
        customerIdTxt.setText(String.valueOf(customer.getCustomerId()));
        nameTxt.setText(customer.getName());
        phoneTxt.setText(customer.getPhone());
        addressTxt.setText(customer.getAddress());
        postalCodeTxt.setText(customer.getPostalCode());
        countryComboBox.getSelectionModel().select(customer.getCountry());
        firstLevelDivComboBox.getSelectionModel().select(customer.getFld());
        firstLevelDivComboBox.setItems(DBFLDs.getCountryFlds(customer.getCountry()));
    }

    /** This method sets the first level divisions in a combo box based on the selected country.
     * Then it enables the combo box so a division can be selected.
     * @param event Country selected.
     */
    @FXML
    void onActionDisplayDivisions(ActionEvent event) throws SQLException {
        firstLevelDivComboBox.setItems(DBFLDs.getCountryFlds(countryComboBox.getValue()));
    }

    /** This method initializes the update customer controller class.
     * It sets the items in the country combo box.
     * @param url file path
     * @param rb null
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        try {
            countryComboBox.setItems(DBCountries.getAllCountries());
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }
}
