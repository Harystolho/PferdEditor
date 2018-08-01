package com.harystolho.controllers;

import java.util.Random;
import java.util.regex.Pattern;

import com.harystolho.Main;
import com.harystolho.canvas.CanvasManager;
import com.harystolho.pe.File;
import com.harystolho.utils.PEUtils;
import com.harystolho.utils.RenderThread;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class MainController implements ResizableInterface {

	@FXML
	private Pane pane;

	@FXML
	private MenuBar menuBar;

	@FXML
	private MenuItem menuNeFile;

	@FXML
	private MenuItem menuSave;

	@FXML
	private MenuItem menuSaveAs;

	@FXML
	private MenuItem menuExit;

	@FXML
	private MenuItem menuSearch;

	@FXML
	private MenuItem menuReplace;

	@FXML
	private MenuItem menuSettings;

	@FXML
	private MenuItem menuCheckForUpdates;

	@FXML
	private MenuItem menuAbout;

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

	@FXML
	private Label fileDirectory;

	private CanvasManager canvasManager;

	@FXML
	void initialize() {
		loadEventHandlers();

		canvasManager = new CanvasManager(canvas);

		loadSaveDirectory();
		setCurrentDirectoryLabel(PEUtils.getSaveFolder());

	}

	private void loadEventHandlers() {

		loadMenuBarItemHandler();

		fileList.getSelectionModel().selectedItemProperty().addListener((obv, oldValue, newValue) -> {
			loadFileInCanvas(newValue);
		});

		fileList.setOnMouseClicked((e) -> {
			if (e.getButton() == MouseButton.SECONDARY) {
				renameFile(fileList.getSelectionModel().getSelectedItem());
			}
		});

		newFile.setOnMouseClicked((e) -> {
			createNewFile();
		});

		saveFile.setOnMouseClicked((e) -> {
			PEUtils.saveFiles(fileList.getItems());
		});

		deleteFile.setOnMouseClicked((e) -> {
			deleteFile(fileList.getSelectionModel().getSelectedItem());
		});

		refresh.setOnMouseClicked((e) -> {
			File f = fileList.getSelectionModel().getSelectedItem();
			if (f != null) {
				f.setLoaded(false);
				f.resetLastWord();
				fileList.getSelectionModel().clearSelection();
			}
		});

		fileDirectory.setOnMouseClicked((e) -> {
			changeDirectory();
		});

	}

	/**
	 * Sets the event handler for items on the menu
	 */
	private void loadMenuBarItemHandler() {

		menuNeFile.setOnAction((e) -> {
			createNewFile();
		});

		menuSave.setOnAction((e) -> {
			PEUtils.saveFiles(fileList.getItems());
		});

		menuSaveAs.setOnAction((e) -> {
			saveFileAs(canvasManager.getCurrentFile());
		});

		menuExit.setOnAction((e) -> {
			PEUtils.exit();
		});

		menuSearch.setOnAction((e) -> {

		});

		menuReplace.setOnAction((e) -> {

		});

		menuSettings.setOnAction((e) -> {

		});

		menuCheckForUpdates.setOnAction((e) -> {

		});

		menuAbout.setOnAction((e) -> {

		});

	}

	private void saveFileAs(File currentFile) {
		FileChooser fc = new FileChooser();
		java.io.File file = fc.showSaveDialog(Main.getApplication().getWindow());

		if (file != null) {
			PEUtils.saveFile(currentFile, file);
		}
	}

	/**
	 * Creates a new {@link File} and adds it to {@link #fileList}
	 */
	private void createNewFile() {
		File file = new File("New File" + new Random().nextInt(100));
		fileList.getItems().add(file);
	}

	public void addNewFile(File file) {
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
	public void loadFileInCanvas(File file) {

		if (file == null) {
			return;
		}

		if (!file.isLoaded()) {
			file.getWords().clear();
			PEUtils.loadFileFromDisk(file);
		}

		canvasManager.setCurrentFile(file);

		if (!RenderThread.isRunning()) {
			canvasManager.initRenderThread();
		}

	}

	public void setCurrentDirectoryLabel(java.io.File directory) {
		// Split the full directory name
		String fullDirName[] = directory.getPath().split(Pattern.quote(java.io.File.separator));
		// Get only the last part (Eg: C:/file_to/folder/LAST_PART)
		String lastDirName = fullDirName[fullDirName.length - 1];
		fileDirectory.setText(lastDirName);
	}

	/**
	 * Opens a new window to rename the selected file
	 * 
	 * @param selectedItem
	 */
	private void renameFile(File selectedItem) {
		Stage stage = new Stage();

		Parent p = PEUtils.loadFXML("renameFile.fxml", (controller) -> {
			RenameFileController ctrl = (RenameFileController) controller;
			ctrl.renameFile(selectedItem);
			ctrl.setStage(stage);
		});

		// TODO update list when file name changes

		Scene scene = new Scene(p);
		scene.getStylesheets().add(ClassLoader.getSystemResource("style.css").toExternalForm());

		stage.setScene(scene);
		stage.show();
	}

	/**
	 * Opens a new window to select the new directory
	 */
	private void changeDirectory() {
		DirectoryChooser dc = new DirectoryChooser();
		dc.setTitle("Choose the new directory folder");
		java.io.File newDir = dc.showDialog(Main.getApplication().getWindow());
		if (newDir != null && newDir.isDirectory()) {
			PEUtils.setSaveFolder(newDir); // Updates the save folder
			setCurrentDirectoryLabel(newDir); // Updates the directory label
			loadSaveDirectory();
		}
	}

	private void loadSaveDirectory() {
		fileList.getItems().clear();
		PEUtils.loadFileNames(this);
	}

	/**
	 * @return if a file is selected
	 */
	public boolean isFileSelected() {
		return fileList.getSelectionModel().getSelectedIndices().isEmpty() ? false : true;
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
