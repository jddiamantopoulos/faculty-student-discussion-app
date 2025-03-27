package messaging.util;

/**
 * An input validation class for messages. Largely a placeholder, but can be updated
 * to prevent SQL injection.
 */
public class MessageValidator {
	
	/**
	 * Validates a message.
	 * 
	 * @param messageText The text of the message.
	 * @return Empty if no error, else returns an error message.
	 */
	public static String validateMessage(String messageText) {
		if (messageText.length() > 0 &&
				messageText.length() <= 500) {
			return "";
		}
		else {
			String errorText = "";
    		if (messageText.length() == 0) {
    			errorText += "You cannot post an empty message! ";
    		}
    		if (messageText.length() > 500) {
    			errorText += "Your message is too long! Please reduce the length to 500 characters or less. ";
    		}
    		return errorText;
		}
	}
	
}