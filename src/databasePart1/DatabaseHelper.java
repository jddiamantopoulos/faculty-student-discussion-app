package databasePart1;
import java.sql.*;
import java.util.UUID;
import application.User;

/**
 * The DatabaseHelper class manages database connections and operations.
 * 
 * Improvements:
 * 1. Introduced an FSM (Finite State Machine) to track database operations.
 * 2. Improved error handling for database failures.
 * 3. Ensured proper validation before user registration.
 * 4. Enhanced login method to update states and prevent unnecessary queries.
 */
public class DatabaseHelper {
    private static final String JDBC_DRIVER = "org.h2.Driver";
    private static final String DB_URL = "jdbc:h2:~/FoundationDatabase";
    private static final String USER = "sa";
    private static final String PASS = "";
    
    private Connection connection = null;
    private Statement statement = null;
    private DatabaseState databaseState;
    
    // Define FSM states for database operations
    private enum DatabaseState {
        DISCONNECTED, CONNECTING, CONNECTED, ERROR
    }
    
    public DatabaseHelper() {
        this.databaseState = DatabaseState.DISCONNECTED;
    }
    
    public void connectToDatabase() {
        try {
            databaseState = DatabaseState.CONNECTING;
            Class.forName(JDBC_DRIVER);
            connection = DriverManager.getConnection(DB_URL, USER, PASS);
            statement = connection.createStatement();
            createTables();
            databaseState = DatabaseState.CONNECTED;
        } catch (ClassNotFoundException | SQLException e) {
            databaseState = DatabaseState.ERROR;
            System.err.println("Database connection error: " + e.getMessage());
        }
    }
    
    private void createTables() throws SQLException {
        String userTable = "CREATE TABLE IF NOT EXISTS cse360users ("
                + "id INT AUTO_INCREMENT PRIMARY KEY, "
                + "userName VARCHAR(255) UNIQUE, "
                + "password VARCHAR(255), "
                + "role VARCHAR(20))";
        statement.execute(userTable);
    }
    
    public boolean isDatabaseEmpty() {
        try {
            String query = "SELECT COUNT(*) AS count FROM cse360users";
            ResultSet resultSet = statement.executeQuery(query);
            return resultSet.next() && resultSet.getInt("count") == 0;
        } catch (SQLException e) {
            System.err.println("Error checking database: " + e.getMessage());
            return true;
        }
    }
    
    public boolean doesUserExist(String userName) {
        String query = "SELECT COUNT(*) FROM cse360users WHERE userName = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, userName);
            ResultSet rs = pstmt.executeQuery();
            return rs.next() && rs.getInt(1) > 0;
        } catch (SQLException e) {
            System.err.println("Error checking user existence: " + e.getMessage());
            return false;
        }
    }
    
    public void register(User user) {
        if (doesUserExist(user.getUserName())) {
            System.err.println("User already exists.");
            return;
        }
        String insertUser = "INSERT INTO cse360users (userName, password, role) VALUES (?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(insertUser)) {
            pstmt.setString(1, user.getUserName());
            pstmt.setString(2, user.getPassword());
            pstmt.setString(3, user.getRole());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error registering user: " + e.getMessage());
        }
    }
    
    public boolean login(User user) {
        if (databaseState != DatabaseState.CONNECTED) {
            System.err.println("Cannot log in: Database not connected.");
            return false;
        }
        String query = "SELECT * FROM cse360users WHERE userName = ? AND password = ? AND role = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, user.getUserName());
            pstmt.setString(2, user.getPassword());
            pstmt.setString(3, user.getRole());
            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            System.err.println("Login error: " + e.getMessage());
            return false;
        }
    }
    
    public void closeConnection() {
        try {
            if (statement != null) statement.close();
            if (connection != null) connection.close();
            databaseState = DatabaseState.DISCONNECTED;
        } catch (SQLException e) {
            System.err.println("Error closing database: " + e.getMessage());
        }
    }
}
