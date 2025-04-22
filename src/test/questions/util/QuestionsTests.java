package test.questions.util;

import static org.junit.Assert.*;

import java.sql.SQLException;
import java.util.ArrayList;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.*;

import accounts.util.User;
import databasePart1.DatabaseHelper;
import questions.util.Answer;
import questions.util.Answers;
import questions.util.Question;
import questions.util.Questions;
import questions.util.Review;

public class QuestionsTests {

	DatabaseHelper db = new DatabaseHelper();
	User user;
	
	@Before
	/**
	 * Sets up the test environment.
	 * @throws Exception Fails when thrown.
	 */
	public void setUp() throws Exception {
		/* Initialize the database */
		db.connectToDatabase();
		db.clear();
		
		/* Create a few different tag lists */
		ArrayList<String> taglist1 = new ArrayList<String>();
		taglist1.add("Lecture");
		ArrayList<String> taglist2 = new ArrayList<String>();
		taglist2.add("General");
		ArrayList<String> taglist3 = new ArrayList<String>();
		taglist3.add("Assignment"); 
		taglist3.add("General");
		
		/* Begin by creating a predetermined set of questions and inserting them into the database. */
		Question q1 = new Question("Question 1", "Question 1 Body", "Q1Author", taglist1);
		Question q2 = new Question("Question 2", "Question 2 Body", "Q2Author", taglist2);
		Question q3 = new Question("Question 3", "Question 3 Body", "Q3Author", taglist1);
		Question q4 = new Question("Question 4", "Question 4 Body", "Q4Author", taglist2);
		Question q4pt2 = new Question("Question 4 part 2", "Question 4 Body part 2", "Q4Author", taglist3);
		db.insertQuestion(q1);
		db.insertQuestion(q2);
		db.insertQuestion(q3);
		db.insertQuestion(q4);
		db.insertQuestion(q4pt2);
		
		/* Now add answers for some questions */
		Answer q1a1 = new Answer("Answer 1 (Q1)", "Answer1Author");
		Answer q1a2 = new Answer("Answer 2 (Q1)", "Answer2Author");
		Answer q2a1 = new Answer("Answer 1 (Q2)", "Answer1Author");
		db.insertAnswer(q1, q1a1);
		db.insertAnswer(q1, q1a2);
		db.insertAnswer(q2, q2a1);
		
		/* Add a new user to view results */
		user = new User("user", "P4$$word", "user");
		db.register(user);
		user.setReviewers("Reviewer1,70;Reviewer2,30;");
		db.updateReviewers(user);
		
		/* And finally, add some reviews */
		db.addReview("Reviewer1", q1.getKey(), "Q1R1", false);
		db.addReview("Reviewer2", q1.getKey(), "Q1R2", false);
		db.addReview("Reviewer1", db.getKeyForAnswer(q2a1.getText()), "Q2A1R1", true);
		db.addReview("Reviewer2", db.getKeyForAnswer(q2a1.getText()), "Q2A1R2", true);
		System.out.println(q2a1.getKey());
		System.out.println("--");
		ArrayList<Review> reviews = db.getReviewsByAuthor("Reviewer1");
		for (int i = 0; i < reviews.size(); i++) {
			System.out.println(reviews.get(i).getqaText());
		}
		System.out.println("--");
		db.getReviewsQA(3, true);
		for (int i = 0; i < reviews.size(); i++) {
			System.out.println(reviews.get(i).getqaText());
		}
	}

	@After
	/**
	 * Tears down the test environment.
	 * @throws Exception Fails when thrown.
	 */
	public void tearDown() throws Exception {
		db.clear();
		db.closeConnection();
	}

	@Test
	/**
	 * Tests the "getByTag()" method in Questions.java.
	 */
	public void testGetByTag() {
		try {
			Questions q = db.getQuestionsAndAnswers();
			Questions returned = q.getByTag("General");
			assertEquals(3, returned.size()); /* all questions w/ taglist 2 or 3 */
			assertEquals("Assignment, General", returned.get(2).getTagsAsString());
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			fail("SQLException thrown.");
			e.printStackTrace();
		}
	}

	@Test
	/**
	 * Tests the "getByAuthor()" method in Questions.java.
	 */
	public void testGetByAuthor() {
		try {
			Questions q = db.getQuestionsAndAnswers();
			Questions returned = q.getByAuthor("Q4Author");
			assertEquals(2, returned.size());
			for (int i = 0; i < returned.size(); i++) {
				assertEquals("Q4Author", returned.get(i).getAuthor());
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			fail("SQLException thrown.");
			e.printStackTrace();
		}
	}

	@Test
	/**
	 * Tests the "getByText()" method in Questions.java.
	 */
	public void testGetByText() {
		try {
			Questions q = db.getQuestionsAndAnswers();
			Questions returned = q.getByText("Question 3");
			assertEquals(1, returned.size());
			assertEquals("Question 3", returned.get(0).getText());
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			fail("SQLException thrown.");
			e.printStackTrace();
		}
	}

	@Test
	/**
	 * Tests the "getUnanswered()" method in Questions.java.
	 */
	public void testGetUnanswered() {
		try {
			Questions q = db.getQuestionsAndAnswers();
			Questions returned = q.getUnanswered();
			assertEquals(3, returned.size()); /* all questions w/o answers */
			for (int i = 0; i < returned.size(); i++) {
				assertEquals(new Answers(), returned.get(i).getAnswers());
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			fail("SQLException thrown.");
			e.printStackTrace();
		}
	}

	@Test
	/**
	 * Tests the "getAnswered()" method in Questions.java.
	 */
	public void testGetAnswered() {
		try {
			Questions q = db.getQuestionsAndAnswers();
			Questions returned = q.getAnswered();
			assertEquals(2, returned.size()); /* all questions w/ answers */
			for (int i = 0; i < returned.size(); i++) {
				assertNotEquals(new Answers(), returned.get(i).getAnswers());
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			fail("SQLException thrown.");
			e.printStackTrace();
		}
	}

	@Test
	/**
	 * Tests the "getReviewedQuestions()" method in Questions.java.
	 */
	public void testGetReviewedQuestions() {
		try {
			Questions q = db.getQuestionsAndAnswers();
			Questions returned = q.getReviewedQuestions(50, user, db);
			assertEquals(1, returned.size()); /* all questions w/ reviews */
			assertEquals("Question 1", returned.get(0).getText());
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			fail("SQLException thrown.");
			e.printStackTrace();
		}
	}

	@Test
	/**
	 * Tests the "getReviewedAnswers()" method in Questions.java.
	 */
	public void testGetReviewedAnswers() {
		try {
			Questions q = db.getQuestionsAndAnswers();
			Questions returned = q.getReviewedAnswers(50, user, db);
			assertEquals(1, returned.size()); /* all questions w/ reviews */
			assertEquals("Question 2", returned.get(0).getText());
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			fail("SQLException thrown.");
			e.printStackTrace();
		}
	}
	
	@Test
	/**
	 * Tests the "getBookmarkedAnswers()" method in Questions.java.
	 */
	public void testGetBookmarkedAnswers() {
		try {
			Questions q = db.getQuestionsAndAnswers();
			db.addAnswerBookmark(user.getUserName(), 7);
			Questions returned = q.getBookmarkedAnswers(user, db);
			assertEquals(1, returned.size()); /* all questions w/ reviews */
			assertEquals("Question 1", returned.get(0).getText());
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			fail("SQLException thrown.");
			e.printStackTrace();
		}
	}

}
