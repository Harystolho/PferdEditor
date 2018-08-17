package com.harystolho.controllers;

import com.harystolho.pe.File;
import com.harystolho.utils.OpenWindow;
import com.harystolho.utils.PEUtils;
import com.harystolho.utils.PropertiesWindowFactory;

import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

public class FileRightClickController {

	@FXML
	private HBox rename;

	@FXML
	private HBox copy;

	@FXML
	private HBox paste;

	@FXML
	private HBox properties;

	private File file;

	@FXML
	void initialize() {
		loadEventHandlers();
	}

	private void loadEventHandlers() {

		rename.setOnMouseClicked((e) -> {
			PropertiesWindowFactory.removeOpenWindow();
			renameFile();
		});

		copy.setOnMouseClicked((e) -> {
			PropertiesWindowFactory.removeOpenWindow();
			copyFile();
		});

		paste.setOnMouseClicked((e) -> {
			PropertiesWindowFactory.removeOpenWindow();
			pasteFile();
		});

		properties.setOnMouseClicked((e) -> {
			PropertiesWindowFactory.removeOpenWindow();
			showProperties();
		});

	}

	/**
	 * Opens a new window to rename the selected file
	 * 
	 * @param file
	 */
	private void renameFile() {
		OpenWindow ow = new OpenWindow("Rename File");

		Parent p = PEUtils.loadFXML("renameFile.fxml", (controller) -> {
			RenameFileController ctrl = (RenameFileController) controller;
			ctrl.setStage(ow.getStage());
			ctrl.renameFile(file);
		});

		ow.openWindow();
	}

	private void copyFile() {
		// TODO copyFile
	}

	private void pasteFile() {
		// TODO pasteFile
	}

	private void showProperties() {
		// TODO showProperties
	}

	public void setFile(File file) {
		this.file = file;
	}

}
