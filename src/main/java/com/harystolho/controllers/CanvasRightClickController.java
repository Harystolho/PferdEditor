package com.harystolho.controllers;

import javafx.fxml.FXML;
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
	}

	private void loadEventHandlers() {
		undo.setOnMouseClicked((e) -> {

		});

		save.setOnMouseClicked((e) -> {

		});

		cut.setOnMouseClicked((e) -> {

		});

		copy.setOnMouseClicked((e) -> {

		});

		paste.setOnMouseClicked((e) -> {

		});
	}
}
