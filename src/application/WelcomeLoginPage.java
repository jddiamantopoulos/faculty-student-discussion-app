package application;

import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.application.Platform;
import javafx.collections.FXCollections;

import java.util.ArrayList;
import java.util.Arrays;

import databasePart1.DatabaseHelper;

/**
 * The WelcomeLoginPage class displays a welcome screen for authenticated users.
 * It allows users to navigate to their respective pages based on their role or quit the application.
 */
public class WelcomeLoginPage {
	
	private final DatabaseHelper databaseHelper;

    public WelcomeLoginPage(DatabaseHelper databaseHelper) {
        this.databaseHelper = databaseHelper;
    }
    public void show(Stage primaryStage, User user) {
    	
    	VBox layout = new VBox(5);
	    layout.setStyle("-fx-alignment: center; -fx-padding: 20;");
	    
	    Label welcomeLabel = new Label("Welcome!!");
	    welcomeLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
	    
	    // Set up the role select list in case it needs to be displayed
	    Label roleSelect = new Label("Select your role: ");
	    roleSelect.setStyle("-fx-font-size: 12px");
	    ArrayList<String> rawList;
	    // Set the list of roles
	    if (user.getRole().equals("admin")) {
	    	rawList = new ArrayList<String>(Arrays.asList("user", user.getRole()));
	    }
	    else {
	    	rawList = new ArrayList<String>(); // initialize to empty even if unused
	    }
	    // Display the list of roles in a ChoiceBox
	    ChoiceBox<String> choice = new ChoiceBox<String>(FXCollections.observableArrayList(rawList)); 
	    
	    // Add a horizontal bar between unrelated elements
	    Separator horizontalSpace = new Separator();
	    Separator horizontalSpace2 = new Separator();
	    
	    // Button to navigate to the user's respective page based on their role
	    Button continueButton = new Button("Continue to your Page");
	    continueButton.setOnAction(a -> {
	    	String role;
	    	if (!(user.getRole().equals("user"))) {
	    		role = choice.getValue().toString();
	    		user.setRole(role);
	    	// System.out.println(role);
	    	}
	    	else {
	    		role = "user";
	    	}
	    	
	    	if(role.equals("admin")) {
	    		new AdminHomePage(databaseHelper, user).show(primaryStage);
	    	}
	    	else if(role.equals("user")) {
	    		new UserHomePage(databaseHelper, user).show(primaryStage);
	    	}
	    });
	    
	    // Button to log out of the application
	    Button logoutButton = new Button("Logout");
	    logoutButton.setOnAction(a -> {
	    	new SetupLoginSelectionPage(databaseHelper).show(primaryStage);
	    });
	    
	    // Button to quit the application
	    Button quitButton = new Button("Quit");
	    quitButton.setOnAction(a -> {
	    	databaseHelper.closeConnection();
	    	Platform.exit(); // Exit the JavaFX application
	    });
	    
	    layout.getChildren().add(welcomeLabel);
	    
	    // Display role selection if applicable				// Note: This is a separate if statement
	    if ("admin".equals(user.getRole())) {				// because it will be used with other roles.
	    	choice.setValue(rawList.get(1));
	    	layout.getChildren().add(roleSelect);
            layout.getChildren().add(choice);
            layout.getChildren().add(horizontalSpace);
	    }
	    
	    // "Invite" button for admin to generate invitation codes
	    if ("admin".equals(user.getRole())) {
            Button inviteButton = new Button("Invite New User");
            inviteButton.setOnAction(a -> {
                new InvitationPage(databaseHelper, user).show(primaryStage);
            });
            layout.getChildren().add(inviteButton);
        }

	    layout.getChildren().addAll(continueButton, horizontalSpace2, logoutButton, quitButton);
	    Scene welcomeScene = new Scene(layout, 800, 400);

	    // Set the scene to primary stage
	    primaryStage.setScene(welcomeScene);
	    primaryStage.setTitle("Welcome Page");
    }
}