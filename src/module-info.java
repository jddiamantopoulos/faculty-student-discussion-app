module FoundationCode {
	requires javafx.controls;
	requires java.sql;
	requires javafx.graphics;
	
	opens main to javafx.graphics, javafx.fxml;
}
