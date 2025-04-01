package accounts.util;

/**
 * This class represents a reviewer and their score. Implements the Comparable interface for sorting.
 */
public class Reviewer implements Comparable<Reviewer> {
	
	private String username;
	private int reviewerScore;
	
	/**
	 * Constructs a new reviewer.
	 * @param username
	 * @param reviewerScore
	 */
	public Reviewer(String username, int reviewerScore) {
		this.username = username;
		if (reviewerScore >= 100) {
			this.reviewerScore = 100;
		}
		else if (reviewerScore <= 0) {
			this.reviewerScore = 0;
		}
		else {
			this.reviewerScore = reviewerScore;
		}
	}
	
	@Override
	/**
	 * Allows reviewers to be sorted by score.
	 * @param o The other reviewer to be compared against.
	 * @return Positive if this reviewer has a higher score.
	 */
	public int compareTo(Reviewer o) {
		/* This one line will be positive if this score is higher, 
		 * negative if the other is higher, and 0 if they're equal.
		 */
		return reviewerScore - o.reviewerScore;
	}
	
	// Getters
	public String getUsername() {
		return username;
	}
	public int getScore() {
		return reviewerScore;
	}
	
	// Setters
	public void setUsername(String username) {
		this.username = username;
	}
	public void setScore(int reviewerScore) {
		if (reviewerScore >= 100) {
			this.reviewerScore = 100;
		}
		else if (reviewerScore <= 0) {
			this.reviewerScore = 0;
		}
		else {
			this.reviewerScore = reviewerScore;
		}
	}
}