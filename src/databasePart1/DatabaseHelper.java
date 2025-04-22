package databasePart1;

import java.sql.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import accounts.util.EmailValidator;
import accounts.util.Reviewer;
import accounts.util.ReviewerProfile;
import accounts.util.User;
import messaging.util.*;
import questions.util.*;
import taskmessaging.util.*;

/**
 * The DatabaseHelper class is responsible for managing the connection to the
 * database, performing operations such as user registration, login validation,
 * and handling invitation codes.
 */
public class DatabaseHelper {

	// JDBC driver name and database URL
	static final String JDBC_DRIVER = "org.h2.Driver";
	static final String DB_URL = "jdbc:h2:~/FoundationDatabase";

	// Database credentials
	static final String USER = "sa";
	static final String PASS = "";
	
	public static int questionKey = 1;
	public static int answerKey = 1;
	public static int messageKey = 1;
	public static int taskMessageKey = 1;

	private Connection connection = null;
	private Statement statement = null;
	
	/**
	 * Connect to the database and create the framework if not found.
	 * @throws SQLException Thrown if a connection is not made.
	 */
	public void connectToDatabase() throws SQLException {
		try {
			Class.forName(JDBC_DRIVER); // Load the JDBC driver
			System.out.println("Connecting to database...");
			connection = DriverManager.getConnection(DB_URL, USER, PASS);
			statement = connection.createStatement();
			// You can use this command to clear the database and restart from fresh.
			//statement.execute("DROP ALL OBJECTS");
			createTables(); // Create the necessary tables if they don't exist
		} catch (ClassNotFoundException e) {
			System.err.println("JDBC Driver not found: " + e.getMessage());
		}
	}
	
