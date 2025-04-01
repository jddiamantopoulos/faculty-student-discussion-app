package accounts.util;

import java.util.ArrayList;

/**
 * The User class represents a user entity in the system.
 * It contains the user's details such as userName, password, role, name, and email.
 */
public class User {
    private String userName;
    private String password;
    private String role;
    private String name;
    private String email;
    private ArrayList<Reviewer> reviewers;

    /**
     * Constructs a new user. Inputs should be validated BEFORE use.
     * @param userName The userName to be used.
     * @param password The password to be used.
     * @param role The user's role (from invite code).
     */
    public User(String userName, String password, String role) {
        this.userName = userName;
        this.password = password;
        this.role = role;
        this.name = "";
        this.email = "";
        this.reviewers = new ArrayList<Reviewer>(); /* Might want to change to pull in reviewers from DB */
    }
    
    /**
     * Constructs a new user with more control over user info. Inputs should be
     * validated BEFORE use.
     * @param userName The user's provided username.
     * @param password The user's provided password.
     * @param role The user's role (from invite code).
     * @param name The user's provided name.
     * @param email The user's provided email.
     */
    public User(String userName, String password, String role, String name, String email) {
        this.userName = userName;
        this.password = password;
        this.role = role;
        this.name = name;
        this.email = email;
    }
    
    // Setters
    public void setRole(String role) { this.role = role; }
    public void setName(String name) { this.name = name; }
    public void setEmail(String email) { this.email = email; }
    public void setPassword(String password) { this.password = password; }
    public void setReviewersList(ArrayList<Reviewer> list) { reviewers = list; }
    /* There is no username setter since this should never be changed */

    // Getters
    public String getUserName() { return userName; }
    public String getPassword() { return password; }
    public String getRole() { return role; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public ArrayList<Reviewer> getReviewers() { return reviewers; }
    
    /**
     * Parses the custom format for reviewer scores to the correct data structure.
     * <p>
     * String format: "[name],[score];[name],[score];"
     * @param dbReviewerString The string stored in the database
     * @return An ArrayList of reviewers.
     */
    public void setReviewers(String dbReviewerString) {
    	ArrayList<Reviewer> newReviewers = new ArrayList<Reviewer>();
    	if (dbReviewerString == null) {
    		this.reviewers = newReviewers;
    		return;
    	}
    	boolean onName = true;
    	String scoreParsed = "";
    	String nameParsed = "";
    	for (int i = 0; i < dbReviewerString.length(); i++) {
    		if (dbReviewerString.charAt(i) == ',') {
    			onName = false;
    		}
    		else if (dbReviewerString.charAt(i) == ';') {
    			onName = true;
    			newReviewers.add(new Reviewer(nameParsed, Integer.parseInt(scoreParsed)));
    			nameParsed = "";
    			scoreParsed = "";
    		}
    		else if (onName) {
    			nameParsed += dbReviewerString.charAt(i);
    		}
    		else {
    			scoreParsed += dbReviewerString.charAt(i);
    		}
    	}
    	
    	reviewers = newReviewers;
    }
    
    /**
     * Returns the DB formatted string of reviewers and their score.
     * @return The DB formatted string of reviewers and their score.
     */
    public String getReviewersAsString() {
    	String output = "";
    	for (int i = 0; i < reviewers.size(); i++) {
    		output += reviewers.get(i).getUsername();
    		output += ',';
    		output += reviewers.get(i).getScore();
    		output += ';';
    	}
    	return output;
    }
}
