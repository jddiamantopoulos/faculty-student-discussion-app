package questions.util;

public class AnswerValidator {
	public static String validateAnswer(Question q, Answer a) {
		boolean isDupe = answerIsDuplicate(q, a);
		if (a.getText().length() > 0 &&
			a.getText().length() <= 2000 &&
			!isDupe) {
			
			return "";
		}
		else {
			String errorText = "";
    		if (a.getText().length() == 0) {
    			errorText += "You cannot post an empty answer! ";
    		}
    		if (a.getText().length() > 2000) {
    			errorText += "Your answer is too long! Please reduce the length to 2000 characters or less. ";
    		}
    		if (isDupe) {
    			errorText += "Your answer already exists! Please provide a new insight or simply \"like\" the original.";
    		}
    		return errorText;
		}
	}
	
	// Checks if an answer already exists for a given question.
	public static boolean answerIsDuplicate(Question q, Answer a) {
		for (int i = 0; i < q.getAnswers().size(); i++) {
			if (q.getAnswers().get(i).getText().equals(a.getText())) {
				return true;
			}
		}
		return false;
	}
}