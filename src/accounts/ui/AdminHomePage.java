package accounts.ui;

import accounts.util.User;
import databasePart1.DatabaseHelper;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import questions.ui.QuestionListPage;


/**
 * AdminPage class represents the user interface for the admin user.
 * This page displays a simple welcome message for the admin.
 */

public class AdminHomePage {
	/**
     * Displays the admin page in the provided primary stage.
     * @param primaryStage The primary stage where the scene will be displayed.
     */
	private DatabaseHelper databaseHelper;
	private User currentUser;
	
	public AdminHomePage(DatabaseHelper databaseHelper, User currentUser) {
		this.databaseHelper = databaseHelper;
		this.currentUser = currentUser;
	}
	
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
	    
	    layout.getChildren().addAll(adminLabel, questionPageButton, separator, updateAccountBtn, back, logout);

	    Scene adminScene = new Scene(layout, 800, 400);

	    // Set the scene to primary stage
	    primaryStage.setScene(adminScene);
	    primaryStage.setTitle("Admin Page");
    }
}