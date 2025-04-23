package test.database;

import static org.junit.Assert.*;
import java.sql.SQLException;
import java.util.List;
import org.junit.Test;
import org.junit.Before;
import org.junit.After;
import databasePart1.DatabaseHelper;
import accounts.util.User;
import questions.util.Question;
import questions.util.Answer;


public class BookmarkTests {
	
	private static DatabaseHelper db = new DatabaseHelper();
	
	@Before
	public void setUpDatabase(){
		try {
			db.connectToDatabase();
			db.clear();
			insertTestUser("testUser");
			insertTestAnswer(1);
			insertTestReviewer("reviewer123");
			
		} catch (SQLException e) {
			e.printStackTrace();
			fail("SQLException thrown");
		}
	}
	
	@After 
	public void tearDownDatabase() {
		db.clear();
		db.closeConnection();
		
	}
	@Test
	public void testAddAndRemoveAnswerBookmark() {
		assertTrue(db.addAnswerBookmark("testUser", 1));
		assertTrue(db.isAnswerBookmarked("testUser", 1));
		
		assertFalse(db.addAnswerBookmark("testUser", 1));
		assertFalse(db.isAnswerBookmarked("testUser", 1));
	}
	
	@Test
	public void testGetBookmarkReviewer() {
		db.addReviewerBookmark("testUser", "reviewer123");
		List<String> reviewers = db.getBookmarkedReviews("testUser");
		assertEquals(1, reviewers.size());
		assertTrue(reviewers.contains("reviewer123"));
	}

	@Test
	public void testGetBookmarkedAnswers() {
		db.addAnswerBookmark("testUser", 1);
		List<String> answers = db.getBookmarkedAnswers("testUser");
		assertEquals(1, answers.size());
		assertTrue(answers.contains("1"));
	}	
	@Test
	public void testAddAndRemoveReviewer() {
		assertTrue(db.addReviewerBookmark("testUser", "reviewer123"));
		assertTrue(db.isReviewerBookmarked("testUser", "reviewer123"));
		
		assertFalse(db.addReviewerBookmark("testUser", "reviewer123"));
		assertFalse(db.isReviewerBookmarked("testUser", "reviewer123"));
	}
	/**
	 * Helper methods
	 * prevention for the memory leak
	 * gives error and doesn't have the program running infinitely 
	 */
	private void insertTestUser(String username) throws SQLException {
		db.register(new User(username, "P4$sword", "student"));
	}
	
	/**
	 * 
	 * 
	 */
	private void insertTestAnswer(int answerId) throws SQLException {
		Question testQuestion = new Question(1,"sample Question","","author1","");
		db.insertQuestion(testQuestion);
		Answer testAnswer = new Answer(answerId,"sample Answer", "author1", "");
		db.insertAnswer(testQuestion, testAnswer);
	}
	
	private void insertTestReviewer(String reviewer) throws SQLException {
		db.register(new User(reviewer, "password", "reviewer"))
		;
	}
}
