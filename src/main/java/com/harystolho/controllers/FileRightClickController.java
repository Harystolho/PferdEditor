package com.harystolho.controllers;

import com.harystolho.PEApplication;
import com.harystolho.misc.OpenWindow;
import com.harystolho.misc.PropertiesWindowFactory;
import com.harystolho.pe.File;
import com.harystolho.utils.PEUtils;

import javafx.fxml.FXML;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.layout.HBox;

/**
 * When the user presses RIGHT CLICK on a file in the file list, it shows this
 * window
 * 
 * @author Harystolho
 *
 */
public class FileRightClickController {

	@FXML
	private HBox rename;

	@FXML
	private HBox copy;

	@FXML
	private HBox delete;

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
			copyFile(file);
		});

		properties.setOnMouseClicked((e) -> {
			PropertiesWindowFactory.removeOpenWindow();
			showProperties();
		});

		delete.setOnMouseClicked((e) -> {
			PropertiesWindowFactory.removeOpenWindow();
			PEApplication.getInstance().getMainController().deleteFile(file);
		});
	}

	/**
	 * Opens a new window to rename the selected file
	 * 
	 * @param file
	 */
	private void renameFile() {
		OpenWindow ow = new OpenWindow("Rename File");
		ow.load("renameFile.fxml", (controller) -> {
			RenameFileController ctrl = (RenameFileController) controller;
			ctrl.setStage(ow.getStage());
			ctrl.renameFile(file);
		});
		ow.openWindow();
	}

	private void showProperties() {
		// TODO showProperties
	}

	public void setFile(File file) {
		this.file = file;
	}

	/**
	 * Copies the whole file content to the clipboard
	 * 
	 * @param f
	 */
	public static void copyFile(File f) {
		if (f == null) {
			return;
		}

		ClipboardContent clipboardContent = new ClipboardContent();
		StringBuilder sb = new StringBuilder();

		// If the file is not loaded then load it, copy it and close it. If it is
		// already opened just copy it.
		boolean wasFileLoaded = true;

		if (!f.isLoaded()) {
			PEUtils.loadFileFromDisk(f);
			wasFileLoaded = false;
		}

		f.getWords().stream().forEach((word) -> {
			switch (word.getType()) {
			case NORMAL:
			case SPACE:
				sb.append(word.getWordAsString());
				break;
			case NEW_LINE:
				sb.append(System.getProperty("line.separator"));
				break;
			case TAB:
				sb.append("\t");
				break;
			}
		});

		clipboardContent.putString(sb.toString());
		Clipboard.getSystemClipboard().setContent(clipboardContent);

		if (!wasFileLoaded) {
			f.unload();
		}
	}

}
