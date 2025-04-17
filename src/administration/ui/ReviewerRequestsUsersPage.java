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
import javafx.geometry.Pos;
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
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import messaging.util.*;

/**
 * This page shows the list of users that have requested the reviewer role.
 */
public class ReviewerRequestsUsersPage {
	
	private final User user;
	private String pageTitle;
	private int listIndex = 0;
	private DatabaseHelper db;
	
	/**
	 * Constructs a new page for a given instructor.
	 * 
	 * @param db The application's DatabaseHelper instance.
	 * @param user The current user of the application.
	 */
    public ReviewerRequestsUsersPage(DatabaseHelper db, User user) {
    	this.db = db;
        this.user = user;
        pageTitle = "Reviewer Requests for Instructor: " + user.getUserName();
    }
    
    /**
     * Shows the MessageUserListPage on the given stage.
     * <p>
     * The application's primaryStage is recommended.
     * 
     * @param primaryStage The stage that this page will be shown on.
     */
    public void show(Stage primaryStage) {
    	VBox layout = new VBox(5);
    	
    	// Create the scrollable pane
    	VBox content = new VBox(3);
        ScrollPane scroller = new ScrollPane(content);
        scroller.setPrefHeight(550);
	    layout.setStyle("-fx-alignment: center; -fx-padding: 20;");
	    
	    // Title box
	    HBox topBox = new HBox(3);
	    Label titleLabel = new Label("Reviewer Requests");
	    titleLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
	    titleLabel.setPrefWidth(380);
	    Button refreshButton = new Button("Refresh");
	    Button acctButton = new Button("Account");
	    topBox.setStyle("-fx-alignment: center;");
	    topBox.getChildren().addAll(titleLabel, refreshButton, acctButton);
	    
	    // Error label
	    Label errorLabel = new Label("");
	    errorLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: red");
        
        // Add users who have requested the reviewer role
	    ArrayList<String> requests;
		try {
			requests = db.getReviewerRequests();
		} catch (SQLException e) {
			e.printStackTrace();
			requests = new ArrayList<String>();
		}
        for (int i = 0; i < requests.size(); i++) {
    		addToList(requests.get(i), content);
    	}
	    
        // ACTION LISTENERS
        
        // Refresh the page
        refreshButton.setOnAction(e -> {
        	new ReviewerRequestsUsersPage(db, user).show(primaryStage);
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
        layout.getChildren().addAll(topBox, errorLabel, scroller);
	    Scene messagesListScene = new Scene(layout, 600, 700);
        
	    // Set the scene to primary stage
	    primaryStage.setScene(messagesListScene);
	    primaryStage.setTitle("Q&A - " + pageTitle);
    	primaryStage.show();
    }
    
    /**
     * Adds listUser to the scrollable pane that houses content.
     * 
     * @param listUser The user to be listed.
     * @param content A VBox housed by the scrollable pane.
     */
    private void addToList(String listUser, VBox content) {
        HBox listedUser = new HBox();
        if (listIndex % 2 == 0) {
        	listedUser.setStyle("-fx-background: rgb(210, 210, 210); -fx-background-color: -fx-background;");
        }
        else {
        	listedUser.setStyle("-fx-background: rgb(225, 225, 225); -fx-background-color: -fx-background;");
        }
        listIndex++;
        listedUser.setPrefSize(550,40);
        Hyperlink userName = new Hyperlink(listUser);
        
        userName.setOnAction(a -> {
        	new ReviewerRequestsPage(db, user, listUser).show();
        });
        
        userName.setPrefWidth(400);
        listedUser.getChildren().addAll(userName);
        // Add to top of list
        content.getChildren().add(0, listedUser);
    }
    
}