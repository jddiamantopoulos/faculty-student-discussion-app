package test.questions.util;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import questions.util.Answer;

import java.util.ArrayList;

public class AnswerTests {

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	/**
	 * Tests the upvote() method of Answer class.
	 */
	public void testUpvote() {
		Answer answer = new Answer("text", "author");
		answer.upvote("author2");
		answer.upvote("author3");
		ArrayList<String> markedHelpfulList = new ArrayList<String>();
		markedHelpfulList.add("author2");
		markedHelpfulList.add("author3");
		assertEquals(answer.getMarkedHelpful(), markedHelpfulList);
	}
}
