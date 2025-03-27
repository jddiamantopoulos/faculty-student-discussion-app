package common.ui;

import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.event.ActionEvent;
import common.util.*;

public class DialogPage {
	
	DialogReturns returnState = DialogReturns.xOut;
	
	public DialogPage() {
		// Nothing to construct
	}
	
	public DialogReturns show(DialogTypes dialogType, String pageTitle, String message) {
		Stage newStage = new Stage();
		VBox dialog = new VBox(5);
		dialog.setStyle("-fx-alignment: center; -fx-padding: 2;");
		Label messageLabel = new Label();
		messageLabel.setText(message);
		messageLabel.setWrapText(true);
		messageLabel.setStyle("-fx-alignment: center; -fx-padding: 2; -fx-font-size: 16px;");
		dialog.getChildren().add(messageLabel);
		
		switch (dialogType) {
			case oneButtonOkay:
				Button okayButton = new Button("Okay");
				okayButton.setOnAction(e -> {
					returnState = DialogReturns.okay;
					newStage.close();
				});
				okayButton.setStyle("-fx-alignment: center; -fx-padding: 2;");
				dialog.getChildren().add(okayButton);
				break;
				
			case twoButtonYesNo:
				HBox buttonBox = new HBox(3);
				Button yesButton = new Button("Yes");
				Button noButton = new Button("No");
				yesButton.setOnAction(e -> {
					returnState = DialogReturns.yes;
					newStage.close();
				});
				buttonBox.getChildren().add(yesButton);
				noButton.setOnAction(e -> {
					returnState = DialogReturns.no;
					newStage.close();
				});
				buttonBox.setStyle("-fx-alignment: center; -fx-padding: 2;");
				buttonBox.getChildren().add(noButton);
				dialog.getChildren().add(buttonBox);
				break;
				
			default:
				System.err.println("This kind of dialog type is unsupported.");
				return DialogReturns.xOut;
		}
		
		Scene helpScene = new Scene(dialog, 300, 150);
        
	    // Set the stage to new stage
	    newStage.setScene(helpScene);
	    newStage.setTitle("Q&A - " + pageTitle);
    	newStage.showAndWait();
    	
    	return returnState;
	}
}