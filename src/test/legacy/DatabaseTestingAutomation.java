package test.legacy;
import java.sql.SQLException;
import java.util.ArrayList;

import accounts.util.*;
import databasePart1.DatabaseHelper;
import questions.util.*;
import test.legacy.*;

// Imported from Eli's HW1 for easier testing.

public class DatabaseTestingAutomation {
	
	static Questions QuestionList = new Questions();
	static Answers AnswerList = new Answers();
	static DatabaseHelper db = new DatabaseHelper();

	/*
	 * This main line displays a header to the console, performs a sequence of
	 * test cases, and then displays a footer with a summary of the results
	 */
	public static void main(String[] args) {
		
		/************** Test cases semi-automation report header **************/
		System.out.println("______________________________________");
		System.out.println("\nTesting Automation");
		
		/************** Start of the test cases **************/
		
		/* COVERAGE:
		 * Mostly just looking for one per method.
		 */
		UnitTests tests = new UnitTests("DatabaseHelper");

        tests.addNote("All outputs from the db helper will have a newline before and after.");
		
        // connect
        
        tests.addNote("Testing DB connection.");
		
		try {
			db.connectToDatabase();
			db.clear();
			tests.manualPassFail(true, "DB Connection Success");
		} catch (Exception e) {
			tests.manualPassFail(false, "DB Connection Fail");
			System.exit(1);
		}
		
		// Huge try block to take care of SQL exceptions
		try {
		
		// db contents
		tests.boolEqualityTest("Is Database Empty", db.isDatabaseEmpty(), true);
		
		
		// user registration
		db.register(new User("UserName", "P4$$word", "user"));
		tests.stringEqualityTest("Can users be added/retrieved?", db.getUser("UserName").getRole(), "user");
		
		// user login
		User user1 = db.getUser("UserName");
		tests.boolEqualityTest("Test login", db.login(user1), true);
		
		// user exists
		tests.boolEqualityTest("User Exists", db.doesUserExist("UserName"), true);
		
		// get user role
		tests.stringEqualityTest("User Role Comparison", db.getUserRole("UserName"), "user");
		
		// INVITE CODES
		
		// generate new code, then validate it
		String code = db.generateInvitationCode("admin");
		tests.stringEqualityTest("Get associated role", db.getAssociatedRole(code), "admin");
		tests.boolEqualityTest("Test code generation", db.validateInvitationCode(code), true);
		
		// add a question and retrieve it
		Question q = new Question(DatabaseHelper.questionKey, "Test Question", "Question Body", "QuestionPoster", "");
		DatabaseHelper.questionKey++;
		db.insertQuestion(q);
		tests.stringEqualityTest("Retrieve Question", db.getQuestions().get(0).getText(), "Test Question");
		
		// add an answer to that question and retrieve it
		Answer a = new Answer("Answer 1", "AnswerPoster");
		q.addAnswer(a);
		db.insertAnswer(q, a);
		db.getAnswers(q);
		tests.stringEqualityTest("Retrieve Answer", q.getAnswers().get(0).getText(), "Answer 1");
		
		db.closeConnection();

		tests.manualPassFail(true, "No SQL Exceptions.");
		
        tests.close();
		} catch (SQLException e) {
			e.printStackTrace();
			tests.manualPassFail(false, "SQL Exception. Closing.");
			tests.close();
		}
		
	}
	
}
