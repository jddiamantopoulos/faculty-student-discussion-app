package databasePart1;

import java.sql.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.UUID;

import accounts.util.EmailValidator;
import accounts.util.User;
import messaging.util.*;
import questions.util.*;

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
			// statement.execute("DROP ALL OBJECTS");
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
		} catch (SQLException e) {
			try {
				// Create tables if they don't exist
				String userTable = "CREATE TABLE IF NOT EXISTS cse360users (" + "id INT AUTO_INCREMENT PRIMARY KEY, "
						+ "userName VARCHAR(255) UNIQUE, " + "password VARCHAR(255), " + "role VARCHAR(20), "
						+ "name VARCHAR(255), " + "email VARCHAR(255))";
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
						+ "text VARCHAR(500)," + "sender VARCHAR(16), " + "recipient VARCHAR(16), "
						+ "isread BIT," + "time VARCHAR(20))";
				statement.execute(messagesTable);
				
				// Create the reviewers table
				String reviewersTable = "CREATE TABLE IF NOT EXISTS reviewerRequests (" 
						+ "username VARCHAR(16) UNIQUE)";
				statement.execute(reviewersTable);
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
					user.setName(name != null ? name : "");
					user.setEmail(email != null ? email : "");
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
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
	}

}
