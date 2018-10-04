package com.harystolho.controllers;

import com.harystolho.PEApplication;
import com.harystolho.canvas.CanvasManager;
import com.harystolho.misc.PropertiesWindowFactory;
import com.harystolho.pe.File;

import javafx.fxml.FXML;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;

/**
 * When the user presses RIGHT CLICK on the canvas, it shows this window
 * 
 * @author Harystolho
 *
 */
public class CanvasRightClickController {

	@FXML
	private HBox undo;

	@FXML
	private HBox save;

	@FXML
	private HBox cut;

	@FXML
	private HBox copy;

	@FXML
	private HBox paste;

	@FXML
	void initialize() {
		loadEventHandlers();
		initCheck();
	}

	private void loadEventHandlers() {
		undo.setOnMouseClicked((e) -> {
			PropertiesWindowFactory.removeOpenWindow();
		});

		save.setOnMouseClicked((e) -> {
			PropertiesWindowFactory.removeOpenWindow();
			PEApplication.getInstance().getMainController().saveOpenedFile();
		});

		cut.setOnMouseClicked((e) -> {
			PropertiesWindowFactory.removeOpenWindow();
		});

		copy.setOnMouseClicked((e) -> {
			PropertiesWindowFactory.removeOpenWindow();
		});

		paste.setOnMouseClicked((e) -> {
			PropertiesWindowFactory.removeOpenWindow();
			pasteFile(CanvasManager.getInstance().getCurrentFile());
		});
	}

	/**
	 * If no text is selected then some options have to be disabled, if the file
	 * hasn't been modified the save option has to be disabled
	 */
	private void initCheck() {
		File file = CanvasManager.getInstance().getCurrentFile();

		if (file != null) {
			if (file.wasModified()) {
				save.setDisable(false);
			} else {
				save.setDisable(true);
			}
		}
	}

	/**
	 * Copies the selected words to clipboard
	 * 
	 * @param f
	 */
	public static void copySelectedWords() {
		File file = CanvasManager.getInstance().getCurrentFile();

		if (file == null) {
			return;
		}

		ClipboardContent clipboardContent = new ClipboardContent();
		StringBuilder sb = new StringBuilder();

		CanvasManager.getInstance().getWordsInsideSelectionBound().forEach((word) -> {
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
	}

	/**
	 * Pastes the clipboard content to the file at the cursor position
	 * 
	 * @param f
	 */
	public static void pasteFile(File f) {
		if (f == null) {
			return;
		}

		Clipboard.getSystemClipboard().getString().chars().forEach((iChar) -> {
			KeyEvent ke = null;

			switch (iChar) {
			case ' ':
				ke = new KeyEvent(null, null, KeyEvent.KEY_PRESSED, "", String.valueOf((char) iChar), KeyCode.SPACE,
						false, false, false, false);
				break;
			case '\n':
				ke = new KeyEvent(null, null, KeyEvent.KEY_PRESSED, "", String.valueOf((char) iChar), KeyCode.ENTER,
						false, false, false, false);
				break;
			case '\r':
				return;
			case '\t':
				ke = new KeyEvent(null, null, KeyEvent.KEY_PRESSED, "", String.valueOf((char) iChar), KeyCode.TAB,
						false, false, false, false);
				break;
			default:
				ke = new KeyEvent(null, null, KeyEvent.KEY_PRESSED, "", String.valueOf((char) iChar), KeyCode.UNDEFINED,
						false, false, false, false);
				break;
			}

			f.type(ke);
		});
	}

}