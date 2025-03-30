package questions.util;

public class Review {
	private int reviewId;
	private String reviewerName;
	private int qaText;
	private String reviewText;
	private boolean isAnswer;
	
	
	public Review(int reviewId, String reviewerName, int qaText, String reviewText, boolean isAnswer) {
		this.reviewId = reviewId;
		this.reviewerName = reviewerName;
		this.qaText = qaText;
		this.reviewText = reviewText;
		this.isAnswer = isAnswer;
	}
	
	public int getReviewId() { return reviewId; }
	public String getReviewerName() { return reviewerName;}
	public int getqaText() { return qaText;}
	public String getReviewText() { return reviewText; }
	public boolean isAnswer() { return isAnswer; }
	
	public void setReviewText(String reviewText) {this.reviewText = reviewText; }
}
