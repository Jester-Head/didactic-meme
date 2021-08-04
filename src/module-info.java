module SchedulingApplication {
	requires javafx.fxml;
	requires javafx.controls;
	requires java.sql;

	opens controller;
	opens main;
	opens model;
	opens view;
	opens utilities;
}
