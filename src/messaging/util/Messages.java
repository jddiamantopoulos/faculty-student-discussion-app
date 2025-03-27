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
	
	/* THIS IS IN PLACE */
	public void filter(String userName1, String userName2) {
		for (int i = 0; i < size(); i++) {
			if ( !(get(i).getSender().equals(userName1) && get(i).getRecipient().equals(userName2)) 
					&& !(get(i).getSender().equals(userName2) && get(i).getRecipient().equals(userName1) ) ) {
				remove(get(i));
			}
		}
	}
	
}