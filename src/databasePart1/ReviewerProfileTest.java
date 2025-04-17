package databasePart1;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;
import accounts.util.ReviewerProfile;
import questions.util.Review;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ReviewerProfileTest {
    private DatabaseHelper dbHelper;
    private ReviewerProfile testProfile;
    
    @Before
    public void setUp() throws SQLException {
        dbHelper = new DatabaseHelper();
        testProfile = new ReviewerProfile("testReviewer");
        testProfile.setBio("Test bio");
        testProfile.addExpertiseArea("Java");
        testProfile.addExpertiseArea("Python");
        testProfile.addStudentFeedback("Great reviewer!");
    }
    
    @Test
    public void testCreateAndRetrieveProfile() throws SQLException {
        // Create profile
        dbHelper.createReviewerProfile(testProfile);
        
        // Retrieve profile
        ReviewerProfile retrievedProfile = dbHelper.getReviewerProfile("testReviewer");
        
        // Verify profile data
        assertNotNull("Retrieved profile should not be null", retrievedProfile);
        assertEquals("Username should match", testProfile.getUsername(), retrievedProfile.getUsername());
        assertEquals("Bio should match", testProfile.getBio(), retrievedProfile.getBio());
        assertEquals("Expertise areas should match", testProfile.getExpertiseAreas(), retrievedProfile.getExpertiseAreas());
        assertEquals("Student feedback should match", testProfile.getStudentFeedback(), retrievedProfile.getStudentFeedback());
    }
    
    @Test
    public void testUpdateProfile() throws SQLException {
        // Create initial profile
        dbHelper.createReviewerProfile(testProfile);
        
        // Update profile
        testProfile.setBio("Updated bio");
        testProfile.addExpertiseArea("JavaScript");
        dbHelper.updateReviewerProfile(testProfile);
        
        // Retrieve updated profile
        ReviewerProfile retrievedProfile = dbHelper.getReviewerProfile("testReviewer");
        
        // Verify updates
        assertEquals("Bio should be updated", "Updated bio", retrievedProfile.getBio());
        assertTrue("Should contain new expertise area", retrievedProfile.getExpertiseAreas().contains("JavaScript"));
    }
    
    @Test
    public void testDeleteProfile() throws SQLException {
        // Create profile
        dbHelper.createReviewerProfile(testProfile);
        
        // Delete profile
        dbHelper.deleteReviewerProfile("testReviewer");
        
        // Try to retrieve deleted profile
        ReviewerProfile retrievedProfile = dbHelper.getReviewerProfile("testReviewer");
        
        // Verify deletion
        assertNull("Profile should be deleted", retrievedProfile);
    }
    
    @Test
    public void testProfileWithReviews() throws SQLException {
        // Create profile with reviews
        Review review = new Review(1, "testReviewer", 1, "testContent", false);
        testProfile.addPastReview(review);
        dbHelper.createReviewerProfile(testProfile);
        
        // Retrieve profile
        ReviewerProfile retrievedProfile = dbHelper.getReviewerProfile("testReviewer");
        
        // Verify reviews
        assertNotNull("Past reviews should not be null", retrievedProfile.getPastReviews());
        assertFalse("Should have at least one review", retrievedProfile.getPastReviews().isEmpty());
    }
}