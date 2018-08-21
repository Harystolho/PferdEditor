package com.harystolho.controllers;

import com.harystolho.pe.File;
import com.harystolho.utils.OpenWindow;
import com.harystolho.utils.PEUtils;
import com.harystolho.utils.PropertiesWindowFactory;

import javafx.fxml.FXML;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;

public class FileRightClickController {

	// TODO REFACTOR WHOLE CODE
	
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

		ow.load("renameFile.fxml", (controller) -> {
			RenameFileController ctrl = (RenameFileController) controller;
			ctrl.setStage(ow.getStage());
			ctrl.renameFile(file);
		});

		ow.openWindow();
	}

	private void copyFile() {
		copyFile(file);
	}

	private void pasteFile() {
		pasteFile(file);
	}

	private void showProperties() {
		// TODO showProperties
	}

	public void setFile(File file) {
		this.file = file;
	}

	public static void copyFile(File f) {
		if (f == null) {
			return;
		}

		ClipboardContent cd = new ClipboardContent();
		StringBuilder sb = new StringBuilder();

		// If the file is not loaded then load it, copy it and close it. If it already
		// opened just copy it.
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

		cd.putString(sb.toString());
		Clipboard.getSystemClipboard().setContent(cd);

		if (!wasFileLoaded) {
			f.unload();
		}
	}

	public static void pasteFile(File f) {
		if (f == null) {
			return;
		}

		Clipboard.getSystemClipboard().getString().chars().forEach((iChar) -> {
			KeyEvent ke = new KeyEvent(null, null, KeyEvent.KEY_PRESSED, " ", String.valueOf((char) iChar),
					KeyCode.UNDEFINED, false, false, false, false);
			f.type(ke);
		});
	}

}
