package messaging.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import questions.util.Answer;

/**
 * A collection of messages. Inherits from ArrayList&lt;Message&gt;
 */
public class Messages extends ArrayList<Message> {
	
	/**
	 * Sorts the list of messages by send time. Utilizes MergeSort O(nlogn), does not sort in place.
	 */
	public void sortRecent() {
		Collections.sort(this, new Comparator<Message>() {  
            @Override  
            public int compare(Message m1, Message m2) {  
            	return m1.getTime().compareTo(m2.getTime());
            }
        });
	}
	
	/**
	 * Filters out all messages not sent in a certain conversation. Filters in-place.
	 * 
	 * @param userName1 The first username.
	 * @param userName2 The second username.
	 */
	public void filter(String userName1, String userName2) {
		for (int i = 0; i < size(); i++) {
			if ( !(get(i).getSender().equals(userName1) && get(i).getRecipient().equals(userName2)) 
					&& !(get(i).getSender().equals(userName2) && get(i).getRecipient().equals(userName1) ) ) {
				remove(get(i));
			}
		}
	}
	
}