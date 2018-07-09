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
	private ImageView deleteFile;

	@FXML
	private ImageView refresh;

	@FXML
	private VBox canvasBox;

	@FXML
	private Canvas canvas;

	@FXML
	private Pane canvasInformationBar;

	@FXML
	private ListView<File> fileList;

	private CanvasManager canvasManager;

	@FXML
	void initialize() {
		Main.getApplication().setMainController(this);

		loadEventHandlers();

		canvasManager = new CanvasManager(canvas);

	}

	private void loadEventHandlers() {

		newFile.setOnMouseClicked((e) -> {
			createNewFile();
		});

		fileList.getSelectionModel().selectedItemProperty().addListener((obv, oldValue, newValue) -> {
			loadFile(newValue);
		});

		deleteFile.setOnMouseClicked((e) -> {
			deleteFile(fileList.getSelectionModel().getSelectedItem());
		});

		refresh.setOnMouseClicked((e) -> {
			fileList.getSelectionModel().clearSelection();
		});

	}

	/**
	 * Creates a new {@link com.harystolho.canvas.File File} and adds it to
	 * {@link #fileList}
	 */
	private void createNewFile() {
		File file = new File("New File" + new Random().nextInt(100));
		fileList.getItems().add(file);
	}

	private void deleteFile(File file) {
		fileList.getItems().remove(file);

		if (fileList.getItems().size() == 0) {
			canvasManager.stopRenderThread();
		}

	}

	/**
	 * Loads this file in the canvas and draws it.
	 * 
	 * @param file the file to load. If the file is <code>null</code> it does
	 *             nothing.
	 */
	private void loadFile(File file) {

		if (file == null) {
			return;
		}

		System.out.println("Loading file: " + file.getName());

		canvasManager.setCurrentFile(file);

		if (!RenderThread.isRunning()) {
			canvasManager.initRenderThread();
		}

	}

	public Canvas getCanvas() {
		return canvas;
	}

	public CanvasManager getCanvasManager() {
		return canvasManager;
	}

	@Override
	public void onWidthResize(int width) {
		menuBar.setPrefWidth(width);

		secundaryMenu.setPrefWidth(width);

		// 10 is right margin
		// 238 is file list on the left
		canvasBox.setPrefWidth(width - 238 - 10);

		canvas.setWidth(canvasBox.getPrefWidth());

	}

	@Override
	public void onHeightResize(int height) {

		canvasBox.setPrefHeight(height - secundaryMenu.getHeight() - menuBar.getHeight());

		// 25 = canvasInformationBar Height
		canvas.setHeight(canvasBox.getPrefHeight() - 25);
	}

}
