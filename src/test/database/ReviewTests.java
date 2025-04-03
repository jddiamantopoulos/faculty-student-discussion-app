package test.database;

import static org.junit.Assert.*;

import java.sql.SQLException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.List;

import org.junit.Test;
import org.junit.Before;
import org.junit.After;

import databasePart1.DatabaseHelper;
import questions.util.Review;

public class ReviewTests {
	DatabaseHelper db; 
	private Connection connection;

  /**
	 * Sets up test data
	 */
	@Before
	public void setUpDatabase() {
		db = new DatabaseHelper();
		try {
			db.connectToDatabase();
			clearDatabase();
			
			insertUser("testReviewer", "reviewer");
			insertQuestion(1, "Sample Question");
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * resets the database state after each test
	 */
	@After
	public void tearDatabase() {
		clearDatabase();
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
		insertReview(1, "testReviewer", 1, "Old review");
		boolean update = db.updateReview(1, "Updated Review");
		assertTrue(update);
	}
	
	/**
	 * Deletes a review
	 */
	@Test
	public void testDeleteReview() {
		insertReview(2, "testReviewer", 1, "To be deleted");
		boolean deleted = db.deleteReview(2);
		assertTrue(deleted);
	}
	
	/**
	 * Retrieves reviews when given a question
	 */
	@Test
	public void testGetReviewsQA() {
		insertReview(3,"testReviewer", 1, "Review 1" );
		insertReview(4, "testReviewer", 1, "Review 2");
		
		List<Review> reviews = db.getReviewsQA(1, false);
		assertEquals(2, reviews.size());
	}
	
	/**
	 * Helper method to insert this test user
	 * @param userName the username of the user
	 * @param role the role of the user
	 */
	private void insertUser(String userName, String role) {
		String query = "INSERT INTO cse360users (userName, role) VALUES (?, ?)";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setString(1, userName);
			pstmt.setString(2, role);
			pstmt.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Helper method to insert test question
	 * @param questionId the Id of question
	 * @param questionText the text of the question
	 */
	private void insertQuestion(int questionId, String questionText) {
		String query = "INSERT INTO questions (questionId, questionText) VALUES (?, ?)";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setInt(1, questionId);
			pstmt.setString(2, questionText);
			pstmt.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	
	/**
	 * Helper method to insert test review
	 * @param reviewId the Id of review
	 * @param reviewerName the username of reviewer
	 * @param questionId the id of question being reviewed
	 * @param reviewText the text of the review
	 */
	private void insertReview(int reviewId, String reviewerName, int questionId, String reviewText ) {
		String query = "INSERT INTO reviews (reviewId, reviewerName, questionId, reviewText) VALUES (?, ?, ?, ?)";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setInt(1, reviewId);
			pstmt.setString(2, reviewerName);
			pstmt.setInt(3, questionId);
			pstmt.setString(4, reviewText);
			pstmt.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Helper method to clear all test data
	 */
	private void clearDatabase() {
		try(PreparedStatement pstmt1 = connection.prepareStatement("DELETE FROM reviews");
			PreparedStatement pstmt2 = connection.prepareStatement("DELETE FROM cse360users");
			PreparedStatement pstmt3 = connection.prepareStatement("DELETE FROM questions")) {
			pstmt1.executeUpdate();
			pstmt2.executeUpdate();
			pstmt3.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
