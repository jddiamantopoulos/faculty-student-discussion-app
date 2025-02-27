package questions.util;

public class QuestionValidator {
	public static String validateQuestion(Questions parentQuestions, Question q) {
		boolean isDupe = questionIsDuplicate(parentQuestions, q);
		if (q.getText().length() > 0 &&
			q.getText().length() <= 255 &&
			!q.getTags().isEmpty() &&
			q.getBody().length() <= 2000 &&
			!isDupe) {
			
			return "";
		}
		else {
			String errorText = "";
    		if (q.getTags().isEmpty()) {
    			errorText += "You must select at least one tag! ";
    		}
    		if (q.getText().length() == 0) {
    			errorText += "You cannot post an empty question! ";
    		}
    		if (q.getText().length() > 255) {
    			errorText += "Your question title is too long! Please reduce the length to 255 characters or less. ";
    		}
    		if (q.getBody().length() > 2000) {
    			errorText += "Your question's body is too long! Please reduce the length to 2000 characters or less. ";
    		}
    		if (isDupe) {
    			errorText += "Your question already exists!";
    		}
    		return errorText;
		}
	}
	
	// This method is somewhat inefficient. Will cause significant wait time when posting a question
	// for large question sets.
	public static boolean questionIsDuplicate(Questions parentQuestions, Question q) {
		for (int i = 0; i < parentQuestions.size(); i++) {
			if (parentQuestions.get(i).getText().equals(q.getText())) {
				return true;
			}
		}
		return false;
	}
}