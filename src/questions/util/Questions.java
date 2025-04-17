package questions.util;

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
	
	public Questions getByTag(String tag) {
		Questions returned = new Questions();
		for (int i = 0; i < size(); i++) {
			if ( get(i).getTags().contains(tag) ) {
				returned.add(get(i));
			}
		}
		return returned;
	}
	
	public Questions getByAuthor(String author) {
		Questions returned = new Questions();
		for (int i = 0; i < size(); i++) {
			if ( get(i).getAuthor().equals(author) ) {
				returned.add(get(i));
			}
		}
		return returned;
	}
	
	// Could also be described as "getByQuestion()"
	public Questions getByText(String text) {
		Questions returned = new Questions();
		for (int i = 0; i < size(); i++) {
			if ( get(i).getText().equals(text) ) {
				returned.add(get(i));
			}
		}
		return returned;
	}
	
	public Questions getUnanswered() {
		Questions returned = new Questions();
		for (int i = 0; i < size(); i++) {
			if ( get(i).getNumAnswers() == 0 ) {
				returned.add(get(i));
			}
		}
		return returned;
	}
	
	public Questions getAnswered() {
		Questions returned = new Questions();
		for (int i = 0; i < size(); i++) {
			if ( get(i).getNumAnswers() > 0 ) {
				returned.add(get(i));
			}
		}
		return returned;
	}
	
	public Questions getReviewedQuestions(int threshold, User user, DatabaseHelper db) {
		Questions returned = new Questions();
		for (int i = 0; i < size(); i++) {
			if (hasQuestionReview(get(i), threshold, user, db)) {
				returned.add(get(i));
			}
		}
		return returned;
	}
	
	public Questions getReviewedAnswers(int threshold, User user, DatabaseHelper db) {
		Questions returned = new Questions();
		for (int i = 0; i < size(); i++) {
			if (hasAnswerReview(get(i), threshold, user, db)) {
				returned.add(get(i));
			}
		}
		return returned;
	}
	
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
	
	private boolean hasAnswerReview(Question q, int threshold, User user, DatabaseHelper db) {
		Answers a = q.getAnswers();
		for (int i = 0; i < a.size(); i++) {
			Answer temp = a.get(i);
			List<Review> reviews = db.getReviewsQA(temp.getKey(), true);
			for (int j = 0; j < reviews.size(); j++) {
				Review tempR = reviews.get(j);
				if (tempR.getReviewerScore(user) >= threshold) {
					return true;
				}
			}
		}
		return false;
	}
	
}