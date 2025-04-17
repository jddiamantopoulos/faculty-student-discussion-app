package accounts.ui;

import accounts.util.Reviewer;
import databasePart1.DatabaseHelper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import java.sql.SQLException;
import java.util.List;

/**
 * This page displays the list of reviewers with low scores.
 * The staff members can then see who has low scores.
 * They can send the low scored reviewers private suggestions .
 * Works with the database to retrieve reviews and to send the suggestions. 
 */
public class LowReviewerPage {
	private DatabaseHelper db;
	private ListView<String> reviewerListView;
	private TextArea suggestionArea;
	private ObservableList<String> reviewerItems;
	private List<Reviewer> lowReviewers;
	
	/**
	 * The window that shows the low-scored reviewers and the suggestion box to send them feedback.
	 * @param primaryStage the window
	 * @param currentStaffUsername the username of the staff
	 */
	public void start(Stage primaryStage, String currentStaffUsername) {
		db = new DatabaseHelper();
		
		reviewerListView = new ListView<>();
		reviewerItems = FXCollections.observableArrayList();
		reviewerListView.setItems(reviewerItems);
		
		suggestionArea = new TextArea();
		suggestionArea.setPromptText("Write suggestion here...");
		suggestionArea.setWrapText(true);
		
		Button sendButton = new Button("Send");
		Button backButton = new Button("Back");
		
		Label reviewerLabel = new Label("Low Scored Reviewers:");
		Label suggestionLabel = new Label ("Private Suggestion:");
		
		VBox leftPane = new VBox(10, reviewerLabel, reviewerListView);
		leftPane.setPrefWidth(250);
		
		VBox rightPane = new VBox(10, suggestionLabel, suggestionArea, sendButton, backButton);
		rightPane.setPadding(new Insets(0, 0, 0, 20));
		
		HBox root = new HBox(20, leftPane, rightPane);
		root.setPadding(new Insets(15));
		
		populateReviewerList();
		
		sendButton.setOnAction(e -> {
			int index = reviewerListView.getSelectionModel().getSelectedIndex();
			String messageText = suggestionArea.getText().trim();
			
			if (index >= 0 && !messageText.isEmpty()) {
				Reviewer selected = lowReviewers.get(index);
				try {
					db.sendPrivateMessage(currentStaffUsername, selected.getUsername(), messageText);
					showAlert(Alert.AlertType.INFORMATION, "Sent", "Suggestion Sent to ", selected.getUsername());
					suggestionArea.clear();
				} catch (SQLException ex) {
					showAlert(Alert.AlertType.ERROR, "Error", "Faield to send : " + ex.getMessage());
				}
			} else {
				showAlert(Alert.AlertType.WARNING, "Missing Inputs", "Select a reviewer and enter suggestion.");
			}
	});
	
	backButton.setOnAction(e -> primaryStage.close());
	
	primaryStage.setScene(new Scene(root, 650, 400));
	primaryStage.setTitle("Low-Scored Reviewers");
	primaryStage.show();
	
}
	
	/**
	 * It fills the reviewer list with reviewers who have scores in the threshold
	 * retrieves the data from the database
	 */
	private void populateReviewerList() {
		try {
			lowReviewers = db.getLowScoredReviewers(50);
			reviewerItems.clear();
			for(Reviewer r : lowReviewers) {
				reviewerItems.add(r.getUsername() + " (Score: " +  r.getScore() + ")");
			}
		} catch (SQLException e) {
			showAlert(Alert.AlertType.ERROR, "Database Error", "Unable to retrieve reviewers: " + e.getMessage());
		}
	}
	
	/**
	 * An alert is shown with the message type, title, and message
	 * @param type the type of alert
	 * @param title the title of the alert
	 * @param message the text of the alert
	 */
	private void showAlert(Alert.AlertType type, String title, String message) {
		Alert alert = new Alert(type);
		alert.setTitle(title);
		alert.setHeaderText(null);
		alert.setContentText(message);
		alert.showAndWait();
	}
}
