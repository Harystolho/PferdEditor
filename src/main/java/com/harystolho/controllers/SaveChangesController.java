package com.harystolho.controllers;

import com.harystolho.Main;
import com.harystolho.pe.File;
import com.harystolho.utils.PEUtils;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public class SaveChangesController {

	@FXML
	private Pane pane;

	@FXML
	private Button saveAs;

	@FXML
	private Button save;

	@FXML
	private Button dontSave;

	@FXML
	private Label file;

	@FXML
	private Button cancel;

	private Stage stage;
	private File PEFile;

	@FXML
	void initialize() {
		loadEventHandlers();
	}

	private void loadEventHandlers() {

		save.setOnAction((e) -> {
			PEUtils.saveFile(PEFile, PEFile.getDiskFile());
			closeFile(PEFile);
			stage.close();
		});

		saveAs.setOnAction((e) -> {
			PEUtils.saveFileAs(PEFile);
			closeFile(PEFile);
			stage.close();
		});

		dontSave.setOnAction((e) -> {
			closeFile(PEFile);
			stage.close();
		});

		cancel.setOnAction((e) -> {
			stage.close();
		});
	}

	private void closeFile(File file) {
		Main.getApplication().getMainController().removeFileFromFileTab(file);
		Main.getApplication().getMainController().selectFirstFileTab();

		file.unload();
	}

	public void setStage(Stage stage) {
		this.stage = stage;
	}

	public void setPEFile(File pEFile) {
		PEFile = pEFile;
		file.setText(PEFile.getName());
	}

}
