package com.harystolho.controllers;

import java.io.File;

import com.harystolho.PEApplication;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

public class WorkspaceLoaderController {
	@FXML
	private ChoiceBox<File> workspaceList;

	@FXML
	private Button load;

	private Stage stage;

	@FXML
	void initialize() {
		loadEventHandlers();
	}

	private void loadEventHandlers() {

		workspaceList.setOnMouseClicked((e) -> {
			DirectoryChooser dc = new DirectoryChooser();

			dc.setTitle("Choose the workspace folder");
			java.io.File workspaceDir = dc.showDialog(PEApplication.getInstance().getWindow());

			if (workspaceDir != null) {
				// TODO choice list not showing file name
				workspaceList.getItems().add(workspaceDir);
			}
		});

		load.setOnAction((e) -> {
			if (workspaceList.getItems().size() > 0) {
				openSelectedDir(workspaceList.getItems().get(0));
			}
		});

	}

	private void openSelectedDir(File dir) {
		PEApplication.getInstance().getMainController().changeDirectory(dir);
		stage.close();
		PEApplication.getInstance().getWindow().show();
	}

	public void setStage(Stage stage) {
		this.stage = stage;

		stage.addEventHandler(KeyEvent.KEY_PRESSED, (e) -> {
			if (e.getCode() == KeyCode.ENTER) {
				if (workspaceList.getItems().size() > 0) {
					openSelectedDir(workspaceList.getItems().get(0));
				}
			}
		});

	}

}
