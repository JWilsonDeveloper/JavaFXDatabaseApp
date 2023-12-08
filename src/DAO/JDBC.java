package DAO;

import com.company.Main;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.*;

/** This class manages the application's connection to the database.
 * @author James Wilson
 */
public abstract class JDBC {

    private static final String protocol = "jdbc";
    private static final String vendor = ":mysql:";
    private static final String location = "//localhost/";
    private static final String databaseName = "client_schedule";
    private static final String jdbcUrl = protocol + vendor + location + databaseName + "?connectionTimeZone = SERVER"; // LOCAL
    private static final String driver = "com.mysql.cj.jdbc.Driver"; // Driver reference
    private static final String userName = "sqlUser"; // Username
    private static String password = "Passw0rd!"; // Password
    public static Connection connection = null;  // Connection Interface

    /** This method drops the database.
     */
    public static void dropDatabase() {
        try {
            Class.forName(driver); // Locate Driver
            connection = DriverManager.getConnection(protocol + vendor + location, userName, password); // Reference Connection object
            Statement statement = connection.createStatement();
            InputStream inputStream = JDBC.class.getClassLoader().getResourceAsStream("sql/drop_db.sql");
            statement.executeUpdate(new String(inputStream.readAllBytes()));
            connection.close();
            System.out.println("Success: drop database " + databaseName);
        }
        catch(Exception e)
        {
            System.out.println("Error dropping database:" + e.getMessage());
        }
    }

    /** This method creates the database if it doesn't exist.
     */
    public static void createDatabase() {
        try {
            Class.forName(driver); // Locate Driver
            connection = DriverManager.getConnection(protocol + vendor + location, userName, password); // Reference Connection object
            Statement statement = connection.createStatement();
            InputStream inputStream = JDBC.class.getClassLoader().getResourceAsStream("sql/client_schedule.sql");
            statement.executeUpdate(new String(inputStream.readAllBytes()));
            connection.close();
            System.out.println("Success: Create database" + databaseName);
        }
        catch(Exception e)
        {
            System.out.println("Error creating database:" + e.getMessage());
        }
    }

    /** This method creates the tables in the database if they do not exist.
     */
    public static void createTables() {
        try {
            Class.forName(driver); // Locate Driver
            connection = DriverManager.getConnection(jdbcUrl, userName, password); // Reference Connection object
            Statement statement = connection.createStatement();
            InputStream inputStream = JDBC.class.getClassLoader().getResourceAsStream("sql/create_countries.sql");
            statement.executeUpdate(new String(inputStream.readAllBytes()));
            inputStream = JDBC.class.getClassLoader().getResourceAsStream("sql/create_flds.sql");
            statement.executeUpdate(new String(inputStream.readAllBytes()));
            inputStream = JDBC.class.getClassLoader().getResourceAsStream("sql/create_customers.sql");
            statement.executeUpdate(new String(inputStream.readAllBytes()));
            inputStream = JDBC.class.getClassLoader().getResourceAsStream("sql/create_contacts.sql");
            statement.executeUpdate(new String(inputStream.readAllBytes()));
            inputStream = JDBC.class.getClassLoader().getResourceAsStream("sql/create_users.sql");
            statement.executeUpdate(new String(inputStream.readAllBytes()));
            inputStream = JDBC.class.getClassLoader().getResourceAsStream("sql/create_appointments.sql");
            statement.executeUpdate(new String(inputStream.readAllBytes()));

            connection.close();
            System.out.println("Success: Create tables");
        }
        catch(Exception e)
        {
            System.out.println("Error creating tables:" + e.getMessage());
        }
    }

    /** This method pre-populates the tables in the database with required data.
     */
    public static void insertData() {
        try {
            Class.forName(driver); // Locate Driver
            connection = DriverManager.getConnection(jdbcUrl, userName, password); // Reference Connection object
            String filePath = "sql/insert_data.sql";
            InputStream inputStream = Main.class.getResourceAsStream("/sql/insert_data.sql");
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            StringBuilder statement = new StringBuilder();

            while ((line = reader.readLine()) != null) {
                // If line is not empty and doesn't start with '--', add it to the statement
                if (!line.trim().isEmpty() && !line.trim().startsWith("--")) {
                    statement.append(line);
                    if (line.endsWith(";")) {
                        try (Statement stmt = connection.createStatement()) {
                            stmt.executeUpdate(statement.toString());
                        }
                        statement.setLength(0);
                    } else {
                        statement.append(" ");
                    }
                }
            }
            connection.close();
            reader.close();
            System.out.println("Success: Data inserted");
        } catch (Exception e) {
            System.out.println("Error inserting data:" + e.getMessage());
        }
    }

    /** This method checks if the database exists.
     * @return A boolean indicating if the database exists.
     */
    public static boolean checkDatabase() {
        boolean exists = false;
        try {
            Class.forName(driver); // Locate Driver
            connection = DriverManager.getConnection(jdbcUrl, userName, password); // Reference Connection object
            DatabaseMetaData metaData = connection.getMetaData();
            ResultSet rs = metaData.getCatalogs();
            while (rs.next()) {
                String dbName = rs.getString("TABLE_CAT");
                if(dbName.equals(databaseName)){
                    exists = true;
                    break;
                }
            }
            rs.close();
            connection.close();
        }
        catch(Exception e)
        {
            System.out.println(e.getMessage());
        }
        return exists;
    }


    /** This method gets a connection to the database.
     * It creates the database and its tables if they do not exist.
     * @return A reference to the connection object.
     */
    public static Connection openConnection() {
        //dropDatabase();
        boolean dbExisted = checkDatabase();
        if(!dbExisted){
            System.out.println("Creating database");
            createDatabase();
            createTables();
            insertData();
        }

        try {
            Class.forName(driver); // Locate Driver
            connection = DriverManager.getConnection(jdbcUrl, userName, password); // Reference Connection object
            System.out.println("Connection successful!");
        }
        catch(Exception e)
        {
            System.out.println("Error:" + e.getMessage());
        }

        return connection;
    }

    /** This method ends the connection to the database.
     */
    public static void closeConnection() {
        try {
            connection.close();
            System.out.println("Connection closed!");
        }
        catch(Exception e)
        {
            System.out.println("Error:" + e.getMessage());
        }
    }

}
