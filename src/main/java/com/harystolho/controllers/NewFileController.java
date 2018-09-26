package com.harystolho.controllers;

import java.io.File;
import java.util.regex.Pattern;

import com.harystolho.PEApplication;
import com.harystolho.utils.PEUtils;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Pane;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

public class NewFileController {

	@FXML
	private Pane pane;

	@FXML
	private Button cancel;

	@FXML
	private TextField directory;

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
		directory.setText(PEUtils.getWorkspaceFolder().getAbsolutePath());
		loadEventHandlers();
	}

	private void loadEventHandlers() {

		directory.setOnMouseClicked((e) -> {
			DirectoryChooser dc = new DirectoryChooser();

			dc.setInitialDirectory(PEUtils.getWorkspaceFolder());

			File dir = dc.showDialog(PEApplication.getInstance().getWindow());

			if (dir != null && dir.exists()) {
				directory.setText(dir.getAbsolutePath());
			}
		});

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
				File folder = new File(directory.getText());

				if (folder.exists()) {
					PEApplication.getInstance().getMainController().createNewFile(folder, name);
					stage.close();
				} else {
					Alert error = new Alert(AlertType.ERROR);
					error.setContentText("The selected directory is not valid or does not exist.");
					error.showAndWait();
				}
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
