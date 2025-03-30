package accounts.ui;

import java.sql.SQLException;

import accounts.util.User;
import databasePart1.DatabaseHelper;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import messaging.ui.MessageUserListPage;
import questions.ui.QuestionListPage;

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
    	VBox layout = new VBox(5);
	    layout.setStyle("-fx-alignment: center; -fx-padding: 20;");
	    
	    // Label to display Hello user
	    Label userLabel = new Label("Hello, " + 
	        (currentUser.getName().isEmpty() ? "User" : currentUser.getName()) + "!");
	    userLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
	    
	    // Button to allow user to go back
	    Button back = new Button("Back");
	    
	    back.setOnAction(a -> {
	    	new WelcomeLoginPage(databaseHelper).show(primaryStage, currentUser);
	    });
	    
	    // Button to allow logout
	    Button logout = new Button("Logout");
	    
	    logout.setOnAction(a -> {
	    	//databaseHelper.closeConnection();  // Removing, causes logout bug
	    	new SetupLoginSelectionPage(databaseHelper).show(primaryStage);
        });
	    
	    // Button to allow account update
	    Button updateAccountBtn = new Button("Update Account Info");
	    updateAccountBtn.setOnAction(a -> {
	        new AccountUpdatePage(databaseHelper, currentUser).show(primaryStage);
	    });
	    
	    // Display a button to go to the question list page
	    Button questionPageButton = new Button("Go to question list");
	    questionPageButton.setOnAction(a -> {
	    	new QuestionListPage(databaseHelper, currentUser).show(primaryStage);
	    });
	    
	    // Button to go to the DMs page
	    Button messagePageButton = new Button("Direct Messages");
	    messagePageButton.setOnAction(a -> {
	    	new MessageUserListPage(databaseHelper, currentUser).show(primaryStage);
	    });
	    
	    // Separator between unrelated elements
	    Separator separator = new Separator();
	    
	    // Logic to show the reviewer request button or a link to the reviewer request page
	    Button reviewerRequestButton = new Button();
	    if (currentUser.getRole().equals("user")) {
	    	reviewerRequestButton.setText("Request Reviewer Role");
	    }
	    else if (currentUser.getRole().equals("instructor") || currentUser.getRole().equals("admin")) {
	    	reviewerRequestButton.setText("View Reviewer Requests");
	    }
	   	
	    reviewerRequestButton.setOnAction(a -> {
	    	if (currentUser.getRole().equals("user")) {
	    		try {
	    			databaseHelper.requestReviewerRole(currentUser.getUserName());
	    			userLabel.setText("Request sent!");
	    		} catch (SQLException e) {
	    			e.printStackTrace();
	    			System.err.println("This error could also have been thrown if the user has already requested the role.");
	    			userLabel.setText("The request could not be sent. You may have already requested the role.");
	    		}
		    }
		    else if (currentUser.getRole().equals("instructor") || currentUser.getRole().equals("admin")) {
		    	new ReviewerRequestsUsersPage(databaseHelper, currentUser).show(primaryStage);
		    }
	    });
	    
	    // Add elements to layout
	    layout.getChildren().addAll(userLabel, questionPageButton, messagePageButton, reviewerRequestButton, separator, updateAccountBtn, back, logout);
	    
	    Scene userScene = new Scene(layout, 800, 400);

	    // Set the scene to primary stage
	    primaryStage.setScene(userScene);
	    primaryStage.setTitle("User Page");
    	
    }
}