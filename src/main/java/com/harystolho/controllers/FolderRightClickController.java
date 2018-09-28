package com.harystolho.controllers;

import java.util.Optional;

import com.harystolho.PEApplication;
import com.harystolho.misc.OpenWindow;
import com.harystolho.misc.PropertiesWindowFactory;
import com.harystolho.misc.explorer.ExplorerFolder;
import com.harystolho.pe.File;
import com.harystolho.utils.PEUtils;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.layout.HBox;

/**
 * When the user presses RIGHT CLICK on a folder in the file explorer, it shows
 * this window
 * 
 * @author Harystolho
 *
 */
public class FolderRightClickController {

	@FXML
	private HBox rename;

	@FXML
	private HBox newFile;

	@FXML
	private HBox copy;

	@FXML
	private HBox delete;

	@FXML
	private HBox properties;

	private ExplorerFolder folder;

	@FXML
	void initialize() {
		loadEventHandlers();
	}

	private void loadEventHandlers() {

		newFile.setOnMouseClicked((e) -> {
			PropertiesWindowFactory.removeOpenWindow();
			MainController.createNewFile(folder.getDiskFile());
		});

		rename.setOnMouseClicked((e) -> {
			PropertiesWindowFactory.removeOpenWindow();
			// TODO rename folder
		});

		copy.setOnMouseClicked((e) -> {
			PropertiesWindowFactory.removeOpenWindow();
			// TODO copy folder
		});

		properties.setOnMouseClicked((e) -> {
			PropertiesWindowFactory.removeOpenWindow();
			showProperties();
		});

		delete.setOnMouseClicked((e) -> {
			PropertiesWindowFactory.removeOpenWindow();
			deleteFolderAlert();
		});
	}

	private void deleteFolderAlert() {
		Alert alert = new Alert(AlertType.CONFIRMATION);

		alert.setTitle("Delete folder");
		alert.setContentText("Are you sure you want to delete this folder?");

		Optional<ButtonType> result = alert.showAndWait();

		if (result.isPresent()) {
			if (result.get() == ButtonType.OK) {
				deleteFiles(folder.getDiskFile()); // Delete files inside the folder
				folder.getDiskFile().delete(); // Delete the folder
				PEApplication.getInstance().getMainController().deleteFolder(folder.getDiskFile());
			}
		}

	}

	private static void deleteFiles(java.io.File folder) {
		for (java.io.File f : folder.listFiles()) {
			if (f.isDirectory()) {
				deleteFiles(f); // Delete files inside folder
				f.delete(); // Delete the folder
			} else {
				f.delete();
			}
		}
	}

	private void showProperties() {
		// TODO showProperties
	}

	public void setFolder(ExplorerFolder folder) {
		this.folder = folder;
	}

}
