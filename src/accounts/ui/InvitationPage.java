package accounts.ui;

import databasePart1.DatabaseHelper;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ChoiceBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.collections.FXCollections;
import java.util.ArrayList;
import java.util.Arrays;

import accounts.util.User;

/**
 * Allows admins to generate invite codes for new users.
 */
public class InvitationPage {

	private final DatabaseHelper databaseHelper;
	private final User user;
	
	/**
	 * Constructs a new InvitationPage.
	 * @param databaseHelper The application's DatabaseHelper instance.
	 * @param user The currently logged in user.
	 */
	public InvitationPage(DatabaseHelper databaseHelper, User user) {
		this.databaseHelper = databaseHelper;
		this.user = user;
	}

	/**
     * Displays the Invite Page in the provided primary stage.
     * 
     * @param primaryStage   The primary stage where the scene will be displayed.
     */
    public void show(Stage primaryStage) {
    	VBox layout = new VBox(5);
	    layout.setStyle("-fx-alignment: center; -fx-padding: 20;");
	    
	    // Label to display the title of the page
	    Label userLabel = new Label("Invite ");
	    userLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
	    
	    // Button to generate the invitation code
	    Button showCodeButton = new Button("Generate Invitation Code");
	    
	    // Create a list of available roles and setup ChoiceBox
	    ArrayList<String> roleList = new ArrayList<>(Arrays.asList("Select Role", "user", "reviewer", "instructor", "admin"));
	    ChoiceBox<String> roleChoice = new ChoiceBox<>(FXCollections.observableArrayList(roleList));
	    roleChoice.setValue(roleList.get(0));
	    
	    // Label to display the generated invitation code
	    Label inviteCodeLabel = new Label("");
	    inviteCodeLabel.setStyle("-fx-font-size: 14px; -fx-font-style: italic;");
	    
	    Label errorLabel = new Label("");
	    errorLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: red;");
	    
	    // Button to go back one screen the application
	    Button backButton = new Button("Go Back");
	    backButton.setOnAction(a -> {
	    	new WelcomeLoginPage(databaseHelper).show(primaryStage, user);
	    });
	    
	    // Verify that a role has been selected before generating an invite code
	    showCodeButton.setOnAction(a -> {
	    	if (roleChoice.getValue() == null || 
	    			roleChoice.getValue().equals("Select Role")) {
	    		errorLabel.setText("ERROR: Please select a role to assign to the new user.");
	    	} else {
	    		// Generate the invitation code using the databaseHelper and set it to the label
	    		String invitationCode = databaseHelper.generateInvitationCode(roleChoice.getValue());
	    		inviteCodeLabel.setText(invitationCode);
	    	}
	    });
	    

	    layout.getChildren().addAll(userLabel, roleChoice, showCodeButton, inviteCodeLabel, backButton, errorLabel);
	    Scene inviteScene = new Scene(layout, 800, 400);

	    // Set the scene to primary stage
	    primaryStage.setScene(inviteScene);
	    primaryStage.setTitle("Invite Page");
    	
    }
}