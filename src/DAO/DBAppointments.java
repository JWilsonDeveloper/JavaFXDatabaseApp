package DAO;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import model.Appointment;
import utility.Time;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.*;
import java.time.format.DateTimeFormatter;

/** This class is a collection of methods that include appointment-related SQL queries/updates sent to the database server.
 * @author James Wilson
 */
public class DBAppointments {

    /** This method gets all appointments.
     * It executes a query that returns all appointments along with their contact names.
     * Then it adds each appointment to a list.
     * @throws SQLException Database error. Issue executing/retrieving SQL operation.
     * @return A list of all appointments.
     */
    public static ObservableList<Appointment> getAllAppointments() throws SQLException {
        ObservableList<Appointment> allAppointments = FXCollections.observableArrayList();

        // Select all appointments with their contact names
        String sql = "SELECT client_schedule.appointments.*, client_schedule.contacts.Contact_Name FROM client_schedule.appointments INNER JOIN client_schedule.contacts ON client_schedule.appointments.Contact_ID=client_schedule.contacts.Contact_ID;";
        PreparedStatement ps = JDBC.connection.prepareStatement(sql);
        ResultSet rs = ps.executeQuery();
        while(rs.next()){
            // Get information for each appointment
            int appointmentID = rs.getInt("Appointment_ID");
            String title = rs.getString("Title");
            String description = rs.getString("Description");
            String location = rs.getString("Location");
            String contact = rs.getString("Contact_Name");
            String type = rs.getString("Type");
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            ZonedDateTime utcStart = ZonedDateTime.of(LocalDateTime.parse(rs.getString("Start"), formatter), ZoneId.of("UTC"));
            ZonedDateTime utcEnd = ZonedDateTime.of(LocalDateTime.parse(rs.getString("End"), formatter), ZoneId.of("UTC"));
            int customerID = rs.getInt("Customer_ID");
            int userID = rs.getInt(("User_ID"));

            // Add appointment to a list
            Appointment appointment = new Appointment(appointmentID, title, description, location, contact, type, utcStart, utcEnd, customerID, userID);
            allAppointments.add(appointment);
        }
        return allAppointments;
    }

    /** This method adds an appointment to the database.
     * It executes an insert operation with information from the arguments, as well as the current time and active user.
     * @param title The appointment's title.
     * @param description The appointment's description.
     * @param location The appointment's location.
     * @param contact The appointment's contact.
     * @param type The appointment's type.
     * @param localStart The appointment's start dateTime in local time.
     * @param localEnd The appointment's end dateTime in local time.
     * @param customerId The appointment's customer Id.
     * @param userId The appointment's user Id.
     * @throws SQLException Database error. Issue executing/retrieving SQL operation.
     * @return An integer representing the number of rows affected. A zero indicates failure of the operation.
     */
    public static int addAppointment(String title, String description, String location, String contact, String type, ZonedDateTime localStart, ZonedDateTime localEnd, int customerId, int userId) throws SQLException {
        // Get active user
        String username = DBUsers.activeUser.getUsername();
        // Get contact id from contact name
        int contactId = DBContacts.getContactId(contact);

        // Convert start/end zoneddatetimes in local time to start/end date and time in utc
        ZonedDateTime utcStart = Time.localToUtc(localStart);
        String startDate = utcStart.toLocalDate().toString();
        String startTime = utcStart.toLocalTime().toString();
        ZonedDateTime utcEnd = Time.localToUtc(localEnd);
        String endDate = utcEnd.toLocalDate().toString();
        String endTime = utcEnd.toLocalTime().toString();

        // Get current datetime formatted in utc
        ZonedDateTime createZdt = ZonedDateTime.now();
        ZonedDateTime createZdtUtc = Time.localToUtc(createZdt);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String createDateTime = createZdtUtc.format(formatter);

        // Insert appointment
        String sql = "INSERT INTO client_schedule.appointments(Title, Description, Location, Type, Start, End, Create_Date, Created_By, Last_Update, Last_Updated_By, Customer_ID, User_ID, Contact_ID) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        PreparedStatement ps = JDBC.connection.prepareStatement(sql);
        ps.setString(1, title);
        ps.setString(2, description);
        ps.setString(3, location);
        ps.setString(4, type);
        ps.setString(5, startDate + " " + startTime);
        ps.setString(6, endDate + " " + endTime);
        ps.setString(7, createDateTime);
        ps.setString(8, username);
        ps.setString(9, createDateTime);
        ps.setString(10, username);
        ps.setInt(11, customerId);
        ps.setInt(12, userId);
        ps.setInt(13, contactId);

        int rowsAffected = ps.executeUpdate();
        return rowsAffected;
    }

