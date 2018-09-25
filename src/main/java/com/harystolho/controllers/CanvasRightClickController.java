package com.harystolho.controllers;

import com.harystolho.PEApplication;
import com.harystolho.canvas.CanvasManager;
import com.harystolho.misc.PropertiesWindowFactory;
import com.harystolho.pe.File;
import com.sun.org.apache.bcel.internal.generic.IFNULL;

import javafx.fxml.FXML;
import javafx.scene.input.Clipboard;
import javafx.scene.input.DataFormat;
import javafx.scene.layout.HBox;

/**
 * When the user presses RIGHT CLICK on the canvas, it shows this window
 * 
 * @author Harystolho
 *
 */
public class CanvasRightClickController {

	// TODO fix hovering issue

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
}
