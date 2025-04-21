package test.database;

import static org.junit.Assert.*;

import java.sql.SQLException;
import java.util.ArrayList;

import org.junit.Test;

import accounts.util.User;
import databasePart1.DatabaseHelper;
import questions.util.Question;
import questions.util.Questions;
import questions.util.Answer;
import questions.util.Answers;

import java.util.ArrayList;

/**
 * Tests the DatabaseHelper's question methods.
 */
public class QuestionTests {
	DatabaseHelper db = new DatabaseHelper();
	
	@Test
	/**
	 * Tests the getQuestions() and insertQuestion() methods.
	 */
	public void testGetAndInsertQuestions() {
		setUpQuestionEnv();
		Question q = new Question("text", "body", "author", new ArrayList<String>());
		try {
			db.insertQuestion(q);
			Questions collection = db.getQuestions();
			Question q2 = collection.get(0);
			assertEquals(q.getKey(), q2.getKey());
			assertEquals(q.getText(), q2.getText());
			assertEquals(q.getAuthor(), q2.getAuthor());
			assertEquals(q.getBody(), q2.getBody());
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			fail("SQLException thrown.");
		}
		
		tearDownQuestionEnv();
	}
	
	@Test
	/**
	 * Tests the updateQuestion() method.
	 */
	public void testUpdateQuestion() {
		setUpQuestionEnv();
		Question q = new Question("text", "body", "author", new ArrayList<String>());
		try {
			db.insertQuestion(q);
			q.setBody("new body!");
			db.updateQuestion(q);
			Questions collection = db.getQuestions();
			Question q2 = collection.get(0);
			assertEquals(q.getBody(), q2.getBody());
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			fail("SQLException thrown.");
		}
		
		tearDownQuestionEnv();
	}
	
	@Test
	/**
	 * Tests the removeQuestion() method.
	 */
	public void testRemoveQuestion() {
		setUpQuestionEnv();
		Question q = new Question("text", "body", "author", new ArrayList<String>());
		try {
			db.insertQuestion(q);
			db.removeQuestion(q);
			Questions collection = db.getQuestions();
			assertEquals(0, collection.getByText("text").size());
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			fail("SQLException thrown.");
		}
		
		tearDownQuestionEnv();
	}
	
	@Test
	/**
	 * Tests the getAnswers() and insertAnswer() methods.
	 */
	public void testGetAndInsertAnswers() {
		setUpQuestionEnv();
		Question q = new Question("text", "body", "author", new ArrayList<String>());
		try {
			db.insertQuestion(q);
			db.insertAnswer(q, new Answer("text1", "author1"));
			db.insertAnswer(q, new Answer("text2", "author2"));
			Questions collection = db.getQuestions();
			Question q2 = collection.get(0);
			assertEquals(q.getAnswers(), q2.getAnswers());
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			fail("SQLException thrown.");
		}
		
		tearDownQuestionEnv();
	}
	
	@Test
	/**
	 * Tests the updateAnswer() method.
	 */
	public void testUpdateAnswer() {
		setUpQuestionEnv();
		Question q = new Question("text", "body", "author", new ArrayList<String>());
		try {
			db.insertQuestion(q);
			Answer a = new Answer("text1", "author1");
			db.insertAnswer(q, a);
			a.setText("new text!");
			db.updateAnswer(a);
			Answers collection = db.getAnswersByAuthor(a.getAuthor());
			Answer a2 = collection.get(0);
			assertEquals(a.getText(), a2.getText());

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			fail("SQLException thrown.");
		}
		
		tearDownQuestionEnv();
	}
	
	@Test
	/**
	 * Tests the removeAnswer() method.
	 */
	public void testRemoveAnswer() {
		setUpQuestionEnv();
		Question q = new Question("text", "body", "author", new ArrayList<String>());
		try {
			db.insertQuestion(q);
			Answer answer1 = new Answer("text1", "author1");
			Answer answer2 = new Answer("text2", "author2");
			db.insertAnswer(q, answer1);
			db.insertAnswer(q, answer2);
			Questions collection = db.getQuestions();
			Question q2 = collection.get(0);
			db.removeAnswer(answer1);
			assertEquals(q.getAnswers(), q2.getAnswers());
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			fail("SQLException thrown.");
		}
		
		tearDownQuestionEnv();
	}
	
	@Test
	/**
	 * Tests the getKeyForAnswer() method.
	 */
	public void testGetKeyForAnswer() {
		setUpQuestionEnv();
		
		Question q = new Question("text", "body", "author", new ArrayList<String>());
		try {
			db.insertQuestion(q);
			Answer answer1 = new Answer("text1", "author1");
			db.insertAnswer(q, answer1);
			assertEquals(1, db.getKeyForAnswer("text1"));
			db.removeAnswer(answer1);
			assertEquals(-1, db.getKeyForAnswer("text1"));
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			fail("SQLException thrown.");
		}
		
		tearDownQuestionEnv();
	}
	
	
	/**
	 * Helper method to set up the environment for messaging.
	 */
	public void setUpQuestionEnv() {
		try {
			db.connectToDatabase();
			db.clear();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Helper method to clear the environment for messaging.
	 */
	private void tearDownQuestionEnv() {
		db.clear();
		db.closeConnection();
	}

}
