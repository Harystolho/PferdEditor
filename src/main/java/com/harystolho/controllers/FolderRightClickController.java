package com.harystolho.controllers;

import com.harystolho.PEApplication;
import com.harystolho.misc.OpenWindow;
import com.harystolho.misc.PropertiesWindowFactory;
import com.harystolho.misc.explorer.ExplorerFolder;
import com.harystolho.pe.File;
import com.harystolho.utils.PEUtils;

import javafx.fxml.FXML;
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
			// TODO delete folder
		});
	}

	private void showProperties() {
		// TODO showProperties
	}

	public void setFolder(ExplorerFolder folder) {
		this.folder = folder;
	}

}
