package taskmessaging.util;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import databasePart1.DatabaseHelper;

/**
 * Objects of this class represent one admin task message.
 */
public class TaskMessage {
	String request;
	String requester;
	String sender;
	String text;
	boolean requestIsOpen;
	LocalDateTime time;
	int primaryKey;
	
	/**
	 * This is the constructor that should be used for a new task message.
	 * 
	 * @param request The request the task message is for.
	 * @param requester The username of the requester for this task message's request.
	 * @param sender The task message's sender.
	 * @param text The task message's text.
	 */
	public TaskMessage(String request, String requester, String sender, String text) {
		DatabaseHelper.taskMessageKey++;
		primaryKey = DatabaseHelper.taskMessageKey;
		this.request = request;
		this.requester = requester;
		this.sender = sender;
		this.text = text;
		requestIsOpen = true;
		time = LocalDateTime.now();
	}
	
	/**
	 * This is the constructor used to retrieve task messages from the database.
	 * <p>
	 * Not recommended for use outside of database classes.
	 * 
	 * @param primaryKey The integer key used by the database.
	 * @param request The request the task message is for.
	 * @param requester The username of the requester for this task message's request.
	 * @param sender The username of the sender for this task message.
	 * @param text The task message's text.
	 * @param requestIsOpen Is the request open or closed by admin(s)?
	 * @param timeAndDate What time was the message sent?
	 */
	public TaskMessage(int primaryKey, String request, String requester, String sender, String text, boolean requestIsOpen, String timeAndDate) {
		this.primaryKey = primaryKey;
		this.request = request;
		this.requester = requester;
		this.sender = sender;
		this.text = text;
		this.requestIsOpen = requestIsOpen;
		try {
			time = LocalDateTime.parse(timeAndDate);
		} catch (DateTimeParseException e) {
			/* To handle the error and continue, we'll set the date to the Unix epoch */
			e.printStackTrace();
			Instant epoch = Instant.ofEpochSecond(0);
			time = LocalDateTime.ofInstant(epoch, ZoneOffset.UTC); /* Epoch is in UTC timezone */
		}
	}
	
	// Setters
	/**
	 * Set the primary key to a new value.
	 * @param newKey The new primary key.
	 */
	public void setKey(int newKey) { primaryKey = newKey; }
	/**
	 * Set the request to a new value.
	 * @param newRequest The new request.
	 */
	public void setRequest(String newRequest) { request = newRequest; }
	/**
	 * Set the requester to a new value.
	 * @param newRequester The new requester.
	 */
	public void setRequester(String newRequester) { requester = newRequester; }
	/**
	 * Set the sender to a new value.
	 * @param newSender The new sender.
	 */
	public void setSender(String newSender) { sender = newSender; }
	/**
	 * Set the task message text to a new value.
	 * @param newText The new task message text.
	 */
	public void setText(String newText) { text = newText; }
	/**
	 * Set the time the task message was sent (used for sorting)
	 * @param newTime The time the task message was sent.
	 */
	public void setTime(LocalDateTime newTime) { time = newTime; }
	/**
	 * Set whether the task message request is open or not.
	 * @param newRequestIsOpenStatus Is the task message request open?
	 */
	public void setRequestIsOpen(boolean newRequestIsOpenStatus) { requestIsOpen = newRequestIsOpenStatus; }
	
	// Getters
	
	/**
	 * Get the primary key.
	 * @return Primary key.
	 */
	public int getKey() { return primaryKey; }
	/**
	 * Get the task message text.
	 * @return Task message text.
	 */
	public String getText() 		{ return text; }
	/**
	 * Get the request of the task message.
	 * @return Task message's request.
	 */
	public String getRequest() { return request; }
	/**
	 * Get the requester of the task message.
	 * @return Task message's requester.
	 */
	public String getRequester() { return requester; }
	/**
	 * Get the sender of the task message.
	 * @return Task message's sender.
	 */
	public String getSender() { return sender; }
	/**
	 * Get the time the task message was sent.
	 * @return Task message send time.
	 */
	public LocalDateTime getTime() { return time; }
	/**
	 * Get the time the task message was sent as the LocalDateTime string.
	 * @return A string that can be converted to LocalDateTime.
	 */
	public String getTimeAsString() { 
		DateTimeFormatter noMS = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
		return time.format(noMS);
	}
	/**
	 * Get whether the task message request is open.
	 * @return Request open status.
	 */
	public boolean getRequestIsOpen() { return requestIsOpen; }
	/**
	 * Get an easier to read string representing local date and time.
	 * @return A string in the format dd/mm/yyyy hh::mm::ss
	 */
	public String getTimeFormatted() {
		short year = (short)time.getYear();
		byte month = (byte)time.getMonthValue();
		byte day = (byte)time.getDayOfMonth();
		byte hour = (byte)time.getHour();
		byte minute = (byte)time.getMinute();
		byte second = (byte)time.getSecond();
		return String.format("%d/%d/%d %d:%d:%d", day, month, year, hour, minute, second);
	}
	
}
