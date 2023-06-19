package DAO;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/** This class is a collection of methods that include first-level-division-related SQL queries/updates sent to the database server.
 * @author James Wilson
 */
public class DBFLDs {

    /** This method gets the id of a specified first level division.
     *  It executes a query that returns the first level divisions with a specified name.
     *  Then it gets the id of the returned division.
     * @param fld The name of the division.
     * @throws SQLException Database error. Issue executing/retrieving SQL operation.
     * @return The id of the first level division.
     */
    public static int getFldId(String fld) throws SQLException {
        // Initialize fldId
        int fldId = 0;

        // Select first level division with a specified division name
        String sql = "SELECT * FROM client_schedule.first_level_divisions WHERE Division=?";
        PreparedStatement ps = JDBC.connection.prepareStatement(sql);
        ps.setString(1, fld);
        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            // Get first level division's id
            fldId = rs.getInt("Division_ID");
        }
        if(fldId <= 0) {
            System.out.println("ERROR - Failed to find fldId");
        }
        return fldId;
    }

    /** This method gets all of the first level divisions.
     *  It executes a query that returns all of the first level divisions.
     *  Then it gets the name of each division and adds it to a list.
     * @throws SQLException Database error. Issue executing/retrieving SQL operation.
     * @return A list of all first level divisions.
     */
    public static ObservableList<String> getAllFlds() throws SQLException {
        ObservableList<String> allFlds = FXCollections.observableArrayList();

        // Select all first level divisions
        String sql = "SELECT * FROM client_schedule.first_level_divisions";
        PreparedStatement ps = JDBC.connection.prepareStatement(sql);
        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            // Add each division's name to a list
            allFlds.add(rs.getString("Division"));
        }
        return allFlds;
    }

    /** This method gets all of the first level divisions in a country.
     *  It executes a query that returns all of the first level divisions tied to the specified country name.
     *  Then it adds each division to a list.
     * @throws SQLException Database error. Issue executing/retrieving SQL operation.
     * @return A list of all first level divisions in the country.
     */
    public static ObservableList<String> getCountryFlds(String country) throws SQLException {
        ObservableList<String> countryFlds = FXCollections.observableArrayList();

        // Select the first level divisions with ids linked to a specified country name
        String sql = "SELECT client_schedule.countries.Country, client_schedule.countries.Country_ID, client_schedule.first_level_divisions.Division FROM client_schedule.countries INNER JOIN client_schedule.first_level_divisions ON client_schedule.first_level_divisions.Country_ID=client_schedule.countries.Country_ID WHERE Country=?";
        PreparedStatement ps = JDBC.connection.prepareStatement(sql);
        ps.setString(1, country);
        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            // Add each first level division name to a list
            countryFlds.add(rs.getString("Division"));
        }
        return countryFlds;
    }
}
