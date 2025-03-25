package databasePart1;

import java.sql.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.UUID;

import accounts.util.EmailValidator;
import accounts.util.User;
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

	private Connection connection = null;
	private Statement statement = null;

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

	public void clear() {
		try {
			statement.execute("DROP ALL OBJECTS");
			createTables();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	private void createTables() throws SQLException {
		// First check if we need to update existing table
		try {
			// For now, we will use questions. Uncomment this when ready.
			// statement.executeQuery("SELECT name FROM cse360users LIMIT 1");
			statement.executeQuery("SELECT text FROM questions LIMIT 1");
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
			} catch (SQLException e2) {
				System.err.println("Multiple database errors.");
				e2.printStackTrace();
				e.printStackTrace();
				System.exit(1);
			}
		}
	}

	// Check if the database is empty
	public boolean isDatabaseEmpty() throws SQLException {
		String query = "SELECT COUNT(*) AS count FROM cse360users";
		ResultSet resultSet = statement.executeQuery(query);
		if (resultSet.next()) {
			return resultSet.getInt("count") == 0;
		}
		return true;
	}

	// Registers a new user in the database.
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

	// Validates a user's login credentials.
	// No need to validate personal info.
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

	// Determines if there are users in the table
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

	// Checks if a user already exists in the database based on their userName.
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

	// Add method to get full user information
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

	// Retrieves the role of a user from the database using their UserName.
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

	// Retrieves the role associated with a given invite code.
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

	// Update the information for a given user
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

	// Adds a new question to the database
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

	// Update a question by title
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

	// Gets all questions from the database
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

	public Questions getQuestionsAndAnswers() throws SQLException {
		Questions q = getQuestions();
		for (int i = 0; i < q.size(); i++) {
			getAnswers(q.get(i));
		}
		return q;
	}
	
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

	// This could potentially be made more efficient (with a cache of retrieved
	// answers) but
	// for now, this is how we'll do it
	public void getAnswers(Question question) throws SQLException {
		String getAnswers = "SELECT * FROM answers WHERE question = ?";
		Answers ans = new Answers();
		try (PreparedStatement pstmt = connection.prepareStatement(getAnswers)) {
			pstmt.setString(1, question.getText());
			ResultSet rs = pstmt.executeQuery();
			while (rs.next()) {
				Answer a = new Answer(rs.getInt("id"), rs.getString("text"), rs.getString("author"), rs.getString("votes"));
				// Somewhere after this point is where the error arises
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

	// Adds a new answer to the database
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

	// Generates a new invitation code and inserts it into the database.
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


	// Marks the invitation code as used in the database.
	private void markInvitationCodeAsUsed(String code) {
		String query = "UPDATE InvitationCodes SET isUsed = TRUE WHERE code = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setString(1, code);
			pstmt.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	// Closes the database connection and statement.
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

}
