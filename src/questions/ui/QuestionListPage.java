package questions.ui;

import databasePart1.DatabaseHelper;

import java.sql.SQLException;
import java.util.ArrayList;

import accounts.util.*;
import accounts.ui.AdminHomePage;
import accounts.ui.UserHomePage;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Separator;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import questions.util.*;

/********************************************************************
 * 
 * CREDIT: 	I used the following tutorials for JavaFX things:
 * 			
 * 			Scrollable list:
 * 		 	https://stackoverflow.com/questions/30105001/how-to-create-a-scrollable-panel-of-components-in-javafx
 * 
 * 			Hyperlinks:
 * 			https://stackoverflow.com/questions/38855779/how-to-add-a-actionlistener-to-a-label-in-javafx
 * 
 * 			Add items to top of page:
 * 			https://stackoverflow.com/questions/29603574/add-new-row-at-top-fxml-tableview
 * 
 * 			Checkbox documentation:
 * 			https://docs.oracle.com/javase/8/javafx/api/javafx/scene/control/CheckBox.html
 * 
 * 			Choicebox:
 * 			https://docs.oracle.com/javase/8/javafx/api/javafx/scene/control/CheckBox.html
 * 
 * 			TextArea:
 * 			https://docs.oracle.com/javase/8/javafx/api/javafx/scene/control/TextArea.html
 * 
 ********************************************************************/

public class QuestionListPage {
	
	private Questions parentQuestions; 	// The original (main) questions
	private Questions questions;		// The questions displayed on the page
	private final User user;
	private String pageTitle;
	private int listIndex = 0;
	private DatabaseHelper db;
	
	// Used for the initial page
    public QuestionListPage(DatabaseHelper db, Questions questions, User testUser) {
    	this.db = db;
        this.questions = questions;
        this.user = testUser;
        parentQuestions = questions;
        pageTitle = "Question List";
    }
    
    // Used to pull questions from the database
    public QuestionListPage(DatabaseHelper db, User testUser) {
    	this.db = db;
    	try { 
    		this.questions = db.getQuestionsAndAnswers();
    	} 
    	catch (SQLException e) {
    		questions = new Questions(); 
    		e.printStackTrace();
    	}
        this.user = testUser;
        parentQuestions = questions;
        pageTitle = "Question List";
    }
    
    // Used if a search is executed
    public QuestionListPage(DatabaseHelper db, Questions questions, Questions parentQuestions, User testUser) {
    	this.db = db;
        this.questions = questions;
        this.user = testUser;
        this.parentQuestions = parentQuestions;
        pageTitle = "Question List (Search Results)";
    }
    
