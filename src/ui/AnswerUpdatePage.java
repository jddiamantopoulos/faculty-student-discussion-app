package ui;

import java.sql.SQLException;
import java.util.ArrayList;

import application.User;
import databasePart1.DatabaseHelper;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import questionsandanswers.*;

public class AnswerUpdatePage {
	private Answer ans;
	private DatabaseHelper db;
	private Question parent;
	private User currUser;
	
	public AnswerUpdatePage(DatabaseHelper db, Question parent, Answer ans, User currUser) {
		this.ans = ans;
		this.db = db;
		this.parent = parent;
		this.currUser = currUser;
	}
	
	public void show(Stage tertiaryStage) {
		VBox layout = new VBox(3);
		
		// Copied over much of the same layout from
		// QuestionListPage
		// Revised for answers
	    Button addButton = new Button("Save");
        TextArea questionBody = new TextArea();
        questionBody.setText(ans.getText());
        questionBody.setWrapText(true);
        Label errorLabel = new Label("");
        errorLabel.setStyle("-fx-text-fill: red;");
        
        addButton.setOnAction(e -> {
        	// Perform the same validation as an original post
        	// Validate inputs
        	Answer newAns = new Answer(questionBody.getText(), currUser.getUserName());
        	if ( !questionBody.getText().equals(ans.getText()) && 
        			questionBody.getText().length() <= 2000 &&
        			!questionBody.getText().isEmpty() ) {
        		// If all has gone correctly, update the question that was passed in
        		ArrayList<String> markedHelpful = ans.getMarkedHelpful(); // preserve other fields
        		try {
        			db.removeAnswer(ans);
        			ans.setText(questionBody.getText());
        			db.insertAnswer(parent, ans);
        		} catch (SQLException ex) {
        			ex.printStackTrace();
        		}
        		tertiaryStage.close();
        	}
        	// Error handling
        	else {
        		String errorText = "";
        		if (questionBody.getText().length() > 2000) {
        			errorText += "Your answer is too long! Please reduce the length to 2000 characters or less.";
        		}
        		else if (questionBody.getText().isEmpty()) {
        			errorText += "Your answer must not be empty!";
        		}
        		else if (questionBody.getText().equals(ans.getText())) {
        			errorText += "You must make a change in order to edit your answer!";
        		}
        		errorLabel.setText(errorText);
        	}
        });
        
        layout.getChildren().addAll(questionBody, addButton, errorLabel);
        layout.setStyle("fx-alignment: center; -fx-padding: 10;");
        
        Scene answerUpdateScene = new Scene(layout, 600, 300);
        
	    // Set the scene to secondary stage
	    tertiaryStage.setScene(answerUpdateScene);
	    tertiaryStage.setTitle("HW2 - " + "Updating Answer");
    	tertiaryStage.show();
	}
}