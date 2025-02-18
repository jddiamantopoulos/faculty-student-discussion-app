package accounts.ui;

import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.sql.SQLException;

import accounts.util.*;
import databasePart1.DatabaseHelper;

/**
 * SetupAccountPage class handles the account setup process for new users.
 * Users provide their userName, password, and a valid invitation code to register.
 */
public class SetupAccountPage {
	
    private final DatabaseHelper databaseHelper;
    // DatabaseHelper to handle database operations.
    public SetupAccountPage(DatabaseHelper databaseHelper) {
        this.databaseHelper = databaseHelper;
    }

    /**
     * Displays the Setup Account page in the provided stage.
     * @param primaryStage The primary stage where the scene will be displayed.
     */
    public void show(Stage primaryStage) {
    	// Input fields for userName, password, and invitation code
    	TextField realNameField = new TextField();
    	realNameField.setPromptText("Enter your name");
        realNameField.setMaxWidth(250);
    	
        TextField userNameField = new TextField();
        userNameField.setPromptText("Enter Username");
        userNameField.setMaxWidth(250);

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Enter Password");
        passwordField.setMaxWidth(250);
        
        TextField emailField = new TextField();
        emailField.setPromptText("Enter Email");
        emailField.setMaxWidth(250);
        
        TextField inviteCodeField = new TextField();
        inviteCodeField.setPromptText("Enter Invitation Code");
        inviteCodeField.setMaxWidth(250);
        
        
        // Label to display error messages for invalid input or registration issues
        Label errorLabel = new Label();
        errorLabel.setStyle("-fx-text-fill: red; -fx-font-size: 12px;");
        

        Button setupButton = new Button("Setup");
        
        setupButton.setOnAction(a -> {
        	// Retrieve user input
        	String realName = realNameField.getText();
            String userName = userNameField.getText();
            String password = passwordField.getText();
            String email = emailField.getText();
            String code = inviteCodeField.getText();
            String newUserRole;
            String userNameError = UserNameRecognizer.checkForValidUserName(userName);
            String passwordError = PasswordEvaluator.evaluatePassword(password);
            String emailError = EmailValidator.validateEmail(email);
            

            
            try {
            	// Check that the name field is not empty
            	// Validation is performed here as requirements are simple
            	if(!realName.equals("") &&
            			realName.length() < 255) {
	            	// Check if the username and password are valid
	            	if(userNameError.equals("") &&
	            			passwordError.equals("")) {
	            		
	            		// Check if the email is valid
	            		if (emailError.equals("")) {
	            			
			            		// Check if the user already exists
			            		if(!databaseHelper.doesUserExist(userName)) {
			        		
			            			// Validate the invitation code
			            			if(databaseHelper.validateInvitationCode(code)) {
			            				
			            				// Get the invite code's associated role from the database
			            				newUserRole = databaseHelper.getAssociatedRole(code);
			        			
			            				// Create a new user and register them in the database
			            				User user = new User(userName, password, newUserRole);
			            				user.setEmail(email);
			            				user.setName(realName);
			            				databaseHelper.register(user);
			            				
			            				// Navigate to the Welcome Login Page
			            				new WelcomeLoginPage(databaseHelper).show(primaryStage, user);
			            				
			            			}
		            			else {
		            				errorLabel.setText("Please enter a valid invitation code");
		            			}
		            		}
		            		else {
		            			errorLabel.setText("This userName is taken!!.. Please use another to setup an account");
		            		}	
	            		} 
	            		else {
	            			errorLabel.setText(emailError);
	            		}
	            	}
	            	else {
	            		errorLabel.setText(userNameError + "\n" + passwordError);
	            	}
            	}
            	else {
            		errorLabel.setText("Please enter a valid name");
            	}
            	
            } catch (SQLException e) {
                System.err.println("Database error: " + e.getMessage());
                e.printStackTrace();
            }
        });

        VBox layout = new VBox(10);
        layout.setStyle("-fx-padding: 20; -fx-alignment: center;");
        layout.getChildren().addAll(realNameField, userNameField, passwordField, emailField, inviteCodeField, setupButton, errorLabel);

        primaryStage.setScene(new Scene(layout, 800, 400));
        primaryStage.setTitle("Account Setup");
        primaryStage.show();
    }
}
