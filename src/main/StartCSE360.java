package main;

import javafx.application.Application;
import javafx.stage.Stage;
import messaging.util.Message;

import java.sql.SQLException;

import accounts.ui.FirstPage;
import accounts.ui.SetupLoginSelectionPage;
import accounts.util.User;
import databasePart1.DatabaseHelper;


public class StartCSE360 extends Application {

	private static final DatabaseHelper databaseHelper = new DatabaseHelper();
	private static boolean MESSAGE_DEBUG_FLAG = false; 
	/* I wish this were C so this could be a macro... conditional compilation of these
	 * debug options would be a godsend. */
	
	public static void main( String[] args )
	{
		 launch(args);
	}
	
	@Override
    public void start(Stage primaryStage) {
        try {
            databaseHelper.connectToDatabase();
            if (MESSAGE_DEBUG_FLAG) {
            	databaseHelper.clear();
            	User user = new User("user", "P4$$word", "user");
            	User admin = new User("Admin", "P4$$word", "admin");
            	User owner = new User("Owner", "P4$$word", "admin");
            	User user2 = new User("user2", "P4$$word", "user");
            	databaseHelper.register(user);
            	databaseHelper.register(user2);
            	databaseHelper.register(admin);
            	databaseHelper.register(owner);
            	Message msg = new Message("Hey, Admin! Test test test!", "user", "Admin");
            	Message msg2 = new Message("Hey, Admin! This message exists too!", "user2", "Admin");
            	databaseHelper.insertMessage(msg);
            	databaseHelper.insertMessage(msg2);
            }
            if (databaseHelper.isDatabaseEmpty()) {
                new FirstPage(databaseHelper).show(primaryStage);
            } else {
                new SetupLoginSelectionPage(databaseHelper).show(primaryStage);
            }
        } catch (SQLException e) {
            System.err.println("Database error: " + e.getMessage());
            e.printStackTrace();
        }
    }
	

}
