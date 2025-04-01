package test.database;

import static org.junit.Assert.*;

import java.sql.SQLException;
import java.util.ArrayList;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import accounts.util.Reviewer;
import accounts.util.User;
import databasePart1.DatabaseHelper;

public class ReviewerWeightingTests {
	
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
	/**
	 * Test the updateReviewers() method.
	 */
	public void testUpdateReviewers() {
		try {
			assertEquals(user.getReviewersAsString(), "");
			String reviewersString = "reviewer1,30;reviewer2,70;";
			user.setReviewers(reviewersString);
			db.updateReviewers(user);
			assertEquals(user.getReviewersAsString(), reviewersString);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			fail("SQLException thrown.");
		}
	}

	@Test
	/**
	 * Test the getReviewersFromDB() method.
	 */
	public void testGetReviewersFromDB() {
		try {
			// Start with the same content as the last test.
			assertEquals(user.getReviewersAsString(), "");
			String reviewersString = "reviewer1,30;reviewer2,70;";
			user.setReviewers(reviewersString);
			db.updateReviewers(user);
			assertEquals(user.getReviewersAsString(), reviewersString);
			
			// Now get the reviewers ArrayList
			ArrayList<Reviewer> reviewers = db.getReviewersFromDB(user);
			assertEquals(reviewers.size(), 2);
			assertEquals(reviewers.get(0).getUsername(), "reviewer1");
			assertEquals(reviewers.get(1).getUsername(), "reviewer2");
			assertEquals(reviewers.get(0).getScore(), 30);
			assertEquals(reviewers.get(1).getScore(), 70);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			fail("SQLException thrown.");
		}
	}

	@Test
	/**
	 * Test the getAllReviewers() method.
	 */
	public void testGetAllReviewers() {
		try {
			// Start with the same content as the last test.
			assertEquals(user.getReviewersAsString(), "");
			String reviewersString = "reviewer1,30;reviewer2,70;";
			user.setReviewers(reviewersString);
			db.updateReviewers(user);
			assertEquals(user.getReviewersAsString(), reviewersString);
			
			// Now get the reviewers ArrayList
			ArrayList<Reviewer> reviewers = db.getAllReviewers(user);
			assertEquals(reviewers.size(), 3);
			assertEquals(reviewers.get(0).getUsername(), "reviewer1");
			assertEquals(reviewers.get(1).getUsername(), "reviewer2");
			assertEquals(reviewers.get(2).getUsername(), "instructor");
			assertEquals(reviewers.get(0).getScore(), 30);
			assertEquals(reviewers.get(1).getScore(), 70);
			assertEquals(reviewers.get(2).getScore(), 50);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			fail("SQLException thrown.");
		}
	}

}
