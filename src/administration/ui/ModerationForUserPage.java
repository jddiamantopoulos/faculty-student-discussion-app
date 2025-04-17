package administration.ui;

import databasePart1.DatabaseHelper;

import java.sql.SQLException;
import java.util.ArrayList;

import accounts.util.*;
import common.ui.DialogPage;
import common.util.DialogTypes;
import common.util.DialogReturns;
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
import messaging.util.*;
import questions.util.Answer;
import questions.util.Answers;
import questions.util.Question;
import questions.util.Questions;
import questions.util.Review;

/**
 * This page displays the activity of a user.
 */
public class ModerationForUserPage {
	
	private final User user;
	private String pageTitle;
	private DatabaseHelper db;
	private String requestUser;
	
	/**
	 * Constructs a new ModerationForUserPage.
	 * 
	 * @param db The application's DatabaseHelper instance
	 * @param user The current user
	 * @param requestUser The username of the requested user
	 */
    public ModerationForUserPage(DatabaseHelper db, User user, String requestUser) {
    	this.db = db;
    	this.user = user;
        this.requestUser = requestUser;
        pageTitle = "Viewing Moderation Pane for " + requestUser;
    }
    
    /**
     * Displays the ModerationForUserPage on a new stage.
     */
    public void show() {
    	Stage conversationStage = new Stage();

    	VBox layout = new VBox(5);
    	
    	// Create the scrollable pane
    	VBox content = new VBox(3);
        ScrollPane scroller = new ScrollPane(content);
        scroller.setPrefHeight(550);
	    layout.setStyle("-fx-alignment: center; -fx-padding: 20;");
	    
	    // Error label
	    Label errorLabel = new Label("");
	    errorLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: red");
	    
	    // Create a search bar
	    HBox search = new HBox(3);
	    // Display the list of options in a ChoiceBox (cloned from WelcomeLoginPage)
	    ArrayList<String> searchTypes = new ArrayList<String>();
	    searchTypes.add("All");
	    searchTypes.add("Questions");
	    searchTypes.add("Answers");
	    searchTypes.add("Reviews");
	    searchTypes.add("Messages");
	    ChoiceBox<String> choice = new ChoiceBox<String>(FXCollections.observableArrayList(searchTypes));
	    choice.setValue("All");
	    Button executeSearch = new Button("Filter");
	    search.setStyle("-fx-alignment: center;");
	    search.getChildren().addAll(choice, executeSearch);
	    
	    executeSearch.setOnAction(e -> {
	    	executeFilter(choice.getValue().toString(), content);
	    });
        
        // Get the questions and answers
        Questions q;
        Answers as;
        Messages ms;
        ArrayList<Review> rs;
		try {
			q = db.getQuestionsByAuthor(requestUser);
			as = db.getAnswersByAuthor(requestUser);
			ms = db.getMessagesByAuthor(requestUser);
			rs = db.getReviewsByAuthor(requestUser);
		} catch (SQLException e) {
			e.printStackTrace();
			q = new Questions();
			as = new Answers();
			ms = new Messages();
			rs = new ArrayList<Review>();
		}
        for (int i = 0; i < q.size(); i++) {
        	addToListQ(q.get(i), content);
        }
        for (int i = 0; i < as.size(); i++) {
        	addToListA(as.get(i), content);
        }
        if (!db.getUserRole(requestUser).equals("admin")) {
		    for (int i = 0; i < ms.size(); i++) {
		    	addToListMsg(ms.get(i), content);
		    }
        }
        if (!db.getUserRole(requestUser).equals("user")) {
	        for (int i = 0; i < rs.size(); i++) {
	        	addToListR(rs.get(i), content);
	        }
        }
	    
        
        // Add elements to master layout
        scroller.setVvalue(1.0); /* https://stackoverflow.com/questions/20333396/javafx-make-scrollpane-scroll-automatically */
        layout.getChildren().addAll(search, errorLabel, scroller);
	    Scene messagesListScene = new Scene(layout, 700, 700);
        
	    // Set the scene to secondary stage
	    conversationStage.setScene(messagesListScene);
	    conversationStage.setTitle("Q&A - " + pageTitle);
	    conversationStage.show();
    }
    
    /**
     * Adds a question to the content VBox that is used in the scrollable pane.
     * 
     * @param q The question to be added.
     * @param content The VBox held in the scrollable pane.
     */
    private void addToListQ(Question q, VBox content) {
        HBox msgBox = new HBox(5);
        VBox textBox = new VBox(2);
        msgBox.setPrefWidth(650);
        msgBox.setStyle("-fx-background: rgb(225, 225, 225); -fx-background-color: -fx-background;");
        Label msgText = new Label("Question: " + q.getText());
        msgText.setWrapText(true);
        Label msgBody = new Label(q.getBody());
        msgBody.setWrapText(true);
        
        Button refButton = new Button("Reference");
        refButton.setStyle("padding: 2px; margins: 2px;");
        
        refButton.setOnAction(e -> {
        	new RefMessageDialogPage(q, db, user).show();
        });
        
        textBox.getChildren().addAll(msgText, msgBody);
        msgBox.getChildren().addAll(textBox, refButton);
        content.getChildren().add(msgBox);
    }
    
