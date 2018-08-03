package com.harystolho.controllers;

import java.awt.Desktop;
import java.awt.Desktop.Action;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Random;
import java.util.regex.Pattern;

import com.harystolho.Main;
import com.harystolho.canvas.CanvasManager;
import com.harystolho.pe.File;
import com.harystolho.utils.PEUtils;
import com.harystolho.utils.PropertiesWindowFactory;
import com.harystolho.utils.PropertiesWindowFactory.window_type;
import com.harystolho.utils.RenderThread;

import javafx.fxml.FXML;
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

		fileList.setOnMouseClicked((e) -> {
			PropertiesWindowFactory.removeOpenWindow();

			if (e.getButton() == MouseButton.PRIMARY) {
				if (e.getClickCount() == 2) { // Double click
					loadFileInCanvas(fileList.getSelectionModel().getSelectedItem());
				}
			} else if (e.getButton() == MouseButton.SECONDARY) {
				if (fileList.getSelectionModel().getSelectedItem() != null) {
					// Opens right click propeties window
					openFileRightClickWindow(fileList.getSelectionModel().getSelectedItem(), e.getSceneX(),
							e.getSceneY());
				}
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
			Main.getApplication().getWindow().close();
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
			openAboutPage();
		});

	}

	private void openAboutPage() {
		Desktop desktop = Desktop.isDesktopSupported() ? Desktop.getDesktop() : null;
		if (desktop != null && desktop.isSupported(Action.BROWSE)) {
			try {
				desktop.browse(new URI("https://github.com/Harystolho/PferdEditor"));
			} catch (IOException | URISyntaxException e) {
				System.out.println("Couldn't open github link");
			}
		}

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
	 * Opens a properties window( this is the window that opens when you press right
	 * click) at the cursor's position
	 * 
	 * @param file
	 * @param x
	 * @param y
	 */
	private void openFileRightClickWindow(File file, double x, double y) {
		PropertiesWindowFactory.open(window_type.FILE, x, y, (controller) -> {
			FileRightClickController fileController = (FileRightClickController) controller;
			fileController.setFile(file);
		});
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
			loadSaveDirectory(); // Loads the new directory
		}
	}

	private void loadSaveDirectory() {
		fileList.getItems().clear();
		loadFileNames();
	}

	/**
	 * Loads the files name from the current directory
	 */
	private void loadFileNames() {
		java.io.File saveFolder = PEUtils.getSaveFolder();
		if (saveFolder.exists()) {
			for (java.io.File file : saveFolder.listFiles()) {
				if (!file.isDirectory()) {
					addNewFile(PEUtils.createFileFromSourceFile(file));
				}
			}
		}
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
