package test.accounts.util;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import accounts.util.User;

public class ReviewerStringTests {

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testSetAndGetReviewers() {
		User user = new User("User1", "P4$$word", "user");
		String set = "reviewer1,90;reviewer2,60;reviewer3,70;";
		user.setReviewers(set);
		String reviewers = user.getReviewersAsString();
		assertEquals(set, reviewers);
	}

}
