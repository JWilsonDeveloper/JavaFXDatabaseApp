package DAO;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import model.Country;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/** This class is a collection of methods that include country-related SQL queries/updates sent to the database server.
 * @author James Wilson
 */
public class DBCountries {

    /** This method gets all country names.
     *  It executes a query that returns all the countries in the database.
     *  Then it adds the name of the country to a list.
     * @throws SQLException Database error. Issue executing/retrieving SQL operation.
     * @return A list of all the country names.
     */
    public static ObservableList<String> getAllCountries() throws SQLException {
        ObservableList<String> allCountries = FXCollections.observableArrayList();

        // Select all countries
        String sql = "SELECT * FROM client_schedule.countries";
        PreparedStatement ps = JDBC.connection.prepareStatement(sql);
        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            // Add each country's name to a list
            allCountries.add(rs.getString("Country"));
        }
        return allCountries;
    }

    /** This method gets all countries with their IDs and a count of the customers in each country.
     *  It executes a query that returns all the countries in the database.
     *  Then it calls the method DBCustomers.getCustomersByCountry() to get the number of customers in each country.
     *  Then it returns a list of country objects, each with an id, a name, and a number of customers.
     * @throws SQLException Database error. Issue executing/retrieving SQL operation.
     * @return A list of Country references.
     */
    public static ObservableList<Country> getAllCountryObjects() throws SQLException {
        ObservableList<Country> allCountries = FXCollections.observableArrayList();

        // Select all countries
        String sql = "SELECT * FROM client_schedule.countries";
        PreparedStatement ps = JDBC.connection.prepareStatement(sql);
        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            // Get information for each country
            String name = rs.getString("Country");
            int countryId = rs.getInt("Country_ID");
            int numCustomers = DBCustomers.getCustomersByCountry(countryId);

            // Add country to a list
            Country country = new Country(countryId, numCustomers, name);
            allCountries.add(country);
        }
        return allCountries;
    }

}
