package taskmessaging.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * A collection of task messages. Inherits from ArrayList&lt;TaskMessage&gt;
 */
public class TaskMessages extends ArrayList<TaskMessage> {
	
	/**
	 * Sorts the list of task messages by send time. Utilizes MergeSort O(nlogn), does not sort in place.
	 */
	public void sortRecent() {
		Collections.sort(this, new Comparator<TaskMessage>() {  
            @Override  
            public int compare(TaskMessage tm1, TaskMessage tm2) {  
            	return tm1.getTime().compareTo(tm2.getTime());
            }
        });
	}
	
	/**
	 * Filters out all task messages not sent in a certain conversation. Filters in-place.
	 * 
	 * @param request The task message request.
	 */
	public void filter(String request) {
		for (int i = 0; i < size(); i++) {
			if ( !(get(i).getRequest().equals(request)) ) {
				remove(get(i));
			}
		}
	}
	
}
