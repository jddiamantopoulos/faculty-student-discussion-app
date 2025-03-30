
	private DatabaseHelper db;
	private Question parent;
	private Answer ans;
	private User currUser;
	
	
	public ReviewPage(DatabaseHelper db, Question parent, Answer ans, User currUser) {
		this.db = db;
		this.parent = parent;
		this.ans = ans;
		this.currUser = currUser;
	}
	
	public void show(Stage tertiaryStage) {
		VBox layout = new VBox(10);
		
		//review section
		Label revlabel = new Label("Reviews for " + (ans != null ? "Answer: " + parent.getText() : "Question: " + parent.getText()));
		layout.getChildren().add(revlabel);
		
		//sees if review for question or answer
		boolean isAnswer = (ans != null);
		int qaText = isAnswer ? ans.getId() : parent.getId();
		
		
		//gets all the reviews for question or answer
		List<Review> reviews = db.getReviewsQA(qaText, isAnswer);
		
		for (Review review : reviews) {
			VBox reviewBox = new VBox();
			Label reviewText = new Label(review.getReviewerName() + ": " + review.getReviewText());
			Button deleteButton = new Button("Delete Review");
			Button editButton = new Button("Edit Review");
			
			//Edit review
			editButton.setOnAction(e -> {
				TextArea editReviewField = new TextArea(review.getReviewText());
				Button saveEditButton = new Button("Save Edit");
				
				saveEditButton.setOnAction(ev -> {
					String updatedText = editReviewField.getText();
					if (!updatedText.isEmpty() && db.updateReview(review.getReviewId(), updatedText)) {
						reviewText.setText(currUser.getUserName() + ": " + updatedText);
						layout.getChildren().remove(editReviewField);
						layout.getChildren().remove(saveEditButton);
					}
				});
				
				layout.getChildren().addAll(editReviewField, saveEditButton);
			});
			
			//deletes review
			deleteButton.setOnAction(e -> {
				if (db.deleteReview(review.getReviewId())) {
					layout.getChildren().remove(reviewBox);
				}
			});
			reviewBox.getChildren().addAll(reviewText, editButton, deleteButton);
			layout.getChildren().add(reviewBox);
		}
		
		TextArea newReviewField = new TextArea();
		newReviewField.setPromptText("Write a new review...");
		Button submitReviewButton = new Button("Submit Review");
		
		submitReviewButton.setOnAction(e -> {
			String reviewText = newReviewField.getText();
			if(!reviewText.isEmpty()) {
				Review newReview = new Review(0, currUser.getUserName(), qaText, reviewText, isAnswer);
				
				if (db.addReview(currUser.getUserName(), qaText, reviewText, isAnswer)) {
					layout.getChildren().add(new Label(currUser.getUserName() + ": " + reviewText));
					newReviewField.clear();
				}
			}
		});
		
		layout.getChildren().addAll(newReviewField, submitReviewButton);
		
		Scene reviewScene = new Scene(layout, 600, 500);
		tertiaryStage.setScene(reviewScene);
		tertiaryStage.setTitle("Manage Reviews for " + (isAnswer ? "Answer" : "Question"));
		tertiaryStage.show();
	}	
}
