package messaging.util;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import databasePart1.DatabaseHelper;

/**
 * Objects of this class represent one message between two users.
 */
public class Message {
	String text;
	String sender;
	String recipient;
	boolean isRead;
	LocalDateTime time;
	int primaryKey;
	
	/**
	 * This is the constructor that should be used for a new message.
	 * 
	 * @param text The message's text.
	 * @param sender The username of the sender.
	 * @param recipient The username of the recipient.
	 */
	public Message(String text, String sender, String recipient) {
		DatabaseHelper.answerKey++;
		primaryKey = DatabaseHelper.answerKey;
		this.text = text;
		this.sender = sender;
		this.recipient = recipient;
		isRead = false;
		time = LocalDateTime.now();
	}
	
	/**
	 * This is the constructor used to retrieve messages from the database.
	 * <p>
	 * Not recommended for use outside of database classes.
	 * 
	 * @param primaryKey The integer key used by the database.
	 * @param text The message text.
	 * @param sender The username of the sender.
	 * @param recipient The username of the recipient.
	 * @param isRead Has the message been read by the recipient?
	 * @param timeAndDate What time was the message sent?
	 */
	public Message(int primaryKey, String text, String sender, String recipient, boolean isRead, String timeAndDate) {
		this.primaryKey = primaryKey;
		this.text = text;
		this.sender = sender;
		this.recipient = recipient;
		this.isRead = isRead;
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
	 * Set the message text to a new value.
	 * @param newText The message text.
	 */
	public void setText(String newText) { text = newText; }
	/**
	 * Set the username of the sender.
	 * @param newSender The username of the sender.
	 */
	public void setSender(String newSender) { sender = newSender; }
	/**
	 * Set the username of the recipient.
	 * @param newRecipient The username of the recipient.
	 */
	public void setRecipient(String newRecipient) { recipient = newRecipient; }
	/**
	 * Set the time the message was sent (used for sorting)
	 * @param newTime The time the message was sent.
	 */
	public void setTime(LocalDateTime newTime) { time = newTime; }
	/**
	 * Set whether the messge has been read or not.
	 * @param newReadStatus Has the message been read?
	 */
	public void setRead(boolean newReadStatus) { isRead = newReadStatus; }
	
	// Getters
	
	/**
	 * Get the primary key.
	 * @return Primary key.
	 */
	public int getKey() { return primaryKey; }
	/**
	 * Get the message text.
	 * @return Message text.
	 */
	public String getText() 		{ return text; }
	/**
	 * Get the username of the sender.
	 * @return Sender's username.
	 */
	public String getSender() { return sender; }
	/**
	 * Get the username of the recipient.
	 * @return Recipient's username.
	 */
	public String getRecipient() { return recipient; }
	/**
	 * Get the time the message was sent.
	 * @return Message send time.
	 */
	public LocalDateTime getTime() { return time; }
	/**
	 * Get the time the message was sent as the LocalDateTime string.
	 * @return A string that can be converted to LocalDateTime.
	 */
	public String getTimeAsString() { 
		DateTimeFormatter noMS = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
		return time.format(noMS);
	}
	/**
	 * Get whether the message has been read.
	 * @return Read status.
	 */
	public boolean getIsRead() { return isRead; }
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