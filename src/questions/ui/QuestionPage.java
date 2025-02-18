package questions.ui;

import java.sql.SQLException;

import accounts.util.User;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Separator;
import javafx.scene.control.TextArea;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import questions.util.*;
import databasePart1.*;

public class QuestionPage {
	
	private int listIndex = 0;
	private final Questions parentQuestions;
	private final Question question;
	private final User user;
	private final DatabaseHelper db;

    public QuestionPage(DatabaseHelper db, Questions parentQuestions, Question question, User user) {
    	this.parentQuestions = parentQuestions;
        this.question = question;
        this.user = user;
        this.db = db;
        
        try {
        	db.getAnswers(question);
        }
        catch (SQLException e) {
        	e.printStackTrace();
        }
        
        question.getAnswers().sortAnswers();
    }
    
    public void show(Stage secondaryStage) {
    	VBox layout = new VBox(5);
    	ScrollPane parentScroller = new ScrollPane(layout);
        parentScroller.setPrefHeight(350);
        parentScroller.setPrefWidth(700);
    	layout.setStyle("-fx-alignment: center; -fx-padding: 5");
    	
    	// Create the scrollable pane
    	VBox content = new VBox(3);
        ScrollPane scroller = new ScrollPane(content);
        scroller.setPrefSize(670, 400);
    	
        // Show the question
    	VBox questionPane = new VBox();   // This was originally an AnchorPane, but those are very difficult to work with.
    	HBox firstRow = new HBox();
    	questionPane.setPrefWidth(670);
    	questionPane.setMinHeight(80.0);
    	questionPane.setStyle("-fx-padding: 5; -fx-background: rgb(210, 210, 210); -fx-background-color: -fx-background;");
    	
    	Label questionText = new Label(question.getText());
    	questionText.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-padding: 5;");
    	questionText.setPrefWidth(600);
    	questionText.setWrapText(true);
    	
    	Separator separator = new Separator();
    	Label questionBody = new Label(question.getBody());
    	questionBody.setStyle("-fx-font-size: 12px; -fx-padding: 5;");
    	questionBody.setWrapText(true);
    	
    	Button questionUpdateButton = new Button("Edit");
    	
    	Label usernameAndTags = new Label("Poster: " + question.getAuthor() + " | Tags: " + question.getTagsAsString());
    	usernameAndTags.setStyle("-fx-padding: 5;");
    	
    	firstRow.getChildren().add(questionText);
    	if (question.getAuthor().equals(user.getUserName()) || user.getRole().equals("admin")) {
    		firstRow.getChildren().add(questionUpdateButton);
    	}
    	questionPane.getChildren().addAll(firstRow, usernameAndTags);
    	if (question.getBody().length() > 0) {
    		questionPane.getChildren().addAll(separator, questionBody);
    	}
    	layout.getChildren().addAll(questionPane, scroller);
    	
    	questionUpdateButton.setOnAction(e -> {
    		new QuestionUpdatePage(db, parentQuestions, question, user).show(secondaryStage);
    	});
    	
    	for (int i = 0; i < question.getAnswers().size(); i++) {
    		addToList(question.getAnswers().get(i), content);
    	}
    	
    	// Post a new answer
    	HBox postAnswer = new HBox(2);
    	TextArea newAnsText = new TextArea();
    	newAnsText.setPromptText("Post a new answer");
    	newAnsText.setPrefWidth(620);
    	Button postButton = new Button("Post");
    	postButton.setPrefHeight(180); 
    	postAnswer.getChildren().addAll(newAnsText, postButton);
    	layout.getChildren().add(postAnswer);
    	Label errorLabel = new Label("");
    	errorLabel.setStyle("-fx-text-fill: red; fx-font-size; 12px;");
    	
    	postButton.setOnAction(e -> {
    		Answer newAns = new Answer(newAnsText.getText(), user.getUserName());
    		String errStr = AnswerValidator.validateAnswer(question, newAns);
    		if (errStr.equals("")) {
    			question.addAnswer(newAns);
    			addToList(newAns, content);
    			try {
    				db.insertAnswer(question, newAns);
    			}
    			catch (SQLException ex) {
    				ex.printStackTrace();
    			}
    			errorLabel.setStyle("-fx-text-fill: green; fx-font-size; 12px;");
    			errorLabel.setText("Answer posted successfully!");
    		}
    		else {
    			errorLabel.setStyle("-fx-text-fill: red; fx-font-size; 12px;");
    			errorLabel.setText(errStr);
    		}
    	});
    	
    	Scene questionScene = new Scene(parentScroller, 700, 600);
        
	    // Set the scene to secondary stage
	    secondaryStage.setScene(questionScene);
	    secondaryStage.setTitle("HW2 - Viewing Question");
    	secondaryStage.show();
    }
    
