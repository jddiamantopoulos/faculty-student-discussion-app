package application;

import databasePart1.DatabaseHelper;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * This page displays a simple welcome message for the user.
 */

public class UserHomePage {
	private DatabaseHelper databaseHelper;
	private User currentUser;
	
	// databaseHelper is not global, needs to be passed 
	// through here to allow it to be passed back to the first page.
	public UserHomePage(DatabaseHelper databaseHelper, User currentUser) {
		this.databaseHelper = databaseHelper;
		this.currentUser = currentUser;
	}

    public void show(Stage primaryStage) {
    	VBox layout = new VBox();
	    layout.setStyle("-fx-alignment: center; -fx-padding: 20;");
	    
	    // Label to display Hello user
	    Label userLabel = new Label("Hello, " + 
	        (currentUser.getName().isEmpty() ? "User" : currentUser.getName()) + "!");
	    userLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
	    
	    // Button to allow logout
	    Button logout = new Button("Logout");
	    
	    logout.setOnAction(a -> {
	    	databaseHelper.closeConnection();  // Close the connection before logout
	    	new SetupLoginSelectionPage(databaseHelper).show(primaryStage);
        });
	    
	    // Button to allow account update
	    Button updateAccountBtn = new Button("Update Account Info");
	    updateAccountBtn.setOnAction(a -> {
	        new AccountUpdatePage(databaseHelper, currentUser).show(primaryStage);
	    });
	    
	    // Add it to your layout before the logout button
	    layout.getChildren().addAll(userLabel, updateAccountBtn, logout);
	    
	    Scene userScene = new Scene(layout, 800, 400);

	    // Set the scene to primary stage
	    primaryStage.setScene(userScene);
	    primaryStage.setTitle("User Page");
    	
    }
}