package DAO;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import model.Customer;
import utility.Time;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

/** This class is a collection of methods that include customer-related SQL queries/updates sent to the database server.
 * @author James Wilson
 */
public class DBCustomers {


    /** This method gets the customer with a specified id.
     * It executes a query that returns the customer with the specified id along with the customer's division and country.
     * @throws SQLException Database error. Issue executing/retrieving SQL operation.
     * @return A reference to the customer.
     */
    public static Customer getCustomer(int customerId) throws SQLException {
        // Initialize strings
        String name = "";
        String phone = "";
        String address = "";
        String postalCode = "";
        String fld = "";
        String country = "";

        // Select customer by their id with their first level division and country
        String sql = "SELECT client_schedule.customers.*, client_schedule.first_level_divisions.Division, client_schedule.countries.Country FROM client_schedule.customers INNER JOIN client_schedule.first_level_divisions ON client_schedule.customers.Division_ID=client_schedule.first_level_divisions.Division_ID INNER JOIN client_schedule.countries ON client_schedule.countries.Country_ID=client_schedule.first_level_divisions.Country_ID WHERE Customer_ID=?";
        PreparedStatement ps = JDBC.connection.prepareStatement(sql);
        ps.setInt(1, customerId);
        ResultSet rs = ps.executeQuery();
        while(rs.next()){
            // Get the information for the customer
            name = rs.getString("Customer_Name");
            phone = rs.getString("Phone");
            address = rs.getString("Address");
            postalCode = rs.getString("Postal_Code");
            fld = rs.getString("Division");
            country = rs.getString("Country");
        }
        // Create reference to the customer
        Customer customer = new Customer(customerId, name, phone, address, postalCode, fld, country);
        return customer;
    }

    /** This method gets all customers.
     * It executes a query that returns all customers along with their divisions and countries.
     * Then it adds each customer to a list.
     * @throws SQLException Database error. Issue executing/retrieving SQL operation.
     * @return A list of all customers.
     */
    public static ObservableList<Customer> getAllCustomers() throws SQLException {
        ObservableList<Customer> allCustomers = FXCollections.observableArrayList();

        // Select all customers with their first level division id and country
        String sql = "SELECT client_schedule.customers.*, client_schedule.first_level_divisions.Division, client_schedule.countries.Country FROM client_schedule.customers INNER JOIN client_schedule.first_level_divisions ON client_schedule.customers.Division_ID=client_schedule.first_level_divisions.Division_ID INNER JOIN client_schedule.countries ON client_schedule.countries.Country_ID=client_schedule.first_level_divisions.Country_ID";
        PreparedStatement ps = JDBC.connection.prepareStatement(sql);
        ResultSet rs = ps.executeQuery();
        while(rs.next()){
            // Get information for each customer
            int customerId = rs.getInt("Customer_ID");
            String name = rs.getString("Customer_Name");
            String phone = rs.getString("Phone");
            String address = rs.getString("Address");
            String postalCode = rs.getString("Postal_Code");
            String fld = rs.getString("Division");
            String country = rs.getString("Country");
            // Add customer to list
            Customer customer = new Customer(customerId, name, phone, address, postalCode, fld, country);
            allCustomers.add(customer);
        }
        return allCustomers;
    }

    /** This method gets all customer ids.
     * It executes a query that returns all customers.
     * Then it adds the id of each customer to a list.
     * @throws SQLException Database error. Issue executing/retrieving SQL operation.
     * @return A list of all customer ids.
     */
    public static ObservableList<Integer> getAllCustomerIds() throws SQLException {
        ObservableList<Integer> customerIds = FXCollections.observableArrayList();

        // Select all customers
        String sql = "SELECT * FROM client_schedule.customers";
        PreparedStatement ps = JDBC.connection.prepareStatement(sql);
        ResultSet rs = ps.executeQuery();
        // Add each customer's id to a list
        while (rs.next()) {
            customerIds.add(rs.getInt("Customer_ID"));
        }
        return customerIds;
    }