    public void addToList(Answer ans, VBox content) {
    	// Now create the display elements
    	// Alternates between darker/lighter backgrounds for contrast
        HBox answer = new HBox(2);
        VBox leftSide = new VBox(2);
        if (listIndex % 2 == 0) {
        	answer.setStyle("-fx-background: rgb(210, 210, 210); -fx-background-color: -fx-background;");
        }
        else {
        	answer.setStyle("-fx-background: rgb(225, 225, 225); -fx-background-color: -fx-background;");
        }
        listIndex++;
        answer.setPrefWidth(650);
        answer.setMinHeight(100);
        Label questionText = new Label(ans.getText());
        questionText.setStyle("-fx-font-size: 10pt");
        questionText.setWrapText(true);
        questionText.setPrefWidth(580);
        
        // Buttons w/ action listeners for removal/editing
        Button button = new Button("Remove");
        Button editButton = new Button("Edit");
        Button likeButton = new Button("Like");
        editButton.setPrefWidth(80);
        button.setPrefWidth(80);
        likeButton.setPrefWidth(80);
        
        // Show/hide depending on settings
        VBox buttonContainer = new VBox(1);
        if (user.getUserName().equals(ans.getAuthor())) {
        	buttonContainer.getChildren().addAll(button, editButton);
        }
        else if (user.getRole().equals("admin")) {
        	buttonContainer.getChildren().addAll(button, editButton, likeButton);
        }
        else {
        	buttonContainer.getChildren().add(likeButton);
        }
        
        Label usernameAndTags = new Label("Poster: " + ans.getAuthor() + " | Marked Helpful: " + ans.getHelpfulAsString(60 - ans.getAuthor().length()));
        AnchorPane.setLeftAnchor(questionText, 5.0);
        AnchorPane.setLeftAnchor(usernameAndTags, 5.0);
        AnchorPane.setBottomAnchor(usernameAndTags, 5.0);
        AnchorPane.setRightAnchor(button, 5.0);
        AnchorPane.setRightAnchor(buttonContainer, 5.0);
        AnchorPane.setTopAnchor(buttonContainer, 5.0);
        AnchorPane.setTopAnchor(button, 5.0);
        leftSide.getChildren().addAll(questionText, usernameAndTags);
        answer.getChildren().addAll(leftSide, buttonContainer);
        
        // Action Listeners
        
        button.setOnAction(a -> { 
        	question.getAnswers().remove(ans);
        	content.getChildren().remove(answer);
        });
        editButton.setOnAction(a -> {
        	new AnswerUpdatePage(db, question, ans, user).show(new Stage());
        });
        likeButton.setOnAction(a -> {
        	if (ans.getMarkedHelpful().contains(user.getUserName())) {
        		try {
        			db.removeAnswer(ans);
        			ans.getMarkedHelpful().remove(user.getUserName());
            		usernameAndTags.setText("Poster: " + ans.getAuthor() + " | Marked Helpful: " + ans.getHelpfulAsString(60 - ans.getAuthor().length()));
        			db.insertAnswer(question, ans);
        		} catch (SQLException ex) {
        			ex.printStackTrace();
        		}
        	}
        	else {
        		try {
        			db.removeAnswer(ans);
        			ans.getMarkedHelpful().add(user.getUserName());
            		usernameAndTags.setText("Poster: " + ans.getAuthor() + " | Marked Helpful: " + ans.getHelpfulAsString(60 - ans.getAuthor().length()));
        			db.insertAnswer(question, ans);
        		} catch (SQLException ex) {
        			ex.printStackTrace();
        		}
        	}
        });
        
        // Add to top of list
        content.getChildren().add(0, answer);
    }
}