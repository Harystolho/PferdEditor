package com.harystolho.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;

public class SaveChangesController {

	@FXML
	private Pane pane;

	@FXML
	private Button saveAs;

	@FXML
	private Button save;

	@FXML
	private Label file;

	@FXML
	private Button cancel;

	@FXML
	void initialize() {
		loadEventHandlers();
	}

	private void loadEventHandlers() {

		save.setOnAction((e) -> {

		});

		saveAs.setOnAction((e) -> {

		});

		cancel.setOnAction((e) -> {

		});
	}

}
