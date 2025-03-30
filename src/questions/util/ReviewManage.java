package questions.util;
import databasePart1.DatabaseHelper;
import java.util.List;

public class ReviewManage {
	private DatabaseHelper db;
	
	public ReviewManage(DatabaseHelper db) {
		this.db = db;
	}
	
	public boolean addReview(String reviewerName, int questionId, String reviewText, boolean isAnswer) {
		return db.addReview(reviewerName, questionId, reviewText, isAnswer); 
	}
		
	public boolean updateReview(int reviewId, String reviewText) {
		return db.updateReview(reviewId, reviewText);
	}
	
	public boolean deleteReview(int reviewId) {
		return db.deleteReview(reviewId);
	}
	
	public List<Review> getReviewsQA(int id, boolean isAnswer) {
		return db.getReviewsQA(id, isAnswer);
	}
}
