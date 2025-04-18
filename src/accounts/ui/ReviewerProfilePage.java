package accounts.ui;

import accounts.util.ReviewerProfile;
import databasePart1.DatabaseHelper;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import questions.util.Review;

import java.sql.SQLException;
import java.util.List;

/**
 * UI class for managing reviewer profiles
 */
public class ReviewerProfilePage {
    private final DatabaseHelper db;
    private final String username;
    private ReviewerProfile profile;
    
    /**
     * Constructs a new reviewer profile page
     * @param db The database helper
     * @param username The username of the reviewer
     */
    public ReviewerProfilePage(DatabaseHelper db, String username) {
        this.db = db;
        this.username = username;
    }
    
    /**
     * Shows the reviewer profile page
     * @param stage The stage to show the page on
     */
    public void show(Stage stage) {
        try {
            profile = db.getReviewerProfile(username);
        } catch (SQLException e) {
            showError("Error loading profile", e.getMessage());
            return;
        }
        
        VBox layout = new VBox(10);
        layout.setPadding(new Insets(10));
        
        // Bio section
        Label bioLabel = new Label("Bio:");
        TextArea bioArea = new TextArea(profile.getBio());
        bioArea.setPrefRowCount(3);
        
        // Expertise areas section
        Label expertiseLabel = new Label("Areas of Expertise:");
        ListView<String> expertiseList = new ListView<>();
        expertiseList.getItems().addAll(profile.getExpertiseAreas());
        
        HBox expertiseControls = new HBox(10);
        TextField newExpertiseField = new TextField();
        newExpertiseField.setPromptText("New expertise area");
        Button addExpertiseButton = new Button("Add");
        Button removeExpertiseButton = new Button("Remove Selected");
        
        addExpertiseButton.setOnAction(e -> {
            String newArea = newExpertiseField.getText().trim();
            if (!newArea.isEmpty()) {
                expertiseList.getItems().add(newArea);
                newExpertiseField.clear();
            }
        });
        
        removeExpertiseButton.setOnAction(e -> {
            String selected = expertiseList.getSelectionModel().getSelectedItem();
            if (selected != null) {
                expertiseList.getItems().remove(selected);
            }
        });
        
        expertiseControls.getChildren().addAll(newExpertiseField, addExpertiseButton, removeExpertiseButton);
        
        // Past reviews section
        Label reviewsLabel = new Label("Past Reviews:");
        ListView<Review> reviewsList = new ListView<>();
        reviewsList.getItems().addAll(profile.getPastReviews());
        reviewsList.setCellFactory(lv -> new ListCell<Review>() {
            @Override
            protected void updateItem(Review review, boolean empty) {
                super.updateItem(review, empty);
                if (empty || review == null) {
                    setText(null);
                } else {
                    setText(review.getReviewText());
                }
            }
        });
        
        // Student feedback section
        Label feedbackLabel = new Label("Student Feedback:");
        ListView<String> feedbackList = new ListView<>();
        feedbackList.getItems().addAll(profile.getStudentFeedback());
        
        // Save button
        Button saveButton = new Button("Save Changes");
        saveButton.setOnAction(e -> {
            try {
                profile.setBio(bioArea.getText());
                profile.setExpertiseAreas(expertiseList.getItems());
                db.updateReviewerProfile(profile);
                showSuccess("Profile updated successfully");
            } catch (SQLException ex) {
                showError("Error saving profile", ex.getMessage());
            }
        });
        
        // Add all components to layout
        layout.getChildren().addAll(
            bioLabel, bioArea,
            expertiseLabel, expertiseList, expertiseControls,
            reviewsLabel, reviewsList,
            feedbackLabel, feedbackList,
            saveButton
        );
        
        Scene scene = new Scene(layout, 600, 800);
        stage.setScene(scene);
        stage.setTitle("Reviewer Profile - " + username);
        stage.show();
    }
    
    private void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    private void showSuccess(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Success");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
} 