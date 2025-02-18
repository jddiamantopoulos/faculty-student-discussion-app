package questions.ui;

import java.sql.SQLException;
import java.util.ArrayList;

import accounts.util.User;
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
import questions.util.*;

public class QuestionUpdatePage {
	private Questions parentQuestions;
	private Question question;
	private User user;
	private DatabaseHelper db;
	
	public QuestionUpdatePage(DatabaseHelper db, Questions parentQuestions, Question question, User user) {
		this.parentQuestions = parentQuestions;
		this.question = question;
		this.user = user;
		this.db = db;
	}
	
	public void show(Stage secondaryStage) {
		VBox layout = new VBox(5);
		layout.setStyle("-fx-padding: 10");
		
		// NOTE: The title MUST be immutable for database purposes (it is used as the primary key).
		
		// Copied over much of the same layout from
		// QuestionListPage
		AnchorPane addQuestion = new AnchorPane();
	    addQuestion.setPrefSize(700,30);
	    Button addButton = new Button("Save");
	    TextField questionField = new TextField();
	    questionField.setText(question.getText());
        questionField.setPrefWidth(630);
        questionField.setDisable(true);
        AnchorPane.setLeftAnchor(questionField, 5.0);
        AnchorPane.setTopAnchor(questionField, 3.0);
        AnchorPane.setTopAnchor(addButton, 3.0);
        AnchorPane.setRightAnchor(addButton, 5.0);
        TextArea questionBody = new TextArea();
        questionBody.setText(question.getBody());
        questionBody.setWrapText(true);
        // Now the list of tags
        HBox tags = new HBox(5);
        ArrayList<String> oldTags = question.getTags();
        Label tagLabel = new Label("Tags: ");
        CheckBox genBox = new CheckBox("General");
        if (oldTags.contains("General")) { genBox.setSelected(true); }
        CheckBox assignBox = new CheckBox("Assignment");
        if (oldTags.contains("Assignment")) { assignBox.setSelected(true); }
        CheckBox projBox = new CheckBox("Project");
        if (oldTags.contains("Project")) { projBox.setSelected(true); }
        CheckBox structBox = new CheckBox("Course Structure");
        if (oldTags.contains("Course Structure")) { structBox.setSelected(true); }
        CheckBox lecBox = new CheckBox("Lecture");
        if (oldTags.contains("Lecture")) { lecBox.setSelected(true); }
        CheckBox socialBox = new CheckBox("Social");
        if (oldTags.contains("Social")) { socialBox.setSelected(true); }
        // the tags will be added separately under the questions
        tags.getChildren().addAll(tagLabel, genBox, assignBox, projBox, structBox, lecBox, socialBox);
        addQuestion.getChildren().addAll(questionField, addButton);
        Label errorLabel = new Label("");
        errorLabel.setWrapText(true);
        
        addButton.setOnAction(e -> {
        	// Perform the same validation as an original post
        	ArrayList<String> newTags = new ArrayList<String>();
        	// Cycle through and add the tags
        	if (genBox.isSelected()) { newTags.add("General"); }
        	if (assignBox.isSelected()) { newTags.add("Assignment"); }
        	if (projBox.isSelected()) { newTags.add("Project"); }
        	if (structBox.isSelected()) { newTags.add("Course Structure"); }
        	if (lecBox.isSelected()) { newTags.add("Lecture"); }
        	if (socialBox.isSelected()) { newTags.add("Social"); }
        	Question newQ = new Question(question.getText(), questionBody.getText(), user.getUserName(), newTags);
        	// Validate inputs
        	String questionError = QuestionValidator.validateQuestion(parentQuestions, newQ);
        	// This still tests for duplicates, so we must check for that
        	if ( questionError.equals("") || questionError.equals("Your question already exists! (like... character for character & case-sensitive... how did you even do that?)") ) {
        		// If all has gone correctly, update the question that was passed in
        		question.setTags(newTags);
        		// question.setText(questionField.getText());
        		question.setBody(questionBody.getText());
        		db.updateQuestion(question);
        		
        		new QuestionPage(db, parentQuestions, question, user).show(secondaryStage);
        	}
        	// Error handling
        	else {
        		errorLabel.setStyle("-fx-text-fill: red; -fx-font-size: 12px;");
        		errorLabel.setText(questionError);
        	}
        });
        
        layout.getChildren().addAll(addQuestion, questionBody, tags, errorLabel);
        
        Scene questionUpdateScene = new Scene(layout, 700, 400);
        
	    // Set the scene to secondary stage
	    secondaryStage.setScene(questionUpdateScene);
	    secondaryStage.setTitle("HW2 - " + "Updating Question");
    	secondaryStage.show();
	}
}