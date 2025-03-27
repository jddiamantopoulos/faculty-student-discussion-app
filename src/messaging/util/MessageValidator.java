package messaging.util;

public class MessageValidator {
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