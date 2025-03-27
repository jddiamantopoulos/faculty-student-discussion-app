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
public class MessageUserListPage {
	
	private Messages messages;
	private final User user;
	private String pageTitle;
	private int listIndex = 0;
	private DatabaseHelper db;
	
	// Used for the initial page
    public MessageUserListPage(DatabaseHelper db, Messages messages, User testUser) {
    	this.db = db;
        this.messages = messages;
        this.user = testUser;
        pageTitle = "Direct Messages for " + user;
    }
    
    // Used to pull messages from the database
    public MessageUserListPage(DatabaseHelper db, User testUser) {
    	this.db = db;
    	this.user = testUser;
    	try { 
    		this.messages = db.getMessages();
    	} 
    	catch (SQLException e) {
    		messages = new Messages(); 
    		e.printStackTrace();
    	}
        pageTitle = "Direct Messages for " + user.getUserName();
    }
    
    public void show(Stage primaryStage) {
    	messages.sortRecent();
    	VBox layout = new VBox(5);
    	
    	// Create the scrollable pane
    	VBox content = new VBox(3);
        ScrollPane scroller = new ScrollPane(content);
        scroller.setPrefHeight(550);
	    layout.setStyle("-fx-alignment: center; -fx-padding: 20;");
	    
	    // Title box
	    HBox topBox = new HBox(3);
	    Label titleLabel = new Label(pageTitle + ":");
	    titleLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
	    titleLabel.setPrefWidth(380);
	    Button helpButton = new Button("Help");
	    Button refreshButton = new Button("Refresh");
	    Button acctButton = new Button("Account");
	    topBox.setStyle("-fx-alignment: center;");
	    topBox.getChildren().addAll(titleLabel, helpButton, refreshButton, acctButton);
	    
	    // Error label
	    Label errorLabel = new Label("");
	    errorLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: red");
        
        // Search bar
        HBox search = new HBox(2);
        search.setStyle("-fx-alignment: center; -fx-padding: 2;");
        // Search bar
        TextField searchBar = new TextField();
        searchBar.setPromptText("Enter Username");
        searchBar.setPrefWidth(440.0);
        // Search button
        Button searchButton = new Button("Search");
        searchButton.setPrefWidth(120.0);
        search.getChildren().addAll(searchBar, searchButton);
        
        // Add users for which conversations exist
        ArrayList<String> relevantUsers = getRelevantUsers(messages, user);
        for (int i = 0; i < relevantUsers.size(); i++) {
    		addToList(relevantUsers.get(i), content);
    	}
	    
        // ACTION LISTENERS
        
        
        // Execute a search
        searchButton.setOnAction(e -> {
        	/* LOGIC INCOMPLETE */
        	/* Should check if user exists, 
        	 * start a new conversation or open existing if so, or display an error if not */
        	String searchedUser = searchBar.getText();
        	if (UserNameRecognizer.checkForValidUserName(searchedUser).equals("") 
        			&& db.doesUserExist(searchedUser)
        			&& !user.getUserName().equals(searchedUser)) {
        		DialogPage dialogPg = new DialogPage();
        		DialogReturns retVal = dialogPg.show(DialogTypes.twoButtonYesNo,
        				"Open Conversation?", "Do you want to open a conversation with this user?");
        		
        		if (retVal == DialogReturns.yes) {
        			/* Open DMs (else do nothing) */
        			new MessagePage(db, user, searchedUser).show();
        		}
        	}
        	else {
        		errorLabel.setText("Error: Could not open conversation with specified user.");
        	}
        });
        
        // Refresh the page
        refreshButton.setOnAction(e -> {
        	new MessageUserListPage(db, user).show(primaryStage);
        });
        
        // Get help
        helpButton.setOnAction(e -> {
        	/* new MessageHelpDialogPage().show(new Stage()); */
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
        layout.getChildren().addAll(topBox, search, errorLabel, scroller);
	    Scene messagesListScene = new Scene(layout, 600, 700);
        
	    // Set the scene to primary stage
	    primaryStage.setScene(messagesListScene);
	    primaryStage.setTitle("Q&A - " + pageTitle);
    	primaryStage.show();
    }
    
    private void addToList(String listUser, VBox content) {
    	// Now create the display elements
    	// Alternates between darker/lighter backgrounds for contrast
        AnchorPane listedUser = new AnchorPane();
        if (listIndex % 2 == 0) {
        	listedUser.setStyle("-fx-background: rgb(210, 210, 210); -fx-background-color: -fx-background;");
        }
        else {
        	listedUser.setStyle("-fx-background: rgb(225, 225, 225); -fx-background-color: -fx-background;");
        }
        listIndex++;
        listedUser.setPrefSize(550,40);
        Hyperlink userName = new Hyperlink(listUser);
        
        // Also indicate whether or not a message has been read
        if (hasUnread(listUser)) {
        	userName.setStyle("-fx-font-weight: bold; -fx-font-size: 16px;");
        }
        else {
        	userName.setStyle("-fx-font-style: italic; -fx-font-size: 16px;");
        }
        
        userName.setOnAction(a -> {
        	new MessagePage(db, user, listUser).show();
        });
        
        Label userRoleLabel = new Label();
        userRoleLabel.setText(db.getUserRole(listUser));
        AnchorPane.setLeftAnchor(userName, 5.0);
        AnchorPane.setBottomAnchor(userRoleLabel, 5.0);
        AnchorPane.setBottomAnchor(userName, 5.0);
        AnchorPane.setRightAnchor(userRoleLabel, 5.0);
        AnchorPane.setTopAnchor(userName, 5.0);
        AnchorPane.setTopAnchor(userRoleLabel, 5.0);
        listedUser.getChildren().addAll(userName, userRoleLabel);
        // Add to top of list
        content.getChildren().add(0, listedUser);
    }
    
    
    private ArrayList<String> getRelevantUsers(Messages messages, User currUser) {
    	ArrayList<String> relevantUsers = new ArrayList<String>();
    	for (int i = 0; i < messages.size(); i++) {
    		Message tempMsg = messages.get(i);
    		if (tempMsg.getSender().equals(currUser.getUserName())) {
    			if (!relevantUsers.contains(tempMsg.getRecipient())) {
    				relevantUsers.add(tempMsg.getRecipient());
    			}
    		}
    		else {
    			if (!relevantUsers.contains(tempMsg.getSender())) {
    				relevantUsers.add(tempMsg.getSender());
    			}
    		}
    	}
    	return relevantUsers;
    }
    
    private boolean hasUnread(String username) {
    	try {
    		Messages conversation = db.getMessagesByUser(user, username);
	    	for (int i = 0; i < conversation.size(); i++) {
	    		if (!conversation.get(i).getIsRead() && conversation.get(i).getSender().equals(username)) {
	    			return true;
	    		}
	    	}
	    	return false;
    	} catch (SQLException e) {
    		e.printStackTrace();
    		return false;
    	}
    }
}