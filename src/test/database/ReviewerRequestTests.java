package test.database;

import static org.junit.Assert.*;

import java.sql.SQLException;
import java.util.ArrayList;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import accounts.util.User;
import databasePart1.DatabaseHelper;
import questions.util.Answer;
import questions.util.Answers;
import questions.util.Question;
import questions.util.Questions;

/**
 * Tests the reviewer request methods in the database.
 */
public class ReviewerRequestTests {
	
	private static DatabaseHelper db = new DatabaseHelper();

	@Before
	/**
	 * Sets up the test environment with a clear database.
	 * @throws Thrown if there's a SQLException
	 */
	public void setUp() throws Exception {
		db.connectToDatabase();
		db.clear();
	}

	@After
	/**
	 * Tears down the test environment with a clear database.
	 * @throws Exception Thrown if there's a SQLException
	 */
	public void tearDown() throws Exception {
		db.clear();
		db.closeConnection();
	}

	@Test
	/**
	 * Tests the "setUserRole()" method
	 */
	public void testSetUserRole() {
		User user1 = new User("user1", "P4$$word", "user");
		User user2 = new User("user2", "P4$$word", "user");
		try {
			db.register(user1);
			db.register(user2);
			String u1ret1 = db.getUserRole("user1");
			String u2ret1 = db.getUserRole("user2");
			assertEquals(u1ret1, "user");
			assertEquals(u2ret1, "user");
			db.setUserRole("user1", "reviewer");
			db.setUserRole("user2", "instructor");
			String u1ret2 = db.getUserRole("user1");
			String u2ret2 = db.getUserRole("user2");
			assertEquals(u1ret2, "reviewer");
			assertEquals(u2ret2, "instructor");
		} catch (SQLException e) {
			fail("SQLException thrown.");
		}
	}

	@Test
	/**
	 * Tests the "requestReviewerRole()" and "getReviewerRequests()" methods.
	 */
	public void testRequestReviewerRoleAndGetReviewerRequests() {
		try {
			db.requestReviewerRole("user");
			db.requestReviewerRole("user2");
			ArrayList<String> requests = db.getReviewerRequests();
			assertEquals(requests.get(0), "user");
			assertEquals(requests.get(1), "user2");
		} catch (SQLException e) {
			fail("SQLException thrown.");
		}
	}

	@Test
	/**
	 * Tests the "getQuestionsByAuthor()" method.
	 */
	public void testGetQuestionsByAuthor() {
		Question q1 = new Question("Test Question", "Test Question Body", "user", new ArrayList<String>());
		Question q2 = new Question("Test Question 2", "Test Question Body", "user", new ArrayList<String>());
		Question q3 = new Question("Test Question wrong user 1", "Test Question Body", "user2", new ArrayList<String>());
		Question q4 = new Question("Test Question wrong user 2", "Test Question Body", "user2", new ArrayList<String>());
		try {
			db.insertQuestion(q1);
			db.insertQuestion(q3);
			db.insertQuestion(q4);
			db.insertQuestion(q2); //needs to be at the end so we're also testing the indices
			Questions q = db.getQuestionsByAuthor("user");
			assertEquals(q.get(0).getText(), q1.getText());
			assertEquals(q.get(1).getText(), q2.getText());
		} catch (SQLException e) {
			fail("SQLException thrown.");
		}
	}

	@Test
	/**
	 * Tests the "getAnswersByAuthor()" method.
	 */
	public void testGetAnswersByAuthor() {
		Question testQ = new Question("Test Question", "Test Question Body", "user", new ArrayList<String>());
		Answer a1 = new Answer("Test Answer", "user");
		Answer a2 = new Answer("Test Answer 2", "user");
		Answer a3 = new Answer("Test Answer wrong user 1", "user2");
		Answer a4 = new Answer("Test Answer wrong user 2", "user2");
		try {
			db.insertAnswer(testQ, a1);
			db.insertAnswer(testQ, a3);
			db.insertAnswer(testQ, a4);
			db.insertAnswer(testQ, a2); //needs to be at the end so we're also testing the indices
			Answers a = db.getAnswersByAuthor("user");
			assertEquals(a.get(0).getText(), a1.getText());
			assertEquals(a.get(1).getText(), a2.getText());
		} catch (SQLException e) {
			fail("SQLException thrown.");
		}
	}

	@Test
	/**
	 * Tests the "rejectReviewerRequest()" method.
	 */
	public void testRejectReviewerRequest() {
		try {
			db.requestReviewerRole("user");
			db.requestReviewerRole("user2");
			db.rejectReviewerRequest("user");
			ArrayList<String> requests = db.getReviewerRequests();
			assertEquals(requests.get(0), "user2");
		} catch (SQLException e) {
			fail("SQLException thrown.");
		}
	}

}
