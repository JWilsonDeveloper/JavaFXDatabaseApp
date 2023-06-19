package DAO;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import model.User;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/** This class is a collection of methods that include user-related SQL queries/updates sent to the database server.
 * @author James Wilson
 */
public class DBUsers {
    // This variable is necessary for recording the created_by and updated_by fields for the appointments and customers.
    public static User activeUser;

    /** This method gets the user with a specified username.
     * It executes a query that returns the user with the matching username.
     * @param username The name of the user.
     * @throws SQLException Database error. Issue executing/retrieving SQL operation.
     * @return A reference to the user.
     */
    public static User getUser(String username) throws SQLException {
        // Initialize userId and password
        int userId = 0;
        String password = "";

        // Select user with a specified username
        String sql = "SELECT * FROM users WHERE User_Name =?";
        PreparedStatement ps = JDBC.connection.prepareStatement(sql);
        ps.setString(1, username);
        ResultSet rs = ps.executeQuery();
        while(rs.next()) {
            // Get user's id and password
            userId = rs.getInt("User_ID");
            password = rs.getString("Password");
        }
        // Create User object
        User user = new User(userId, username, password);
        return user;
    }

    /** This method gets all user ids.
     * It executes a query that returns all the users.
     * Then it gets the id for each user and adds it to a list.
     * @throws SQLException Database error. Issue executing/retrieving SQL operation.
     * @return A list of the user ids.
     */
    public static ObservableList<Integer> getAllUserIds() throws SQLException {
        ObservableList<Integer> userIds = FXCollections.observableArrayList();

        // Select all users
        String sql = "SELECT * FROM client_schedule.users";
        PreparedStatement ps = JDBC.connection.prepareStatement(sql);
        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            // Get each user's id
            userIds.add(rs.getInt("User_ID"));
        }
        return userIds;
    }
}
