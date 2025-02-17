package questionsandanswers;

import java.util.ArrayList;

public class Questions extends ArrayList<Question> {
	
	/* Most of the functionality already exists in ArrayLists, and
	*  it was tempting to just use one without creating a whole new
	*  class. But eventually I got to the part where I needed these
	*  methods, and I'm now very glad that I took the time to extend
	*  the arraylists.
	*/
	
	public Questions getByTag(String tag) {
		Questions returned = new Questions();
		for (int i = 0; i < size(); i++) {
			if ( get(i).getTags().contains(tag) ) {
				returned.add(get(i));
			}
		}
		return returned;
	}
	
	public Questions getByAuthor(String author) {
		Questions returned = new Questions();
		for (int i = 0; i < size(); i++) {
			if ( get(i).getAuthor().equals(author) ) {
				returned.add(get(i));
			}
		}
		return returned;
	}
	
	// Could also be described as "getByQuestion()"
	public Questions getByText(String text) {
		Questions returned = new Questions();
		for (int i = 0; i < size(); i++) {
			if ( get(i).getText().equals(text) ) {
				returned.add(get(i));
			}
		}
		return returned;
	}
	
	public Questions getUnanswered() {
		Questions returned = new Questions();
		for (int i = 0; i < size(); i++) {
			if ( get(i).getNumAnswers() == 0 ) {
				returned.add(get(i));
			}
		}
		return returned;
	}
	
	public Questions getAnswered() {
		Questions returned = new Questions();
		for (int i = 0; i < size(); i++) {
			if ( get(i).getNumAnswers() > 0 ) {
				returned.add(get(i));
			}
		}
		return returned;
	}
}