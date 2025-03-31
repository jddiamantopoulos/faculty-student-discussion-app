package questions.ui;

import java.util.List;

import accounts.util.User;
import databasePart1.DatabaseHelper;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import questions.util.Answer;
import questions.util.Question;
import questions.util.Review;

/**
 * This page houses the reviews for a question or answer.
 */
public class ReviewPage {
	private DatabaseHelper db;
	private Question parent;
	private Answer ans;
	private User currUser;
	
	/**
	 * Constructs a new review page
	 * @param db The application's DatabaseHelper
	 * @param parent The question being reviewed (null if isAnswer)
	 * @param ans The answer being reviewed (null if !isAnswer)
	 * @param currUser The currently signed in user of the application
	 */
	public ReviewPage(DatabaseHelper db, Question parent, Answer ans, User currUser) {
		this.db = db;
		this.parent = parent;
		this.ans = ans;
		this.currUser = currUser;
	}
	
	/**
	 * Shows the application on the given stage
	 * @param tertiaryStage Usually "new Stage()" is passed in.
	 */
	public void show(Stage tertiaryStage) {
		VBox layout = new VBox(10);
		
		//review section
		Label revlabel = new Label("Reviews for " + (ans != null ? "Answer: " + ans.getText() : "Question: " + parent.getText()));
		layout.getChildren().add(revlabel);
		
		//sees if review for question or answer
		boolean isAnswer = (ans != null);
		int qaText = isAnswer ? ans.getKey() : parent.getKey();
		
		
		//gets all the reviews for question or answer
		List<Review> reviews = db.getReviewsQA(qaText, isAnswer);
		
		for (Review review : reviews) {
			VBox reviewBox = new VBox();
			Label reviewText = new Label(review.getReviewerName() + ": " + review.getReviewText());
			Button deleteButton = new Button("Delete Review");
			Button editButton = new Button("Edit Review");
			
			//Edit review
			editButton.setOnAction(e -> {
				TextArea editReviewField = new TextArea(review.getReviewText());
				Button saveEditButton = new Button("Save Edit");
				
				saveEditButton.setOnAction(ev -> {
					String updatedText = editReviewField.getText();
					if (!updatedText.isEmpty() && db.updateReview(review.getReviewId(), updatedText)) {
						reviewText.setText(currUser.getUserName() + ": " + updatedText);
						layout.getChildren().remove(editReviewField);
						layout.getChildren().remove(saveEditButton);
					}
				});
				
				layout.getChildren().addAll(editReviewField, saveEditButton);
			});
			
			//deletes review
			deleteButton.setOnAction(e -> {
				if (db.deleteReview(review.getReviewId())) {
					layout.getChildren().remove(reviewBox);
				}
			});
			reviewBox.getChildren().add(reviewText);
			
			// Conditional showing/hiding based on status (admins and author)
			if (currUser.getRole().equals("admin") || currUser.getUserName().equals(review.getReviewerName())) {
				reviewBox.getChildren().addAll(editButton, deleteButton);
			}
			
			layout.getChildren().add(reviewBox);
		}
		
		TextArea newReviewField = new TextArea();
		newReviewField.setPromptText("Write a new review...");
		Button submitReviewButton = new Button("Submit Review");
		
		submitReviewButton.setOnAction(e -> {
			String reviewText = newReviewField.getText();
			if(!reviewText.isEmpty()) {
				Review newReview = new Review(0, currUser.getUserName(), qaText, reviewText, isAnswer);
				
				if (db.addReview(currUser.getUserName(), qaText, reviewText, isAnswer)) {
					layout.getChildren().add(new Label(currUser.getUserName() + ": " + reviewText));
					newReviewField.clear();
				}
				else {
					System.err.println("Could not post review.");
				}
			}
		});
		
		layout.getChildren().addAll(newReviewField, submitReviewButton);
		
		Scene reviewScene = new Scene(layout, 600, 500);
		tertiaryStage.setScene(reviewScene);
		tertiaryStage.setTitle("Manage Reviews for " + (isAnswer ? "Answer" : "Question"));
		tertiaryStage.show();
	}	
}
