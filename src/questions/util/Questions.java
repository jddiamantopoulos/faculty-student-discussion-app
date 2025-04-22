package questions.util;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import accounts.util.User;
import databasePart1.DatabaseHelper;

public class Questions extends ArrayList<Question> {
	
	/* Most of the functionality already exists in ArrayLists, and
	*  it was tempting to just use one without creating a whole new
	*  class. But eventually I got to the part where I needed these
	*  methods, and I'm now very glad that I took the time to extend
	*  the arraylists.
	*/
	
	/**
	 * Returns the subset of the questions which have a given tag.
	 * @param tag The tag to search for.
	 * @return The subset of questions which have a given tag.
	 */
	public Questions getByTag(String tag) {
		Questions returned = new Questions();
		for (int i = 0; i < size(); i++) {
			if ( get(i).getTags().contains(tag) ) {
				returned.add(get(i));
			}
		}
		return returned;
	}
	
	/**
	 * Searches for the questions written by a particular author.
	 * @param author The author to search for.
	 * @return The subset of the questions written by an author.
	 */
	public Questions getByAuthor(String author) {
		Questions returned = new Questions();
		for (int i = 0; i < size(); i++) {
			if ( get(i).getAuthor().equals(author) ) {
				returned.add(get(i));
			}
		}
		return returned;
	}
	
	/**
	 * Searches for the subset of the questions which have a particular text.
	 * @param text The text to search for.
	 * @return The subset of the questions that have that text.
	 */
	public Questions getByText(String text) {
		Questions returned = new Questions();
		for (int i = 0; i < size(); i++) {
			if ( get(i).getText().equals(text) ) {
				returned.add(get(i));
			}
		}
		return returned;
	}
	
	/**
	 * Searches for the subset of questions which do not have answers yet.
	 * @return The subset of questions which do not have answers yet.
	 */
	public Questions getUnanswered() {
		Questions returned = new Questions();
		for (int i = 0; i < size(); i++) {
			if ( get(i).getNumAnswers() == 0 ) {
				returned.add(get(i));
			}
		}
		return returned;
	}
	
	/**
	 * Searches for the subset of questions that have answers.
	 * @return The subset of questions that have answers.
	 */
	public Questions getAnswered() {
		Questions returned = new Questions();
		for (int i = 0; i < size(); i++) {
			if ( get(i).getNumAnswers() > 0 ) {
				returned.add(get(i));
			}
		}
		return returned;
	}
	
	/**
	 * Searches for the subset of questions with reviews by reviewers whose scores are above a certain threshold.
	 * 
	 * @param threshold The minimum score to search for.
	 * @param user The current user of the application.
	 * @param db The application's database helper.
	 * @return The subset of questions with reviews by reviewers whose scores are above a certain threshold.
	 */
	public Questions getReviewedQuestions(int threshold, User user, DatabaseHelper db) {
		Questions returned = new Questions();
		for (int i = 0; i < size(); i++) {
			if (hasQuestionReview(get(i), threshold, user, db)) {
				returned.add(get(i));
			}
		}
		return returned;
	}
	
	/**
	 * Searches for the subset of questions with answers that have reviews by reviewers whose scores are above a certain threshold.
	 * 
	 * @param threshold The minimum score to search for.
	 * @param user The current user of the application.
	 * @param db The application's database helper.
	 * @return The subset of questions with answers that have reviews by reviewers whose scores are above a certain threshold.
	 */
	public Questions getReviewedAnswers(int threshold, User user, DatabaseHelper db) {
		Questions returned = new Questions();
		for (int i = 0; i < size(); i++) {
			if (hasAnswerReview(get(i), threshold, user, db)) {
				returned.add(get(i));
			}
		}
		return returned;
	}
	
	/**
	 * Searches for the subset of questions with bookmarked answers.
	 * 
	 * @param user The current user of the application.
	 * @param db The application's database helper.
	 * @return The subset of questions with answers bookmarked answers.
	 */
	public Questions getBookmarkedAnswers(User user, DatabaseHelper db) {
		Questions returned = new Questions();
		for (int i = 0; i < size(); i++) {
			Question tempQ = get(i);
			if (hasBookmarkedAnswer(tempQ, user, db)) {
				returned.add(tempQ);
			}
		}
		return returned;
	}
	
	/**
	 * Private method: Does a question have reviews w/ scores exceeding the threshold?
	 * @param q The question being examined.
	 * @param threshold The minimum reviewer score.
	 * @param user The user whose scores are used.
	 * @param db The application's databaseHelper.
	 * @return True if a question has qualifying reviews.
	 */
	private boolean hasQuestionReview(Question q, int threshold, User user, DatabaseHelper db) {
		List<Review> reviews = db.getReviewsQA(q.getKey(), false);
		for (int i = 0; i < reviews.size(); i++) {
			Review temp = reviews.get(i);
			if (temp.getReviewerScore(user) >= threshold) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Private method: Does a question's answers have reviews w/ scores exceeding the threshold?
	 * @param q The question being examined.
	 * @param threshold The minimum reviewer score.
	 * @param user The user whose scores are used.
	 * @param db The application's databaseHelper.
	 * @return True if a question's answers have qualifying reviews.
	 */
	private boolean hasAnswerReview(Question q, int threshold, User user, DatabaseHelper db) {
		Answers a = q.getAnswers();
		for (int i = 0; i < a.size(); i++) {
			Answer temp = a.get(i);
			List<Review> reviews;
			try {
				reviews = db.getReviewsQA(db.getKeyForAnswer(temp.getText()), true);
			} catch (SQLException e) {
				/* COVERAGE EXCEPTION: Cannot be simulated because keys should be valid. */
				e.printStackTrace();
				return false;
			}
			System.err.println(reviews.size());
			for (int j = 0; j < reviews.size(); j++) {
				Review tempR = reviews.get(j);
				if (tempR.getReviewerScore(user) >= threshold) {
					return true;
				}
			}
		}
		return false;
	}
	
	private boolean hasBookmarkedAnswer(Question q, User user, DatabaseHelper db) {
		Answers a = q.getAnswers();
		boolean retVal = false;
		for (int i = 0; i < a.size(); i++) {
			if (db.isAnswerBookmarked(user.getUserName(), a.get(i).getKey())) {
				return true;
			}
		}
		return false;
	}
	
}