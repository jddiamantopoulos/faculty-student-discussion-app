package questionsandanswers;

import java.util.ArrayList;

public class Answer {
	String text;
	String author;
	ArrayList<String> markedAsHelpful; // In TP2, this should be ArrayList<User>
	
	// Constructor for new answer
	public Answer(String text, String author) {
		this.text = text;
		this.author = author;
		markedAsHelpful = new ArrayList<String>();
	}
	
	// Constructor for imported answer
	public Answer(String text, String author, ArrayList<String> markedAsHelpful) {
		this.text = text;
		this.author = author;
		this.markedAsHelpful = markedAsHelpful;
	}
	
	// Constructor for imported answer (CSV)
	public Answer(String text, String author, String votesCSV) {
		this.text = text;
		this.author = author;
		this.markedAsHelpful = parseCSV(votesCSV);
		}
	
	public boolean upvote(String username) {
		if (!markedAsHelpful.contains(username)) {
			markedAsHelpful.add(username); 
			return true;
		}
		else {
			return false;
		}
	}
	
	public void setText(String newText) { text = newText; }
	public void setAuthor(String newAuthorUsername) { author = newAuthorUsername; }
	public void setMarkedHelpful(ArrayList<String> markedHelpful) { markedAsHelpful = markedHelpful; }
	
	public String getText() 		{ return text; }
	public String getAuthor() { return author; }
	public ArrayList<String> getMarkedHelpful() { return markedAsHelpful; }
	public String getHelpfulAsString(int numChars) {
		String retstr = "";
		int numLikes = 0;
		if (markedAsHelpful.isEmpty()) {
			return "0";
		}
		for (int i = 0; i < markedAsHelpful.size(); i++) {
			if (retstr.length() == 0) {
				retstr += markedAsHelpful.get(i);
				numLikes++;
			}
			else {
				retstr += ", " + markedAsHelpful.get(i);
				numLikes++;
			}
		}
		if (retstr.length() >= numChars) {
			return numLikes + " (" + retstr.substring(0, numChars - 3) + "...)";
		}
		return numLikes + " (" + retstr + ")";
	}
	
	// Copied over from Question.java
	public String getLikesCSV() {
		String retStr = "";
		int numTags = 0;
		if (markedAsHelpful.isEmpty()) {
			retStr = "";
		}
		else {
			for (int i = 0; i < markedAsHelpful.size(); i++) {
				if (numTags == 0) {
					retStr += markedAsHelpful.get(i);
					numTags++;
				}
				else {
					retStr += "," + markedAsHelpful.get(i);
					numTags++;
				}
			}
		}
		return retStr;
	}
	
	// Copied over from Question.java
	private ArrayList<String> parseCSV(String tagsCSV) {
		ArrayList<String> retList = new ArrayList<String>();
		String str = "";
		for (int i = 0; i < tagsCSV.length(); i++) {
			if (tagsCSV.charAt(i) == ',') {
				retList.add(str);
				str = "";
			}
			else {
				str += tagsCSV.charAt(i);
			}
		}
		if (!str.equals("")) {
			retList.add(str);
		}
		return retList;
	}
	
}