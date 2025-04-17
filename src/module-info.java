/**
 * Student question and answer system.
 */
module FoundationCode {
	requires javafx.controls;
	requires java.sql;
	requires javafx.graphics;
	requires junit;
	
	opens main to javafx.graphics, javafx.fxml;
	exports main; /* Not sure what this does, but it fixed something I broke a while back */
}
