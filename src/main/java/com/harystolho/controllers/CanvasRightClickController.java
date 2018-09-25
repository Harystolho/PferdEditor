package com.harystolho.controllers;

import javafx.fxml.FXML;
import javafx.scene.layout.HBox;

/**
 * When the user presses RIGHT CLICK on a file in the file list, it shows this
 * window
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
	}

	private void loadEventHandlers() {

	}
}