    public void show(Stage primaryStage) {
    	
    	VBox layout = new VBox(5);
    	
    	// Create the scrollable pane
    	VBox content = new VBox(3);
        ScrollPane scroller = new ScrollPane(content);
        scroller.setPrefHeight(400);
	    layout.setStyle("-fx-alignment: center; -fx-padding: 20;");
	    
	    // Title box
	    HBox topBox = new HBox(3);
	    Label titleLabel = new Label(pageTitle + ":");
	    titleLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
	    titleLabel.setPrefWidth(580);
	    Button helpButton = new Button("Help");
	    Button refreshButton = new Button("Refresh");
	    Button acctButton = new Button("Account");
	    topBox.setStyle("-fx-alignment: center;");
	    topBox.getChildren().addAll(titleLabel, helpButton, refreshButton, acctButton);
	    
	    // Error label
	    Label errorLabel = new Label("");
	    errorLabel.setStyle("-fx-font-size: 12px;");
	    
	    // Add new questions
	    Separator bar = new Separator();
	    AnchorPane addQuestion = new AnchorPane();
	    addQuestion.setPrefSize(800,60);
	    Button addButton = new Button("Add");
	    TextField questionField = new TextField();
	    questionField.setPromptText("Enter Question Title");
        questionField.setPrefWidth(700);
        AnchorPane.setLeftAnchor(questionField, 5.0);
        AnchorPane.setTopAnchor(questionField, 3.0);
        AnchorPane.setTopAnchor(addButton, 3.0);
        AnchorPane.setRightAnchor(addButton, 5.0);
        TextArea questionBody = new TextArea();
        questionBody.setWrapText(true);
        questionBody.setPromptText("Enter Question Body");
        // Now the list of tags
        HBox tags = new HBox(5);
        Label tagLabel = new Label("Tags: ");
        CheckBox genBox = new CheckBox("General");
        CheckBox assignBox = new CheckBox("Assignment");
        CheckBox projBox = new CheckBox("Project");
        CheckBox structBox = new CheckBox("Course Structure");
        CheckBox lecBox = new CheckBox("Lecture");
        CheckBox socialBox = new CheckBox("Social");
        // the tags will be added separately under the questions
        tags.getChildren().addAll(tagLabel, genBox, assignBox, projBox, structBox, lecBox, socialBox);
        addQuestion.getChildren().addAll(questionField, addButton);
        
        // Search bar
        HBox search = new HBox(2);
        search.setStyle("-fx-alignment: center; -fx-padding: 2;");
        // Search type
        ArrayList<String> searchTypeL = new ArrayList<String>();
        searchTypeL.add("Tags");
        searchTypeL.add("Author");
        searchTypeL.add("Question");
        searchTypeL.add("Answered");
        searchTypeL.add("Unanswered");
        searchTypeL.add("Question Reviewed");
        searchTypeL.add("Answer Reviewed");
        searchTypeL.add("Answer Bookmarked");
        ChoiceBox<String> searchType = new ChoiceBox<String>(FXCollections.observableArrayList(searchTypeL));
        searchType.setValue("(Search Type)");
        searchType.setPrefWidth(120.0);
        // Tags (if "tag" type search)
        ArrayList<String> possibleTags = new ArrayList<String>();
        possibleTags.add("General");
        possibleTags.add("Assignment");
        possibleTags.add("Project");
        possibleTags.add("Course Structure");
        possibleTags.add("Lecture");
        possibleTags.add("Social");
        ChoiceBox<String> searchTags = new ChoiceBox<String>(FXCollections.observableArrayList(possibleTags));
        searchTags.setValue("(Tag)");
        searchTags.setPrefWidth(120.0);
        // Search bar (if "author" or "question" search)
        TextField searchBar = new TextField();
        searchBar.setPromptText("Enter Search Parameters (Author / Question / Minimum Reviewer Score)");
        searchBar.setPrefWidth(400.0);
        // Search button
        Button searchButton = new Button("Search");
        searchButton.setPrefWidth(120.0);
        search.getChildren().addAll(searchType, searchBar, searchTags, searchButton);
        
        // Add pre-existing questions. Iterates in reverse so recent = first
        for (int i = 0; i < questions.size(); i++) {
    		addToList(questions.get(i), content);
    	}
	    
        // ACTION LISTENERS
        
        // Add new questions
        addButton.setOnAction(e -> {
        	ArrayList<String> newTags = new ArrayList<String>();
        	// Cycle through and add the tags
        	if (genBox.isSelected()) { newTags.add("General"); }
        	if (assignBox.isSelected()) { newTags.add("Assignment"); }
        	if (projBox.isSelected()) { newTags.add("Project"); }
        	if (structBox.isSelected()) { newTags.add("Course Structure"); }
        	if (lecBox.isSelected()) { newTags.add("Lecture"); }
        	if (socialBox.isSelected()) { newTags.add("Social"); }
        	Question newQ = new Question(questionField.getText(), questionBody.getText(), user.getUserName(), newTags);
        	// Validate inputs
        	String questionError = QuestionValidator.validateQuestion(parentQuestions, newQ);
        	if ( questionError.equals("") ) {
        		// Begin by creating the new question object
        		questions.add(newQ);
        		parentQuestions.add(newQ);
        		// And add it to the database
        		try { db.insertQuestion(newQ); }
        		catch (SQLException e2) { e2.printStackTrace(); }
        		// Now, add it to the scrollable list
        		addToList(newQ, content);
        		// Clear the fields
        		questionField.setText("");
        		questionBody.setText("");
        		genBox.setSelected(false);
        		assignBox.setSelected(false);
        		projBox.setSelected(false);
        		structBox.setSelected(false);
        		socialBox.setSelected(false);
        		
        		// If all went well, inform the user!
	            errorLabel.setStyle("-fx-text-fill: green; -fx-font-size: 12px;");
	            errorLabel.setText("Question successfully posted!");
        	}
        	// Error handling
        	else {
        		errorLabel.setStyle("-fx-text-fill: red; -fx-font-size: 12px;");
        		errorLabel.setText(questionError);
        	}
        });
        
        // Execute a search
        searchButton.setOnAction(e -> {
        	String resultMsg = executeSearch(searchTags.getValue(), searchType.getValue(), searchBar.getText(), primaryStage);
        	errorLabel.setStyle("-fx-text-fill: red; -fx-font-size: 12px;");
        	errorLabel.setText(resultMsg);
        });
        
        // Refresh the page
        refreshButton.setOnAction(e -> {
        	new QuestionListPage(db, user).show(primaryStage);
        });
        
        // Get help
        helpButton.setOnAction(e -> {
        	new HelpDialogPage().show(new Stage());
        });
        
        acctButton.setOnAction(e -> {
        	if (user.getRole().equals("admin")) {
        		new AdminHomePage(db, user).show(primaryStage);
        	}
        	else {
        		new UserHomePage(db, user).show(primaryStage);
        	}
        });
        
        // Add elements to master layout
        layout.getChildren().addAll(topBox, search, scroller, bar, addQuestion, questionBody, tags, errorLabel);
	    Scene questionListScene = new Scene(layout, 800, 600);
        
	    // Set the scene to primary stage
	    primaryStage.setScene(questionListScene);
	    primaryStage.setTitle("HW2 - " + pageTitle);
    	primaryStage.show();
    }
    
