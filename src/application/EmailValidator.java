package application;

public class EmailValidator {
	// Determine if the email passes basic validation
	public static String validateEmail(String input) {
		int currIndex = 0;
		int state = 0;
		String errorMessage = "";
		
		while (currIndex < input.length()) {
			switch (state) {
			
			// Local component
			case 0:
				if (isAlphaNumeric(input.charAt(currIndex)) || isSpChar(input.charAt(currIndex))) {
					currIndex++;
				}
				else if (input.charAt(currIndex) == '@') {
					state = 1;
					currIndex++;
				}
				else {
					errorMessage += "Invalid character in local component (must be alphanumeric or a special character).";
					state = 5;
				}
				break;
			// SLD component
			case 1:
				if (isAlphaNumeric(input.charAt(currIndex))) {
					currIndex++;
				}
				else if (input.charAt(currIndex) == '.') {
					state = 2;
					currIndex++;
				}
				else {
					errorMessage += "Invalid character in SLD component (must be alphanumeric).";
					state = 5;
				}
				break;
			// TLD component
			case 2:
				if (isAlphaNumeric(input.charAt(currIndex))) {
					if (currIndex == input.length() - 1) {
						state = 3;
					}
					else {
						currIndex++;
					}
				}
				else {
					state = 5;
				}
				break;
			default:
				return errorMessage;
			}
		}
		if (state == 3) {
			return "";
		}
		else {
			return "There was a problem validating this email. Please report the issue.";
		}
	}
	
	private static boolean isSpChar(char currChar) {
		if (currChar == '.' || currChar == '+' ||
				 currChar == '-' || currChar == '_' ||
				 currChar == '!' || currChar == '?') {
			return true;
		}
		else {
			return false;
		}
	}
		
	private static boolean isAlphaNumeric(char currChar) {
		if ((currChar >= 'A' && currChar <= 'Z') ||
				(currChar >= 'a' && currChar <= 'z') ||
				(currChar >= '0' && currChar <= '9')) {
			return true;
		}
		else {
			return false;
		}
	}
	 
}