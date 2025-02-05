package application;

import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import java.sql.SQLException;
import databasePart1.*;

/**
 * The UserLoginPage class provides a login interface with FSM-based login handling.
 * 
 * Improvements:
 * 1. Added a system (`LoginState`) to track different steps of the login process.  
 *2. Made sure users can't try logging in multiple times at the same time.  
 *3. Moved login checks into one place (`authenticateUser`) to update states and handle errors better.  
 *4. Added a check to make sure username and password fields aren't empty before logging in.  
 *5. Included a logout function to reset the login process.  
 *6. Cleaned up the code by putting UI setup in a separate method for better readability.
 */
public class UserLoginPage {
    private final DatabaseHelper databaseHelper;
    private LoginState loginState;
    private TextField userNameField;
    private PasswordField passwordField;
    private Label errorLabel;

    // Define FSM states
    private enum LoginState {
        ENTER_CREDENTIALS,  // Initial state where user enters login details
        AUTHENTICATING,  // State indicating authentication is in progress
        LOGIN_SUCCESS,  // Successful login transition
        LOGIN_FAILURE  // Failed login attempt
    }

    public UserLoginPage(DatabaseHelper databaseHelper) {
        this.databaseHelper = databaseHelper;
        this.loginState = LoginState.ENTER_CREDENTIALS; // Initial state
    }

    public void show(Stage primaryStage) {
        initializeUI(primaryStage);
    }

    /**
     * Initializes UI components to improve code readability and modularity.
     */
    private void initializeUI(Stage primaryStage) {
        userNameField = new TextField();
        userNameField.setPromptText("Enter userName");
        userNameField.setMaxWidth(250);

        passwordField = new PasswordField();
        passwordField.setPromptText("Enter Password");
        passwordField.setMaxWidth(250);
        
        errorLabel = new Label();
        errorLabel.setStyle("-fx-text-fill: red; -fx-font-size: 12px;");

        Button loginButton = new Button("Login");
        Button logoutButton = new Button("Logout");
        
        loginButton.setOnAction(a -> handleLogin(primaryStage));
        logoutButton.setOnAction(a -> handleLogout());

        VBox layout = new VBox(10);
        layout.setStyle("-fx-padding: 20; -fx-alignment: center;");
        layout.getChildren().addAll(userNameField, passwordField, loginButton, logoutButton, errorLabel);

        primaryStage.setScene(new Scene(layout, 800, 400));
        primaryStage.setTitle("User Login");
        primaryStage.show();
    }

    /**
     * Handles login attempts, including input validation and authentication state management.
     */
    private void handleLogin(Stage primaryStage) {
        if (loginState == LoginState.ENTER_CREDENTIALS) {
            String userName = userNameField.getText().trim();
            String password = passwordField.getText().trim();

            if (userName.isEmpty() || password.isEmpty()) {
                errorLabel.setText("Username and Password cannot be empty.");
                return;
            }

            loginState = LoginState.AUTHENTICATING;
            authenticateUser(userName, password, primaryStage);
        }
    }

    /**
     * Handles user authentication while managing FSM state transitions.
     * Ensures proper error handling and state updates based on the authentication outcome.
     */
    private void authenticateUser(String userName, String password, Stage primaryStage) {
        try {
            User user = new User(userName, password, "");
            WelcomeLoginPage welcomeLoginPage = new WelcomeLoginPage(databaseHelper);
            
            String role = databaseHelper.getUserRole(userName);
            if (role != null) {
                user.setRole(role);
                if (databaseHelper.login(user)) {
                    loginState = LoginState.LOGIN_SUCCESS;
                    welcomeLoginPage.show(primaryStage, user);
                } else {
                    loginState = LoginState.LOGIN_FAILURE;
                    errorLabel.setText("Error logging in");
                }
            } else {
                loginState = LoginState.LOGIN_FAILURE;
                errorLabel.setText("User account doesn't exist");
            }
        } catch (SQLException e) {
            System.err.println("Database error: " + e.getMessage());
            e.printStackTrace();
            loginState = LoginState.LOGIN_FAILURE;
        }
    }

    /**
     * Handles logout functionality by resetting the FSM state.
     */
    private void handleLogout() {
        loginState = LoginState.ENTER_CREDENTIALS;
        userNameField.clear();
        passwordField.clear();
        errorLabel.setText("");
    }
}

