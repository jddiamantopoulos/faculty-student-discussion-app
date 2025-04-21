/**
 * Student question and answer system.
 */
module FoundationCode {
	requires javafx.controls;
	requires java.sql;
	requires javafx.graphics;
	requires junit;
	requires javafx.base;
	
	opens main to javafx.graphics, javafx.fxml;
}