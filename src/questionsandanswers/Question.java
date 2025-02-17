package questionsandanswers;

import java.util.ArrayList;

public class Question {
	private String text;				// What is the question actually asking?
	private String body;				// More details of the question
	private String author;				// Username of the author
	private ArrayList<String> tags;		// Requires dynamic resizing
	private Answers answers;			// The list of answers for this question
	
	// This constructor makes a new question.
	public Question(String text, String questionBody, String author, ArrayList<String> tags) {
		this.text = text;
		this.body = questionBody;
		this.author = author;
		this.tags = tags;
		answers = new Answers();
	}
	
	// This constructor is used when importing questions from the database.
	public Question(String text, String questionBody, String author, String tagsCSV) {
		this.text = text;
		this.body = questionBody;
		this.author = author;
		this.tags = parseTagsCSV(tagsCSV);
		answers = new Answers();
	}
	
	// This constructor is used when importing other kinds of existing questions.
	public Question(String text, String body, String author, ArrayList<String> tags, Answers answers) {
		this.text = text;
		this.body = body;
		this.author = author;
		this.tags = tags;
		this.answers = answers;
	}
	
	// Adders
	public void addTag(String newTag) { tags.add(newTag); }
	public void addAnswer(Answer answer) { answers.add(answer) ; }
	
	// Setters
	public void setText(String newText) { text = newText; }
	public void setAuthor(String newAuthor) { author = newAuthor; }
	public void setBody(String newBody) { body = newBody; }
	public void setTags(ArrayList<String> newTags) { tags = newTags; }
	public void setAnswers(Answers newAnswers) { answers = newAnswers; }
	
	// Getters
	public String getText() { return text; }
	public String getAuthor() { return author; }
	public String getBody() { return body; }
	public ArrayList<String> getTags() { return tags; }
	public Answers getAnswers() { return answers; }
	public int getNumAnswers() { return answers.size(); }
	public String getTagsAsString() {
		String retStr = "";
		int numTags = 0;
		if (tags.isEmpty()) {
			retStr = "No tags are associated with this question";
		}
		else {
			for (int i = 0; i < tags.size(); i++) {
				if (numTags == 0) {
					retStr += tags.get(i);
					numTags++;
				}
				else {
					retStr += ", " + tags.get(i);
					numTags++;
				}
			}
		}
		return retStr;
	}
	// Slight variation on above for DB storage
	public String getTagsCSV() {
		String retStr = "";
		int numTags = 0;
		if (tags.isEmpty()) {
			retStr = "";
		}
		else {
			for (int i = 0; i < tags.size(); i++) {
				if (numTags == 0) {
					retStr += tags.get(i);
					numTags++;
				}
				else {
					retStr += "," + tags.get(i);
					numTags++;
				}
			}
		}
		return retStr;
	}
	
	private ArrayList<String> parseTagsCSV(String tagsCSV) {
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