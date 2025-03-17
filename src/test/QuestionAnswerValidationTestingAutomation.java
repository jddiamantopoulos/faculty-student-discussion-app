package test;
import java.util.ArrayList;

import accounts.util.*;
import questions.util.*;

/*******
 * <p> Title: UsernameRecognitionTestingAutomation Class. </p>
 * 
 * <p> Description: Repurposed the PasswordEvaluator module tests </p>
 * 
 * <p> Copyright: Lynn Robert Carter © 2022 </p>
 * 
 * @author Lynn Robert Carter, Eli Sells
 * 
 * @version 1.00	2022-02-25 A set of semi-automated test cases
 * @version 2.00	2024-09-22 Updated for use at ASU
 * @version 2.10	2025-02-04 Updated for comprehensive testing of
 * 							   Monday25 submission.
 * @version 2.20	2025-02-12 Updated for Question and Answer validation.
 * 
 */
public class QuestionAnswerValidationTestingAutomation {
	
	static int numPassed = 0;	// Counter of the number of passed tests
	static int numFailed = 0;	// Counter of the number of failed tests
	
	static Questions QuestionList = new Questions();
	static Answers AnswerList = new Answers();

	/*
	 * This mainline displays a header to the console, performs a sequence of
	 * test cases, and then displays a footer with a summary of the results
	 */
	public static void main(String[] args) {
		
		// Set up some necessary variables
		ArrayList<String> generalTag = new ArrayList<String>();
		generalTag.add("General");
		ArrayList<String> assignmentTag = new ArrayList<String>();
		assignmentTag.add("Assignment");
		String likes = "someUser,someOtherUser,thirdUser";
		
		/************** Test cases semi-automation report header **************/
		System.out.println("______________________________________");
		System.out.println("\nTesting Automation");
		
		/************** Start of the test cases **************/
		
		// Question-related cases are first
		
		// Testing all failure conditions.
		
		// Empty Question (body can be empty, but text cannot).
		Question testQ1 = new Question("", "", "testUser", new ArrayList<String>());
		performTestCaseQ(1, testQ1, false);
		// Text too long
		Question testQ2 = new Question(generateString(256), "", "testUser", new ArrayList<String>());
		performTestCaseQ(2, testQ2, false);
		// Body too long
		Question testQ3 = new Question("this field is irrelevant", generateString(2001), "testUser", new ArrayList<String>());
		performTestCaseQ(3, testQ3, false);
		// Duplicate Question (pt1 has been manually verified as a valid question)
		// performTestCase will pass QuestionList into the validator
		Question testQ4pt1 = new Question("duplicate", "", "testUser", new ArrayList<String>());
		QuestionList.add(testQ4pt1);
		Question testQ4pt2 = new Question("duplicate", "body can be different", "differentUser", new ArrayList<String>());
		performTestCaseQ(4, testQ4pt2, false);
		
		// Some valid cases (taken from eddiscussion)
		// These two will be passed in to the answer cases
		
		// https://edstem.org/us/courses/72657/discussion/6164729
		Question testQ5 = new Question("Week 05 Live Event",
				"Will the Week 05 Live Event get uploaded to canvas?",
				"Anonymous",
				generalTag);
		performTestCaseQ(5, testQ5, true);
		
		// https://edstem.org/us/courses/72657/discussion/6161198
		Question testQ6 = new Question("C in CRUD",
				"For the \"C\" (create) in CRUD, is it acceptable to implement this functionality in the constructor or do we need a separate create method? For the Question/Answer classes, there does not seem to be any advantage to a separate create method.  But for the QuestionList/AnswerList classes, I suppose a create method would be useful for appending a new Questin/Answer to the respective lists.  Am I thinking about this correctly? I believe the same logic would apply for a delete method, too.  delete makes sense for QuestionList/AnswerList but not so much for Question/Answer",
				"MatthewZenaldin",
				assignmentTag); // He used the general tag, but I want to show that this tag plays no role.
		performTestCaseQ(6, testQ6, true);
		
		// Answer Related Cases
		// Invalid cases
		
		// Empty answer
		Answer testA1 = new Answer("", "testUser");
		performTestCaseA(7, testA1, testQ5, false);
		// Text too long
		Answer testA2 = new Answer(generateString(2001), "testUser");
		performTestCaseA(8, testA2, testQ6, false);
		// Duplicate Answer (pt1 has been manually verified as a valid answer)
		// performTestCase will pass a question containing AnswerList into the validator
		Answer testA3pt1 = new Answer("duplicate", "testUser", likes);
		AnswerList.add(testA3pt1);
		testQ5.setAnswers(AnswerList);
		Answer testA3pt2 = new Answer("duplicate", "otherUser");
		performTestCaseA(9, testA3pt2, testQ5, false);
		
		// Valid cases
		
		// https://edstem.org/us/courses/72657/discussion/6164729
		Answer testA4 = new Answer("Yes, the live event will be updated in canvas", "AdvianaKirubalin", likes);
		performTestCaseA(10, testA4, testQ5, true);
		
		// https://edstem.org/us/courses/72657/discussion/6161198
		Answer testA5 = new Answer("Programming goal is to reuse components in a smart way. What you're doing is on the right path since there’s no conflict. Using the constructor for Question and Answer while having separate create and delete methods for QuestionList and AnswerList makes sense, as it keeps object creation simple while allowing better management of collections. That said, there are multiple ways to implement this and your approach might be one of them.", "TienDatDang", likes);
		performTestCaseA(11, testA5, testQ6, true);
		
		/************** End of the test cases **************/
		
		/************** Test cases semi-automation report footer **************/
		System.out.println("____________________________________________________________________________");
		System.out.println();
		System.out.println("Number of tests passed: "+ numPassed);
		System.out.println("Number of tests failed: "+ numFailed);
	}
	
