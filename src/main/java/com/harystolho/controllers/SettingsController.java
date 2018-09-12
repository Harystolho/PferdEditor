package com.harystolho.controllers;

import com.harystolho.canvas.CanvasManager;
import com.harystolho.misc.StyleLoader;
import com.harystolho.pe.File;
import com.harystolho.pe.Word;

import javafx.fxml.FXML;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class SettingsController {

	@FXML
	private VBox settingsPane;

	@FXML
	private ListView<String> settingsList;

	@FXML
	private Button cancel;

	@FXML
	private Label tabName;

	@FXML
	private Group panesGroup;

	@FXML
	private Pane generalTab;

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
	private Label textSize;
	@FXML
	private TextField textSizeInput;

	@FXML
	private Pane updatesTab;

	@FXML
	private Button apply;

	private Stage stage;

	@FXML
	void initialize() {
		addMenuItems();
		loadEventHandlers();
	}

	private void loadEventHandlers() {

		settingsList.setOnMouseClicked((e) -> {
			switch (settingsList.getSelectionModel().getSelectedIndex()) {
			case 0:
				showGeneral();
				break;
			case 1:
				showFontsAndColor();
				break;
			case 2:
				showUpdates();
				break;
			}
		});

		apply.setOnAction((e) -> {
			// TODO freezes application
			saveAll();
		});

		cancel.setOnAction((e) -> {
			stage.close();
		});
	}

	private void saveAll() {
		saveFontsTab();
		reRenderWords();
	}

	private void saveFontsTab() {
		StyleLoader.setTextColor(textColorPicker.getValue());
		StyleLoader.setBgColor(bgColorPicker.getValue());
		StyleLoader.setLineColor(lineColorPicker.getValue());
		StyleLoader.setCursorColor(cursorColorPicker.getValue());

		try {
			double size = Double.valueOf(textSizeInput.getText());
			StyleLoader.setFontSize(size);
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

		tabName.setText("Fonts and Color");

		textColorPicker.setValue(StyleLoader.getTextColor());
		bgColorPicker.setValue(StyleLoader.getBgColor());
		lineColorPicker.setValue(StyleLoader.getBackgroundLineColor());
		cursorColorPicker.setValue(StyleLoader.getCursorColor());
		textSizeInput.setText(String.valueOf((int) StyleLoader.getFontSize()));

		fontsTab.setVisible(true);
	}

	private void showUpdates() {
		hideRightPanes();
		updatesTab.setVisible(true);
	}

	private void hideRightPanes() {
		for (Node node : panesGroup.getChildren()) {
			node.setVisible(false);
		}
		tabName.setText("");
	}

	private void addMenuItems() {
		settingsList.getItems().add("General");
		settingsList.getItems().add("Fonts and Color");
		settingsList.getItems().add("Updates");
	}

	/**
	 * If the text color or size was changed it has to update all the words because
	 * they contain a field that determines their color and their size
	 */
	private void reRenderWords() {
		File file = CanvasManager.getInstance().getCurrentFile();

		if (file != null) {
			file.getDrawLock().readLock().lock();

			for (Word word : file.getWords()) {
				word.updateDrawingColor();
				word.updateDrawingSize();
			}

			file.getDrawLock().readLock().unlock();
		}

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
