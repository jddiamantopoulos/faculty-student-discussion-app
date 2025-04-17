package test.database;

import static org.junit.Assert.*;

import java.sql.SQLException;

import org.junit.Test;

import accounts.util.User;
import databasePart1.DatabaseHelper;
import messaging.util.Message;
import messaging.util.Messages;

/**
 * Tests the DatabaseHelper's message methods.
 */
public class MessageTests {
	DatabaseHelper db = new DatabaseHelper();
	
	@Test
	/**
	 * Tests the getMessages() and insertMessage methods.
	 */
	public void testGetAndInsertMessages() {
		setUpMessageEnv();
		Message m = new Message("This message is a test.", "user1", "user2");
		try {
			db.insertMessage(m);
			Messages collection = db.getMessages();
			Message m2 = collection.get(0);
			assertEquals(m.getText(), m2.getText());
			assertEquals(m.getSender(), m2.getSender());
			assertEquals(m.getRecipient(), m2.getRecipient());
			assertEquals(m.getKey(), m2.getKey());
			assertEquals(m.getTimeAsString(), m2.getTimeAsString());
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			fail("SQLException thrown.");
		}
		
		tearDownMessageEnv();
	}

	@Test
	/**
	 * Tests the getMessagesByUser() method.
	 */
	public void testGetMessagesByUser() {
		setUpMessageEnv();
		User user = new User("user1", "P4$$word", "user1");
		Message m1 = new Message("Test 1.", "user1", "user2");
		Message m2 = new Message("Test 2.", "user2", "user3");
		Message m3 = new Message("Test 3.", "user3", "user4");
		Message m4 = new Message("Test 4.", "user2", "user1");
		try {
			db.insertMessage(m1);
			db.insertMessage(m2);
			db.insertMessage(m3);
			db.insertMessage(m4);
			Messages m = db.getMessagesByUser(user, "user2");
			assertEquals(m.get(0).getText(), m1.getText());
			assertEquals(m.get(1).getText(), m2.getText());
			assertEquals(m.get(2).getText(), m4.getText());
		} catch (SQLException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
		fail("SQLException thrown.");
		}
	}

	@Test
	/**
	 * Tests the getMessagesForConvo() method.
	 */
	public void testGetMessagesForConvo() {
		setUpMessageEnv();
		User user = new User("user1", "P4$$word", "user1");
		Message m1 = new Message("Test 1.", "user1", "user2");
		Message m2 = new Message("Test 2.", "user2", "user3");
		Message m3 = new Message("Test 3.", "user3", "user4");
		Message m4 = new Message("Test 4.", "user2", "user1");
		try {
			db.insertMessage(m1);
			db.insertMessage(m2);
			db.insertMessage(m3);
			db.insertMessage(m4);
			Messages m = db.getMessagesForConvo(user, "user2");
			assertEquals(m.get(0).getText(), m1.getText());
			assertEquals(m.get(1).getText(), m4.getText());
		} catch (SQLException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
		fail("SQLException thrown.");
		}
	}
	
	@Test
	/**
	 * Tests the getMessagesForSpy() method.
	 */
	public void testGetMessagesForSpy() {
		setUpMessageEnv();
		User user = new User("user1", "P4$$word", "user1");
		Message m1 = new Message("Test 1.", "user1", "user2");
		Message m2 = new Message("Test 2.", "user2", "user3");
		Message m3 = new Message("Test 3.", "user3", "user4");
		Message m4 = new Message("Test 4.", "user2", "user1");
		try {
			db.insertMessage(m1);
			db.insertMessage(m2);
			db.insertMessage(m3);
			db.insertMessage(m4);
			Messages m = db.getMessagesForSpy(user.getUserName(), "user2");
			assertEquals(m.get(0).getText(), m1.getText());
			assertEquals(m.get(1).getText(), m4.getText());
		} catch (SQLException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
		fail("SQLException thrown.");
		}
	}

	@Test
	/**
	 * Tests the setMessageRead() method.
	 */
	public void testSetMessageRead() {
		setUpMessageEnv();
		Message m = new Message("This message is a test.", "user1", "user2");
		m.setRead(false);
		try {
			db.insertMessage(m);
			Messages collection = db.getMessages();
			Message m2 = collection.get(0);
			assertEquals(m2.getIsRead(), false);
			db.setMessageRead(m2);
			collection = db.getMessages();
			m2 = collection.get(0);
			assertEquals(m2.getIsRead(), true);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			fail("SQLException thrown.");
		}
		
		tearDownMessageEnv();
	}
	
	/**
	 * Helper method to set up the environment for messaging.
	 */
	private void setUpMessageEnv() {
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
	private void tearDownMessageEnv() {
		db.clear();
		db.closeConnection();
	}

}