    /** This method updates an appointment in the database.
     * It executes an update operation with information from the arguments, as well as the current time and active user.
     * @param appointmentId The appointment's Id.
     * @param title The appointment's title.
     * @param description The appointment's description.
     * @param location The appointment's location.
     * @param contact The appointment's contact.
     * @param type The appointment's type.
     * @param localStart The appointment's start dateTime in local time.
     * @param localEnd The appointment's end dateTime in local time.
     * @param customerId The appointment's customer Id.
     * @param userId The appointment's user Id.
     * @throws SQLException Database error. Issue executing/retrieving SQL operation.
     * @return An integer representing the number of rows affected. A zero indicates failure of the operation.
     */
    public static int updateAppointment(int appointmentId, String title, String description, String location, String contact, String type, ZonedDateTime localStart, ZonedDateTime localEnd, int customerId, int userId) throws SQLException {
        // Get active user
        String username = DBUsers.activeUser.getUsername();
        // Get contact id from contact name
        int contactId = DBContacts.getContactId(contact);

        // Convert start/end zoneddatetimes in local time to start/end date and time in utc
        ZonedDateTime utcStart = Time.localToUtc(localStart);
        String startDate = utcStart.toLocalDate().toString();
        String startTime = utcStart.toLocalTime().toString();
        ZonedDateTime utcEnd = Time.localToUtc(localEnd);
        String endDate = utcEnd.toLocalDate().toString();
        String endTime = utcEnd.toLocalTime().toString();

        // Get current datetime formatted in utc
        ZonedDateTime updateZdt = ZonedDateTime.now();
        ZonedDateTime updateZdtUtc = Time.localToUtc(updateZdt);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String updateDateTime = updateZdtUtc.format(formatter);

        // Update appointment with a specified appointment id
        String sql = "UPDATE client_schedule.appointments SET Title=?, Description=?, Location=?, Type=?, Start=?, End=?, Last_Update=?, Last_Updated_By=?, Customer_ID=?, User_ID=?, Contact_ID=? WHERE Appointment_ID=?";
        PreparedStatement ps = JDBC.connection.prepareStatement(sql);
        ps.setString(1, title);
        ps.setString(2, description);
        ps.setString(3, location);
        ps.setString(4, type);
        ps.setString(5, startDate + " " + startTime);
        ps.setString(6, endDate + " " + endTime);
        ps.setString(7, updateDateTime);
        ps.setString(8, username);
        ps.setInt(9, customerId);
        ps.setInt(10, userId);
        ps.setInt(11, contactId);
        ps.setInt(12, appointmentId);
        int rowsAffected = ps.executeUpdate();
        return rowsAffected;
    }

    /** This method deletes an appointment from the database.
     * It executes a delete operation using a specified appointment id.
     * @param appointmentId The id of the appointment to be deleted.
     * @throws SQLException Database error. Issue executing/retrieving SQL operation.
     * @return An integer representing the number of rows affected. A zero indicates failure of the operation.
     */
    public static int deleteAppointment(int appointmentId) throws SQLException {
        // Delete appointment with a specified appointment id
        String sql = "DELETE FROM client_schedule.appointments WHERE Appointment_ID = ?";
        PreparedStatement ps = JDBC.connection.prepareStatement(sql);
        ps.setInt(1, appointmentId);
        int rowsAffected = ps.executeUpdate();
        return rowsAffected;
    }

    /** This method deletes all appointments tied to a specified customer from the database.
     * It executes a delete operation using a specified customer id.
     * @param customerId The id of the customer whose appointments are to be deleted.
     * @throws SQLException Database error. Issue executing/retrieving SQL operation.
     * @return An integer representing the number of rows affected. A zero indicates failure of the operation.
     */
    public static int deleteAppointmentsOfCustomer(int customerId) throws SQLException {
        // Delete all appointments with a specified customer id
        String sql = "DELETE FROM client_schedule.appointments WHERE Customer_ID = ?";
        PreparedStatement ps = JDBC.connection.prepareStatement(sql);
        ps.setInt(1, customerId);
        int rowsAffected = ps.executeUpdate();
        return rowsAffected;
    }

    /** This method gets all the types of existing appointments.
     * It executes a query that returns all appointments.
     * Then the method adds the type of each appointment to a list, not allowing for repeated values.
     * @throws SQLException Database error. Issue executing/retrieving SQL operation.
     * @return A list of all appointment types.
     */
    public static ObservableList<String> getAllTypes() throws SQLException {
        ObservableList<String> allTypes = FXCollections.observableArrayList();

        // Select all appointments
        String sql = "SELECT * FROM client_schedule.appointments";
        PreparedStatement ps = JDBC.connection.prepareStatement(sql);
        ResultSet rs = ps.executeQuery();
        while(rs.next()){
            // Add each appointment's type to a list
            String type = rs.getString("Type");
            boolean tFound = false;
            for(String t: allTypes) {
                if(t.equals(type)) {
                    tFound = true;
                }
            }
            if(tFound == false) {
                allTypes.add(type);
            }
        }
        return allTypes;
    }

