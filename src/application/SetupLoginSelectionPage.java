package application;

import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import databasePart1.DatabaseHelper;

/**
 * The SetupLoginSelectionPage class allows users to choose between setting up a new account
 * or logging into an existing account. It provides two buttons for navigation to the respective pages.
 */
public class SetupLoginSelectionPage {
	
    private final DatabaseHelper databaseHelper;

    public SetupLoginSelectionPage(DatabaseHelper databaseHelper) {
        this.databaseHelper = databaseHelper;
    }

    public void show(Stage primaryStage) {
        // First check if any users exist in the database
        if (!databaseHelper.hasUsers()) {
            // If no users exist, go directly to admin setup
            new AdminSetupPage(databaseHelper).show(primaryStage);
            return;
        }

        // If users exist, show the normal selection page
        Label titleLabel = new Label("Welcome to Account Management");
        titleLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");
        
        Button setupButton = new Button("Setup New Account");
        setupButton.setStyle("-fx-font-size: 14px; -fx-min-width: 150px;");
        
        Button loginButton = new Button("Login");
        loginButton.setStyle("-fx-font-size: 14px; -fx-min-width: 150px;");
        
        setupButton.setOnAction(event -> {
            new SetupAccountPage(databaseHelper).show(primaryStage);
        });
        
        loginButton.setOnAction(event -> {
            new UserLoginPage(databaseHelper).show(primaryStage);
        });

        // Create vertical layout with spacing and padding
        VBox layout = new VBox(20); // 20 pixels spacing between elements
        layout.setStyle("-fx-padding: 40; -fx-alignment: center;");
        layout.getChildren().addAll(titleLabel, setupButton, loginButton);

        // Create scene with proper size
        Scene scene = new Scene(layout, 800, 400);
        
        // Set the stage
        primaryStage.setScene(scene);
        primaryStage.setTitle("Account Setup");
        primaryStage.show();
    }
}
