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
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
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
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import messaging.util.Message;
import messaging.util.Messages;
import taskmessaging.util.*;

/**
 * This page shows the list of task message requests.
 */
public class TaskMessageListPage {
	
	private TaskMessages taskMessages;
	private final User user;
	private String pageTitle;
	private int listIndex = 0;
	private DatabaseHelper db;
    
    /**
	 * Constructs a new TaskMessageListPage and gets the list of task messages from the database.
	 * 
	 * @param db The application's DatabaseHelper instance.
	 * @param user The current user of the application.
	 */
    public TaskMessageListPage(DatabaseHelper db, User user) {
    	this.db = db;
        this.user = user;
    	try { 
    		this.taskMessages = db.getTaskMessages();
    	} 
    	catch (SQLException e) {
    		taskMessages = new TaskMessages(); 
    		e.printStackTrace();
    	}
        pageTitle = "View all task requests for admins";
    }
    
    /**
     * Shows the TaskMessageListPage on the given stage.
     * <p>
     * The application's primaryStage is recommended.
     * 
     * @param primaryStage The stage that this page will be shown on.
     */
    public void show(Stage primaryStage) {
    	taskMessages.sortRecent();
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
	    Button refreshButton = new Button("Refresh");
	    Button acctButton = new Button("Account");
	    topBox.setStyle("-fx-alignment: center;");
	    topBox.getChildren().addAll(titleLabel, refreshButton, acctButton);
	    
	    // Error label
	    Label errorLabel = new Label("");
	    errorLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: red");
        
        // Search bar
        HBox search = new HBox(2);
        search.setStyle("-fx-alignment: center; -fx-padding: 2;");
        // Search bar
        TextField searchBar = new TextField();
        searchBar.setPromptText("Enter Request");
        searchBar.setPrefWidth(440.0);
        // Search button
        Button searchButton = new Button("Search");
        searchButton.setPrefWidth(120.0);
        search.getChildren().addAll(searchBar, searchButton);
        
        // Add requests for which conversations exist
        ArrayList<String> relevantRequests = getRelevantRequests(taskMessages);
        for (int i = 0; i < relevantRequests.size(); i++) {
    		addToList(relevantRequests.get(i), content, primaryStage);
    	}
	    
        // ACTION LISTENERS

        
        // Execute a search/send a new task message request
        searchButton.setOnAction(e -> {
        	String searchedRequest = searchBar.getText();
        	if (TaskMessageValidator.validateTaskMessage(searchedRequest) == "") {
	    		DialogPage dialogPg = new DialogPage();
	    		DialogReturns retVal = dialogPg.show(DialogTypes.twoButtonYesNo,
	    				"Open Admin Request?", "Do you want to open an admin request?");
	    		
	    		if (retVal == DialogReturns.yes) {
	    			/* Open task messaging (else do nothing) */
	    			
					String requester = "";
					boolean openStatus = true;
	    			
	    			try {
	    				// if request already in database then open it
						if (db.getTaskMessagesForConvo(searchedRequest).size() != 0) {
							requester = db.getRequesterByRequest(searchedRequest);
							openStatus = db.getTaskMessageRequestOpenStatus(searchedRequest);
						}
						// else if not admin and not in database, create new request with current user as requester
						else if (!user.getUserName().equals("admin")) {
							requester = user.getUserName();
						}
						// else if admin and not in database, display error message
						else if (user.getUserName().equals("admin")) {
							Alert adminErrorMessage = new Alert(AlertType.ERROR, "This task request does not exist.\nAdmins cannot create new task requests.");
							adminErrorMessage.show();
						}
						
					} catch (SQLException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
	    			
	    			try {
						if (db.getTaskMessagesForConvo(searchedRequest).size() != 0 || !user.getUserName().equals("admin")) {
							new TaskMessagePage(db, user, searchedRequest, requester, openStatus, "open").show(primaryStage);
						}
					} catch (SQLException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
	    		}
    		} else {
    			errorLabel.setText(TaskMessageValidator.validateTaskMessage(searchedRequest));
    		}
        });
        
        // Refresh the page
        refreshButton.setOnAction(e -> {
        	new TaskMessageListPage(db, user).show(primaryStage);
        });
        
        acctButton.setOnAction(e -> {
        	if (user.getRole().equals("admin")) {
        		new AdminHomePage(db, user).show(primaryStage);
        	}
        	else {
        		new UserHomePage(db, user).show(primaryStage);
        	}
        });
        
        layout.getChildren().addAll(topBox, search, errorLabel, scroller);
        
	    Scene taskMessagesListScene = new Scene(layout, 600, 700);
        
	    // Set the scene to primary stage
	    primaryStage.setScene(taskMessagesListScene);
	    primaryStage.setTitle("Q&A - " + pageTitle);
    	primaryStage.show();
    }
    
    /**
     * Adds listRequest to the scrollable pane that houses content.
     * 
     * @param listRequest The request to be listed.
     * @param content A VBox housed by the scrollable pane.
     */
    private void addToList(String listRequest, VBox content, Stage primaryStage) {
    	// Now create the display elements
    	// Alternates between darker/lighter backgrounds for contrast
        HBox listedRequest = new HBox();
        if (listIndex % 2 == 0) {
        	listedRequest.setStyle("-fx-background: rgb(210, 210, 210); -fx-background-color: -fx-background;");
        }
        else {
        	listedRequest.setStyle("-fx-background: rgb(225, 225, 225); -fx-background-color: -fx-background;");
        }
        listIndex++;
        listedRequest.setPrefSize(550,40);
        Hyperlink request = new Hyperlink(listRequest);
        
        // Also indicate whether or not a request is open
        if (requestIsOpen(listRequest)) {
        	request.setStyle("-fx-font-weight: bold; -fx-font-size: 16px;");
        }
        else {
        	request.setStyle("-fx-font-style: italic; -fx-font-size: 16px;");
        }

        String requester = db.getRequesterByRequest(listRequest);
        String role = db.getUserRole(requester);
        
        Label userRoleLabel = new Label();
        userRoleLabel.setText(role);
        userRoleLabel.setStyle("-fx-font-size: 16px; -fx-alignment: center;");
        userRoleLabel.setPrefWidth(100);
        
        Label vBar1 = new Label("|");
        vBar1.setStyle("-fx-font-size: 16px;");
        vBar1.setPrefWidth(10);
   
        Label userNameLabel = new Label();
        userNameLabel.setText(requester);
        userNameLabel.setStyle("-fx-font-size: 16px; -fx-alignment: center;");
        userNameLabel.setPrefWidth(100);
        
        Label vBar2 = new Label("|");
        vBar2.setStyle("-fx-font-size: 16px;");
        vBar2.setPrefWidth(10);
        
        request.setPrefWidth(150);
        
        Label vBar3 = new Label("|");
        vBar3.setStyle("-fx-font-size: 16px;");
        vBar3.setPrefWidth(10);
        
        Label openStatusLabel = new Label();
        openStatusLabel.setStyle("-fx-font-size: 16px; -fx-alignment: center;");
        openStatusLabel.setPrefWidth(100);
        
        if (requestIsOpen(listRequest)) {
            openStatusLabel.setText("Open");
            openStatusLabel.setStyle("-fx-text-fill: green;");
        } else {
        	openStatusLabel.setText("Closed");
            openStatusLabel.setStyle("-fx-text-fill: red;");
        }
        
        request.setOnAction(a -> {
        	if (requestIsOpen(listRequest)) {
        		new TaskMessagePage(db, user, listRequest, requester, true, "open").show(primaryStage);
        	} else {
        		new TaskMessagePage(db, user, listRequest, requester, false, "open").show(primaryStage);
        	}
        });
        
        Button reopenButton = new Button("Reopen");
        Button closeButton = new Button("Close");
        
        reopenButton.setOnAction(a -> {
        	DialogPage dialogPg = new DialogPage();
    		DialogReturns retVal = dialogPg.show(DialogTypes.twoButtonYesNo,
    				"Reopen Admin Request?", "Do you want to reopen this admin request?");
    		
    		if (retVal == DialogReturns.yes) {
	        	new TaskMessagePage(db, user, listRequest, requester, true, "closed").show(primaryStage);
    		}
        });
        
        closeButton.setOnAction(a -> {
        	DialogPage dialogPg = new DialogPage();
    		DialogReturns retVal = dialogPg.show(DialogTypes.twoButtonYesNo,
    				"Close Admin Request?", "Are you sure you want to close this admin request?");
    		
    		if (retVal == DialogReturns.yes) {
	        	db.setTaskMessageRequestClosed(listRequest);
	        	new TaskMessageListPage(db, user).show(primaryStage);
	    	}
        });
        
        listedRequest.getChildren().addAll(userRoleLabel, vBar1, userNameLabel, vBar2, request, vBar3, openStatusLabel);
        
        if (requestIsOpen(listRequest) && user.getRole().equals("admin")) {
        	listedRequest.getChildren().add(closeButton);
        }
        
        else if (!requestIsOpen(listRequest) && !user.getRole().equals("admin")) {
        	listedRequest.getChildren().add(reopenButton);
        }
        
        // Add to top of list
        content.getChildren().add(0, listedRequest);
    }
    
    /**
     * Gets the list of valid requests from the application's task messages.
     * 
     * @param taskMessages The list of all task messages for this workspace.
     * @return
     */
    private ArrayList<String> getRelevantRequests(TaskMessages taskMessages) {
    	ArrayList<String> relevantRequests = new ArrayList<String>();
    	for (int i = 0; i < taskMessages.size(); i++) {
    		TaskMessage tempTmsg = taskMessages.get(i);
    		if (!relevantRequests.contains(tempTmsg.getRequest())) {
    			relevantRequests.add(tempTmsg.getRequest());
    		}
    	}
    	return relevantRequests;
    }
    
    /**
     * Determines if the provided request is open.
     * 
     * @param request The request.
     * @return
     */
    private boolean requestIsOpen(String request) {
    	try {
    		TaskMessages conversation = db.getTaskMessagesForConvo(request);
    		if (conversation.get(0).getRequestIsOpen()) {
    			return true;
    		}
	    	return false;
    	} catch (SQLException e) {
    		e.printStackTrace();
    		return false;
    	}
    }
}
