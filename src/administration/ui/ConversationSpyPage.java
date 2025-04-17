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

/**
 * This page displays the direct message conversation between two users
 */
public class ConversationSpyPage {
	
	private Messages messages;
	private String user1;
	private String pageTitle;
	private String user2;
	private DatabaseHelper db;
	
	/**
	 * Constructs a new ConversationSpyPage and fetches the relevant messages from
	 * the database.
	 * 
	 * @param user1 One of the users whose conversation is displayed.
	 * @param user2 One of the users whose conversation is displayed.
	 * @param db The application's DatabaseHelper instance
	 */
    public ConversationSpyPage(String user1, String user2, DatabaseHelper db) {
    	this.db = db;
    	this.user1 = user1;
    	this.user2 = user2;
    	try { 
    		this.messages = db.getMessagesForSpy(user1, user2);
    	} 
    	catch (SQLException e) {
    		messages = new Messages(); 
    		e.printStackTrace();
    	}
        pageTitle = "Spying on direct messages between " + user1 + " and " + user2 + ".";
    }
    /**
     * Displays the ConversationSpyPage on a new stage.
     */
    public void show() {
    	Stage conversationStage = new Stage();
    	
    	messages.sortRecent();
    	VBox layout = new VBox(5);
    	
    	// Create the scrollable pane
    	VBox content = new VBox(3);
        ScrollPane scroller = new ScrollPane(content);
        scroller.setPrefHeight(550);
	    layout.setStyle("-fx-alignment: center; -fx-padding: 20;");
        
        for (int i = 0; i < messages.size(); i++) {
        	addToList(messages.get(i), content);
        }

        
        // Add elements to master layout
        scroller.setVvalue(1.0); /* https://stackoverflow.com/questions/20333396/javafx-make-scrollpane-scroll-automatically */
        layout.getChildren().addAll(scroller);
	    Scene messagesListScene = new Scene(layout, 600, 700);
        
	    // Set the scene to primary stage
	    conversationStage.setScene(messagesListScene);
	    conversationStage.setTitle("Q&A - " + pageTitle);
	    conversationStage.show();
    }
    
    /**
     * Adds a message to the content VBox that is used in the scrollable pane.
     * 
     * @param message The message to be added.
     * @param content The VBox held in the scrollable pane.
     */
    private void addToList(Message message, VBox content) {
    	// Now create the display elements
    	// Alternates between darker/lighter backgrounds for contrast
        //AnchorPane messagePane = new AnchorPane();
        VBox msgBox = new VBox(2);
        if (!message.getSender().equals(user1)) {
        	msgBox.setStyle("-fx-background: rgb(210, 210, 210); -fx-background-color: -fx-background;");
        }
        else {
        	msgBox.setStyle("-fx-background: rgb(225, 225, 225); -fx-background-color: -fx-background;");
        }
        msgBox.setPrefWidth(550);
        Label msgText = new Label(message.getText());
        msgText.setWrapText(true);
        
        // Also indicate whether or not a message has been read
        if (!message.getIsRead()) {
        	msgText.setStyle("-fx-font-weight: bold; -fx-font-size: 12px;");
        }
        else {
        	msgText.setStyle("-fx-font-style: italic; -fx-font-size: 12px;");
        }
        
        Label senderNameLabel = new Label();
        senderNameLabel.setText(message.getSender());
        msgBox.getChildren().addAll(senderNameLabel, msgText);
        content.getChildren().add(msgBox);
    }
    
   
}