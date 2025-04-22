package administration.ui;

import java.sql.SQLException;

import accounts.util.User;
import accounts.util.UserNameRecognizer;
import administration.util.RefDialogTypes;
import common.util.DialogReturns;
import common.util.DialogTypes;
import databasePart1.DatabaseHelper;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import messaging.util.Message;
import messaging.util.MessageValidator;
import questions.util.Answer;
import questions.util.Question;
import questions.util.Review;

/**
 * A specialized dialog page that allows staff members (or higher) to send a reference to a
 * message, review, answer, or question in a message to another user.
 */
public class RefMessageDialogPage {
	
	private final DatabaseHelper db;
	private final User user;
	private final RefDialogTypes refDialogType;
	private final Question q;
	private final Answer a;
	private final Message m;
	private final Review r;
	
	/**
	 * The constructor used when a question is passed in
	 */
	public RefMessageDialogPage(Question q, DatabaseHelper db, User user) {
		this.q = q;
		a = null;
		m = null;
		r = null;
		refDialogType = RefDialogTypes.question;
		this.user = user;
		this.db = db;
	}
	
	/**
	 * The constructor used when an answer is passed in
	 */
	public RefMessageDialogPage(Answer a, DatabaseHelper db, User user) {
		q = null;
		this.a = a;
		m = null;
		r = null;
		refDialogType = RefDialogTypes.answer;
		this.user = user;
		this.db = db;
	}
	
	/**
	 * The constructor used when a message is passed in
	 */
	public RefMessageDialogPage(Message m, DatabaseHelper db, User user) {
		q = null;
		a = null;
		this.m = m;
		r = null;
		refDialogType = RefDialogTypes.message;
		this.user = user;
		this.db = db;
	}
	
	/**
	 * The constructor used when a review is passed in
	 */
	public RefMessageDialogPage(Review r, DatabaseHelper db, User user) {
		q = null;
		a = null;
		m = null;
		this.r = r;
		refDialogType = RefDialogTypes.review;
		this.user = user;
		this.db = db;
	}
	
	/**
	 * Shows a new RefMessageDialogPage.
	 */
	public void show() {
		/* Set up page */
		Stage newStage = new Stage();
		VBox dialog = new VBox(5);
		dialog.setStyle("-fx-alignment: center; -fx-padding: 2;");
		
		/* Add an error field for handling */
		Label errorLabel = new Label();
		errorLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: red");
		
		/* To which user should this be sent? */
		TextField receivingUser = new TextField();
		receivingUser.setPromptText("Recipient");
		receivingUser.setPrefWidth(200);
		
		/* Allow the user to confirm what is being forwarded */
		Label primaryLabel = new Label();
		switch (refDialogType) {
			case question:
				primaryLabel.setText("Question: " + q.getText());
				break;
			case answer:
				primaryLabel.setText("Answer: " + a.getText());
				break;
			case review:
				primaryLabel.setText("Review: " + r.getReviewText());
				break;
			case message:
				primaryLabel.setText("Message: " + m.getText());
				break;
			default:
				System.err.println("Improper RefMessageDialogPage type.");
				return;
		}
		primaryLabel.setWrapText(true);
		primaryLabel.setStyle("-fx-alignment: center; -fx-padding: 2; -fx-font-size: 16px;");
		
		/* Allow the user to include an extra message */
		TextArea messageArea = new TextArea();
		messageArea.setWrapText(true);
		messageArea.setPromptText("Include any additional notes");
		
		/* Send button + action listener */
		Button sendButton = new Button("Send");
		
		sendButton.setOnAction(e -> {
			/* Get all relevant data */
			String recipient = receivingUser.getText();
			boolean recipientQualified = false;
			String additionalMessage = messageArea.getText();
			/* Validate entries */
			String recipientErr = UserNameRecognizer.checkForValidUserName(recipient);
			String msgErr = MessageValidator.validateMessage(additionalMessage);
			recipientQualified = (db.getUserRole(recipient).equals("admin") || db.getUserRole(recipient).equals("staff"));
			System.err.println(recipientQualified);
			String compoundErr = "";
			if (!recipientErr.equals("")) {
				compoundErr = (recipientErr + ". ");
			}
			if (!msgErr.equals("")) {
				compoundErr = (msgErr + ". ");
			}
			if (refDialogType == RefDialogTypes.message
					&& !recipientQualified) {
				compoundErr = ("This recipient cannot be forwarded messages! (Invalid permissions). ");
			}
			/* Required to restrict this to only staff and admins */
			if (compoundErr.equals("")) {
				/* Construct the message */
				errorLabel.setText(compoundErr);
				String messageText = "";
				switch (refDialogType) {
					case question:
						messageText = "This is an auto-generated message.\n" +
								"Note: " + additionalMessage + "\n" +
								"Sent by: " + q.getAuthor() + "\n" +
								"Question: " + q.getText() + "\n" +
								"Body: " + q.getBody();
						break;
					case answer:
						messageText = "This is an auto-generated message.\n" +
								"Note: " + additionalMessage + "\n" +
								"Sent by: " + a.getAuthor() + "\n" +
								"Answer: " + a.getText();
						break;
					case review:
						messageText = "This is an auto-generated message.\n" +
								"Note: " + additionalMessage + "\n" +
								"Sent by: " + r.getReviewerName() + "\n" +
								"Review: " + r.getReviewText();
						break;
					case message:
						messageText = "This is an auto-generated message.\n" +
								"Note: " + additionalMessage + "\n" +
								"Sent by: " + m.getSender() + "\n" +
								"To: " + m.getRecipient() + "\n" +
								"Message: " + m.getText();
						break;
					default:
						System.err.println("Improper RefMessageDialogPage type.");
						return;
				}
			
				/* Finally, send the message! */
				Message msg = new Message(messageText, user.getUserName(), recipient);
				try {
					db.insertMessage(msg);
				} catch (SQLException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
					return;
				}
				errorLabel.setText("Sent!");
				return;
			}
			else {
				/* Handle error */
				errorLabel.setText(compoundErr);
			}
		});
		
		dialog.getChildren().addAll(primaryLabel, errorLabel, receivingUser, messageArea, sendButton);
		
		Scene helpScene = new Scene(dialog, 500, 400);
        
	    // Set the stage to new stage
	    newStage.setScene(helpScene);
	    newStage.setTitle("Moderation - Forward user activity");
    	newStage.show();
	}
}