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
 * 1. Introduced an FSM (Finite State Machine) with `LoginState` enum to track different login stages.
 * 2. Ensured state-based transitions to prevent repeating login attempts.
 * 3. Centralized authentication in `authenticateUser` to manage state updates and error handling.
 */
public class UserLoginPage {
    private final DatabaseHelper databaseHelper;
    private LoginState loginState;
    
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
        TextField userNameField = new TextField();
        userNameField.setPromptText("Enter userName");
        userNameField.setMaxWidth(250);

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Enter Password");
        passwordField.setMaxWidth(250);
        
        Label errorLabel = new Label();
        errorLabel.setStyle("-fx-text-fill: red; -fx-font-size: 12px;");

        Button loginButton = new Button("Login");
        
        // Making sure the login process only proceeds when in ENTER_CREDENTIALS state
        loginButton.setOnAction(a -> {
            if (loginState == LoginState.ENTER_CREDENTIALS) {
                loginState = LoginState.AUTHENTICATING;
                authenticateUser(userNameField.getText(), passwordField.getText(), errorLabel, primaryStage);
            }
        });

        VBox layout = new VBox(10);
        layout.setStyle("-fx-padding: 20; -fx-alignment: center;");
        layout.getChildren().addAll(userNameField, passwordField, loginButton, errorLabel);

        primaryStage.setScene(new Scene(layout, 800, 400));
        primaryStage.setTitle("User Login");
        primaryStage.show();
    }

    /**
     * Handles user authentication while managing FSM state transitions.
     * Ensures proper error handling and state updates based on the authentication outcome.
     */
    private void authenticateUser(String userName, String password, Label errorLabel, Stage primaryStage) {
        try {
            User user = new User(userName, password, "");
            WelcomeLoginPage welcomeLoginPage = new WelcomeLoginPage(databaseHelper);
            
            // Retrieve user role from the database
            String role = databaseHelper.getUserRole(userName);
            if (role != null) {
                user.setRole(role);
                if (databaseHelper.login(user)) {
                    loginState = LoginState.LOGIN_SUCCESS; // Successful login transition
                    welcomeLoginPage.show(primaryStage, user);
                } else {
                    loginState = LoginState.LOGIN_FAILURE; // Failed login transition
                    errorLabel.setText("Error logging in");
                }
            } else {
                loginState = LoginState.LOGIN_FAILURE; // User does not exist in database
                errorLabel.setText("User account doesn't exist");
            }
        } catch (SQLException e) {
            System.err.println("Database error: " + e.getMessage());
            e.printStackTrace();
            loginState = LoginState.LOGIN_FAILURE; // Handle database errors
        }
    }
}
