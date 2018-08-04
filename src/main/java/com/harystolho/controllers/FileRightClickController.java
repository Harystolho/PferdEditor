package com.harystolho.controllers;

import com.harystolho.pe.File;
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
			copyFile();
			PropertiesWindowFactory.removeOpenWindow();
		});

		paste.setOnMouseClicked((e) -> {
			pasteFile();
			PropertiesWindowFactory.removeOpenWindow();
		});

		properties.setOnMouseClicked((e) -> {
			showProperties();
			PropertiesWindowFactory.removeOpenWindow();
		});

	}

	/**
	 * Opens a new window to rename the selected file
	 * 
	 * @param file
	 */
	private void renameFile() {
		Stage stage = new Stage();

		Parent p = PEUtils.loadFXML("renameFile.fxml", (controller) -> {
			RenameFileController ctrl = (RenameFileController) controller;
			ctrl.renameFile(file);
			ctrl.setStage(stage);
		});

		// TODO update list when file name changes

		Scene scene = new Scene(p);
		scene.getStylesheets().add(ClassLoader.getSystemResource("style.css").toExternalForm());

		scene.setOnKeyPressed((e) -> {
			if (e.getCode() == KeyCode.ESCAPE) {
				stage.close();
			}
		});

		stage.setScene(scene);
		stage.show();
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