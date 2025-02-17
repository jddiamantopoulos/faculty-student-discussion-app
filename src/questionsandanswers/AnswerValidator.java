package questionsandanswers;

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
	
	// This method is VERY inefficient. Will cause significant wait time when posting an answer
	// for large answer sets. If a better version of what's happening here exists, we need
	// to replace it eventually.
	public static boolean answerIsDuplicate(Question q, Answer a) {
		for (int i = 0; i < q.getAnswers().size(); i++) {
			if (q.getAnswers().get(i).getText().equals(a.getText())) {
				return true;
			}
		}
		return false;
	}
}