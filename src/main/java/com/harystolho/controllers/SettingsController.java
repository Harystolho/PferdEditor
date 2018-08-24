package com.harystolho.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class SettingsController {

	@FXML
	private VBox settingsPane;

	@FXML
	private ListView<?> settingsList;

	@FXML
	private Button cancel;

	@FXML
	private Button apply;

	private Stage stage;

	@FXML
	void initialize() {
		loadEventHandlers();
	}

	private void loadEventHandlers() {
		apply.setOnAction((e) -> {

		});

		cancel.setOnAction((e) -> {
			stage.close();
		});
	}

	public void setStage(Stage stage) {
		this.stage = stage;

		stage.addEventHandler(KeyEvent.KEY_PRESSED, (e) -> {
			if (e.getCode() == KeyCode.ENTER) {
				
			}
		});

	}

}
