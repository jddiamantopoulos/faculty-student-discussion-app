package application;

import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import java.sql.SQLException;
import databasePart1.*;

/**
 * The AdminSetupPage class handles the setup process for creating an administrator account.
 * 
 * Improvements:
 * 1. Introduced an FSM (Finite State Machine) with `AdminState` enum to track different setup stages.
 * 2. Prevents empty username/password entries.
 * 3. Displays clear messages for failures (e.g., duplicate username).
 * 4. Ensures correct flow through the setup process.
 */
public class AdminSetupPage {
    private final DatabaseHelper databaseHelper;
    private AdminState adminState;
    private TextField userNameField;
    private PasswordField passwordField;
    private Label errorLabel;

    // Define FSM states
    private enum AdminState {
        ENTER_CREDENTIALS,  // Initial state where admin enters credentials
        CREATING_ADMIN,  // State indicating admin account creation is in progress
        SETUP_SUCCESS,  // Successful setup transition
        SETUP_FAILURE  // Failed setup attempt
    }

    public AdminSetupPage(DatabaseHelper databaseHelper) {
        this.databaseHelper = databaseHelper;
        this.adminState = AdminState.ENTER_CREDENTIALS; // Initial state
    }

    public void show(Stage primaryStage) {
        initializeUI(primaryStage);
    }

    /**
     * Initializes UI components for admin setup.
     */
    private void initializeUI(Stage primaryStage) {
        userNameField = new TextField();
        userNameField.setPromptText("Enter Admin userName");
        userNameField.setMaxWidth(250);

        passwordField = new PasswordField();
        passwordField.setPromptText("Enter Password");
        passwordField.setMaxWidth(250);
        
        errorLabel = new Label();
        errorLabel.setStyle("-fx-text-fill: red; -fx-font-size: 12px;");

        Button setupButton = new Button("Setup");
        setupButton.setOnAction(a -> handleSetup(primaryStage));

        VBox layout = new VBox(10);
        layout.setStyle("-fx-padding: 20; -fx-alignment: center;");
        layout.getChildren().addAll(userNameField, passwordField, setupButton, errorLabel);

        primaryStage.setScene(new Scene(layout, 800, 400));
        primaryStage.setTitle("Administrator Setup");
        primaryStage.show();
    }

    /**
     * Handles admin account setup attempts, including input validation and state management.
     */
    private void handleSetup(Stage primaryStage) {
        if (adminState == AdminState.ENTER_CREDENTIALS) {
            String userName = userNameField.getText().trim();
            String password = passwordField.getText().trim();

            if (userName.isEmpty() || password.isEmpty()) {
                errorLabel.setText("Admin username and password cannot be empty.");
                return;
            }

            adminState = AdminState.CREATING_ADMIN;
            setupAdmin(userName, password, primaryStage);
        }
    }

    /**
     * Creates the admin account while managing FSM state transitions.
     */
    private void setupAdmin(String userName, String password, Stage primaryStage) {
        try {
            if (databaseHelper.doesUserExist(userName)) {
                adminState = AdminState.SETUP_FAILURE;
                errorLabel.setText("Admin username already exists.");
                return;
            }

            User admin = new User(userName, password, "admin");
            databaseHelper.register(admin);
            adminState = AdminState.SETUP_SUCCESS;
            
            new WelcomeLoginPage(databaseHelper).show(primaryStage, admin);
        } catch (SQLException e) {
            System.err.println("Database error: " + e.getMessage());
            e.printStackTrace();
            adminState = AdminState.SETUP_FAILURE;
            errorLabel.setText("Error setting up administrator account.");
        }
    }
}
