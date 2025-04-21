package questions.util;
import databasePart1.DatabaseHelper;
import java.util.List;
import java.util.ArrayList;

/**
 * Wrappers for database functions
 */
public class ReviewManage {
	private DatabaseHelper db;
	private List<Review> reviews = new ArrayList<>();
	private int feedbackIdNum = 1;
	
	/**
	 * Constructs a new review manager
	 * @param db The application's database helper instance
	 */
	public ReviewManage(DatabaseHelper db) {
		this.db = db;
	}
	
	/**
	 * Adds a review to the database
	 * @param reviewerName The name of the review author
	 * @param questionId The DB id of the question being reviewed
	 * @param reviewText The text of the review
	 * @param isAnswer Is this a review of an answer?
	 * @return True if added successfully
	 */
	public boolean addReview(String reviewerName, int questionId, String reviewText, boolean isAnswer) {
		return db.addReview(reviewerName, questionId, reviewText, isAnswer); 
	}
	
	/**
	 * Updates a review's text
	 * @param reviewId The PKey of the review being updated
	 * @param reviewText The new text to be set
	 * @return True if successfully updated
	 */
	public boolean updateReview(int reviewId, String reviewText) {
		return db.updateReview(reviewId, reviewText);
	}
	
	/**
	 * Deletes a review
	 * @param reviewId The PKey of the review being deleted
	 * @return True if successfully deleted.
	 */
	public boolean deleteReview(int reviewId) {
		return db.deleteReview(reviewId);
	}
	
	/**
	 * Returns a List of reviews for a given question or answer
	 * @param id The ID of the question or answer
	 * @param isAnswer Is it an answer?
	 * @return A list of reviews.
	 */
	public List<Review> getReviewsQA(int id, boolean isAnswer) {
		return db.getReviewsQA(id, isAnswer);
	}

	/**
	 * adds to the like count for the review
	 * @param reviewId the id of the liked review
	 */
	public void addLikeToReview(int reviewId) {
		Review r = findReviewById(reviewId);
		if (r != null) {
			r.addLike();
		}
	}
	
	/**
	 * adds feedback to the review
	 * @param reviewId the id of the review to add feedback
	 * @param studentId the id of the student giving the feedback
	 * @param text the text of the feedback
	 */
	public void addFeedbackToReview(int reviewId, int studentId, String text) {
		Review r = findReviewById(reviewId);
		if (r != null) {
			ReviewFeedback feedback = new ReviewFeedback(++feedbackIdNum, reviewId, studentId, text);
			r.addFeedback(feedback);
		}
	}
	
	/**
	 * finds the review by id
	 * @param id the id of the review to find
	 * @return r the review is found, null if no match is found
	 */
	public Review findReviewById(int id) {
		for (Review r : reviews) {
			if (r.getReviewId() == id) 
				return r;
		}
		return null;
	}
}
