package com.harystolho.controllers;

import com.harystolho.canvas.CanvasManager;
import com.harystolho.misc.StyleLoader;
import com.harystolho.thread.FileUpdaterThread;

import javafx.fxml.FXML;
import javafx.scene.Group;
import javafx.scene.control.Button;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * Controller used to change the application settings
 * 
 * @author Harystolho
 *
 */
public class SettingsController {

	@FXML
	private VBox settingsPane;

	@FXML
	private VBox settingsBox;

	// Settings List
	@FXML
	private Label general;
	@FXML
	private Label fontsAndColor;
	@FXML
	private Label updates;
	// END

	@FXML
	private Button cancel;

	@FXML
	private Label tabName;

	@FXML
	private Group panesGroup;

	// General Tab
	@FXML
	private Pane generalTab;
	// END

	// Fonts Tab
	@FXML
	private Pane fontsTab;
	@FXML
	private Label textColor;
	@FXML
	private ColorPicker textColorPicker;
	@FXML
	private Label bgColor;
	@FXML
	private ColorPicker bgColorPicker;
	@FXML
	private Label lineColor;
	@FXML
	private ColorPicker lineColorPicker;
	@FXML
	private Label cursorColor;
	@FXML
	private ColorPicker cursorColorPicker;
	@FXML
	private Label selectionColor;
	@FXML
	private ColorPicker selectionColorPicker;
	@FXML
	private Label textSize;
	@FXML
	private TextField textSizeInput;
	// END

	// Updates Tab
	@FXML
	private Pane updatesTab;
	// END

	@FXML
	private Button apply;

	private Stage stage;

	@FXML
	void initialize() {
		loadEventHandlers();
	}

	private void loadEventHandlers() {

		general.setOnMouseClicked((e) -> {
			showGeneral();
		});

		fontsAndColor.setOnMouseClicked((e) -> {
			showFontsAndColor();
		});

		updates.setOnMouseClicked((e) -> {
			showUpdates();
		});

		apply.setOnAction((e) -> {
			saveAll();
			stage.close();
		});

		cancel.setOnAction((e) -> {
			stage.close();
		});
	}

	/**
	 * Saves all the settings
	 */
	private void saveAll() {
		saveFontsTab();
		CanvasManager.getInstance().reRenderWords();
	}

	private void saveFontsTab() {
		StyleLoader.setTextColor(textColorPicker.getValue());
		StyleLoader.setBgColor(bgColorPicker.getValue());
		StyleLoader.setLineColor(lineColorPicker.getValue());
		StyleLoader.setCursorColor(cursorColorPicker.getValue());
		StyleLoader.setSelectionColor(selectionColorPicker.getValue());

		changeFontSize();

	}

	private void changeFontSize() {
		int oldFontSize = (int) StyleLoader.getFontSize();

		try {
			double newFontSize = Double.valueOf(textSizeInput.getText());

			// If the new font size is different from the old one then it has to recalculate
			// some information
			if (oldFontSize != newFontSize) {
				StyleLoader.setFontSize(newFontSize);

				// Recalculate
				FileUpdaterThread.calculate();
				CanvasManager.getInstance().preRender();
				CanvasManager.getInstance().setCursorY(CanvasManager.getInstance().getCursorY());
				CanvasManager.getInstance().setCursorX(CanvasManager.getInstance().getCursorX());
			}

		} catch (NumberFormatException e) {
			// do nothing
		}

	}

	private void showGeneral() {
		hideRightPanes();
		generalTab.setVisible(true);
	}

	private void showFontsAndColor() {
		hideRightPanes();
		fontsTab.setVisible(true);

		tabName.setText("Fonts and Color");

		textColorPicker.setValue(StyleLoader.getTextColor());
		bgColorPicker.setValue(StyleLoader.getBgColor());
		lineColorPicker.setValue(StyleLoader.getBackgroundLineColor());
		cursorColorPicker.setValue(StyleLoader.getCursorColor());
		selectionColorPicker.setValue(StyleLoader.getSelectionColor());
		textSizeInput.setText(String.valueOf((int) StyleLoader.getFontSize()));
	}

	private void showUpdates() {
		hideRightPanes();
		updatesTab.setVisible(true);
	}

	private void hideRightPanes() {
		panesGroup.getChildren().forEach((p) -> {
			p.setVisible(false);
		});

		tabName.setText("");
	}

	public void setStage(Stage stage) {
		this.stage = stage;

		stage.addEventHandler(KeyEvent.KEY_PRESSED, (e) -> {
			if (e.getCode() == KeyCode.END) {
				stage.close();
			}
		});

	}

}
