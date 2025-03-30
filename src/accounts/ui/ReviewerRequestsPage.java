package accounts.ui;

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

/**
 * This page displays the questions and answers posted by a user requesting the reviewer role.
 */
public class ReviewerRequestsPage {
	
	private final User user;
	private String pageTitle;
	private DatabaseHelper db;
	private String requestUser;
	
	/**
	 * Constructs a new ReviewerRequestPage.
	 * 
	 * @param db The application's DatabaseHelper instance
	 * @param user The current user
	 * @param requestUser The username of the requested user
	 */
    public ReviewerRequestsPage(DatabaseHelper db, User user, String requestUser) {
    	this.db = db;
    	this.user = user;
        this.requestUser = requestUser;
        pageTitle = "Viewing Reviewer Request of " + requestUser;
    }
    
    /**
     * Displays the ReviewerRequestPage on a new stage.
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
        
        // Approve or reject
        HBox appRej = new HBox(2);
        appRej.setStyle("-fx-alignment: center; -fx-padding: 2;");
        Button approve = new Button("Approve");
        Button reject = new Button("Reject");
        appRej.getChildren().addAll(approve, reject);
        
        // Get the questions and answers
        Questions q;
        Answers as;
		try {
			q = db.getQuestionsByAuthor(requestUser);
			as = db.getAnswersByAuthor(requestUser);
		} catch (SQLException e) {
			e.printStackTrace();
			q = new Questions();
			as = new Answers();
		}
        for (int i = 0; i < q.size(); i++) {
        	addToListQ(q.get(i), content);
        }
        for (int i = 0; i < as.size(); i++) {
        	addToListA(as.get(i), content);
        }
	    
        approve.setOnAction(a -> {
        	db.setUserRole(requestUser, "reviewer");
        	boolean status = db.rejectReviewerRequest(requestUser);
        	if (status) {
        		errorLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: green");
        		errorLabel.setText("Approved! You can close this page.");
        	}
        	else {
        		errorLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: red");
        		errorLabel.setText("Something went wrong. Try again later.");
        	}
        });
        
        reject.setOnAction(a -> {
        	boolean status = db.rejectReviewerRequest(requestUser);
        	if (status) {
        		errorLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: green");
        		errorLabel.setText("Rejected! You can close this page.");
        	}
        	else {
        		errorLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: red");
        		errorLabel.setText("Something went wrong. Try again later.");
        	}
        });
        
        // Add elements to master layout
        scroller.setVvalue(1.0); /* https://stackoverflow.com/questions/20333396/javafx-make-scrollpane-scroll-automatically */
        layout.getChildren().addAll(appRej, errorLabel, scroller);
	    Scene messagesListScene = new Scene(layout, 600, 700);
        
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
        VBox msgBox = new VBox(2);
        msgBox.setPrefWidth(550);
        msgBox.setStyle("-fx-background: rgb(225, 225, 225); -fx-background-color: -fx-background;");
        Label msgText = new Label(q.getText());
        msgText.setWrapText(true);
        Label msgBody = new Label(q.getBody());
        msgBody.setWrapText(true);
        
        msgBox.getChildren().addAll(msgText, msgBody);
        content.getChildren().add(msgBox);
    }
    
    /**
     * Adds an answer to the content VBox that is used in the scrollable pane.
     * 
     * @param a The answer to be added.
     * @param content The VBox held in the scrollable pane.
     */
    private void addToListA(Answer a, VBox content) {
        VBox msgBox = new VBox(2);
        msgBox.setPrefWidth(550);
        msgBox.setStyle("-fx-background: rgb(210, 210, 210); -fx-background-color: -fx-background;");
        Label msgText = new Label(a.getText());
        msgText.setWrapText(true);
        
        msgBox.getChildren().addAll(msgText);
        content.getChildren().add(msgBox);
    }
    
   
}