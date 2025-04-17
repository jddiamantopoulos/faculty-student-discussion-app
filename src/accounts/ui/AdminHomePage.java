package accounts.ui;

import java.sql.SQLException;

import accounts.util.User;
import administration.ui.AdministrationSearchPage;
import administration.ui.ReviewerRequestsUsersPage;
import databasePart1.DatabaseHelper;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import messaging.ui.MessageUserListPage;
import questions.ui.QuestionListPage;


/**
 * AdminPage class represents the user interface for the admin user.
 * This page displays a simple welcome message for the admin.
 */

public class AdminHomePage {
	private DatabaseHelper databaseHelper;
	private User currentUser;
	
	/**
	 * Constructs a new AdminHomePage.
	 * @param databaseHelper The DatabaseHelper instance for this instance of the application.
	 * @param currentUser The user who is logged in.
	 */
	public AdminHomePage(DatabaseHelper databaseHelper, User currentUser) {
		this.databaseHelper = databaseHelper;
		this.currentUser = currentUser;
	}
	
	/**
     * Displays the admin page in the provided primary stage.
     * @param primaryStage The primary stage where the scene will be displayed.
     */
    public void show(Stage primaryStage) {
    	VBox layout = new VBox(5);
    	
	    layout.setStyle("-fx-alignment: center; -fx-padding: 20;");
	    
	    // label to display the welcome message for the admin
	    Label adminLabel = new Label("Hello, " + 
	        (currentUser.getName().isEmpty() ? "Admin" : currentUser.getName()) + "!");
	    
	    // Display a button to go to the question list page
	    Button questionPageButton = new Button("Go to question list");
	    questionPageButton.setOnAction(a -> {
	    	new QuestionListPage(databaseHelper, currentUser).show(primaryStage);
	    });
	    
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
	    
	    adminLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
	    
	    // Button to allow user to go back
	    Button back = new Button("Back");
	    
	    back.setOnAction(a -> {
	    	new WelcomeLoginPage(databaseHelper).show(primaryStage, currentUser);
	    });
	    
	    // Button to allow logout
	    Button logout = new Button("Logout");
	    
	    logout.setOnAction(a -> {
	    	// databaseHelper.closeConnection();  // Removing, fixes logout bug
	    	new SetupLoginSelectionPage(databaseHelper).show(primaryStage);
        });
	    
	    Button updateAccountBtn = new Button("Update Account Info");
	    updateAccountBtn.setOnAction(a -> {
	    	new AccountUpdatePage(databaseHelper, currentUser).show(primaryStage);
	    });
	    
	    // Show a link to the reviewer request page
	    Button reviewerRequestButton = new Button("View Reviewer Requests");
	    
	    reviewerRequestButton.setOnAction(a -> {
		    new ReviewerRequestsUsersPage(databaseHelper, currentUser).show(primaryStage);
	    });
	    
	    Button moderationButton = new Button("Moderation Home");
	    
	    moderationButton.setOnAction(a -> {
	    	new AdministrationSearchPage(databaseHelper, currentUser).show(primaryStage);
	    });
	    
	    layout.getChildren().addAll(adminLabel, questionPageButton, messagePageButton, reviewerRequestButton, moderationButton, separator, reviewerScoresPageButton, updateAccountBtn, back, logout);

	    Scene adminScene = new Scene(layout, 800, 400);

	    // Set the scene to primary stage
	    primaryStage.setScene(adminScene);
	    primaryStage.setTitle("Admin Page");
    }
}