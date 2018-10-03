package com.harystolho.controllers;

import java.io.File;

import com.harystolho.PEApplication;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

/**
 * Controller used to change the workspace
 * 
 * @author Harystolho
 *
 */
public class WorkspaceLoaderController {
	@FXML
	private ComboBox<File> workspaceList;

	@FXML
	private Button load;

	private Stage stage;

	private File selectWorkspaceText;

	@FXML
	void initialize() {

		addItems();

		loadEventHandlers();
	}

	private void addItems() {
		selectWorkspaceText = new File("") {
			@Override
			public String toString() {
				return "Add new workspace folder";
			}
		};

		workspaceList.getItems().add(selectWorkspaceText);
	}

	private void loadEventHandlers() {

		workspaceList.getSelectionModel().selectedItemProperty().addListener((obv, oldValue, newValue) -> {
			if (newValue == selectWorkspaceText) {
				showDirectoryChooser(newValue);
			}
		});

		load.setOnAction((e) -> {
			if (workspaceList.getItems().size() > 0) {
				openSelectedDir(workspaceList.getItems().get(0));
			}
		});

	}

	private void showDirectoryChooser(File newValue) {
		DirectoryChooser dc = new DirectoryChooser();

		dc.setTitle("Choose the workspace folder");
		java.io.File workspaceDir = dc.showDialog(PEApplication.getInstance().getWindow());

		if (workspaceDir != null) {
			workspaceList.getItems().add(0, workspaceDir);
			workspaceList.getSelectionModel().selectFirst();
		}
	}

	private void openSelectedDir(File dir) {
		PEApplication.getInstance().getMainController().updateWorkspaceDirectory(dir);
		stage.close(); // Hide this window
		PEApplication.getInstance().getWindow().show(); // Show the main window again
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
