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
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
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
import questions.util.Review;

/**
 * This page shows the list of users that have requested the reviewer role.
 */
public class UpdateReviewerScoresPage {
	
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
    public UpdateReviewerScoresPage(DatabaseHelper db, User user) {
    	this.db = db;
        this.user = user;
        pageTitle = "Reviewer Weighting for " + user.getUserName();
    }
    
    /**
     * Shows the UpdateReviewerScoresPage on the given stage.
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
	    Label titleLabel = new Label("Reviewer Scores");
	    titleLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
	    titleLabel.setPrefWidth(380);
	    Button refreshButton = new Button("Refresh");
	    Button acctButton = new Button("Account");
	    topBox.setStyle("-fx-alignment: center;");
	    topBox.getChildren().addAll(titleLabel, refreshButton, acctButton);
	    
	    // Error label
	    Label errorLabel = new Label("");
	    errorLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: red");
        
        // Add reviewers
	    ArrayList<Reviewer> reviewers;
		try {
			reviewers = db.getAllReviewers(user);
		} catch (SQLException e) {
			e.printStackTrace();
			reviewers = new ArrayList<Reviewer>();
		}
        for (int i = 0; i < reviewers.size(); i++) {
    		addToList(reviewers.get(i), content, errorLabel);
    	}
	    
        // ACTION LISTENERS
        
        // Refresh the page
        refreshButton.setOnAction(e -> {
        	new UpdateReviewerScoresPage(db, user).show(primaryStage);
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
     * Adds a reviewer to the scrollable pane that houses content.
     * 
     * @param reviewer The user to be listed.
     * @param content A VBox housed by the scrollable pane.
     * @param errorLabel A label to display error messages
     */
    private void addToList(Reviewer reviewer, VBox content, Label errorLabel) {
        HBox listedUser = new HBox();
        if (listIndex % 2 == 0) {
        	listedUser.setStyle("-fx-background: rgb(210, 210, 210); -fx-background-color: -fx-background;");
        }
        else {
        	listedUser.setStyle("-fx-background: rgb(225, 225, 225); -fx-background-color: -fx-background;");
        }
        listIndex++;
        listedUser.setPrefSize(550,40);
        
        String bookmarked = "";
        if (db.isReviewerBookmarked(user.getUserName(), reviewer.getUsername())) {
        	bookmarked = "* ";
        }
        
        Hyperlink userName = new Hyperlink(bookmarked + reviewer.getUsername());
        
        TextField updateScore = new TextField();
        updateScore.setText(Integer.toString(reviewer.getScore()));
        updateScore.setPrefWidth(150);
        
        Button executeButton = new Button("Execute Update");
        executeButton.setPrefWidth(150);
        
        executeButton.setOnAction(e -> {
        	String text = updateScore.getText();
        	int newScore;
        	try {
        		newScore = Integer.parseInt(text);
        		if (newScore >= 0 && newScore <= 100) {
        			reviewer.setScore(newScore);
        			try {
        				db.updateReviewers(user);
        				errorLabel.setText("Score updated successfully!");
        				errorLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: green");
        			} catch (SQLException sqlEx) {
        				errorLabel.setText("Unable to update score.");
        				errorLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: red");
        			}
        		}
        		else {
        			errorLabel.setText("You must enter an integer between 0 and 100!");
        			errorLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: red");
        		}
        	} catch (NumberFormatException ex) {
        		errorLabel.setText("You must enter an integer between 0 and 100!");
        		errorLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: red");
        	}
        });
        
        userName.setOnAction(a -> {
        	try {
                ReviewerProfile profile = db.getReviewerProfile(reviewer.getUsername());
                if (profile == null) {
                    Alert createPrompt = new Alert(Alert.AlertType.CONFIRMATION);
                    createPrompt.setTitle("Create Profile");
                    createPrompt.setHeaderText("No reviewer profile found.");
                    createPrompt.setContentText("Creating a default profile for testing.");
                    createPrompt.showAndWait();

                    profile = new ReviewerProfile(reviewer.getUsername());
                    profile.setBio("This is a default bio.");
                    profile.setExpertiseAreas(new ArrayList<>());
                    profile.setPastReviews(new ArrayList<Review>());
                    profile.setStudentFeedback(new ArrayList<>());

                    db.createDefaultReviewerProfile(profile);
                }
                new ViewReviewerProfilePage(db, reviewer.getUsername()).show(new Stage());
            } catch (Exception ex) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Profile Error");
                alert.setHeaderText("Could not load or create reviewer profile");
                alert.setContentText(ex.getMessage());
                alert.showAndWait();
            }
        });
        
        userName.setPrefWidth(400);
        listedUser.getChildren().addAll(userName, updateScore, executeButton);
        // Add to top of list
        content.getChildren().add(0, listedUser);
    }
    
}