package com.harystolho.controllers;

import com.harystolho.misc.StyleLoader;
import com.harystolho.utils.PEConfiguration;

import javafx.css.StyleableLongProperty;
import javafx.fxml.FXML;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
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
			saveAll();
		});

		cancel.setOnAction((e) -> {
			stage.close();
		});
	}

	private void saveAll() {
		saveFontsTab();
	}

	private void saveFontsTab() {
		StyleLoader.setTextColor(textColorPicker.getValue());
		StyleLoader.setBgColor(bgColorPicker.getValue());
		StyleLoader.setLineColor(lineColorPicker.getValue());
		StyleLoader.setCursorColor(cursorColorPicker.getValue());
	}

	private void showGeneral() {
		hideRightPanes();
		generalTab.setVisible(true);
	}

	private void showFontsAndColor() {
		hideRightPanes();

		textColorPicker.setValue(StyleLoader.getTextColor());
		bgColorPicker.setValue(StyleLoader.getBgColor());
		lineColorPicker.setValue(StyleLoader.getBackgroundLineColor());

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
	}

	private void addMenuItems() {
		settingsList.getItems().add("General");
		settingsList.getItems().add("Fonts and Color");
		settingsList.getItems().add("Updates");
	}

	public void setStage(Stage stage) {
		this.stage = stage;

		stage.addEventHandler(KeyEvent.KEY_PRESSED, (e) -> {
			if (e.getCode() == KeyCode.ENTER) {

			}
		});

	}

}
