package com.harystolho.controllers;

import java.util.Random;

import com.harystolho.Main;
import com.harystolho.canvas.CanvasManager;
import com.harystolho.canvas.File;
import com.harystolho.utils.RenderThread;

import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuBar;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

public class MainController implements ResizableInterface {

	@FXML
	private Pane pane;

	@FXML
	private MenuBar menuBar;

	@FXML
	private FlowPane secundaryMenu;

	@FXML
	private ImageView newFile;

	@FXML
	private ImageView saveFile;

	@FXML
	private VBox canvasBox;

	@FXML
	private Canvas canvas;

	@FXML
	private Pane canvasInformationBar;

	@FXML
	private ListView<File> fileList;

	private CanvasManager cm;

	@FXML
	void initialize() {
		Main.getApplication().setMainController(this);

		loadEventHandlers();

		cm = new CanvasManager(canvas);

	}

	private void loadEventHandlers() {

		newFile.setOnMouseClicked((e) -> {
			File file = new File("New File" + new Random().nextInt(100));
			fileList.getItems().add(file);
		});

		fileList.getSelectionModel().selectedItemProperty().addListener((obv, oldValue, newValue) -> {
			loadFile(newValue);
		});

	}

	private void loadFile(File file) {
		System.out.println("Loading file: " + file.getName());

		cm.setCurrentFile(file);
		
		cm.initRenderThread();
		
	}

	public Canvas getCanvas() {
		return canvas;
	}

	public CanvasManager getCanvasManager() {
		return cm;
	}

	@Override
	public void onWidthResize(int width) {
		menuBar.setPrefWidth(width);

		secundaryMenu.setPrefWidth(width);

		canvasBox.setPrefWidth(width - 238);

	}

	@Override
	public void onHeightResize(int height) {

		canvasBox.setPrefHeight(height - secundaryMenu.getHeight() - menuBar.getHeight());

		// 25 = canvasInformationBar Height
		canvas.setHeight(canvasBox.getPrefHeight() - 25);
	}

}