    private void addToList(Question newQ, VBox content) {
    	// Now create the display elements
    	// Alternates between darker/lighter backgrounds for contrast
        AnchorPane question = new AnchorPane();
        if (listIndex % 2 == 0) {
        	question.setStyle("-fx-background: rgb(210, 210, 210); -fx-background-color: -fx-background;");
        }
        else {
        	question.setStyle("-fx-background: rgb(225, 225, 225); -fx-background-color: -fx-background;");
        }
        listIndex++;
        question.setPrefSize(756.65,50);
        Hyperlink questionText;
        
        // Ensure the new question's length isn't too much for the label
        if (newQ.getText().length() > 62) {
        	String shortenedText = newQ.getText().substring(0,62) + "...";
        	questionText = new Hyperlink(shortenedText);
        }
        else {
        	questionText = new Hyperlink(newQ.getText());
        }
        
        // Also indicate whether or not a question has been answered
        if (newQ.getNumAnswers() == 0) {
        	questionText.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
        }
        else {
        	questionText.setStyle("-fx-font-style: italic; -fx-font-size: 14px;");
        }
        
        // Button w/ action listener for removal
        Button button = new Button("Remove Question");
        button.setOnAction(a -> { 
        	try {
				db.removeQuestion(newQ);
				questions.remove(newQ);
				content.getChildren().remove(question);
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        });
        
        /* See the full question text and associated answers.
        * A design decision was made to put this in a pop up so the
        * user doesn't have to scroll all the way back down if this
        * doesn't meet their needs. They should even be able to open
        * multiple different questions! */
        questionText.setOnAction(a -> {
        	new QuestionPage(db, parentQuestions, newQ, user).show(new Stage());
        });
        
        // Element disablement logic
        if (user.getRole().equals("admin") || user.getRole().equals("instructor") || user.getUserName().equals(newQ.getAuthor())) {
        	question.getChildren().add(button);
        }
        Label usernameAndTags = new Label("\tPoster: " + newQ.getAuthor() + " | Tags: " + newQ.getTagsAsString());
        AnchorPane.setLeftAnchor(questionText, 5.0);
        AnchorPane.setLeftAnchor(usernameAndTags, 5.0);
        AnchorPane.setBottomAnchor(usernameAndTags, 5.0);
        AnchorPane.setRightAnchor(button, 5.0);
        AnchorPane.setTopAnchor(questionText, 5.0);
        AnchorPane.setTopAnchor(button, 5.0);
        question.getChildren().addAll(questionText, usernameAndTags);
        // Add to top of list
        content.getChildren().add(0, question);
    }
    
    // Handle the retrieval of subsets of the questions.
    private String executeSearch(String tags, String type, String searchBar, Stage primaryStage) {
    	switch (type) {
    		case "Tags":
    			if (!tags.equals("(Tag)")) {
    				Questions newPgQuestions = parentQuestions.getByTag(tags);
    				if (newPgQuestions.size() > 0) {
    					new QuestionListPage(db, newPgQuestions, parentQuestions, user).show(primaryStage);
    					return "";
    				}
    				else {
    					return "No results found for those search parameters.";
    				}
    			}
    			else {
    				return "Be sure to select a tag if you are searching by tag!";
    			}
    		case "Author":
    			if (!searchBar.equals("")) {
    				Questions newPgQuestions = parentQuestions.getByAuthor(searchBar);
    				if (newPgQuestions.size() > 0) {
    					new QuestionListPage(db, newPgQuestions, parentQuestions, user).show(primaryStage);
    					return "";
    				}
    				else {
    					return "No results found for those search parameters.";
    				}
    			}
    			else {
    				return "Be sure to enter an author if you are searching by author!";
    			}
    		case "Question":
    			if (!searchBar.equals("")) {
    				Questions newPgQuestions = parentQuestions.getByText(searchBar);
    				if (newPgQuestions.size() > 0) {
    					new QuestionListPage(db, newPgQuestions, parentQuestions, user).show(primaryStage);
    					return "";
    				}
    				else {
    					return "No results found for those search parameters. (Note: This type of search is not recommended as it requires an exact match)";
    				}
    			}
    			else {
    				return "Be sure to enter a question if you are searching by question!";
    			}
    		case "Unanswered":
    			Questions newPgQuestions = parentQuestions.getUnanswered();
    			if (newPgQuestions.size() > 0) {
					new QuestionListPage(db, newPgQuestions, parentQuestions, user).show(primaryStage);
					return "";
				}
				else {
					return "No results found for those search parameters.";
				}
    		case "Answered":
    			Questions newPgQuestions1 = parentQuestions.getAnswered(); // Got local variable issues with this one for some reason :/
    			if (newPgQuestions1.size() > 0) {
					new QuestionListPage(db, newPgQuestions1, parentQuestions, user).show(primaryStage);
					return "";
				}
				else {
					return "No results found for those search parameters.";
				} 
    		case "Question Reviewed":
    			int threshold = 50;
    			try {
    				threshold = Integer.parseInt(searchBar);
    			} catch (NumberFormatException e) {
    				return "Must enter an integer!";
    			}
    			Questions newPgQuestions2 = parentQuestions.getReviewedQuestions(threshold, user, db);
    			if (newPgQuestions2.size() > 0 && threshold <= 100 && threshold >= 0) {
					new QuestionListPage(db, newPgQuestions2, parentQuestions, user).show(primaryStage);
					return "";
				}
				else {
					return "No results found for those search parameters.";
				} 
    		case "Answer Reviewed":
    			int threshold1 = 50;
    			try {
    				threshold1 = Integer.parseInt(searchBar);
    			} catch (NumberFormatException e) {
    				return "Must enter an integer!";
    			}
    			Questions newPgQuestions3 = parentQuestions.getReviewedAnswers(threshold1, user, db);
    			if (newPgQuestions3.size() > 0 && threshold1 <= 100 && threshold1 >= 0) {
					new QuestionListPage(db, newPgQuestions3, parentQuestions, user).show(primaryStage);
					return "";
				}
				else {
					return "No results found for those search parameters.";
				} 
    		case "Answer Bookmarked":
    			Questions newPgQuestions4 = parentQuestions.getBookmarkedAnswers(user, db);
    			if (newPgQuestions4.size() > 0) {
					new QuestionListPage(db, newPgQuestions4, parentQuestions, user).show(primaryStage);
					return "";
				}
				else {
					return "No results found for those search parameters.";
				}
    		case "(Search Type)":
    			return "Be sure to enter a search type!";
    		default:
    			return "Something went wrong. (Uncaught break).";
    	}
    }
}