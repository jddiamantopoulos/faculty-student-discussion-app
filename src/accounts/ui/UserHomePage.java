package accounts.ui;

import java.sql.SQLException;
import java.util.ArrayList;

import accounts.util.User;
import accounts.util.ReviewerProfile;
import administration.ui.AdministrationSearchPage;
import administration.ui.ReviewerRequestsUsersPage;
import databasePart1.DatabaseHelper;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import messaging.ui.MessageUserListPage;
import questions.ui.QuestionListPage;
import accounts.ui.ReviewerProfilePage;
import questions.util.Review;

/**
 * This page allows users to navigate to different modules of the application.
 */
public class UserHomePage {
    private DatabaseHelper databaseHelper;
    private User currentUser;

    public UserHomePage(DatabaseHelper databaseHelper, User currentUser) {
        this.databaseHelper = databaseHelper;
        this.currentUser = currentUser;
    }

    public void show(Stage primaryStage) {
        VBox layout = new VBox(5);
        layout.setStyle("-fx-alignment: center; -fx-padding: 20;");

        Label userLabel = new Label("Hello, " +
            (currentUser.getName().isEmpty() ? "User" : currentUser.getName()) + "!");
        userLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        Button back = new Button("Back");
        back.setOnAction(a -> new WelcomeLoginPage(databaseHelper).show(primaryStage, currentUser));

        Button logout = new Button("Logout");
        logout.setOnAction(a -> new SetupLoginSelectionPage(databaseHelper).show(primaryStage));

        Button updateAccountBtn = new Button("Update Account Info");
        updateAccountBtn.setOnAction(a -> new AccountUpdatePage(databaseHelper, currentUser).show(primaryStage));

        Button questionPageButton = new Button("Go to question list");
        questionPageButton.setOnAction(a -> new QuestionListPage(databaseHelper, currentUser).show(primaryStage));

        Button messagePageButton = new Button("Direct Messages");
        messagePageButton.setOnAction(a -> new MessageUserListPage(databaseHelper, currentUser).show(primaryStage));

        Button reviewerScoresPageButton = new Button("Update Reviewer Scores");
        reviewerScoresPageButton.setOnAction(a -> new UpdateReviewerScoresPage(databaseHelper, currentUser).show(primaryStage));

        Button reviewerProfileButton = new Button("Reviewer Profile");
        reviewerProfileButton.setOnAction(e -> {
            try {
                ReviewerProfile profile = databaseHelper.getReviewerProfile(currentUser.getUserName());
                if (profile == null) {
                    Alert createPrompt = new Alert(Alert.AlertType.CONFIRMATION);
                    createPrompt.setTitle("Create Profile");
                    createPrompt.setHeaderText("No reviewer profile found.");
                    createPrompt.setContentText("Creating a default profile for testing.");
                    createPrompt.showAndWait();

                    profile = new ReviewerProfile(currentUser.getUserName());
                    profile.setBio("This is a default bio.");
                    profile.setExpertiseAreas(new ArrayList<>());
                    profile.setPastReviews(new ArrayList<Review>());
                    profile.setStudentFeedback(new ArrayList<>());

                    databaseHelper.createDefaultReviewerProfile(profile);
                }
                ReviewerProfilePage profilePage = new ReviewerProfilePage(databaseHelper, currentUser.getUserName());
                profilePage.show(new Stage());
            } catch (Exception ex) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Profile Error");
                alert.setHeaderText("Could not load or create reviewer profile");
                alert.setContentText(ex.getMessage());
                alert.showAndWait();
            }
        });

        Separator separator = new Separator();

        Button reviewerRequestButton = new Button();
        if (currentUser.getRole().equals("user")) {
            reviewerRequestButton.setText("Request Reviewer Role");
        } else if (currentUser.getRole().equals("staff") || currentUser.getRole().equals("instructor") || currentUser.getRole().equals("admin")) {
            reviewerRequestButton.setText("View Reviewer Requests");
        }

        reviewerRequestButton.setOnAction(a -> {
            if (currentUser.getRole().equals("user")) {
                try {
                    databaseHelper.requestReviewerRole(currentUser.getUserName());
                    userLabel.setText("Request sent!");
                } catch (SQLException e) {
                    e.printStackTrace();
                    userLabel.setText("The request could not be sent. You may have already requested the role.");
                }
            } else {
                new ReviewerRequestsUsersPage(databaseHelper, currentUser).show(primaryStage);
            }
        });

        Button moderationButton = new Button("Moderation Home");
        moderationButton.setOnAction(a -> new AdministrationSearchPage(databaseHelper, currentUser).show(primaryStage));

        layout.getChildren().addAll(userLabel, questionPageButton, messagePageButton, reviewerRequestButton);
        if (currentUser.getRole().equals("staff") || currentUser.getRole().equals("admin")) {
            layout.getChildren().add(moderationButton);
        }

        if (currentUser.getRole().equals("reviewer")) {
            layout.getChildren().add(reviewerProfileButton);
        }

        layout.getChildren().addAll(separator, reviewerScoresPageButton, updateAccountBtn, back, logout);

        if (currentUser.getRole().equals("reviewer")) {
            layout.getChildren().remove(reviewerRequestButton);
        }

        Scene userScene = new Scene(layout, 800, 400);
        primaryStage.setScene(userScene);
        primaryStage.setTitle("User Page");
    }
}


