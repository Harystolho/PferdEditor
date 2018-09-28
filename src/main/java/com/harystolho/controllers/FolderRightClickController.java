package com.harystolho.controllers;

import java.util.Arrays;
import java.util.Optional;

import com.harystolho.PEApplication;
import com.harystolho.misc.PropertiesWindowFactory;
import com.harystolho.misc.explorer.ExplorerFolder;
import com.harystolho.utils.PEUtils;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DataFormat;
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
			copyFolder();
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

	private void copyFolder() {
		if (folder.getDiskFile() == null || !folder.getDiskFile().exists()) {
			return;
		}

		ClipboardContent clipboardContent = new ClipboardContent();

		clipboardContent.putFiles(Arrays.asList(folder.getDiskFile()));

		Clipboard.getSystemClipboard().setContent(clipboardContent);
	}

	/**
	 * Shows an alert to confirm the folder deletion. If the user presses OK, it
	 * will delete all files inside the folder and then the folder itself
	 */
	private void deleteFolderAlert() {
		Alert alert = new Alert(AlertType.CONFIRMATION);

		alert.setTitle("Delete folder");
		alert.setHeaderText("Are you sure you want to delete this folder?");
		alert.setContentText("This can't be undone");

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
		for (java.io.File file : folder.listFiles()) {
			if (file.isDirectory()) {
				deleteFiles(file); // Delete files inside folder
				file.delete(); // Delete the folder
			} else {
				file.delete();
			}
		}
	}

	private void showProperties() {
		// TODO show folder Properties
	}

	public void setFolder(ExplorerFolder folder) {
		this.folder = folder;
	}

}
