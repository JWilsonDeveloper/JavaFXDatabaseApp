package model;

/** This class defines the User object.
 * @author James Wilson
 */
public class User {
    private int userId;
    private String username;
    private String password;

    public User(int userId, String username, String password) {
        this.userId = userId;
        this.username = username;
        this.password = password;
    }

    /**
     * @return The user's id.
     */
    public int getUserId() {
        return userId;
    }

    /**
     * @param userId The integer with which the user's id will be set.
     */
    public void setUserId(int userId) {
        this.userId = userId;
    }

    /**
     * @return The user's username
     */
    public String getUsername() {
        return username;
    }

    /**
     * @param username The String with which the user's username will be set.
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * @return The user's password
     */
    public String getPassword() {
        return password;
    }

    /**
     * @param password The String with which the user's password will be set.
     */
    public void setPassword(String password) {
        this.password = password;
    }
}
