package accounts.ui;

import accounts.util.*;
import databasePart1.DatabaseHelper;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

/**
 * AccountUpdatePage allows users to update their account information
 * including name, email, and password.
 */
public class AccountUpdatePage {
    private final DatabaseHelper databaseHelper;
    private final User currentUser;

    public AccountUpdatePage(DatabaseHelper databaseHelper, User currentUser) {
        this.databaseHelper = databaseHelper;
        this.currentUser = currentUser;
    }

    public void show(Stage primaryStage) {
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));
        grid.setStyle("-fx-alignment: center;");

        // Title
        Label titleLabel = new Label("Update Account Information");
        titleLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");
        grid.add(titleLabel, 0, 0, 2, 1);

        // Username display (non-editable)
        Label usernameLabel = new Label("Username:");
        Label usernameValue = new Label(currentUser.getUserName());
        usernameValue.setStyle("-fx-font-style: italic;");
        grid.add(usernameLabel, 0, 1);
        grid.add(usernameValue, 1, 1);

        // Name field
        Label nameLabel = new Label("Full Name:");
        TextField nameField = new TextField(currentUser.getName());
        nameField.setPromptText("Enter your full name");
        grid.add(nameLabel, 0, 2);
        grid.add(nameField, 1, 2);

        // Email field
        Label emailLabel = new Label("Email:");
        TextField emailField = new TextField(currentUser.getEmail());
        emailField.setPromptText("Enter your email");
        grid.add(emailLabel, 0, 3);
        grid.add(emailField, 1, 3);

        // New password field (optional)
        Label passwordLabel = new Label("New Password:");
        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Leave blank to keep current password");
        grid.add(passwordLabel, 0, 4);
        grid.add(passwordField, 1, 4);

        // Error/Success message label
        Label messageLabel = new Label();
        messageLabel.setWrapText(true);
        grid.add(messageLabel, 0, 5, 2, 1);

        // Buttons
        Button updateButton = new Button("Save Changes");
        Button cancelButton = new Button("Cancel");
        
        // Create a button container
        GridPane buttonContainer = new GridPane();
        buttonContainer.setHgap(10);
        buttonContainer.add(updateButton, 0, 0);
        buttonContainer.add(cancelButton, 1, 0);
        grid.add(buttonContainer, 0, 6, 2, 1);

        updateButton.setOnAction(a -> {
            String name = nameField.getText().trim();
            String email = emailField.getText().trim();
            String newPassword = passwordField.getText();
            String emailError = EmailValidator.validateEmail(email);
            String passwordError = PasswordEvaluator.evaluatePassword(newPassword);

            // Input validation
            if (name.isEmpty()) {
                showError(messageLabel, "Name cannot be empty");
                return;
            }

            // Email validation using the FSM class
            if (!emailError.equals("")) {
                showError(messageLabel, emailError);
                return;
            }

            // Password validation if a new password is provided
            if (!newPassword.isEmpty()) {
                if (!passwordError.equals("")) {
                    showError(messageLabel, passwordError);
                    return;
                }
            }

            try {
                // Update user information
                currentUser.setName(name);
                currentUser.setEmail(email);
                if (!newPassword.isEmpty()) {
                    currentUser.setPassword(newPassword);
                }
                
                databaseHelper.updateUserInfo(currentUser);
                
                // Show success message
                messageLabel.setStyle("-fx-text-fill: green;");
                messageLabel.setText("Account information updated successfully!");
            } catch (Exception ex) {
                showError(messageLabel, "Error updating account information");
                ex.printStackTrace();
            }
        });

        cancelButton.setOnAction(a -> {
            if (currentUser.getRole().equals("admin")) {
                new AdminHomePage(databaseHelper, currentUser).show(primaryStage);
            } else {
                new UserHomePage(databaseHelper, currentUser).show(primaryStage);
            }
        });

        Scene scene = new Scene(grid, 800, 400);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Update Account Information");
    }

    private void showError(Label messageLabel, String message) {
        messageLabel.setStyle("-fx-text-fill: red;");
        messageLabel.setText(message);
    }
}