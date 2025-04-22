package test.database;

import static org.junit.Assert.*;

import java.sql.SQLException;

import org.junit.Test;

import databasePart1.DatabaseHelper;
import taskmessaging.util.TaskMessage;
import taskmessaging.util.TaskMessages;

/**
 * Tests the DatabaseHelper's task message methods.
 */
public class TaskMessageTests {
	DatabaseHelper db = new DatabaseHelper();
	
	@Test
	/**
	 * Tests the getTaskMessages() and insertTaskMessage() methods.
	 */
	public void testGetAndInsertTaskMessages() {
		setUpTaskMessageEnv();
		TaskMessage tm = new TaskMessage("request", "requester", "sender", "text");
		try {
			db.insertTaskMessage(tm);
			TaskMessages collection = db.getTaskMessages();
			TaskMessage tm2 = collection.get(0);
			assertEquals(tm.getText(), tm2.getText());
			assertEquals(tm.getRequest(), tm2.getRequest());
			assertEquals(tm.getRequester(), tm2.getRequester());
			assertEquals(tm.getSender(), tm2.getSender());
			assertEquals(tm.getKey(), tm2.getKey());
			assertEquals(tm.getTimeAsString(), tm2.getTimeAsString());
			assertEquals(tm.getRequestIsOpen(), tm2.getRequestIsOpen());
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			fail("SQLException thrown.");
		}
		
		tearDownTaskMessageEnv();
	}
	
	@Test
	/**
	 * Tests the getRequesterByRequest() method.
	 */
	public void testGetRequesterByRequest() {
		setUpTaskMessageEnv();
		TaskMessage tm = new TaskMessage("request", "requester", "sender", "text");
		try {
			db.insertTaskMessage(tm);
			assertEquals(db.getRequesterByRequest("request"), "requester");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			fail("SQLException thrown.");
		}
		
		tearDownTaskMessageEnv();
	}
	
	@Test
	/**
	 * Tests the getTaskMessagesForConvo() method.
	 */
	public void testGetTaskMessagesForConvo() {
		setUpTaskMessageEnv();
		TaskMessage tm1 = new TaskMessage("request", "requester", "sender1", "text1");
		TaskMessage tm2 = new TaskMessage("request", "requester", "sender2", "text2");
		TaskMessage tm3 = new TaskMessage("request", "requester", "sender3", "text3");
		try {
			db.insertTaskMessage(tm1);
			db.insertTaskMessage(tm2);
			db.insertTaskMessage(tm3);
			TaskMessages taskMessages = db.getTaskMessagesForConvo("request");
			assertEquals(taskMessages.get(0).getText(), tm1.getText());
			assertEquals(taskMessages.get(1).getText(), tm2.getText());
			assertEquals(taskMessages.get(2).getText(), tm3.getText());
			assertEquals(taskMessages.get(0).getSender(), tm1.getSender());
			assertEquals(taskMessages.get(1).getSender(), tm2.getSender());
			assertEquals(taskMessages.get(2).getSender(), tm3.getSender());
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			fail("SQLException thrown.");
		}
		
		tearDownTaskMessageEnv();
	}
	
	@Test
	/**
	 * Tests the getTaskMessageRequestOpenStatus() method.
	 */
	public void testGetTaskMessageRequestOpenStatus() {
		setUpTaskMessageEnv();
		TaskMessage tm1 = new TaskMessage("request", "requester", "sender1", "text1");
		TaskMessage tm2 = new TaskMessage("request", "requester", "sender2", "text2");
		try {
			db.insertTaskMessage(tm1);
			db.insertTaskMessage(tm2);
			boolean requestIsOpen = db.getTaskMessageRequestOpenStatus("request");
			assertEquals(requestIsOpen, tm1.getRequestIsOpen());
			assertEquals(requestIsOpen, tm2.getRequestIsOpen());
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			fail("SQLException thrown.");
		}
		
		tearDownTaskMessageEnv();
	}
	
	@Test
	/**
	 * Tests the setTaskMessageRequestClosed() method.
	 */
	public void testSetTaskMessageRequestClosed() {
		setUpTaskMessageEnv();
		TaskMessage tm1 = new TaskMessage("request", "requester", "sender1", "text1");
		TaskMessage tm2 = new TaskMessage("request", "requester", "sender2", "text2");
		try {
			db.insertTaskMessage(tm1);
			db.insertTaskMessage(tm2);
			db.setTaskMessageRequestClosed("request");
			assertEquals(db.getTaskMessageRequestOpenStatus("request"), false);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			fail("SQLException thrown.");
		}
		
		tearDownTaskMessageEnv();
	}
	
	@Test
	/**
	 * Tests the setTaskMessageRequestOpen() method.
	 */
	public void testSetTaskMessageRequestOpen() {
		setUpTaskMessageEnv();
		TaskMessage tm1 = new TaskMessage("request", "requester", "sender1", "text1");
		TaskMessage tm2 = new TaskMessage("request", "requester", "sender2", "text2");
		try {
			db.insertTaskMessage(tm1);
			db.insertTaskMessage(tm2);
			db.setTaskMessageRequestClosed("request");
			db.setTaskMessageRequestOpen("request");
			assertEquals(db.getTaskMessageRequestOpenStatus("request"), true);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			fail("SQLException thrown.");
		}
		
		tearDownTaskMessageEnv();
	}
	
	/**
	 * Helper method to set up the environment for task messaging.
	 */
	private void setUpTaskMessageEnv() {
		try {
			db.connectToDatabase();
			db.clear();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Helper method to clear the environment for task messaging.
	 */
	private void tearDownTaskMessageEnv() {
		db.clear();
		db.closeConnection();
	}
}
