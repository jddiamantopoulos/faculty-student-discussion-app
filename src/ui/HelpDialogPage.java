package ui;

import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class HelpDialogPage {
	
	public HelpDialogPage() {
		// Nothing to construct
	}
	
	public void show(Stage newStage) {
		VBox helpItems = new VBox(5);
		helpItems.setStyle("-fx-padding: 10;");
		
		Label header = new Label("How to use the Q&A page:");
		header.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");
		Separator space1 = new Separator();
		
		// Searches
		Label searchTitle = new Label("Searching - View a subset of the available questions");
		searchTitle.setStyle("-fx-font-size: 14px;");
		Label searchL1 = new Label("The \"Search Type\" menu lets you specify what type of search you want to perform (required for all searches).");
		searchL1.setStyle("-fx-font-size: 12px;");
		Label searchL2 = new Label("The search bar lets you specify the author or title of the question you want to find (author/question search types).");
		searchL2.setStyle("-fx-font-size: 12px;");
		Label searchL3 = new Label("The \"tags\" box lets you look for a specific question tag (tag search only).");
		searchL3.setStyle("-fx-font-size: 12px;");
		Label searchL4 = new Label("When you've finished entering your search parameters, just hit search! Then, refresh to clear the search.");
		searchL4.setStyle("-fx-font-size: 12px;");
		Separator space2 = new Separator();
		
		// View Question
		Label viewTitle = new Label("Viewing - Select a question and see its answers");
		viewTitle.setStyle("-fx-font-size: 14px;");
		Label viewL1 = new Label("Simply quick on the blue link to go to a question! There, you can see any available answers.");
		viewL1.setStyle("-fx-font-size: 12px;");
		Label viewL2 = new Label("If the link is bold, it's still unanswered. Italic links have been answered.");
		viewL2.setStyle("-fx-font-size: 12px;");
		Label viewL3 = new Label("Underneath, you can see the username of the author and any associated tags.");
		viewL3.setStyle("-fx-font-size: 12px;");
		Separator space3 = new Separator();
		
		// Writing Questions
		Label writeTitle = new Label("Writing - Create your own question");
		writeTitle.setStyle("-fx-font-size: 14px;");
		Label writeL1 = new Label("First, enter a quick summary of your question or relevant keywords (e.g. \"assignment 1\"");
		writeL1.setStyle("-fx-font-size: 12px;");
		Label writeL2 = new Label("Then, enter more details in the body.");
		writeL2.setStyle("-fx-font-size: 12px;");
		Label writeL3 = new Label("Finally, select the relevant tags, and hit post!");
		writeL3.setStyle("-fx-font-size: 12px;");
		Separator space4 = new Separator();
		
		helpItems.getChildren().addAll(header, space1, searchTitle, searchL1, searchL2, searchL3, searchL4,
				space2, viewTitle, viewL1, viewL2, viewL3,
				space3, writeTitle, writeL1, writeL2, writeL3, space4);
		Scene helpScene = new Scene(helpItems, 620, 500);
        
	    // Set the scene to new stage
	    newStage.setScene(helpScene);
	    newStage.setTitle("HW2 - " + "Help");
    	newStage.show();
	}
}