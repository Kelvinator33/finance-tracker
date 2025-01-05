package dbms;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;


//utility class for managing database connections
public class db_connection {
    // A private static variable to hold the database connection object.
    private static Connection conn;


    // Database URL, username, and password constants for the connection.
    public static final String URL = "jdbc:mysql://localhost:3306/expense_tracker";
    public static final String USER = "root";
    public static final String PASSWORD = "Germany@1962";


    //Establishes and returns a connection to the database
    public static Connection getConnection() {
        try {
            return DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to connect to the database!");
        }
    }

    //Closes the existing database connection if it is open.
    public static void closeConnection() {
        try {
            if (conn != null && !conn.isClosed()) {
                conn.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
