package messaging.util;

import java.time.*;
import java.time.format.DateTimeParseException;

import databasePart1.DatabaseHelper;

public class Message {
	String text;
	String sender;
	String recipient;
	boolean isRead;
	LocalDateTime time;
	int primaryKey;
	
	// Constructor for new message
	public Message(String text, String sender, String recipient) {
		primaryKey = DatabaseHelper.answerKey;
		DatabaseHelper.answerKey++;
		this.text = text;
		this.sender = sender;
		this.recipient = recipient;
		isRead = false;
		time = LocalDateTime.now();
	}
	
	// Constructor for imported answer
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
	public void setKey(int newKey) { primaryKey = newKey; }
	public void setText(String newText) { text = newText; }
	public void setSender(String newSender) { sender = newSender; }
	public void setRecipient(String newRecipient) { recipient = newRecipient; }
	public void setTime(LocalDateTime newTime) { time = newTime; }
	public void setRead(boolean newReadStatus) { isRead = newReadStatus; }
	
	// Getters
	public int getKey() { return primaryKey; }
	public String getText() 		{ return text; }
	public String getSender() { return sender; }
	public String getRecipient() { return recipient; }
	public LocalDateTime getTime() { return time; }
	public boolean getIsRead() { return isRead; }
	/* This method will be used a lot, so we're optimizing memory usage */
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