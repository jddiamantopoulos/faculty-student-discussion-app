package test.administration.ui;

import static org.junit.Assert.*;

import java.sql.SQLException;
import java.util.ArrayList;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import accounts.util.Reviewer;
import accounts.util.User;
import administration.ui.ReviewerScorecardPage;
import databasePart1.DatabaseHelper;
import questions.util.Question;

public class ReviewerScorecardCalculationTests {
	
	private static DatabaseHelper db = new DatabaseHelper();
	private static User user = new User("user", "P4$$word", "user");
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
		db.insertQuestion(new Question("Question Text", "Q", "user", new ArrayList<String>()));
		db.addReview("reviewer1", 1, "review", false);
		db.addReview("reviewer2", 1, "review2", false);
		db.addReview("instructor", 1, "review3", false);
		db.likeReview(1, "reviewer2");
		db.likeReview(1, "user");
		db.likeReview(2, "user");
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
	 * Tests the method that automatically calculates reviewer scorecards.
	 */
	public void testCalculateReviewerScorecards() {
		
		// Add reviewers
	    final ArrayList<Reviewer> reviewers = new ArrayList<Reviewer>();
		try {
			ArrayList<String> reviewersNames = db.getReviewers();
			for (int i = 0; i < reviewersNames.size(); i++) {
				reviewers.add(new Reviewer(reviewersNames.get(i), db.getReviewerScorecard(reviewersNames.get(i))));
			}
		} catch (SQLException e) {
			e.printStackTrace();
			reviewers.clear();
		}
		
		calculate(reviewers);
		
		System.out.println(db.getReviewerScorecard("reviewer1"));
		System.out.println(db.getReviewerScorecard("reviewer2"));
		System.out.println(db.getReviewerScorecard("instructor"));
		
		assertEquals((int)85, (int)db.getReviewerScorecard("reviewer1"));
		assertEquals((int)50, (int)db.getReviewerScorecard("reviewer2"));
		assertEquals((int)15, (int)db.getReviewerScorecard("instructor"));
	}
	
	/**
	 * Had to do something weird to trick the JVM into collecting this garbage.
	 * @param reviewers The ArrayList of reviewers.
	 */
	private void calculate(ArrayList<Reviewer> reviewers) {
		new ReviewerScorecardPage(db, instructor).calculateReviewerScorecards(reviewers);
	}

}