	/*
	 * This method sets up the input value for the test from the input parameters,
	 * displays test execution information, invokes precisely the same recognizer
	 * that the interactive JavaFX mainline uses, interprets the returned value,
	 * and displays the interpreted result.
	 */
	
	private static String generateString(int numChars) {
		String ret = "";
		for (int i = 0; i < numChars; i++) {
			ret += 'A';
		}
		return ret;
	}
	
	private static void performTestCaseQ(int testCase, Question inputQuestion, boolean expectedPass) {
		/************** Display an individual test case header **************/
		System.out.println("____________________________________________________________________________\n\nTest case: " + testCase);
		System.out.println("Input: \"" + inputQuestion.getText() + "\"");
		System.out.println("______________");
		System.out.println("\nFinite state machine execution trace:");
		
		/************** Call the recognizer to process the input **************/
		String resultText= QuestionValidator.validateQuestion(QuestionList, inputQuestion);
		
		/************** Interpret the result and display that interpreted information **************/
		System.out.println();
		
		// If the resulting text is empty, the recognizer accepted the input
		if (resultText != "") {
			 // If the test case expected the test to pass then this is a failure
			if (expectedPass) {
				System.out.println("***Failure*** The question <" + inputQuestion.getText() + "> is invalid." + 
						"\nBut it was supposed to be valid, so this is a failure!\n");
				System.out.println("Error message: " + resultText);
				numFailed++;
			}
			// If the test case expected the test to fail then this is a success
			else {			
				System.out.println("***Success*** The question <" + inputQuestion.getText() + "> is invalid." + 
						"\nBut it was supposed to be invalid, so this is a pass!\n");
				System.out.println("Error message: " + resultText);
				numPassed++;
			}
		}
		
		// If the resulting text is empty, the recognizer accepted the input
		else {	
			// If the test case expected the test to pass then this is a success
			if (expectedPass) {	
				System.out.println("***Success*** The question <" + inputQuestion.getText() + 
						"> is valid, so this is a pass!");
				numPassed++;
			}
			// If the test case expected the test to fail then this is a failure
			else {
				System.out.println("***Failure*** The question <" + inputQuestion.getText() + 
						"> was judged as valid" + 
						"\nBut it was supposed to be invalid, so this is a failure!");
				numFailed++;
			}
			System.out.println("Error message: " + resultText);
		}
		// displayEvaluation(); // Not used for this class.
	}
	
	private static void performTestCaseA(int testCase, Answer testAnswer, Question testQuestion, boolean expectedPass) {
		/************** Display an individual test case header **************/
		System.out.println("____________________________________________________________________________\n\nTest case: " + testCase);
		System.out.println("Input: \"" + testAnswer.getText() + "\"");
		System.out.println("______________");
		System.out.println("\nFinite state machine execution trace:");
		
		/************** Call the recognizer to process the input **************/
		String resultText= AnswerValidator.validateAnswer(testQuestion, testAnswer);
		
		/************** Interpret the result and display that interpreted information **************/
		System.out.println();
		
		// If the resulting text is empty, the recognizer accepted the input
		if (resultText != "") {
			 // If the test case expected the test to pass then this is a failure
			if (expectedPass) {
				System.out.println("***Failure*** The answer <" + testAnswer.getText() + "> is invalid." + 
						"\nBut it was supposed to be valid, so this is a failure!\n");
				System.out.println("Error message: " + resultText);
				numFailed++;
			}
			// If the test case expected the test to fail then this is a success
			else {			
				System.out.println("***Success*** The answer <" + testAnswer.getText() + "> is invalid." + 
						"\nBut it was supposed to be invalid, so this is a pass!\n");
				System.out.println("Error message: " + resultText);
				numPassed++;
			}
		}
		
		// If the resulting text is empty, the recognizer accepted the input
		else {	
			// If the test case expected the test to pass then this is a success
			if (expectedPass) {	
				System.out.println("***Success*** The answer <" + testAnswer.getText() + 
						"> is valid, so this is a pass!");
				numPassed++;
			}
			// If the test case expected the test to fail then this is a failure
			else {
				System.out.println("***Failure*** The answer <" + testAnswer.getText() + 
						"> was judged as valid" + 
						"\nBut it was supposed to be invalid, so this is a failure!");
				numFailed++;
			}
			System.out.println("Error message: " + resultText);
		}
		// displayEvaluation(); // Not used for this class.
	}
	
}
