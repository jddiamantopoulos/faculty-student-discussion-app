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
import questions.util.Review;

/**
 * This page shows the list of users with the reviewer role.
 */
public class ReviewerScorecardPage {
	
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
    public ReviewerScorecardPage(DatabaseHelper db, User user) {
    	this.db = db;
        this.user = user;
        pageTitle = "Reviewer Scorecards - " + user.getUserName();
    }
    
    /**
     * Shows the ReviewerScorecardPage on the given stage.
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
	    titleLabel.setPrefWidth(200);
	    Button refreshButton = new Button("Refresh");
	    Button acctButton = new Button("Account");
	    Button useAlgo = new Button("Calculate from Algorithm");
	    topBox.setStyle("-fx-alignment: center;");
	    topBox.getChildren().addAll(titleLabel, useAlgo, refreshButton, acctButton);
	    
	    // Error label
	    Label errorLabel = new Label("");
	    errorLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: red");
        
        // Add reviewers
	    final ArrayList<Reviewer> reviewers = new ArrayList<Reviewer>();
		try {
			ArrayList<String> reviewersNames = db.getReviewers();
			for (int i = 0; i < reviewersNames.size(); i++) {
				reviewers.add(new Reviewer(reviewersNames.get(i), db.getReviewerScorecard(reviewersNames.get(i))));
			}
		} catch (SQLException e) {
			e.printStackTrace();
			reviewers.clear();
		}
        for (int i = 0; i < reviewers.size(); i++) {
    		addToList(reviewers.get(i), content, errorLabel);
    	}
	    
        // ACTION LISTENERS
        
        // Refresh the page
        refreshButton.setOnAction(e -> {
        	new ReviewerScorecardPage(db, user).show(primaryStage);
        });
        
        acctButton.setOnAction(e -> {
        	if (user.getRole().equals("admin")) {
        		new AdminHomePage(db, user).show(primaryStage);
        	}
        	else {
        		new UserHomePage(db, user).show(primaryStage);
        	}
        });
        
        useAlgo.setOnAction(a -> {
	        if (calculateReviewerScorecards(reviewers)) {
				errorLabel.setText("Scorecard updated successfully! (Refresh to see)");
				errorLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: green");
			} 
			else {
				errorLabel.setText("Unable to update scorecard.");
				errorLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: red");
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
        Label userName = new Label(reviewer.getUsername());
        
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
        			if (db.updateReviewerScorecard(reviewer.getUsername(), reviewer.getScore())) {
        				errorLabel.setText("Scorecard updated successfully!");
        				errorLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: green");
        			} 
        			else {
        				errorLabel.setText("Unable to update scorecard.");
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
        
        userName.setPrefWidth(400);
        listedUser.getChildren().addAll(userName, updateScore, executeButton);
        // Add to top of list
        content.getChildren().add(0, listedUser);
    }
    
    /**
     * Calculates the reviewer scorecards based on a proprietary algorithm.
     * @param reviewers The list of reviewers.
     * @return True if calculated successfully.
     */
    public boolean calculateReviewerScorecards(ArrayList<Reviewer> reviewers) {
    	try {
	    	long totalLikes = 0;
	    	int numReviewers = reviewers.size();
	    	int[] reviewerLikes = new int[numReviewers];
	    	for (int i = 0; i < numReviewers; i++) {
	    		int userLikes = 0;
	    		ArrayList<Review> reviews = db.getReviewsByAuthor(reviewers.get(i).getUsername());
	    		for (int j = 0; j < reviews.size(); j++) {
	    			userLikes += db.getReviewLikes(reviews.get(j).getReviewId());
	    		}
	    		totalLikes += userLikes;
	    		reviewerLikes[i] = userLikes;
	    	}
	    	for (int i = 0; i < numReviewers; i++) {
	    		double weight = ((double)reviewerLikes[i] / ((double)totalLikes / (double)numReviewers));
	    		
	    		if (weight <= 1.4 && weight >= 0.6) {
	    			db.updateReviewerScorecard(reviewers.get(i).getUsername(), (int)Math.round(weight * 50));
	    		}
	    		else if (weight > 1.4) {
	    			db.updateReviewerScorecard(reviewers.get(i).getUsername(), 70 + Math.abs((int)Math.round((weight - 1.4) * 25)));
	    		}
	    		else {
	    			db.updateReviewerScorecard(reviewers.get(i).getUsername(), 30 - Math.abs((int)Math.round((weight - 0.6) * 25)));
	    		}
	    	}
	    	return true;
    	} catch (SQLException e) {
    		return false;
    	}
    }
}