    /**
     * Adds an answer to the content VBox that is used in the scrollable pane.
     * 
     * @param a The answer to be added.
     * @param content The VBox held in the scrollable pane.
     */
    private void addToListA(Answer a, VBox content) {
    	HBox msgBox = new HBox(5);
        msgBox.setPrefWidth(650);
        msgBox.setStyle("-fx-background: rgb(225, 225, 225); -fx-background-color: -fx-background;");
        Label msgText = new Label("Answer: " + a.getText());
        msgText.setWrapText(true);
        
        Button refButton = new Button("Reference");
        refButton.setStyle("padding: 2px; margins: 2px;");
        
        refButton.setOnAction(e -> {
        	new RefMessageDialogPage(a, db, user).show();
        });
        
        msgBox.getChildren().addAll(msgText, refButton);
        content.getChildren().add(msgBox);
    }
    
    /**
     * Adds an answer to the content VBox that is used in the scrollable pane.
     * 
     * @param m The message to be added.
     * @param content The VBox held in the scrollable pane.
     */
    private void addToListMsg(Message m, VBox content) {
    	HBox msgBox = new HBox(5);
        msgBox.setPrefWidth(650);
        msgBox.setStyle("-fx-background: rgb(225, 225, 225); -fx-background-color: -fx-background;");
        Label msgText = new Label("Message (to " + m.getRecipient() +"): " + m.getText());
        msgText.setWrapText(true);
        
        Button refButton = new Button("Reference");
        refButton.setStyle("padding: 2px; margins: 2px;");
        
        refButton.setOnAction(e -> {
        	new RefMessageDialogPage(m, db, user).show();
        });
        
        msgBox.getChildren().addAll(msgText, refButton);
        content.getChildren().add(msgBox);
    }
    
    /**
     * Adds an answer to the content VBox that is used in the scrollable pane.
     * 
     * @param a The answer to be added.
     * @param content The VBox held in the scrollable pane.
     */
    private void addToListR(Review r, VBox content) {
    	HBox msgBox = new HBox(5);
        msgBox.setPrefWidth(650);
        msgBox.setStyle("-fx-background: rgb(225, 225, 225); -fx-background-color: -fx-background;");
        Label msgText = new Label("Review: " + r.getReviewText());
        msgText.setWrapText(true);
        
        Button refButton = new Button("Reference");
        refButton.setStyle("padding: 2px; margins: 2px;");
        
        refButton.setOnAction(e -> {
        	new RefMessageDialogPage(r, db, user).show();
        });
        
        msgBox.getChildren().addAll(msgText, refButton);
        content.getChildren().add(msgBox);
    }
    
    /**
     * Filter the items shown on this page.
     * @param filterType Questions, Answers, Reviews, Messages, or All.
     * @param content The VBox housed by the scrollable pane.
     */
    private void executeFilter(String filterType, VBox content) {
    	content.getChildren().clear();
    	
    	if (filterType.equals("Questions")) {
    		Questions q;
    		try {
    			q = db.getQuestionsByAuthor(requestUser);
    		} catch (SQLException e) {
    			e.printStackTrace();
    			q = new Questions();
    		}
            for (int i = 0; i < q.size(); i++) {
            	addToListQ(q.get(i), content);
            }
    	} /* Questions */
    	
    	else if (filterType.equals("Answers")) {
    		Answers a;
    		try {
    			a = db.getAnswersByAuthor(requestUser);
    		} catch (SQLException e) {
    			e.printStackTrace();
    			a = new Answers();
    		}
            for (int i = 0; i < a.size(); i++) {
            	addToListA(a.get(i), content);
            }
    	} /* Answers */
    	
    	else if (filterType.equals("Messages")) {
    		Messages m;
    		try {
    			m = db.getMessagesByAuthor(requestUser);
    		} catch (SQLException e) {
    			e.printStackTrace();
    			m = new Messages();
    		}
            for (int i = 0; i < m.size(); i++) {
            	addToListMsg(m.get(i), content);
            }
    	} /* Messages */
    	
    	else if (filterType.equals("Reviews")) {
    		ArrayList<Review> r;
    		try {
    			r = db.getReviewsByAuthor(requestUser);
    		} catch (SQLException e) {
    			e.printStackTrace();
    			r = new ArrayList<Review>();
    		}
            for (int i = 0; i < r.size(); i++) {
            	addToListR(r.get(i), content);
            }
    	} /* Reviews */
    	
    	else if (filterType.equals("All")) {
    		// Get the questions and answers
            Questions q;
            Answers as;
            Messages ms;
            ArrayList<Review> rs;
    		try {
    			q = db.getQuestionsByAuthor(requestUser);
    			as = db.getAnswersByAuthor(requestUser);
    			ms = db.getMessagesByAuthor(requestUser);
    			rs = db.getReviewsByAuthor(requestUser);
    		} catch (SQLException e) {
    			e.printStackTrace();
    			q = new Questions();
    			as = new Answers();
    			ms = new Messages();
    			rs = new ArrayList<Review>();
    		}
            for (int i = 0; i < q.size(); i++) {
            	addToListQ(q.get(i), content);
            }
            for (int i = 0; i < as.size(); i++) {
            	addToListA(as.get(i), content);
            }
            if (!db.getUserRole(requestUser).equals("admin")) {
    		    for (int i = 0; i < ms.size(); i++) {
    		    	addToListMsg(ms.get(i), content);
    		    }
            }
            if (!db.getUserRole(requestUser).equals("user")) {
    	        for (int i = 0; i < rs.size(); i++) {
    	        	addToListR(rs.get(i), content);
    	        }
            }
    	}
    	
    	else {
    		System.err.println("FilterType error: Wrong argument passed.");
    	}
    	
    }
   
}