    /** This method gets the number of customers in a country.
     * It executes a query that returns the count of customers with a specified country id.
     * @throws SQLException Database error. Issue executing/retrieving SQL operation.
     * @return The number of customers in the country.
     */
    public static int getCustomersByCountry(int countryId) throws SQLException {
        // Initialize numCustomers
        int numCustomers = 0;

        // Count number of customers with a specified country id
        String sql = "SELECT Count(Customer_ID) FROM client_schedule.countries INNER JOIN client_schedule.first_level_divisions ON client_schedule.countries.Country_ID=client_schedule.first_level_divisions.Country_ID INNER JOIN client_schedule.customers ON client_schedule.first_level_divisions.Division_ID=client_schedule.customers.Division_ID WHERE client_schedule.countries.Country_ID=?";
        PreparedStatement ps = JDBC.connection.prepareStatement(sql);
        ps.setInt(1, countryId);
        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            numCustomers = rs.getInt("Count(Customer_ID)");
        }
        return numCustomers;
    }

    /** This method adds a customer to the database.
     * It executes an insert operation with information from the arguments, as well as the current time and active user.
     * @param customerName The customer's name.
     * @param address The customer's address.
     * @param postal The customer's postal code.
     * @param phone The customer's phone.
     * @param fld The customer's first level division.
     * @throws SQLException Database error. Issue executing/retrieving SQL operation.
     * @return An integer representing the number of rows affected. A zero indicates failure of the operation.
     */
    public static int addCustomer(String customerName, String address, String postal, String phone, String fld) throws SQLException {
        // Get active user
        String username = DBUsers.activeUser.getUsername();
        // Get fld id from fld
        int fldId = DBFLDs.getFldId(fld);

        // Convert current zoneddatetime to local date and time
        ZonedDateTime createZdt = ZonedDateTime.now();
        ZonedDateTime createZdtUtc = Time.localToUtc(createZdt);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String createDateTime = createZdtUtc.format(formatter);

        // Insert customer
        String sql = "INSERT INTO client_schedule.customers(Customer_Name, Address, Postal_Code, Phone, Create_Date, Created_By, Last_Update, Last_Updated_By, Division_ID) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        PreparedStatement ps = JDBC.connection.prepareStatement(sql);
        ps.setString(1, customerName);
        ps.setString(2, address);
        ps.setString(3, postal);
        ps.setString(4, phone);
        ps.setString(5, createDateTime);
        ps.setString(6, username);
        ps.setString(7, createDateTime);
        ps.setString(8, username);
        ps.setInt(9, fldId);
        int rowsAffected = ps.executeUpdate();
        return rowsAffected;
    }

    /** This method deletes a customer from the database.
     * It executes a delete operation using a specified customer id.
     * It also calls the DBAppointments.deleteAppointmentsOfCustomer() method to delete the customer's appointments.
     * @param customerId The id of the customer to be deleted.
     * @throws SQLException Database error. Issue executing/retrieving SQL operation.
     * @return An integer representing the number of rows affected. A zero indicates failure of the operation.
     */
    public static int deleteCustomer(int customerId) throws SQLException {
        // Before a customer can be deleted from the database, all of the customer's
        // appointments must be deleted first, due to foreign key constraints

        // Delete customer's appointments
        int apptsDeleted = DBAppointments.deleteAppointmentsOfCustomer(customerId);

        // Delete customer
        String sql = "DELETE FROM client_schedule.customers WHERE Customer_ID = ?";
        PreparedStatement ps = JDBC.connection.prepareStatement(sql);
        ps.setInt(1, customerId);
        int rowsAffected = ps.executeUpdate();
        return rowsAffected;
    }

    /** This method updates a customer in the database.
     * It executes an update operation with information from the arguments, as well as the current time and active user.
     * @param customerId The customer's id.
     * @param customerName The customer's name.
     * @param address The customer's address.
     * @param postal The customer's postal code.
     * @param phone The customer's phone.
     * @param fld The customer's first level division.
     * @throws SQLException Database error. Issue executing/retrieving SQL operation.
     * @return An integer representing the number of rows affected. A zero indicates failure of the operation.
     */
    public static int updateCustomer(int customerId, String customerName, String address, String postal, String phone, String fld) throws SQLException {
        // Get active user
        String username = DBUsers.activeUser.getUsername();
        // Get fld id from fld
        int fldId = DBFLDs.getFldId(fld);

        // Convert current zoneddatetime to local date and time
        ZonedDateTime updateZdt = ZonedDateTime.now();
        ZonedDateTime updateZdtUtc = Time.localToUtc(updateZdt);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String updateDateTime = updateZdtUtc.format(formatter);

        // Update customer
        String sql = "UPDATE client_schedule.customers SET Customer_Name=?, Address=?, Postal_Code=?, Phone=?, Last_Update=?, Last_Updated_By=?, Division_ID=? WHERE Customer_ID=?";
        PreparedStatement ps = JDBC.connection.prepareStatement(sql);
        ps.setString(1, customerName);
        ps.setString(2, address);
        ps.setString(3, postal);
        ps.setString(4, phone);
        ps.setString(5, updateDateTime);
        ps.setString(6, username);
        ps.setInt(7, fldId);
        ps.setInt(8, customerId);
        int rowsAffected = ps.executeUpdate();
        return rowsAffected;
    }
}