package taskmessaging.ui;

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
import taskmessaging.util.*;

/**
 * This page displays the task message conversation of a given request and allows 
 * new task messages to be sent.
 */
public class TaskMessagePage {
	
	private TaskMessages taskMessages;
	private String request;
	private String requester;
	private boolean requestIsOpen;
	private String prevState;
	private User user;
	private String pageTitle;
	private DatabaseHelper db;
	
	/**
	 * Constructs a new TaskMessagePage and fetches the relevant messages from
	 * the database.
	 * 
	 * @param db The application's DatabaseHelper instance
	 * @param user The user of the application.
	 * @param request The task messages' request
	 * @param requester The task messages' requester
	 * @param requestIsOpen Whether the task messages' request is open or not
	 * @param prevState Whether the task messages' request was previously open or not, "open" or "closed"
	 */
    public TaskMessagePage(DatabaseHelper db, User user, String request, String requester, boolean requestIsOpen, String prevState) {
    	this.db = db;
    	this.user = user;
    	this.request = request;
    	this.requester = requester;
    	this.requestIsOpen = requestIsOpen;
    	this.prevState = prevState;
    	try { 
    		this.taskMessages = db.getTaskMessagesForConvo(request);
    	} 
    	catch (SQLException e) {
    		taskMessages = new TaskMessages(); 
    		e.printStackTrace();
    	}
        pageTitle = "Request: " + request;
    }
    /**
     * Displays the TaskMessagePage on a new stage.
     */
    public void show(Stage primaryStage) {
    	Stage conversationStage = new Stage();
    	
    	taskMessages.sortRecent();
    	VBox layout = new VBox(5);
    	
    	// Create the scrollable pane
    	VBox content = new VBox(3);
        ScrollPane scroller = new ScrollPane(content);
        scroller.setPrefHeight(550);
	    layout.setStyle("-fx-alignment: center; -fx-padding: 20;");
	    
	    // Error label
	    Label errorLabel = new Label("");
	    errorLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: red");
        
        // Send box
        HBox send = new HBox(2);
        send.setStyle("-fx-alignment: center; -fx-padding: 2;");
        // Send bar
        TextField textBar = new TextField();
        textBar.setPromptText("Enter Task Message");
        textBar.setPrefWidth(440.0);
        // Send button
        Button sendButton = new Button("Send");
        sendButton.setPrefWidth(120.0);
        send.getChildren().addAll(textBar, sendButton);
        
        for (int i = 0; i < taskMessages.size(); i++) {
        	addToList(taskMessages.get(i), content);
        }
	    
        // ACTION LISTENERS
        
        
        // Execute a send
        sendButton.setOnAction(e -> {
        	/* LOGIC INCOMPLETE */
        	String taskMessageText = textBar.getText();
        	String tmsgValidation = TaskMessageValidator.validateTaskMessage(taskMessageText);
        	if (tmsgValidation.equals("")) {
        		TaskMessage tmsg = new TaskMessage(request, requester, user.getUserName(), taskMessageText);
        		try {
        			if (db.getTaskMessagesForConvo(request).size() == 0 || prevState.equals("closed")) {
        				send.getChildren().removeAll(textBar, sendButton);
        	        	db.setTaskMessageRequestOpen(request);
        			}
					db.insertTaskMessage(tmsg);
					new TaskMessageListPage(db, user).show(primaryStage);
				} catch (SQLException e1) {
					// Auto-generated catch block
					e1.printStackTrace();
				}
        		addToList(tmsg, content);
        		textBar.setText("");
        	}
        	else {
        		errorLabel.setText(tmsgValidation);
        	}
        });
        
        // Add elements to master layout
        scroller.setVvalue(1.0); /* https://stackoverflow.com/questions/20333396/javafx-make-scrollpane-scroll-automatically */
        
        Label closedLabel = new Label("This request is closed.");
        closedLabel.setStyle("-fx-font-size: 25px; -fx-text-fill: black");
        
        try {
			if (db.getTaskMessagesForConvo(request).size() == 0 || prevState.equals("closed")) {
				layout.getChildren().addAll(errorLabel, scroller, send);
			} else if (!requestIsOpen) {
				layout.getChildren().addAll(scroller, closedLabel);
			} else if (!user.getRole().equals("admin")) {
				layout.getChildren().addAll(scroller);
			} else {
				layout.getChildren().addAll(errorLabel, scroller, send);
			}
		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
        
	    Scene taskMessagesListScene = new Scene(layout, 600, 700);
        
	    // Set the scene to primary stage
	    conversationStage.setScene(taskMessagesListScene);
	    conversationStage.setTitle("Q&A - " + pageTitle);
	    conversationStage.show();
    }
    
    /**
     * Adds a task message to the content VBox that is used in the scrollable pane.
     * 
     * @param taskMessage The task message to be added.
     * @param content The VBox held in the scrollable pane.
     */
    private void addToList(TaskMessage taskMessage, VBox content) {
    	// Now create the display elements
    	// Alternates between darker/lighter backgrounds for contrast
        //AnchorPane messagePane = new AnchorPane();
        VBox tmsgBox = new VBox(2);
        tmsgBox.setStyle("-fx-background: rgb(210, 210, 210); -fx-background-color: -fx-background;");
        tmsgBox.setPrefWidth(550);
        
        Label senderNameLabel = new Label();
        senderNameLabel.setText(taskMessage.getSender());
        
        Label tmsgText = new Label(taskMessage.getText());
        tmsgText.setWrapText(true);
        
        tmsgBox.getChildren().addAll(senderNameLabel, tmsgText);
        content.getChildren().add(tmsgBox);
    }
    
   
}
