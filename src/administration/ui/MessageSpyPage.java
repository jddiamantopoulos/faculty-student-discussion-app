package administration.ui;

import accounts.util.User;
import accounts.util.UserNameRecognizer;
import databasePart1.DatabaseHelper;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * Allows staff and admins to retrieve the DMs between two users.
 */
public class MessageSpyPage {
	
	private final DatabaseHelper db;
	private final User user;
	
	/**
	 * Constructs a new instance of the page.
	 * @param db The application's databaseHelper instance.
	 * @param user The current user of the application.
	 */
	public MessageSpyPage(DatabaseHelper db, User user) {
		this.db = db;
		this.user = user;
	}
	
	/**
	 * Shows the page on a new stage.
	 */
	public void show() {
		VBox parent = new VBox(5);
		parent.setStyle("-fx-alignment: center; -fx-padding: 20;");
		Label title = new Label("Message Spy");
		title.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
		Label errorLabel = new Label();
		errorLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: red");
		HBox child = new HBox(5);
		child.setStyle("-fx-alignment: center; -fx-padding: 10;");
		TextField user1 = new TextField();
		TextField user2 = new TextField();
		user1.setPromptText("First User");
		user2.setPromptText("Second User");
		Button spyButton = new Button("Spy");
		
		child.getChildren().addAll(user1, user2);
		parent.getChildren().addAll(title, errorLabel, child, spyButton);
		
		spyButton.setOnAction(e -> {
			if (UserNameRecognizer.checkForValidUserName(user1.getText()).equals("") 
					&& UserNameRecognizer.checkForValidUserName(user2.getText()).equals("")
					&& db.doesUserExist(user1.getText())
					&& db.doesUserExist(user2.getText())) {
				new ConversationSpyPage(user1.getText(), user2.getText(), db).show();
				return;
			}
			else {
				errorLabel.setText("One or both of these usernames are invalid");
			}
		});
		
		Scene newScene = new Scene(parent, 400, 400);
		Stage dialogStage = new Stage();
		dialogStage.setScene(newScene);
		dialogStage.setTitle("MessageSpy");
		dialogStage.show();
	}
}