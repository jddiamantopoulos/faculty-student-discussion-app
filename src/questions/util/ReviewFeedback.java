package questions.util;

/**
 * This file contains the class that shows the feedback left on a review by a student
 * feedback id, the review that has feedback, the student who gave the feedback, and the text of the feedback
 */
public class ReviewFeedback {
	private int feedbackId;
	private int reviewId;
	private int studentId;
	private String feedbackText;
	private String feedbackBy;
	
	/**
	 * Constructs a new object with the feedback details to go through with it
	 * @param feedbackId id of feedback
	 * @param reviewId id of review that is connected with feedback
	 * @param studentId id of student who left feedback
	 * @param feedbackText text of the feedback
	 */
	public ReviewFeedback (int feedbackId, int reviewId, int studentId, String feedbackText) {
		this.feedbackId = feedbackId;
		this.reviewId = reviewId;
		this.studentId = studentId;
		this.feedbackText = feedbackText;
	}
	
	public ReviewFeedback(String feedbackBy, String feedbackText) {
		this.feedbackBy = feedbackBy; 
		this.feedbackText = feedbackText;
	}
  
	//getters
	public int getFeedbackId() { return feedbackId; }
	public int getReviewId() { return reviewId; }
	public int getStudentId() { return studentId; }
	public String getFeedbackText() { return feedbackText; }
	public String getFeedbackBy() { return feedbackBy; }
}
