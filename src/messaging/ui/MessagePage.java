package messaging.ui;

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

/* Cloned from questions.ui.questionlistpage */
public class MessagePage {
	
	private Messages messages;
	private final User user;
	private String pageTitle;
	private String recipient;
	private DatabaseHelper db;
	
	// Used for the page
    public MessagePage(DatabaseHelper db, User testUser, String recipient) {
    	this.db = db;
    	this.user = testUser;
        this.recipient = recipient;
    	try { 
    		this.messages = db.getMessagesForConvo(user, recipient);
    	} 
    	catch (SQLException e) {
    		messages = new Messages(); 
    		e.printStackTrace();
    	}
        pageTitle = "Direct Messages with " + recipient;
    }
    
    public void show() {
    	Stage conversationStage = new Stage();
    	
    	messages.sortRecent();
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
        // Search bar
        TextField textBar = new TextField();
        textBar.setPromptText("Enter Message");
        textBar.setPrefWidth(440.0);
        // Search button
        Button sendButton = new Button("Send");
        sendButton.setPrefWidth(120.0);
        send.getChildren().addAll(textBar, sendButton);
        
        for (int i = 0; i < messages.size(); i++) {
        	addToList(messages.get(i), content);
        	System.out.println(messages.get(i).getText());
        }
	    
        // ACTION LISTENERS
        
        // Execute a search
        sendButton.setOnAction(e -> {
        	/* LOGIC INCOMPLETE */
        	String messageText = textBar.getText();
        	String msgValidation = MessageValidator.validateMessage(messageText);
        	if (msgValidation.equals("")) {
        		Message msg = new Message(messageText, user.getUserName(), recipient);
        		try {
					db.insertMessage(msg);
				} catch (SQLException e1) {
					// Auto-generated catch block
					e1.printStackTrace();
				}
        		addToList(msg, content);
        		textBar.setText("");
        	}
        	else {
        		errorLabel.setText(msgValidation);
        	}
        });
        
        // Add elements to master layout
        scroller.setVvalue(1.0); /* https://stackoverflow.com/questions/20333396/javafx-make-scrollpane-scroll-automatically */
        layout.getChildren().addAll(errorLabel, scroller, send);
	    Scene messagesListScene = new Scene(layout, 600, 700);
        
	    // Set the scene to primary stage
	    conversationStage.setScene(messagesListScene);
	    conversationStage.setTitle("Q&A - " + pageTitle);
	    conversationStage.show();
    }
    
    
    private void addToList(Message message, VBox content) {
    	// Now create the display elements
    	// Alternates between darker/lighter backgrounds for contrast
        AnchorPane messagePane = new AnchorPane();
        if (!message.getSender().equals(user.getUserName())) {
        	messagePane.setStyle("-fx-background: rgb(210, 210, 210); -fx-background-color: -fx-background;");
        }
        else {
        	messagePane.setStyle("-fx-background: rgb(225, 225, 225); -fx-background-color: -fx-background;");
        }
        messagePane.setPrefSize(550,40);
        Label msgText = new Label(message.getText());
        
        // Also indicate whether or not a message has been read
        if (!message.getIsRead()) {
        	msgText.setStyle("-fx-font-weight: bold; -fx-font-size: 12px;");
        }
        else {
        	msgText.setStyle("-fx-font-style: italic; -fx-font-size: 12px;");
        }
        
        Label senderNameLabel = new Label();
        //userRoleLabel.setText(db.getUserRole(listUser));
        AnchorPane.setLeftAnchor(msgText, 5.0);
        AnchorPane.setBottomAnchor(msgText, 5.0);
        AnchorPane.setRightAnchor(senderNameLabel, 5.0);
        AnchorPane.setTopAnchor(senderNameLabel, 5.0);
        messagePane.getChildren().addAll(msgText, senderNameLabel);
        // Add to bottom of list
        content.getChildren().add(messagePane);
    }
    
   
}