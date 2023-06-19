package DAO;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/** This class is a collection of methods that include contact-related SQL queries/updates sent to the database server.
 * @author James Wilson
 */
public class DBContacts {

    /** This method gets the contact id of a specified contact.
     *  It executes a query that returns all contacts with the specified contact name.
     * @param contact The name of the contact.
     * @throws SQLException Database error. Issue executing/retrieving SQL operation.
     * @return The contact id of the contact.
     */
    public static int getContactId(String contact) throws SQLException {
        // Initialize contactId
        int contactId = 0;

        // Select contact by their name
        String sql = "SELECT * FROM client_schedule.contacts WHERE Contact_Name=?";
        PreparedStatement ps = JDBC.connection.prepareStatement(sql);
        ps.setString(1, contact);
        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            // Get contact's id
            contactId = rs.getInt("Contact_ID");
        }
        if(contactId <= 0) {
            System.out.println("ERROR - Failed to find contactId");
        }
        return contactId;
    }

    /** This method gets all contact names.
     *  It executes a query that returns all contacts.
     *  Then it adds the name of each contact to a list.
     * @throws SQLException Database error. Issue executing/retrieving SQL operation.
     * @return A list of all contact names.
     */
    public static ObservableList<String> getAllContactNames() throws SQLException {
        ObservableList<String> contactNames = FXCollections.observableArrayList();

        // Select all contacts
        String sql = "SELECT * FROM client_schedule.contacts";
        PreparedStatement ps = JDBC.connection.prepareStatement(sql);
        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            // Add each contact's name to a list
            contactNames.add(rs.getString("Contact_Name"));
        }
        return contactNames;
    }
}
