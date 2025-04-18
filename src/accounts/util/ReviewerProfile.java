package accounts.util;

import java.util.ArrayList;
import java.util.List;
import questions.util.Review;

/**
 * Represents a reviewer's profile including their bio, areas of expertise,
 * past reviews, and student feedback.
 */
public class ReviewerProfile {
    private String username;
    private String bio;
    private List<String> expertiseAreas;
    private List<Review> pastReviews;
    private List<String> studentFeedback;

    /**
     * Constructs a new reviewer profile.
     * @param username The username of the reviewer
     */
    public ReviewerProfile(String username) {
        this.username = username;
        this.bio = "";
        this.expertiseAreas = new ArrayList<>();
        this.pastReviews = new ArrayList<>();
        this.studentFeedback = new ArrayList<>();
    }

    // Getters
    public String getUsername() { return username; }
    public String getBio() { return bio; }
    public List<String> getExpertiseAreas() { return expertiseAreas; }
    public List<Review> getPastReviews() { return pastReviews; }
    public List<String> getStudentFeedback() { return studentFeedback; }

    // Setters
    public void setBio(String bio) { this.bio = bio; }
    public void setExpertiseAreas(List<String> areas) { this.expertiseAreas = areas; }
    public void setPastReviews(List<Review> reviews) { this.pastReviews = reviews; }
    public void setStudentFeedback(List<String> feedback) { this.studentFeedback = feedback; }

    // Helper methods
    public void addExpertiseArea(String area) { 
        if (!expertiseAreas.contains(area)) {
            expertiseAreas.add(area);
        }
    }

    public void removeExpertiseArea(String area) {
        expertiseAreas.remove(area);
    }

    public void addPastReview(Review review) {
        pastReviews.add(review);
    }

    public void addStudentFeedback(String feedback) {
        studentFeedback.add(feedback);
    }
}