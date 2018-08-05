package com.harystolho.controllers;

import com.harystolho.Main;
import com.harystolho.pe.File;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

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
			file.setName(fileName.getText());
			stage.close();
			Main.getApplication().getMainController().refrestFileList();
		});

		cancel.setOnAction((e) -> {
			stage.close();
		});

	}

	public void renameFile(File file) {
		this.file = file;
	}

	public void setStage(Stage stage) {
		this.stage = stage;
	}

}
