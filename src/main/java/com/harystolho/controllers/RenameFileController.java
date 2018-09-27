package com.harystolho.controllers;

import com.harystolho.PEApplication;
import com.harystolho.pe.File;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

/**
 * Controller used to rename a file
 * 
 * @author Harystolho
 *
 */
public class RenameFileController {

	@FXML
	private Pane pane;

	@FXML
	private TextField fileName;

	@FXML
	private Button rename;

	@FXML
	private Button cancel;

	private Stage stage;
	private File file;

	@FXML
	void initialize() {
		loadEventHandlers();
	}

	private void loadEventHandlers() {

		rename.setOnAction((e) -> {
			rename();
		});

		cancel.setOnAction((e) -> {
			stage.close();
		});

	}

	private void rename() {
		file.setName(fileName.getText());
		stage.close();

		PEApplication.getInstance().getMainController().getFileTabManager().updateFileNameOnFileTab(file);
		PEApplication.getInstance().getMainController().refreshFile(file);
	}

	public void renameFile(File file) {
		this.file = file;
		fileName.setText(file.getName());
	}

	public void setStage(Stage stage) {
		this.stage = stage;

		stage.addEventHandler(KeyEvent.KEY_PRESSED, (e) -> {
			if (e.getCode() == KeyCode.ENTER) {
				rename();
			}
		});

	}

}
