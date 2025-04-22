package test.database;

import static org.junit.Assert.*;
import java.sql.*;
import java.util.List;
import java.util.ArrayList;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import accounts.util.User;
import databasePart1.DatabaseHelper;
import questions.util.Question;

public class ReviewFeedbackLikesTests {

	private static DatabaseHelper db = new DatabaseHelper();
	private int reviewId;
	private int questionId;

	@Before
	public void setUp() {
		try {
			db.connectToDatabase();
			db.clear();

			insertUser("author", "user");
			insertUser("reviewer", "reviewer");
			insertUser("like1", "user");
			insertUser("like2", "user");
			insertUser("feedbacker", "user");

			insertQuestion("Sample Question", "This is the body", "author");
			insertReview(questionId, "reviewer", "This is a review");

		} catch (SQLException e) {
			e.printStackTrace();
			fail("Failed due to SQLException.");
		}
	}

	@After
	public void tearDown() {
		db.clear();
		db.closeConnection();
	}

	@Test
	public void testLikeAndUnlikeReview() {
		assertTrue(db.likeReview(reviewId, "liker1"));
		assertTrue(db.likeReview(reviewId, "liker2"));
		assertTrue(db.unlikeReview(reviewId, "liker1"));
	}

	@Test
	public void testAddReviewFeedbackAndRetrieve() {
		assertTrue(db.addReviewFeedback(reviewId, "feedbacker", "Great review!"));
		List<String> feedbacks = db.getReviewFeedback(reviewId);
		assertEquals(1, feedbacks.size());
		assertEquals("feedbacker: Great review!", feedbacks.get(0));
	}

	@Test
	public void testIncrementLikeCount() {
		try {
			assertTrue(db.incrementReviewLike(db.getReviewsByAuthor("reviewer").get(0), "user"));
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			fail("SQLException");
		}
	}

	//Helpers

	private void insertUser(String username, String role) throws SQLException {
		User user = new User(username, "P4$sword", role);
		db.register(user);
	}

	private void insertQuestion(String text, String body, String author) throws SQLException {
	    ArrayList<String> tags = new ArrayList<>(); // empty tags list
	    Question q = new Question(text, body, author, tags);
	    db.insertQuestion(q); 
	}

	private void insertReview(int qId, String reviewer, String text) throws SQLException {
		db.addReview(reviewer, questionId, text, false);
	}
		
}
