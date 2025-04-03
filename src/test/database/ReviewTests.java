package test.database;

import static org.junit.Assert.*;

import java.sql.SQLException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.List;

import org.junit.Test;

import accounts.util.User;

import org.junit.Before;
import org.junit.After;

import databasePart1.DatabaseHelper;
import questions.util.Question;
import questions.util.Review;

public class ReviewTests {
	private static DatabaseHelper db = new DatabaseHelper();

	/**
	 * Sets up test data
	 */
	@Before
	public void setUpDatabase() {
		try {
			db.connectToDatabase();
			db.clear();
			insertUser("testReviewer", "reviewer");
			insertQuestion(1, "Sample Question");
			
		} catch (SQLException e) {
			e.printStackTrace();
			fail("SQLException thrown.");
		}
	}
	
	/**
	 * Resets the database state after each test
	 */
	@After
	public void tearDatabase() {
		db.clear();
		db.closeConnection();
	}
	
	/**
	 * checks if the user is a reviewer
	 */
	@Test
	public void testIsReviewer() {
		assertTrue(db.isReviewer("testReviewer"));
		assertFalse(db.isReviewer("nonExistentUser"));
		
	}
	
	/**
	 * Adds review to the database
	 */
	@Test
	public void testAddReview() {
		boolean add = db.addReview("testReviewer", 1, "Great Question!", false);
		assertTrue(add);		
	}
	
	/**
	 * Updates an existing review
	 */
	@Test
	public void testUpdateReview() {
		db.addReview("testReviewer", 1, "Old review", false);
		boolean update = db.updateReview(1, "Updated Review");
		assertTrue(update);
	}
	
	/**
	 * Deletes a review
	 */
	@Test
	public void testDeleteReview() {
		db.addReview("testReviewer", 1, "To be deleted", false);
		boolean deleted = db.deleteReview(1);
		assertTrue(deleted);
	}
	
	/**
	 * Retrieves reviews when given a question
	 */
	@Test
	public void testGetReviewsQA() {
		db.addReview("testReviewer", 1, "Review 1", false);
		db.addReview("testReviewer", 1, "Review 2", false);
		
		List<Review> reviews = db.getReviewsQA(1, false);
		assertEquals(2, reviews.size());
	}
	
	/**
	 * Helper method to insert this test user
	 * @param userName the username of the user
	 * @param role the role of the user
	 */
	private void insertUser(String userName, String role) throws SQLException {
		User tempUser = new User(userName, "P4$$word", role);
		db.register(tempUser);
	}
	
	/**
	 * Helper method to insert test question
	 * @param questionId the Id of question
	 * @param questionText the text of the question
	 */
	private void insertQuestion(int questionId, String questionText) throws SQLException {
		db.insertQuestion(new Question(questionId, questionText, "", "testAuthor", ""));
	}

}
