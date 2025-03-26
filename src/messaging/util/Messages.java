package messaging.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import questions.util.Answer;

public class Messages extends ArrayList<Message> {
	
	public void sortRecent() {
		Collections.sort(this, new Comparator<Message>() {  
            @Override  
            public int compare(Message m1, Message m2) {  
            	return m1.getTime().compareTo(m2.getTime());
            }
        });
	}
	
}