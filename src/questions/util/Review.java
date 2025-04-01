package questions.util;

import java.util.ArrayList;

import accounts.util.Reviewer;
import accounts.util.User;

/**
 * Represents a review of a question or answer
 */
public class Review {
	private int reviewId;
	private String reviewerName;
	private int qaText;
	private String reviewText;
	private boolean isAnswer;
	
	/**
	 * Constructs a new review
	 * @param reviewId The ID of this review in the database
	 * @param reviewerName The name of the author
	 * @param qaText The primary key of the question or answer being reviewed
	 * @param reviewText The text of the review
	 * @param isAnswer Is this a review of an answer?
	 */
	public Review(int reviewId, String reviewerName, int qaText, String reviewText, boolean isAnswer) {
		this.reviewId = reviewId;
		this.reviewerName = reviewerName;
		this.qaText = qaText;
		this.reviewText = reviewText;
		this.isAnswer = isAnswer;
	}
	
	// Getters
	public int getReviewId() { return reviewId; }
	public String getReviewerName() { return reviewerName;}
	public int getqaText() { return qaText;}
	public String getReviewText() { return reviewText; }
	public boolean isAnswer() { return isAnswer; }

	// Setters (for updating a review)
	public void setReviewText(String reviewText) {this.reviewText = reviewText; }
	
	public int getReviewerScore(User user) {
		ArrayList<Reviewer> reviewers = user.getReviewers();
		if (reviewers == null) {
			return 50;
		}
		for (int i = 0; i < reviewers.size(); i++) {
			if (reviewers.get(i).getUsername().equals(reviewerName)) {
				return reviewers.get(i).getScore();
			}
		}
		return 50;
	}

}
