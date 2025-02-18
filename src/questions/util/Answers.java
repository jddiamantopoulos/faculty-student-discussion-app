package questions.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class Answers extends ArrayList<Answer> {
	
	// Placeholder for now, inherits all necessary methods
	
	// Fairly inefficient [O(n^2)].
	public void mergeAnswers(Answers a) {
		for (int i = 0; i < a.size(); i++) {
			for (int j = 0; j < size(); j++)
			if (!get(j).getText().contains(a.get(i).getText())) {
				add(a.get(i));
			}
		}
	}
	
	// Sort by numLikes
	// guide: https://www.tpointtech.com/how-to-sort-arraylist-in-java
	// Implements mergesort. Runtime O(nlog(n)), very high memory usage.
	public void sortAnswers() {
		Collections.sort(this, new Comparator<Answer>() {  
            @Override  
            public int compare(Answer a1, Answer a2) {  
            	if (a1.getMarkedHelpful().size() > a2.getMarkedHelpful().size()) {
            		return 1;
            	}
            	else if (a1.getMarkedHelpful().size() < a2.getMarkedHelpful().size()) {
            		return -1;
            	}
            	else {
            		return 0;
            	}
            }  
        });  
	}
	
}