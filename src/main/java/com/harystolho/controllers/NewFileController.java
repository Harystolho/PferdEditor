package com.harystolho.controllers;

import java.util.regex.Pattern;

import com.harystolho.PEApplication;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public class NewFileController {

	@FXML
	private Pane pane;

	@FXML
	private Button cancel;

	@FXML
	private TextField file;

	@FXML
	private Button createFile;

	@FXML
	private Label message;

	private Stage stage;

	private static final Pattern validFileName = Pattern.compile("^[\\w\\-. ]+$");

	@FXML
	void initialize() {
		loadEventHandlers();
	}

	private void loadEventHandlers() {

		createFile.setOnAction((e) -> {
			createNewFile(file.getText());
		});

		cancel.setOnAction((e) -> {
			stage.close();
		});

	}

	private void createNewFile(String name) {
		if (!name.isEmpty()) {
			if (isNameValid(name)) {
				PEApplication.getInstance().getMainController().createNewFile(name);
				stage.close();
			}
		}

	}

	private boolean isNameValid(String text) {
		if (validFileName.matcher(text).matches()) {
			message.setText("");
			return true;
		} else {
			message.setText("Error: Invalid File Name");
			return false;
		}
	}

	public void setStage(Stage stage) {
		this.stage = stage;

		stage.addEventHandler(KeyEvent.KEY_PRESSED, (e) -> {
			if (e.getCode() == KeyCode.ENTER) {
				createNewFile(file.getText());
			}
		});

	}

}
