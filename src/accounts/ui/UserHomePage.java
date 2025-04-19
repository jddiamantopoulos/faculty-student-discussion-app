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
import taskmessaging.ui.TaskMessageListPage;

/**
 * This page allows users to navigate to different modules of the application.
 */

public class UserHomePage {
	private DatabaseHelper databaseHelper;
	private User currentUser;
	
	/**
	 * Constructs a new home page for the user.
	 * @param databaseHelper The application's DatabaseHelper instance.
	 * @param currentUser The current user of the application.
	 */
	public UserHomePage(DatabaseHelper databaseHelper, User currentUser) {
		this.databaseHelper = databaseHelper;
		this.currentUser = currentUser;
	}
	
	/**
	 * Shows the page on the provided stage.
	 * @param primaryStage The application's main stage.
	 */
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
	    
	    // Button to update reviewer scores
	    Button reviewerScoresPageButton = new Button("Update Reviewer Scores");
	    reviewerScoresPageButton.setOnAction(a -> {
	    	new UpdateReviewerScoresPage(databaseHelper, currentUser).show(primaryStage);
	    });
	    
	    // Separator between unrelated elements
	    Separator separator = new Separator();
	    
	    // Logic to show the reviewer request button or a link to the reviewer request page
	    Button reviewerRequestButton = new Button();
	    if (currentUser.getRole().equals("user")) {
	    	reviewerRequestButton.setText("Request Reviewer Role");
	    }
	    else if (currentUser.getRole().equals("staff") || currentUser.getRole().equals("instructor") || currentUser.getRole().equals("admin")) {
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
		    else if (currentUser.getRole().equals("staff") || currentUser.getRole().equals("instructor") || currentUser.getRole().equals("admin")) {
		    	new ReviewerRequestsUsersPage(databaseHelper, currentUser).show(primaryStage);
		    }
	    });
	    
	    // Button to request an admin to perform a task
	    Button adminTaskRequestButton = new Button("Request Task For Admin");
	    
	    adminTaskRequestButton.setOnAction(a -> {
	    	new TaskMessageListPage(databaseHelper, currentUser).show(primaryStage);
	    });
	    
	    // Add elements to layout
	    layout.getChildren().addAll(userLabel, questionPageButton, messagePageButton, reviewerRequestButton);
	    
	    if (currentUser.getRole().equals("staff") || currentUser.getRole().equals("instructor")) {
	    	layout.getChildren().add(adminTaskRequestButton);
	    }
	    
	    layout.getChildren().addAll(separator, reviewerScoresPageButton, updateAccountBtn, back, logout);
	    
	    // PATCH: Remove button if user is reviewer
	    if (currentUser.getRole().equals("reviewer")) {
	    	layout.getChildren().remove(reviewerRequestButton);
	    }
	    
	    Scene userScene = new Scene(layout, 800, 400);

	    // Set the scene to primary stage
	    primaryStage.setScene(userScene);
	    primaryStage.setTitle("User Page");
    	
    }
}