    /** This method finds the number of appointments for a specified type and month of this year.
     * It executes a query that returns the count of appointments with start dates between the first of the current month and the first of following month.
     * @param month The month in which the appointments take place.
     * @param type The type of the appointments.
     * @throws SQLException Database error. Issue executing/retrieving SQL operation.
     * @return The number of appointments for a specified type and month of this year.
     */
    public static int getNumAppts(Month month, String type) throws SQLException{
        // Initialize numAppts
        int numAppts = 0;
        // Get current year
        String thisYear = String.valueOf(Year.now());

        // Count the number of appointments of a specified type that take place between the beginning and the end of a specified month
        String sql = "SELECT Count(Appointment_ID) FROM client_schedule.appointments WHERE Start >=? AND End <=? AND Type=?";
        PreparedStatement ps = JDBC.connection.prepareStatement(sql);
        ps.setString(1, thisYear + "-" + month.getValue() + "-01 00:00:00");
        ps.setString(2, thisYear + "-" + (month.getValue() + 1) + "-01 00:00:00");
        ps.setString(3, type);
        ResultSet rs = ps.executeQuery();
        while(rs.next()){
            numAppts = rs.getInt("Count(Appointment_ID)");
        }
        return numAppts;
    }

    /** This method gets all of the appointments tied to a specified contact.
     * It executes a query that returns the appointments with contact ids that are tied to the specified contact.
     * @param contactName The name of the contact.
     * @throws SQLException Database error. Issue executing/retrieving SQL operation.
     * @return A list of the contact's appointments.
     */
    public static ObservableList<Appointment> getApptsByContact(String contactName) throws SQLException {
        ObservableList<Appointment> contactAppts = FXCollections.observableArrayList();

        // Select all appointments with a specified contact name
        String sql = "Select * FROM client_schedule.appointments INNER JOIN client_schedule.contacts ON client_schedule.appointments.Contact_ID=client_schedule.contacts.Contact_ID WHERE client_schedule.contacts.Contact_Name=?";
        PreparedStatement ps = JDBC.connection.prepareStatement(sql);
        ps.setString(1, contactName);
        ResultSet rs = ps.executeQuery();
        while(rs.next()){
            // Get information for each appointment
            int appointmentID = rs.getInt("Appointment_ID");
            String title = rs.getString("Title");
            String description = rs.getString("Description");
            String location = rs.getString("Location");
            String type = rs.getString("Type");
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            ZonedDateTime utcStart = ZonedDateTime.of(LocalDateTime.parse(rs.getString("Start"), formatter), ZoneId.of("UTC"));
            ZonedDateTime utcEnd = ZonedDateTime.of(LocalDateTime.parse(rs.getString("End"), formatter), ZoneId.of("UTC"));
            int customerID = rs.getInt("Customer_ID");
            int userID = rs.getInt(("User_ID"));

            // Add appointment to a list
            Appointment appointment = new Appointment(appointmentID, title, description, location, contactName, type, utcStart, utcEnd, customerID, userID);
            contactAppts.add(appointment);
        }
        return contactAppts;
    }

    /** This method gets all ongoing or upcoming appointments.
     * It executes a query that returns the appointments with start times between 60 minutes ago and 15 minutes from now.
     * @throws SQLException Database error. Issue executing/retrieving SQL operation.
     * @return A list of ongoing and upcoming appointments.
     */
    public static ObservableList<Appointment> getImminentAppts() throws SQLException {
        ObservableList<Appointment> imminentAppts = FXCollections.observableArrayList();

        // Get dateTimes from 60 minutes ago and 15 minutes from now
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        ZonedDateTime nowUtcZdt = Time.localToUtc(ZonedDateTime.now());
        String lowEnd = nowUtcZdt.minusMinutes(60).format(formatter);
        String highEnd = nowUtcZdt.plusMinutes(15).format(formatter);

        // Get appointments with start times between 60 minutes ago and 15 minutes from now
        String sql = "SELECT client_schedule.appointments.*, client_schedule.contacts.Contact_Name FROM client_schedule.appointments INNER JOIN client_schedule.contacts ON client_schedule.appointments.Contact_ID=client_schedule.contacts.Contact_ID WHERE Start>=? AND Start<=?";
        PreparedStatement ps = JDBC.connection.prepareStatement(sql);
        ps.setString(1, lowEnd);
        ps.setString(2, highEnd);
        ResultSet rs = ps.executeQuery();
        while(rs.next()){
            // Get information for each appointment
            int appointmentID = rs.getInt("Appointment_ID");
            String title = rs.getString("Title");
            String description = rs.getString("Description");
            String location = rs.getString("Location");
            String contact = rs.getString("Contact_Name");
            String type = rs.getString("Type");
            ZonedDateTime utcStart = ZonedDateTime.of(LocalDateTime.parse(rs.getString("Start"), formatter), ZoneId.of("UTC"));
            ZonedDateTime utcEnd = ZonedDateTime.of(LocalDateTime.parse(rs.getString("End"), formatter), ZoneId.of("UTC"));
            int customerID = rs.getInt("Customer_ID");
            int userID = rs.getInt(("User_ID"));

            // Add appointment to a list
            Appointment appointment = new Appointment(appointmentID, title, description, location, contact, type, utcStart, utcEnd, customerID, userID);
            imminentAppts.add(appointment);
        }
        return imminentAppts;
    }
}
