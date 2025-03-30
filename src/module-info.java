module FoundationCode {
	requires javafx.controls;
	requires java.sql;
	requires javafx.graphics;
	requires junit;
	
	opens main to javafx.graphics, javafx.fxml;
}
