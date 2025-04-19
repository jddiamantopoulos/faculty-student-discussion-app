package taskmessaging.util;

/**
 * An input validation class for task messages. Largely a placeholder, but can be updated
 * to prevent SQL injection.
 */
public class TaskMessageValidator {
	
	/**
	 * Validates a task message.
	 * 
	 * @param taskMessageText The text of the task message.
	 * @return Empty if no error, else returns an error message.
	 */
	public static String validateTaskMessage(String taskMessageText) {
		if (taskMessageText.length() > 0 &&
				taskMessageText.length() <= 500) {
			return "";
		}
		else {
			String errorText = "";
    		if (taskMessageText.length() == 0) {
    			errorText += "You cannot post an empty task request message! ";
    		}
    		if (taskMessageText.length() > 500) {
    			errorText += "Your task request message is too long! Please reduce the length to 500 characters or less. ";
    		}
    		return errorText;
		}
	}
	
}
