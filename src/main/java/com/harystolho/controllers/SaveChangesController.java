package com.harystolho.controllers;

import com.harystolho.pe.File;
import com.harystolho.utils.PEUtils;
import com.harystolho.utils.PropertiesWindowFactory;

import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

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
