package test.database;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import accounts.util.User;
import databasePart1.DatabaseHelper;

public class ReviewerScorecardTests {

	private static DatabaseHelper db = new DatabaseHelper();
	private static User user = new User("User", "P4$$word", "user");
	private static User reviewer1 = new User("reviewer1", "P4$$word", "reviewer");
	private static User reviewer2 = new User("reviewer2", "P4$$word", "reviewer");
	private static User instructor = new User("instructor", "P4$$word", "reviewer");

	@Before
	/**
	 * Sets up the test environment with a clear database.
	 * @throws Thrown if there's a SQLException
	 */
	public void setUp() throws Exception {
		db.connectToDatabase();
		db.clear();
		user.setReviewers("");
		db.register(user);
		db.register(reviewer1);
		db.register(reviewer2);
		db.register(instructor);
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
	public void testUpdateAndGetReviewerScorecard() {
		db.updateReviewerScorecard("reviewerEX", 30);
		int result = db.getReviewerScorecard("reviewerEX");
		assertEquals(30, result);
	}

}
