package questions.ui;

import java.util.List;

import accounts.util.User;
import databasePart1.DatabaseHelper;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import questions.util.Answer;
import questions.util.Question;
import questions.util.Review;
import questions.util.ReviewFeedback;

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
		
		sortReviews(reviews);
		reviews = reviews.reversed();

		
		for (Review review : reviews) {
			VBox reviewBox = new VBox(5);
			reviewBox.setStyle("-fx-background-color: rgb(225, 225, 225);");
			Label reviewText = new Label(review.getReviewerName() + ": " + review.getReviewText());
			Label likeLabel = new Label("Likes: " + review.getLikeNum());
			Button likeButton = new Button ("Like");
			Button feedbackButton = new Button("Feedback");
			Button bookmarkButton = new Button("Bookmark");
			

			//Existing review
			VBox fbBox = new VBox(5);
			int reviewId = review.getReviewId();
			List<String> feedbackList = db.getReviewFeedback(reviewId);
			for (String feedback : feedbackList) {
			    Label feedbackLabel = new Label(feedback);
			    fbBox.getChildren().add(feedbackLabel);
			}
			
			
			//like review
			likeButton.setOnAction(e -> {
				review.addLike();
				if (db.incrementReviewLike(review.getReviewId())) {
					likeLabel.setText("Likes: " + review.getLikeNum());
				}
			});
			
			TextArea feedbackArea = new TextArea();
			feedbackArea.setPromptText("Leave feedback for this review");
			Button submitFeedback = new Button("Submit Feedback");
			
			if (!currUser.getUserName().equals(review.getReviewerName())) {
				fbBox.getChildren().addAll(feedbackArea, submitFeedback);
			}
			
			submitFeedback.setOnAction(e -> {
				String feedbackText = feedbackArea.getText().trim();
				if (!feedbackText.isEmpty()) {
					db.addReviewFeedback(review.getReviewId(), currUser.getUserName(), feedbackText);
					review.addFeedback(new ReviewFeedback(currUser.getUserName(), feedbackText));
					feedbackArea.clear();
					fbBox.getChildren().addFirst(new Label(currUser.getUserName() + ": " + feedbackText));
				}
			});
			
			bookmarkButton.setOnAction(e -> {
			    if (!review.getReviewerName().equals(currUser.getUserName())) {
			        boolean success = db.addReviewerBookmark(currUser.getUserName(), review.getReviewerName());
			        if (success) {
			            bookmarkButton.setText("Bookmarked!");
			            bookmarkButton.setDisable(true);
			        } else {
			            System.err.println("Could not bookmark reviewer.");
			        }
			    }
			});

			
			
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
			
			HBox bottomBox = new HBox(5);
			if (!currUser.getUserName().equals(review.getReviewerName())) {
				bottomBox.getChildren().add(likeButton);
			}
			bottomBox.getChildren().addAll(likeLabel);
			reviewBox.getChildren().add(reviewText);
			
			// Conditional showing/hiding based on status (admins and author)
			if (currUser.getRole().equals("admin") || currUser.getUserName().equals(review.getReviewerName())) {
				bottomBox.getChildren().addAll(editButton, deleteButton);
			}
			
			reviewBox.getChildren().addAll(bottomBox, feedbackButton);
			layout.getChildren().add(reviewBox);
			
			/* https://docs.oracle.com/javase/8/javafx/api/javafx/beans/property/BooleanProperty.html */
			/* needs to be wrapped because of the lambda */
			BooleanProperty fbShown = new SimpleBooleanProperty(false);
			feedbackButton.setOnAction(e -> {
				if (fbShown.get()) {
					reviewBox.getChildren().remove(fbBox);
					fbShown.set(false);
				}
				else {
					reviewBox.getChildren().add(fbBox);
					fbShown.set(true);
				}
			});
			
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
		
		if (!currUser.getRole().equals("user")) {
			layout.getChildren().addAll(newReviewField, submitReviewButton);
		}
		
		Scene reviewScene = new Scene(layout, 600, 500);
		tertiaryStage.setScene(reviewScene);
		tertiaryStage.setTitle("Manage Reviews for " + (isAnswer ? "Answer" : "Question"));
		tertiaryStage.show();
	}	
	
	/**
	 * Sorts the list of reviews in O(n^2). Sacrificing performance for simplicity.
	 * @param reviews A list of reviews.
	 */
	private void sortReviews(List<Review> reviews) {
		for (int i = 0; i < reviews.size(); i++) {
			for (int j = 0; j < reviews.size(); j++) {
				if (compareReviews(reviews.get(i), reviews.get(j)) < 0) {
					Review temp = reviews.get(i);
					reviews.set(i, reviews.get(j));
					reviews.set(j, temp);
				}
			}
		}
	}
	
	/**
	 * Compare two reviews
	 * @param r1 The original review
	 * @param r2 The review to compare against
	 * @return Greater than zero if r1.getScore > r2.getScore
	 */
	private int compareReviews(Review r1, Review r2) {
		int r1s = r1.getReviewerScore(currUser);
		int r2s = r2.getReviewerScore(currUser);
		return r1s - r2s;
	}
}