	/**
	 * Drops all objects from the database.
	 */
	public void clear() {
		try {
			statement.execute("DROP ALL OBJECTS");
			questionKey = 1;
			answerKey = 1;
			messageKey = 1;
			createTables();
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Creates the tables if not found.
	 * @throws SQLException Should be handled internally, but exists to catch just in case.
	 */
	private void createTables() throws SQLException {
		// First check if we need to update existing table
		try {
			// Check at least one table per update
			statement.executeQuery("SELECT name FROM cse360users LIMIT 1");
			statement.executeQuery("SELECT text FROM questions LIMIT 1");
			statement.executeQuery("SELECT text FROM messages LIMIT 1");
			statement.executeQuery("SELECT text FROM taskMessages LIMIT 1");
			statement.executeQuery("SELECT text FROM reviews LIMIT 1");
		} catch (SQLException e) {
			try {
				// Create tables if they don't exist
				String userTable = "CREATE TABLE IF NOT EXISTS cse360users (" + "id INT AUTO_INCREMENT PRIMARY KEY, "
						+ "userName VARCHAR(255) UNIQUE, " + "password VARCHAR(255), " + "role VARCHAR(20), "
						+ "name VARCHAR(255), " + "email VARCHAR(255), " + "reviewerScores VARCHAR(2000))";
				statement.execute(userTable);

				// Create the invitation codes table
				String invitationCodesTable = "CREATE TABLE IF NOT EXISTS InvitationCodes ("
						+ "code VARCHAR(10) PRIMARY KEY," + "role VARCHAR(127), " + "isUsed BOOLEAN DEFAULT FALSE)";
				statement.execute(invitationCodesTable);

				// Create the questions table
				String questionsTable = "CREATE TABLE IF NOT EXISTS questions (" + "id INT PRIMARY KEY,"
						+ "text VARCHAR(255)," + "body VARCHAR(2000), " + "author VARCHAR(16), " + "tags VARCHAR(64))"; // CSV:
																														// Actually
																														// only
																														// 53
																														// at
																														// this
																														// point,
																														// 64
																														// is
																														// for
																														// safety
				statement.execute(questionsTable);

				// Create the answers table
				String answersTable = "CREATE TABLE IF NOT EXISTS answers (" + "id INT PRIMARY KEY,"
						+ "question VARCHAR(255)," + "text VARCHAR(2000), " + "author VARCHAR(16), "
						+ "votes VARCHAR(1700))"; // CSV
				statement.execute(answersTable);
				
				// Create the messages table
				String messagesTable = "CREATE TABLE IF NOT EXISTS messages (" + "id INT PRIMARY KEY,"
						+ "text VARCHAR(3000)," + "sender VARCHAR(16), " + "recipient VARCHAR(16), "
						+ "isread BIT," + "time VARCHAR(20))";
				statement.execute(messagesTable);
				
				// Create the task messages table
				String taskMessagesTable = "CREATE TABLE IF NOT EXISTS taskMessages (" + "id INT PRIMARY KEY,"
						+ "request VARCHAR(16)," + "requester VARCHAR(16), " + "sender VARCHAR(16), " + "text VARCHAR(500), "
						+ "requestisopen BIT," + "time VARCHAR(20))";
				statement.execute(taskMessagesTable);
				
				// Create the reviewers table
				String reviewersTable = "CREATE TABLE IF NOT EXISTS reviewerRequests (" 
						+ "username VARCHAR(16) UNIQUE)";
				statement.execute(reviewersTable);
				
				// Create the reviews table
				String reviewsTable = "CREATE TABLE IF NOT EXISTS reviews (" 
						+ "reviewId INT AUTO_INCREMENT PRIMARY KEY, " + "reviewerName VARCHAR(16), "
						+ "answerId INT, " + "questionId INT, " + "reviewText VARCHAR(2000))";
				statement.execute(reviewsTable);
				
				//adds likeNum column if it doesn't exist
				try {
					statement.executeQuery("SELECT likeNum FROM reviews LIMIT 1");
	           		} catch (SQLException e3) {
	                	    String alterReviewsTable = "ALTER TABLE reviews ADD COLUMN likeNum INT DEFAULT 0";
	                            statement.execute(alterReviewsTable);
	                            System.out.println("Added 'likeNum' column to reviews table.");
				}
				// Create the reviewer profiles table
				String reviewerProfilesTable = "CREATE TABLE IF NOT EXISTS reviewer_profiles (" +
						"username VARCHAR(16) PRIMARY KEY, " +
						"bio VARCHAR(2000), " +
						"expertise_areas VARCHAR(2000), " +
						"student_feedback VARCHAR(2000))";
				statement.execute(reviewerProfilesTable);
				
				// Create table for review likes 
				String reviewLikesTable = "CREATE TABLE IF NOT EXISTS reviewLikes ("
				        + "reviewId INT, " + "likedBy VARCHAR(16), " + "PRIMARY KEY (reviewId, likedBy))";
				statement.execute(reviewLikesTable);

				// Create table for review feedback
				String reviewFeedbackTable = "CREATE TABLE IF NOT EXISTS reviewFeedback ("
				        + "feedbackId INT AUTO_INCREMENT PRIMARY KEY, " + "reviewId INT, " 
						+ "feedbackBy VARCHAR(16), " + "feedbackText VARCHAR(2000))";
				statement.execute(reviewFeedbackTable);
				
			} catch (SQLException e2) {
				System.err.println("Multiple database errors.");
				e2.printStackTrace();
				e.printStackTrace();
				System.exit(1);
			}
		}
	}

	/**
	 * Checks if the database is empty.
	 * @return Is the database empty?
	 * @throws SQLException Thrown if there is an issue with the connection.
	 */
	public boolean isDatabaseEmpty() throws SQLException {
		String query = "SELECT COUNT(*) AS count FROM cse360users";
		ResultSet resultSet = statement.executeQuery(query);
		if (resultSet.next()) {
			return resultSet.getInt("count") == 0;
		}
		return true;
	}

	/**
	 * Registers a new user. Input validation should be handled by the caller.
	 * @param user The user to be registered.
	 * @throws SQLException Thrown if there is an issue with the connection.
	 */
	public void register(User user) throws SQLException {
		String insertUser = "INSERT INTO cse360users (userName, password, role, name, email) VALUES (?, ?, ?, ?, ?)";
		try (PreparedStatement pstmt = connection.prepareStatement(insertUser)) {
			pstmt.setString(1, user.getUserName());
			pstmt.setString(2, user.getPassword());
			pstmt.setString(3, user.getRole());
			pstmt.setString(4, user.getName());
			pstmt.setString(5, user.getEmail());
			pstmt.executeUpdate();
		}
	}

	/**
	 * Log in as a user.
	 * @param user The user to be logged in as.
	 * @return True if successful, false if not.
	 * @throws SQLException Thrown if there is an issue with the connection.
	 */
	public boolean login(User user) throws SQLException {
		String query = "SELECT * FROM cse360users WHERE userName = ? AND password = ? AND role = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setString(1, user.getUserName());
			pstmt.setString(2, user.getPassword());
			pstmt.setString(3, user.getRole());
			try (ResultSet rs = pstmt.executeQuery()) {
				if (rs.next()) {
					// Update the user object with name and email
					String name = rs.getString("name");
					String email = rs.getString("email");
					String reviewers = rs.getString("reviewerScores");
					user.setName(name != null ? name : "");
					user.setEmail(email != null ? email : "");
					user.setReviewers(reviewers);
					return true;
				} else {
					return false;
				}
			}
		}
	}

	/**
	 * Determines if the database has any users.
	 * @return True if there are any, false if empty.
	 */
	public boolean hasUsers() {
		try {
			String query = "SELECT COUNT(*) FROM cse360users";
			java.sql.ResultSet rs = statement.executeQuery(query);
			if (rs.next()) {
				return rs.getInt(1) > 0;
			}
		} catch (SQLException e) {
			System.err.println("Error checking for users: " + e.getMessage());
		}
		return false;
		
	}

	/**
	 * Checks if a user already exists in the database based on their userName.
	 * @param userName The user to be checked.
	 * @return True if user exists.
	 */
	public boolean doesUserExist(String userName) {
		String query = "SELECT COUNT(*) FROM cse360users WHERE userName = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {

			pstmt.setString(1, userName);
			ResultSet rs = pstmt.executeQuery();

			if (rs.next()) {
				// If the count is greater than 0, the user exists
				return rs.getInt(1) > 0;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false; // If an error occurs, assume user doesn't exist
	}

	/**
	 * Add method to get full user information
	 * @param userName The user to be looked up.
	 * @return A user based on that username.
	 * @throws SQLException Should be handled internally, just there for coverage.
	 */
	public User getUser(String userName) throws SQLException {
		String query = "SELECT * FROM cse360users WHERE userName = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setString(1, userName);
			ResultSet rs = pstmt.executeQuery();
			if (rs.next()) {
				String name = rs.getString("name");
				String email = rs.getString("email");
				return new User(rs.getString("userName"), rs.getString("password"), rs.getString("role"),
						name != null ? name : "", email != null ? email : "");
			}
		}
		return null;
	}

	/**
	 * Retrieves the role of a user from the database using their UserName.
	 * @param userName The user that the role should be retrieved for.
	 * @return A string specifying the user's role.
	 */
	public String getUserRole(String userName) {
		String query = "SELECT role FROM cse360users WHERE userName = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setString(1, userName);
			ResultSet rs = pstmt.executeQuery();

			if (rs.next()) {
				return rs.getString("role"); // Return the role if user exists
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null; // If no user exists or an error occurs
	}
	
	/**
	 * Sets the role of a user from the database using their UserName.
	 * @param userName The user to be updated
	 * @param role The role to be set
	 */
	public void setUserRole(String userName, String role) {
		String query = "UPDATE cse360users SET role = ? WHERE userName = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setString(1, role);
			pstmt.setString(2, userName);
			pstmt.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Retrieves the role associated with a given invite code.
	 * @param code The invitation code to be used
	 * @return A string specifying the role attached to the code.
	 */
	public String getAssociatedRole(String code) {
		String query = "SELECT role FROM invitationCodes WHERE code = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setString(1, code);
			ResultSet rs = pstmt.executeQuery();

			if (rs.next()) {
				return rs.getString("role");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Update the information for a given user
	 * @param user The user to be updated.
	 * @throws SQLException Should be handled internally.
	 */
	public void updateUserInfo(User user) throws SQLException {
		String query = "UPDATE cse360users SET name = ?, email = ?, password = ? WHERE userName = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setString(1, user.getName());
			pstmt.setString(2, user.getEmail());
			pstmt.setString(3, user.getPassword());
			pstmt.setString(4, user.getUserName());
			pstmt.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 *  Gets all users from the database.
	 *  @throws SQLException Should be handled internally.
	 *  @return A list of all users in the database.
	 */
	public ArrayList<String> getAllUsers() throws SQLException {
		String getUsers = "SELECT * FROM cse360users";
		ArrayList<String> users = new ArrayList<String>();
		try (ResultSet rs = statement.executeQuery(getUsers);) {
			while (rs.next()) {
				users.add(rs.getString("userName"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return users;
	}

	/*
	 * QUESTION METHODS
	 */

	/**
	 * Insert a new question to the database.
	 * @param q The question to be inserted.
	 * @throws SQLException Should be handled internally.
	 */
	public void insertQuestion(Question q) throws SQLException {
		String insertQuestion = "INSERT INTO questions (id, text, body, author, tags) VALUES (?, ?, ?, ?, ?)";
		try (PreparedStatement pstmt = connection.prepareStatement(insertQuestion)) {
			pstmt.setInt(1, q.getKey());
			pstmt.setString(2, q.getText());
			pstmt.setString(3, q.getBody());
			pstmt.setString(4, q.getAuthor());
			pstmt.setString(5, q.getTagsCSV());
			pstmt.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Update a question by title
	 * @param q The question to be updated
	 */
	public void updateQuestion(Question q) {
		String insertQuestion = "UPDATE questions SET body = ?, author = ?, tags = ? WHERE text = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(insertQuestion)) {
			pstmt.setString(4, q.getText());
			pstmt.setString(1, q.getBody());
			pstmt.setString(2, q.getAuthor());
			pstmt.setString(3, q.getTagsCSV());
			pstmt.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	// Insert a set of questions to the database
	/*
	 * public void insertQuestions(Questions questions) { for (int i = 0; i <
	 * questions.size(); i++) { insertQuestion(questions.get(i)); } }
	 */

	// Insert a set of questions and answers to the database
	// CAUTION: Inefficient, avoid use [O(n^2)]
	// CAUTION: NO OVERWRITE CHECKS
	// public void insertQuestionsAndAnswers(Questions questions) throws SQLException {
	//	for (int i = 0; i < questions.size(); i++) {
	//		insertQuestion(questions.get(i));
	//		Answers tempAnswers = questions.get(i).getAnswers();
	//		for (int j = 0; j < tempAnswers.size(); j++) {
	//			insertAnswer(questions.get(i), tempAnswers.get(j));
	//		}
	//	}
	// }

	/**
	 *  Gets all questions from the database
	 *  @throws SQLException Should be handled internally.
	 */
	public Questions getQuestions() throws SQLException {
		String getQuestion = "SELECT * FROM questions";
		Questions questions = new Questions();
		try (ResultSet rs = statement.executeQuery(getQuestion);) {
			while (rs.next()) {
				Question q = new Question(rs.getInt("id"), rs.getString("text"), rs.getString("body"), rs.getString("author"),
						rs.getString("tags"));
				questions.add(q);
				questionKey++;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return questions;
	}
	
	/**
	 * Removes a question from the database.
	 * @param q The question to be removed.
	 * @throws SQLException Should be handled internally.
	 */
	public void removeQuestion(Question q) throws SQLException {
		String removeQuestion = "DELETE FROM questions WHERE text = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(removeQuestion)) {
			pstmt.setString(1, q.getText());
			pstmt.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Gets all questions and answers in the database.
	 * @return A complete questions class with associations to answers.
	 * @throws SQLException
	 */
	public Questions getQuestionsAndAnswers() throws SQLException {
		Questions q = getQuestions();
		for (int i = 0; i < q.size(); i++) {
			getAnswers(q.get(i));
		}
		return q;
	}

	/*
	 * ANSWER METHODS
	 */

	/**
	 * Gets all the answers for a given question
	 * @param question The question to be retrieved.
	 * @throws SQLException Should be handled internally.
	 */
	public void getAnswers(Question question) throws SQLException {
		String getAnswers = "SELECT * FROM answers WHERE question = ?";
		Answers ans = new Answers();
		try (PreparedStatement pstmt = connection.prepareStatement(getAnswers)) {
			pstmt.setString(1, question.getText());
			ResultSet rs = pstmt.executeQuery();
			while (rs.next()) {
				Answer a = new Answer(rs.getInt("id"), rs.getString("text"), rs.getString("author"), rs.getString("votes"));
				ans.add(a);
				answerKey++;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		question.setAnswers(ans);
		for (int i = 0; i < question.getAnswers().size(); i++) {
		}
	}
	
	/**
	 * Gets the primary key for a given answer.
	 * @param answerText The text of the answer to search for.
	 * @throws SQLException Should be handled internally.
	 */
	public int getKeyForAnswer(String answerText) throws SQLException {
		String getAnswers = "SELECT * FROM answers WHERE text = ?";
		Answers ans = new Answers();
		try (PreparedStatement pstmt = connection.prepareStatement(getAnswers)) {
			pstmt.setString(1, answerText);
			ResultSet rs = pstmt.executeQuery();
			if (rs.next()) {
				return rs.getInt("id");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return -1;
	}
	
	/**
	 *  Adds a new answer to the database
	 * @param q The question associated with the answer
	 * @param a The answer to be added
	 * @throws SQLException Should be handled internally.
	 */
	public void insertAnswer(Question q, Answer a) throws SQLException {
		String insertAnswer = "INSERT INTO answers (id, question, text, author, votes) VALUES (?, ?, ?, ?, ?)";
		try (PreparedStatement pstmt = connection.prepareStatement(insertAnswer)) {
			pstmt.setInt(1, a.getKey());
			pstmt.setString(2, q.getText());
			pstmt.setString(3, a.getText());
			pstmt.setString(4, a.getAuthor());
			pstmt.setString(5, a.getLikesCSV());
			pstmt.executeUpdate();
			answerKey++;
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Update an answer by id
	 * @param a The answer to be updated
	 */
	public void updateAnswer(Answer a) {
		String updateAnswer = "UPDATE answers SET text = ? WHERE id = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(updateAnswer)) {
			pstmt.setString(1, a.getText());
			pstmt.setInt(2, a.getKey());
			pstmt.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Removes an answer from the database.
	 * @param a The answer to be removed.
	 * @throws SQLException Should be handled internally.
	 */
	public void removeAnswer(Answer a) throws SQLException {
		String removeAnswer = "DELETE FROM answers WHERE text = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(removeAnswer)) {
			pstmt.setString(1, a.getText());
			pstmt.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	/*
	 * // Update an answer by title public void updateQuestion(Answer a, ) throws
	 * SQLException { String insertQuestion =
	 * "UPDATE questions SET body = ?, author = ?, tags = ? WHERE text = ?"; try
	 * (PreparedStatement pstmt = connection.prepareStatement(insertQuestion)) {
	 * pstmt.setString(4, a.getText()); pstmt.setString(2, a.getAuthor());
	 * pstmt.setString(3, a.getLikesCSV()); pstmt.executeUpdate(); } }
	 */

	/*
	 * INVITE CODE METHODS
	 */


	/**
	 * Checks if the user has the reviewer role
	 * @param userName The username of the user
	 * @return True if is reviewer
	 */
	public boolean isReviewer(String userName) {
		String query = "SELECT role FROM cse360users WHERE userName = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setString(1,userName);
			ResultSet rs = pstmt.executeQuery();
			if (rs.next()) {
				return "reviewer".equals(rs.getString("role"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
			}
		    return false;
		}
	/**
	 * Adds a review to the database
	 * @param reviewerName The name of the review author
	 * @param questionId The ID associated with the question
	 * @param reviewText The text of the review
	 * @param isAnswer Is the review of an answer?
	 * @return True if added successfully.
	 */
	public boolean addReview(String reviewerName, int questionId, String reviewText, boolean isAnswer) {
		String query  = "";
		
		if (isAnswer) {
			query = "INSERT INTO reviews(reviewerName, answerId, reviewText) VALUES (?, ?, ?)";
		} else {
			query = "INSERT INTO reviews(reviewerName, questionId, reviewText) VALUES (?, ?, ?)";
		}
		try(PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setString(1,  reviewerName);
			pstmt.setInt(2, questionId);
			pstmt.setString(3, reviewText);
			return pstmt.executeUpdate() > 0;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * Updates a review in the database
	 * @param reviewId PKey of the review
	 * @param reviewText
	 * @return True if updated successfully.
	 */
	public boolean updateReview(int reviewId, String reviewText) {
		String query = "UPDATE reviews SET reviewText = ? WHERE reviewId = ?";
		try(PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setString(1, reviewText);
			pstmt.setInt(2, reviewId);
			return pstmt.executeUpdate() > 0;
	} catch (SQLException e) {
		e.printStackTrace();
	}
		return false;
	}

	/**
	 * Allows the user to delete a review
	 * @param reviewId PKey of the review in the database
	 * @return True if successfully deleted.
	 */
	public boolean deleteReview(int reviewId) {
		String query = "DELETE FROM reviews WHERE reviewId = ?";
		try(PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setInt(1, reviewId);
			return pstmt.executeUpdate() > 0;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * Retrieves all of the reviews for a question or answer
	 * @param id The ID of the question or answer
	 * @param isAnswer True if is answer
	 * @return A list of all the reviews.
	 */
	public List<Review> getReviewsQA(int id, boolean isAnswer) {
		List<Review> reviews = new ArrayList<>();
		String query = "";
		
		if (isAnswer) {
			query = "SELECT * FROM reviews WHERE answerId = ?";
		} else {
			query = "SELECT * FROM reviews WHERE questionId = ?";
		}
		
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setInt(1,  id);
			ResultSet rs = pstmt.executeQuery();
			while (rs.next()) {
				if (isAnswer) {
					reviews.add(new Review(
							rs.getInt("reviewId"),
							rs.getString("reviewerName"),
							rs.getInt("answerId"),
							rs.getString("reviewText"),
							isAnswer
							));
				}
				else {
					reviews.add(new Review(
							rs.getInt("reviewId"),
							rs.getString("reviewerName"),
							rs.getInt("questionId"),
							rs.getString("reviewText"),
							isAnswer
							));
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return reviews;
	}
	
	
	/**
	 * Generates a new invitation code and inserts it into the database.
	 * @param role The role associated with the code
	 * @return The invite code
	 */
	public String generateInvitationCode(String role) {
		String code = UUID.randomUUID().toString().substring(0, 4); // Generate a random 4-character code
		String query = "INSERT INTO InvitationCodes (code, role) VALUES (?, ?)";

		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setString(1, code);
			pstmt.setString(2, role);
			pstmt.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return code;
	}

	/**
	 * Determines whether the invite code is valid. Input validation is handled by this method.
	 * <p>
	 * The code is marked as used when this method is called.
	 * @param code The code to be checked.
	 * @return True if it's valid.
	 */
	public boolean validateInvitationCode(String code) {
		 // Before asking the database, we'll check if it's in the right format. 
		 if (code.length() == 4) {
			 for (int i = 0; i < 4; i++) { 
				 if (!EmailValidator.isAlphaNumeric(code.charAt(i))) {
					 return false; 
				 }
			 }
		} else {
			return false; 
		} String query = "SELECT * FROM InvitationCodes WHERE code = ? AND isUsed = FALSE"; 
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setString(1, code); ResultSet rs = pstmt.executeQuery(); 
			if (rs.next()) {  
				markInvitationCodeAsUsed(code); 
				return true; 
			} 
		} catch (SQLException e) { 
			e.printStackTrace(); 
		} 
		return false;
	}


	/**
	 * Marks the invitation code as used in the database.
	 * @param code The code to be marked.
	 */
	private void markInvitationCodeAsUsed(String code) {
		String query = "UPDATE InvitationCodes SET isUsed = TRUE WHERE code = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setString(1, code);
			pstmt.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Gets all messages in the database.
	 * @return A messages collection.
	 * @throws SQLException Should be handled internally.
	 */
	public Messages getMessages() throws SQLException {
		String query = "SELECT * FROM messages";
		Messages messages = new Messages();
		try (PreparedStatement pstmt = connection.prepareStatement(query);) {
			ResultSet rs = pstmt.executeQuery();
			while (rs.next()) {
				Message m = new Message(rs.getInt("id"), rs.getString("text"), rs.getString("sender"), rs.getString("recipient"),
						rs.getBoolean("isread"), rs.getString("time"));
				messages.add(m);
				messageKey++;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return messages;
	}
	
	/**
	 * Gets all task messages in the database.
	 * @return A task messages collection.
	 * @throws SQLException Should be handled internally.
	 */
	public TaskMessages getTaskMessages() throws SQLException {
		String query = "SELECT * FROM taskMessages";
		TaskMessages taskMessages = new TaskMessages();
		try (PreparedStatement pstmt = connection.prepareStatement(query);) {
			ResultSet rs = pstmt.executeQuery();
			while (rs.next()) {
				TaskMessage tm = new TaskMessage(rs.getInt("id"), rs.getString("request"), rs.getString("requester"), rs.getString("sender"), rs.getString("text"),
						rs.getBoolean("requestisopen"), rs.getString("time"));
				taskMessages.add(tm);
				taskMessageKey++;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return taskMessages;
	}
	
	/**
	 * Gets the requester of a given request.
	 * 
	 * @param request The request whose requester will be found.
	 * @return The requester of this request.
	 */
	public String getRequesterByRequest(String request) {
		String getRequester = "SELECT requester FROM taskMessages WHERE request = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(getRequester)) {
			pstmt.setString(1, request);
			ResultSet rs = pstmt.executeQuery();

			if (rs.next()) {
				return rs.getString("requester");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * Gets all of the messages between two users.
	 * @param user The application's user
	 * @param otherUser The username of the other user
	 * @return A messages collection
	 * @throws SQLException Should be handled internally.
	 */
	public Messages getMessagesByUser(User user, String otherUser) throws SQLException {
		String query = "SELECT * FROM messages WHERE sender = ? OR recipient = ? OR sender = ? OR recipient = ?";
		Messages messages = new Messages();
		try (PreparedStatement pstmt = connection.prepareStatement(query);) {
			pstmt.setString(1, user.getUserName());
			pstmt.setString(2, user.getUserName());
			pstmt.setString(3, otherUser);
			pstmt.setString(4, otherUser);
			ResultSet rs = pstmt.executeQuery();
			while (rs.next()) {
				Message m = new Message(rs.getInt("id"), rs.getString("text"), rs.getString("sender"), rs.getString("recipient"),
						rs.getBoolean("isread"), rs.getString("time"));
				messages.add(m);
				//messageKey++;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return messages;
	}
	
	/**
	 * Gets all of the messages in a given conversation.
	 * @param user The application's user
	 * @param otherUser The user the conversation is between.
	 * @return A messages collection.
	 * @throws SQLException Should be handled internally.
	 */
	public Messages getMessagesForConvo(User user, String otherUser) throws SQLException {
		String query = "SELECT * FROM messages WHERE (sender = ? AND recipient = ?) OR (sender = ? AND recipient = ?)";
		Messages messages = new Messages();
		try (PreparedStatement pstmt = connection.prepareStatement(query);) {
			pstmt.setString(1, user.getUserName());
			pstmt.setString(2, otherUser);
			pstmt.setString(3, otherUser);
			pstmt.setString(4, user.getUserName());
			ResultSet rs = pstmt.executeQuery();
			while (rs.next()) {
				Message m = new Message(rs.getInt("id"), rs.getString("text"), rs.getString("sender"), rs.getString("recipient"),
						rs.getBoolean("isread"), rs.getString("time"));
				messages.add(m);
				//messageKey++;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return messages;
	}
	
	/**
	 * Gets all of the task messages in a given conversation.
	 * @param request The task messages' request.
	 * @return A task messages collection.
	 * @throws SQLException Should be handled internally.
	 */
	public TaskMessages getTaskMessagesForConvo(String request) throws SQLException {
		String query = "SELECT * FROM taskMessages WHERE request = ?";
		TaskMessages taskMessages = new TaskMessages();
		try (PreparedStatement pstmt = connection.prepareStatement(query);) {
			pstmt.setString(1, request);
			ResultSet rs = pstmt.executeQuery();
			while (rs.next()) {
				TaskMessage tm = new TaskMessage(rs.getInt("id"), rs.getString("request"), rs.getString("requester"), rs.getString("sender"), rs.getString("text"),
						rs.getBoolean("requestisopen"), rs.getString("time"));
				taskMessages.add(tm);
				//taskMessageKey++;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return taskMessages;
	}

	/**
	 * Gets all of the messages in a given conversation. Used in MessageSpy.
	 * @param user The application's user
	 * @param otherUser The user the conversation is between.
	 * @return A messages collection.
	 * @throws SQLException Should be handled internally.
	 */
	public Messages getMessagesForSpy(String user, String otherUser) throws SQLException {
		String query = "SELECT * FROM messages WHERE (sender = ? AND recipient = ?) OR (sender = ? AND recipient = ?)";
		Messages messages = new Messages();
		try (PreparedStatement pstmt = connection.prepareStatement(query);) {
			pstmt.setString(1, user);
			pstmt.setString(2, otherUser);
			pstmt.setString(3, otherUser);
			pstmt.setString(4, user);
			ResultSet rs = pstmt.executeQuery();
			while (rs.next()) {
				Message m = new Message(rs.getInt("id"), rs.getString("text"), rs.getString("sender"), rs.getString("recipient"),
						rs.getBoolean("isread"), rs.getString("time"));
				messages.add(m);
				//messageKey++;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return messages;
	}
	
	/**
	 * Inserts a message into the database.
	 * @param m The message to be inserted.
	 * @throws SQLException Should be handled internally.
	 */
	public void insertMessage(Message m) throws SQLException {
		String insertQuestion = "INSERT INTO messages (id, text, sender, recipient, time, isread) VALUES (?, ?, ?, ?, ?, ?)";
		// but first, check if the id will be valid
		String maximumID = "SELECT MAX(id) AS maximum FROM messages;";
		int max;
		try (PreparedStatement stmt = connection.prepareStatement(maximumID)) {
			ResultSet rs = stmt.executeQuery();
			if (rs.next()) {
				max = rs.getInt("maximum");
				m.setKey(max + 1);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		try (PreparedStatement pstmt = connection.prepareStatement(insertQuestion)) {
			pstmt.setInt(1, m.getKey());
			pstmt.setString(2, m.getText());
			pstmt.setString(3, m.getSender());
			pstmt.setString(4, m.getRecipient());
			pstmt.setString(5, m.getTimeAsString());
			pstmt.setBoolean(6, m.getIsRead());
			pstmt.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Inserts a task message into the database.
	 * @param tm The task message to be inserted.
	 * @throws SQLException Should be handled internally.
	 */
	public void insertTaskMessage(TaskMessage tm) throws SQLException {
		String insertTaskMessage = "INSERT INTO taskMessages (id, request, requester, sender, text, time, requestisopen) VALUES (?, ?, ?, ?, ?, ?, ?)";
		// but first, check if the id will be valid
		String maximumID = "SELECT MAX(id) AS maximum FROM taskMessages;";
		int max;
		try (PreparedStatement stmt = connection.prepareStatement(maximumID)) {
			ResultSet rs = stmt.executeQuery();
			if (rs.next()) {
				max = rs.getInt("maximum");
				tm.setKey(max + 1);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		try (PreparedStatement pstmt = connection.prepareStatement(insertTaskMessage)) {
			pstmt.setInt(1, tm.getKey());
			pstmt.setString(2, tm.getRequest());
			pstmt.setString(3, tm.getRequester());
			pstmt.setString(4, tm.getSender());
			pstmt.setString(5, tm.getText());
			pstmt.setString(6, tm.getTimeAsString());
			pstmt.setBoolean(7, tm.getRequestIsOpen());
			pstmt.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/**
	 *  Closes the database connection and statement.
	 */
	public void closeConnection() {
		try {
			if (statement != null)
				statement.close();
		} catch (SQLException se2) {
			se2.printStackTrace();
		}
		try {
			if (connection != null)
				connection.close();
		} catch (SQLException se) {
			se.printStackTrace();
		}
	}
	
	/**
	 * Marks a message as read.
	 * @param message The message to be marked.
	 */
	public void setMessageRead(Message message) {
		String query = "UPDATE messages SET isread = TRUE WHERE id = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setInt(1, message.getKey());
			pstmt.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
	}
	
	/**
	 * Gets a task message request's open status.
	 * @param request The task message request.
	 */
	public boolean getTaskMessageRequestOpenStatus(String request) {
		String query = "SELECT requestisopen FROM taskMessages WHERE request = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setString(1, request);
			ResultSet rs = pstmt.executeQuery();
			if (rs.next()) {
				return getTaskMessagesForConvo(request).get(0).getRequestIsOpen();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return false;
		
	}
	
	/**
	 * Marks a task message request as closed.
	 * @param request The task message request to be marked closed.
	 */
	public void setTaskMessageRequestClosed(String request) {
		String query = "UPDATE taskMessages SET requestisopen = FALSE WHERE request = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setString(1, request);
			pstmt.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
	}
	
	/**
	 * Marks a task message request as open.
	 * @param request The task message request to be marked open.
	 */
	public void setTaskMessageRequestOpen(String request) {
		String query = "UPDATE taskMessages SET requestisopen = TRUE WHERE request = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setString(1, request);
			pstmt.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
	}
	
	/**
	 * Inserts a user into the reviewerRequests database.
	 * <p>
	 * For the sake of error messages, this method requires exception handling by the caller.
	 * @param username The username of the user to be entered.
	 * @throws SQLException Handled by the caller. Usually a duplicate entry exception.
	 */
	public void requestReviewerRole(String username) throws SQLException {
		String stmt = "INSERT INTO reviewerRequests (username) VALUES (?)";
		PreparedStatement pstmt = connection.prepareStatement(stmt);
		pstmt.setString(1, username);
		pstmt.executeUpdate();
	}
	
	/**
	 * Gets an ArrayList of all of the usernames in the reviewerRequests database.
	 * 
	 * @return The users in the reviewerRequests database.
	 * @throws SQLException Should be handled internally, but throws this regardless.
	 */
	public ArrayList<String> getReviewerRequests() throws SQLException {
		ArrayList<String> requests = new ArrayList<String>();
		String query = "SELECT * FROM reviewerRequests;";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			ResultSet rs = pstmt.executeQuery();
			while (rs.next()) {
				requests.add(rs.getString("username"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return requests;
	}
	
	/**
	 * Gets a set of questions written by an author. Should not be used for the main Q and A page.
	 * 
	 * @param author The author who wrote the questions.
	 * @return The questions written by this author.
	 * @throws SQLException Unlikely to be thrown, but here for safety.
	 */
	public Questions getQuestionsByAuthor(String author) throws SQLException {
		String getQuestion = "SELECT * FROM questions WHERE author = ?";
		PreparedStatement pstmt = connection.prepareStatement(getQuestion);
		pstmt.setString(1, author);
		Questions questions = new Questions();
		try (ResultSet rs = pstmt.executeQuery()) {
			while (rs.next()) {
				Question q = new Question(rs.getInt("id"), rs.getString("text"), rs.getString("body"), rs.getString("author"),
						rs.getString("tags"));
				questions.add(q);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return questions;
	}
	
	/**
	 * Gets a set of messages written by an author. Should not be used for the main message page.
	 * 
	 * @param author The author who wrote the questions.
	 * @return The questions written by this author.
	 * @throws SQLException Unlikely to be thrown, but here for safety.
	 */
	public Messages getMessagesByAuthor(String author) throws SQLException {
		String getMessage = "SELECT * FROM messages WHERE sender = ?";
		PreparedStatement pstmt = connection.prepareStatement(getMessage);
		pstmt.setString(1, author);
		Messages messages = new Messages();
		try (ResultSet rs = pstmt.executeQuery()) {
			while (rs.next()) {
				Message q = new Message(rs.getInt("id"), rs.getString("text"), rs.getString("sender"), rs.getString("recipient"), rs.getBoolean("isread"), rs.getString("time"));
				messages.add(q);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return messages;
	}
	
	/**
	 * Gets a set of answers written by an author. Should not be used for the main Q and A page.
	 * 
	 * @param author The author who wrote the answers.
	 * @return The answers written by this author.
	 * @throws SQLException Unlikely to be thrown, but here for safety.
	 */
	public Answers getAnswersByAuthor(String author) throws SQLException {
		String getQuestion = "SELECT * FROM answers WHERE author = ?";
		PreparedStatement pstmt = connection.prepareStatement(getQuestion);
		pstmt.setString(1, author);
		Answers as = new Answers();
		try (ResultSet rs = pstmt.executeQuery()) {
			while (rs.next()) {
				Answer a = new Answer(rs.getInt("id"), rs.getString("text"), rs.getString("author"),
						rs.getString("votes"));
				as.add(a);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return as;
	}
	
	/**
	 * Gets a set of reviews written by an author. Should not be used for the main review pages.
	 * 
	 * @param author The author who wrote the questions.
	 * @return The questions written by this author.
	 * @throws SQLException Unlikely to be thrown, but here for safety.
	 */
	public ArrayList<Review> getReviewsByAuthor(String author) throws SQLException {
		String getQReviews = "SELECT * FROM reviews WHERE reviewerName = ? AND answerId IS NULL";
		String getAReviews = "SELECT * FROM reviews WHERE reviewerName = ? AND questionId IS NULL";
		ArrayList<Review> reviews = new ArrayList<Review>();
		try (PreparedStatement pstmt = connection.prepareStatement(getQReviews)) {
			pstmt.setString(1, author);
			ResultSet rs = pstmt.executeQuery();
			while (rs.next()) {
					reviews.add(new Review(
							rs.getInt("reviewId"),
							rs.getString("reviewerName"),
							rs.getInt("questionId"),
							rs.getString("reviewText"),
							false
							));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		try (PreparedStatement pstmt = connection.prepareStatement(getAReviews)) {
			pstmt.setString(1, author);
			ResultSet rs = pstmt.executeQuery();
			while (rs.next()) {
					reviews.add(new Review(
							rs.getInt("reviewId"),
							rs.getString("reviewerName"),
							rs.getInt("answerId"),
							rs.getString("reviewText"),
							true
							));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return reviews;
	}
	
	/**
	 * Delete a user from the requests database.
	 * @param requestUser The user who put out the request.
	 * @return True if the request is processed.
	 */
	public boolean rejectReviewerRequest(String requestUser) {
		String query = "DELETE FROM reviewerRequests WHERE username = ?";
		PreparedStatement pstmt;
		try {
			pstmt = connection.prepareStatement(query);
			pstmt.setString(1, requestUser);
			pstmt.executeUpdate();
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}
	
	/**
	 * Updates the list of reviewers for a user in the database.
	 * @param user The user to be updated.
	 * @throws SQLException Permits handling of being unable to update.
	 */
	public void updateReviewers(User user) throws SQLException {
		String query = "UPDATE cse360users SET reviewerScores = ? WHERE userName = ?";
		PreparedStatement pstmt = connection.prepareStatement(query);
		pstmt.setString(1, user.getReviewersAsString());
		pstmt.setString(2, user.getUserName());
		pstmt.executeUpdate();
	}
	
	/**
	 * Get an ArrayList of reviewers from the database for a given user.
	 * @param user The user to get reviewer scores for.
	 * @return The list of reviewers.
	 * @throws SQLException Permits error handling if they cannot be found.
	 */
	public ArrayList<Reviewer> getReviewersFromDB(User user) throws SQLException {
		String getQuestion = "SELECT * FROM cse360users WHERE userName = ?";
		PreparedStatement pstmt = connection.prepareStatement(getQuestion);
		pstmt.setString(1, user.getUserName());
		try (ResultSet rs = pstmt.executeQuery()) {
			if (rs.next()) {
				user.setReviewers(rs.getString("reviewerScores"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return user.getReviewers();
	}
	
	/**
	 * Gets an arraylist of reviewers from the database including those not in the user's list.
	 * @param user The user to retrieve reviewers for.
	 * @return A list of reviewers.
	 * @throws SQLException Permits error handling if they cannot be found.
	 */
	public ArrayList<Reviewer> getAllReviewers(User user) throws SQLException {
		String getQuestion = "SELECT * FROM cse360users WHERE role != 'user'";
		ArrayList<Reviewer> list = user.getReviewers();
		PreparedStatement pstmt = connection.prepareStatement(getQuestion);
		try (ResultSet rs = pstmt.executeQuery()) {
			if (list != null) {
				while (rs.next()) {
					Reviewer temp = new Reviewer(rs.getString("userName"), 50);
					boolean found = false;
					// WARNING: very inefficient
					for (int i = 0; i < list.size(); i++) {
						if (list.get(i).getUsername().equals(temp.getUsername())) {
							found = true;
						}
					}
					if (!found) {
						list.add(temp);
					}
				}
				user.setReviewersList(list);
				return list;
			}
			else {
				list = new ArrayList<Reviewer>();
				while (rs.next()) {
					Reviewer temp = new Reviewer(rs.getString("userName"), 50);
					list.add(temp);
				}
				user.setReviewersList(list);
				return list;
			}
		} catch (SQLException e) {
			e.printStackTrace();
			return user.getReviewers();
		}
	}

	/**
	 * Creates a new reviewer profile in the database.
	 * @param profile The reviewer profile to create
	 * @throws SQLException if there is a database error
	 */
	public void createReviewerProfile(ReviewerProfile profile) throws SQLException {
		String query = "INSERT INTO reviewer_profiles (username, bio, expertise_areas, student_feedback) VALUES (?, ?, ?, ?)";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setString(1, profile.getUsername());
			pstmt.setString(2, profile.getBio());
			pstmt.setString(3, String.join(";", profile.getExpertiseAreas()));
			pstmt.setString(4, String.join(";", profile.getStudentFeedback()));
			pstmt.executeUpdate();
		}
	}

	/**
	 * Updates an existing reviewer profile in the database.
	 * @param profile The reviewer profile to update
	 * @throws SQLException if there is a database error
	 */
	public void updateReviewerProfile(ReviewerProfile profile) throws SQLException {
		String query = "UPDATE reviewer_profiles SET bio = ?, expertise_areas = ?, student_feedback = ? WHERE username = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setString(1, profile.getBio());
			pstmt.setString(2, String.join(";", profile.getExpertiseAreas()));
			pstmt.setString(3, String.join(";", profile.getStudentFeedback()));
			pstmt.setString(4, profile.getUsername());
			pstmt.executeUpdate();
		}
	}

	/**
	 * Retrieves a reviewer profile from the database.
	 * @param username The username of the reviewer
	 * @return The reviewer profile, or null if not found
	 * @throws SQLException if there is a database error
	 */
	public ReviewerProfile getReviewerProfile(String username) throws SQLException {
		String query = "SELECT * FROM reviewer_profiles WHERE username = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setString(1, username);
			ResultSet rs = pstmt.executeQuery();
			if (rs.next()) {
				ReviewerProfile profile = new ReviewerProfile(username);
				profile.setBio(rs.getString("bio"));
				profile.setExpertiseAreas(new ArrayList<>(Arrays.asList(rs.getString("expertise_areas").split(";"))));
				profile.setStudentFeedback(new ArrayList<>(Arrays.asList(rs.getString("student_feedback").split(";"))));
				
				// Get past reviews
				List<Review> reviews = getReviewsByAuthor(username);
				profile.setPastReviews(reviews);
				
				return profile;
			}
		}
		return null;
		
	}

	public void createDefaultReviewerProfile(ReviewerProfile profile) throws SQLException {
	    String query = "INSERT INTO reviewer_profiles (username, bio, expertise_areas, student_feedback) VALUES (?, ?, '', '')";
	    try (PreparedStatement stmt = connection.prepareStatement(query)) {
	        stmt.setString(1, profile.getUsername());
	        stmt.setString(2, profile.getBio());
	        stmt.executeUpdate();
	    }
	}

	/**
	 * Deletes a reviewer profile from the database.
	 * @param username The username of the reviewer whose profile should be deleted
	 * @throws SQLException if there is a database error
	 */
	public void deleteReviewerProfile(String username) throws SQLException {
		String query = "DELETE FROM reviewer_profiles WHERE username = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setString(1, username);
			pstmt.executeUpdate();
		}
	}

	/**
	 * Adds student feedback to a reviewer's profile
	 * @param username The username of the reviewer
	 * @param feedback The feedback to add
	 * @throws SQLException If there is an error accessing the database
	 */
	public void addStudentFeedback(String username, String feedback) throws SQLException {
		ReviewerProfile profile = getReviewerProfile(username);
		profile.addStudentFeedback(feedback);
		updateReviewerProfile(profile);
	}
	
	/**
	 * Helper method to parse a CSV string into a List
	 * @param csv The CSV string to parse
	 * @return A List containing the parsed values
	 */
	private List<String> parseCSV(String csv) {
		List<String> result = new ArrayList<>();
		if (csv != null && !csv.isEmpty()) {
			String[] items = csv.split(",");
			for (String item : items) {
				result.add(item.trim());
			}
		}
		return result;
	}
	
	/**
	 * Helper method to convert a List to a CSV string
	 * @param list The List to convert
	 * @return A CSV string containing the List items
	 */
	private String convertToCSV(List<String> list) {
		if (list == null || list.isEmpty()) {
			return "";
		}
		return String.join(",", list);
	}

	/**
	 * Creates the database and its tables if they don't exist.
	 */
	public void createDatabase() {
		try {
			// Create users table
			String createUsers = "CREATE TABLE IF NOT EXISTS cse360users ("
					+ "userName VARCHAR(255) PRIMARY KEY,"
					+ "password VARCHAR(255),"
					+ "role VARCHAR(255),"
					+ "name VARCHAR(255),"
					+ "email VARCHAR(255),"
					+ "reviewerScores TEXT"
					+ ")";
			Statement stmt = connection.createStatement();
			stmt.execute(createUsers);
			
			// Create reviewer_profiles table
			String createReviewerProfiles = "CREATE TABLE IF NOT EXISTS reviewer_profiles ("
					+ "username VARCHAR(255) PRIMARY KEY,"
					+ "bio TEXT,"
					+ "expertise_areas TEXT,"
					+ "student_feedback TEXT,"
					+ "FOREIGN KEY (username) REFERENCES cse360users(userName)"
					+ ")";
			stmt.execute(createReviewerProfiles);
			
			// ... existing code ...
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}


	/**
	 * Adds like to review from user
	 * @param reviewId the id of the review being liked
	 * @param likedBy the username of the user who liked
	 * @return true if like has been added, false if not
	 */
	public boolean likeReview(int reviewId, String likedBy) {
		String query = "INSERT INTO reviewLikes (reviewId, likedBy) VALUES (?, ?)";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setInt(1, reviewId);
			pstmt.setString(2, likedBy);
			pstmt.executeUpdate();
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}
	
	/**
	 * Removes the like from a review that the user had previously liked
	 * @param reviewId the id of the review being unliked
	 * @param likedBy the username of the user removing the like
	 * @return true if the like was removed, false if not
	 */
	public boolean unlikeReview (int reviewId, String likedBy) {
		String query = "DELETE FROM reviewLikes WHERE reviewId = ? AND likedBy = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setInt(1, reviewId);
			pstmt.setString(2, likedBy);
			pstmt.executeUpdate();
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}
	
	/**
	 *Adds feedback on a review from one user to another
	 * @param reviewId the id of the review the feedback is going to
	 * @param feedbackBy username of the user giving feedback
	 * @param feedbackText the text of the feedback
	 * @return
	 */
	public boolean addReviewFeedback(int reviewId, String feedbackBy, String feedbackText) {
		String query = "INSERT INTO reviewFeedback (reviewId, feedbackBy, feedbackText) VALUES (?, ?, ?)";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setInt(1, reviewId);
			pstmt.setString(2, feedbackBy);
			pstmt.setString(3, feedbackText);
			pstmt.executeUpdate();
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}
	
	/**
	 * retrieves the list of feedback strings for the review
	 * @param reviewId the id of the review
	 * @return a list of the feedback strings or an empty list if none are present or an error
	 */
	public List<String> getReviewFeedback(int reviewId) {
		List<String> feedbackList = new ArrayList<>();
		String query = "SELECT feedbackBy, feedbackText FROM reviewFeedback WHERE reviewId = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setInt(1, reviewId);
			ResultSet rs = pstmt.executeQuery();
			while (rs.next()) {
				String feedbackBy = rs.getString("feedbackBy");
				String feedbackText = rs.getString("feedbackText");
				feedbackList.add(feedbackBy + ": " + feedbackText);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return feedbackList;
	}
	
	/**
	 * Counts the number of likes the count increases for a review
	 * @param reviewId the id of the review to increment likes 
	 * @return true if successful, false if not
	 */
	public boolean incrementReviewLike(int reviewId) {
		String query = "UPDATE reviews SET likeNum = likeNum + 1 WHERE reviewId = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setInt(1, reviewId);
			pstmt.executeUpdate();
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}
	/**
	 * 1. 
	 * @param userId
	 * @param answerId
	 * @return
	 */
	public boolean addAnswerBookmark(String userId, int answerId) {
	    String query = "INSERT IGNORE INTO AnswerBookmarks (userId, answerId) VALUES (?, ?)";
	    try (PreparedStatement pstmt = connection.prepareStatement(query))
	        {
	        pstmt.setString(1, userId);
	        pstmt.setInt(2, answerId);
	        pstmt.executeUpdate();
	        return true;
	    } catch (SQLException e) {
	        e.printStackTrace();
	        return false;
	    }
	}
	
	/**
	 * 2.
	 * @param userId
	 * @return
	 */

	public List<Integer> getBookmarkedAnswers(int userId){
	    List<Integer> answers = new ArrayList<>();
	    String query = "SELECT answerId FROM AnswerBookmarks WHERE userId = ?";
	    try (PreparedStatement pstmt = connection.prepareStatement(query))
	          {
	        pstmt.setInt(1, userId);
	        ResultSet rs = pstmt.executeQuery();
	        while (rs.next()) {
	        	answers.add(rs.getInt("answerId"));
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	    return answers;
	}
	
	/**
	 * 3.
	 * @param userId
	 * @param answerId
	 * @return
	 */
	public boolean removeAnswerBookmark(int userId, int answerId) {
	    String query = "DELETE FROM AnswerBookmarks WHERE userId = ? AND answerId = ?";
	    try (PreparedStatement pstmt = connection.prepareStatement(query))
	         {
	        pstmt.setInt(1, userId);
	        pstmt.setInt(2, answerId);
	        pstmt.executeUpdate();
	        return true;
	    } catch (SQLException e) {
	        e.printStackTrace();
	        return false;
	    }
	}
	/**
	 * 1.
	 * @param userId
	 * @param reviewerId
	 * @return
	 */
	public boolean addReviewerBookmark(int userId, int reviewerId) {
	    String query = "INSERT IGNORE INTO ReviewerBookmarks (userId, reviewerId) VALUES (?, ?)";
	    try (PreparedStatement pstmt = connection.prepareStatement(query))
	         {
	        pstmt.setInt(1, userId);
	        pstmt.setInt(2, reviewerId);
	        pstmt.executeUpdate();
	        return true;
	    } catch (SQLException e) {
	        e.printStackTrace();
	        return false;
	    }
	}
	
/**
 * 2.
 * @param userId
 * @return
 */
	public List<Integer> getBookmarkedReviews(int userId) {
	    List<Integer> reviewerIds = new ArrayList<>();
	    String query = "SELECT reviewerId FROM ReviewerBookmarks WHERE userId =?";
	    try (PreparedStatement pstmt = connection.prepareStatement(query))
	        {
	        pstmt.setInt(1, userId);
	        ResultSet rs = pstmt.executeQuery();
	        while (rs.next()) {
	        	reviewerIds.add(rs.getInt("reviewerId"));
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	    return reviewerIds;
	}
	
	/**
	 * 3.
	 * @param userId
	 * @param reviewerId
	 * @return
	 */
	    
	public boolean removeReviewBookmark(int userId, int reviewerId) {
	    String query = "DELETE FROM ReviewBookmarks WHERE userId = ? AND reviewerId = ?";
	    try (PreparedStatement pstmt = connection.prepareStatement(query))
	         {
	        pstmt.setInt(1, userId);
	        pstmt.setInt(2, reviewerId);
	        pstmt.executeUpdate();
	        return true;
	    } catch (SQLException e) {
	        e.printStackTrace();
	        return false;
	    }
	}
}
	
