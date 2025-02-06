package application;

<<<<<<< HEAD
import databasePart1.*;
=======
import databasePart1.DatabaseHelper;
>>>>>>> TP1-HS
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ChoiceBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
<<<<<<< HEAD
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.collections.*;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * InvitePage class represents the page where an admin can generate an invitation code.
 * The invitation code is displayed upon clicking a button.
 */
=======
import javafx.collections.FXCollections;
import java.util.ArrayList;
import java.util.Arrays;
>>>>>>> TP1-HS

public class InvitationPage {

	private final DatabaseHelper databaseHelper;

	public InvitationPage(DatabaseHelper databaseHelper) {
		this.databaseHelper = databaseHelper;
	}

	/**
     * Displays the Invite Page in the provided primary stage.
     * 
     * @param primaryStage   The primary stage where the scene will be displayed.
     */
<<<<<<< HEAD
    public void show(DatabaseHelper databaseHelper,Stage primaryStage) {
=======
    public void show(Stage primaryStage) {
>>>>>>> TP1-HS
    	VBox layout = new VBox(5);
	    layout.setStyle("-fx-alignment: center; -fx-padding: 20;");
	    
	    // Label to display the title of the page
	    Label userLabel = new Label("Invite ");
	    userLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
	    
	    // Button to generate the invitation code
	    Button showCodeButton = new Button("Generate Invitation Code");
	    
<<<<<<< HEAD
	    // Create a list of available roles
	    ArrayList<String> rawList = new ArrayList<String>(Arrays.asList("Select Role", "user", "admin"));
	    ObservableList<String> list = FXCollections.observableArrayList(rawList);
	    ChoiceBox choice = new ChoiceBox(FXCollections.observableArrayList(rawList)); 
	    choice.setValue(rawList.get(0));
	    
	    // Label to display the generated invitation code
	    Label inviteCodeLabel = new Label(""); ;
        inviteCodeLabel.setStyle("-fx-font-size: 14px; -fx-font-style: italic;");
        
        Label errorLabel = new Label("");
        errorLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: red;");
        
        // Verify that a role has been selected before generating an invite code
        showCodeButton.setOnAction(a -> {
        	if (choice.getValue() == null ||
        			choice.getValue().equals("Select Role")) {
        		errorLabel.setText("ERROR: Please select a role to assign to the new user.");
        	}
        	else {
	        	// Generate the invitation code using the databaseHelper and set it to the label
	            String invitationCode = databaseHelper.generateInvitationCode(choice.getValue().toString());
	            inviteCodeLabel.setText(invitationCode);
        	}
        });
	    

        layout.getChildren().addAll(userLabel, choice, showCodeButton, inviteCodeLabel, errorLabel);
=======
	    // Create a list of available roles and setup ChoiceBox
	    ArrayList<String> roleList = new ArrayList<>(Arrays.asList("Select Role", "user", "admin"));
	    ChoiceBox<String> roleChoice = new ChoiceBox<>(FXCollections.observableArrayList(roleList));
	    roleChoice.setValue(roleList.get(0));
	    
	    // Label to display the generated invitation code
	    Label inviteCodeLabel = new Label("");
	    inviteCodeLabel.setStyle("-fx-font-size: 14px; -fx-font-style: italic;");
	    
	    Label errorLabel = new Label("");
	    errorLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: red;");
	    
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
	    

	    layout.getChildren().addAll(userLabel, roleChoice, showCodeButton, inviteCodeLabel, errorLabel);
>>>>>>> TP1-HS
	    Scene inviteScene = new Scene(layout, 800, 400);

	    // Set the scene to primary stage
	    primaryStage.setScene(inviteScene);
	    primaryStage.setTitle("Invite Page");
    	
    }
}