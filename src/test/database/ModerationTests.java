package test.database;

import static org.junit.Assert.*;

import java.sql.SQLException;
import java.util.ArrayList;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import accounts.util.User;
import databasePart1.DatabaseHelper;
import messaging.util.Message;
import messaging.util.Messages;
import questions.util.Answer;
import questions.util.Question;
import questions.util.Review;

public class ModerationTests {
	
	private static DatabaseHelper db = new DatabaseHelper();
	@Before
	/**
	 * Sets up the test environment with a clear database.
	 * @throws Thrown if there's a SQLException
	 */
	public void setUp() throws Exception {
		db.connectToDatabase();
		db.clear();
		
		// Now add some predetermined content to the database.
		db.register(new User("user", "P4$$word", "user"));
		db.register(new User("administrator", "P4$$word", "admin"));
		db.register(new User("staffMember", "P4$$word", "staff"));
		Question newQ = new Question("Question Text", "Question Body", "user", new ArrayList<String>());
		db.insertQuestion(newQ);
		db.insertAnswer(newQ, new Answer("Answer Text", "admin"));
		db.addReview("staffMember", 0, "This is a review of a question", false);
		db.addReview("administrator", 0, "This is a review of an answer", true);
		db.insertMessage(new Message("Message Text", "administrator", "staffMember"));
		db.insertMessage(new Message("Reply Text", "staffMember", "administrator"));
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
	public void testGetAllUsers() {
		try {
			ArrayList<String> users = db.getAllUsers();
			assertEquals(users.size(), 3);
			assertEquals(users.get(0), "user");
			assertEquals(users.get(1), "administrator");
			assertEquals(users.get(2), "staffMember");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			fail("SQLException thrown.");
		}
	}

	@Test
	public void testGetMessagesByAuthor() {
		try {
			Messages m = db.getMessagesByAuthor("staffMember");
			assertEquals(m.size(), 1);
			assertEquals(m.get(0).getText(), "Reply Text");
		} catch (SQLException e) {
			fail("SQLException thrown.");
		}
	}

	@Test
	public void testGetReviewsByAuthor() {
		try {
			ArrayList<Review> r = db.getReviewsByAuthor("staffMember");
			assertEquals(r.size(), 1);
			assertEquals(r.get(0).getReviewText(), "This is a review of a question");
		} catch (SQLException e) {
			fail("SQLException thrown.");
		}
	}

}
