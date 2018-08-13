package com.harystolho.controllers;

import java.awt.Desktop;
import java.awt.Desktop.Action;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.regex.Pattern;

import com.harystolho.Main;
import com.harystolho.canvas.CanvasManager;
import com.harystolho.misc.Tab;
import com.harystolho.pe.File;
import com.harystolho.thread.FileUpdaterThread;
import com.harystolho.thread.RenderThread;
import com.harystolho.utils.PEUtils;
import com.harystolho.utils.PropertiesWindowFactory;
import com.harystolho.utils.PropertiesWindowFactory.window_type;

import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
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
	private HBox filesTab;

	@FXML
	private VBox canvasBox;

	@FXML
	private Canvas canvas;

	@FXML
	private Pane canvasInformationBar;

	@FXML
	private ListView<File> fileList;

	@FXML
	private Pane rightScrollBar;
	private double lastY = 0;

	@FXML
	private Rectangle rightScrollInside;

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
					// Opens right click properties window
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

		rightScrollBar.setOnMousePressed((e) -> {
			lastY = e.getY();
		});

		rightScrollBar.setOnMouseDragged((e) -> {

			// If cursor is inside scroll bar
			if (e.getY() >= rightScrollInside.getLayoutY()
					&& e.getY() <= rightScrollInside.getLayoutY() + rightScrollInside.getHeight()) {
				double displacement = lastY - e.getY();

				canvasManager.setScrollY((int) (canvasManager.getScrollY()
						- (FileUpdaterThread.getBiggestY() * (displacement / rightScrollBar.getHeight()))));

				lastY = e.getY();
			}
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

	/**
	 * Opens this project's GitHub page
	 */
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
	 * Saves the file that is being drawn
	 */
	public void saveOpenedFile() {
		if (canvasManager.getCurrentFile() != null) {
			PEUtils.saveFile(canvasManager.getCurrentFile(), canvasManager.getCurrentFile().getDiskFile());

			// Updates '*' before the tab's label
			for (Node node : filesTab.getChildren()) {
				Tab tab = (Tab) node;
				if (tab.getUserData() == canvasManager.getCurrentFile()) {
					tab.setModified(false);
				}
			}
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
		if (file != null) {
			fileList.getItems().remove(file);

			if (file.isLoaded()) {
				closeFile(file);
			}
		}
	}

	/**
	 * Loads this file in the canvas and updates the selected file tab
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
			createFileTabLabel(file);
		}

		canvasManager.setCurrentFile(file);

		// Calculates information about the new file
		FileUpdaterThread.calculate(file);

		if (!RenderThread.isRunning()) {
			canvas.setCursor(Cursor.TEXT);
			canvasManager.initRenderThread();
		}

		updateFilesTabSelection(file);

	}

	/**
	 * Adds a CSS class to the select file tab to show it's select
	 * 
	 * @param file
	 */
	private void updateFilesTabSelection(File file) {
		for (Node node : filesTab.getChildren()) {
			Tab tab = (Tab) node;
			if (tab.getUserData() != file) {
				tab.setSelected(false);
			} else {
				tab.setSelected(true);
			}

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

		for (Node node : filesTab.getChildren()) {
			closeFile((File) node.getUserData());
		}

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
	 * Creates and adds a new tab in the {@link #filesTab}
	 * 
	 * @param file
	 */
	private void createFileTabLabel(File file) {
		Tab tab = new Tab(file, this);
		filesTab.getChildren().add(tab);
	}

	/**
	 * Removes the <code>file</code> from the {@link #filesTab} and selects the
	 * first one if there's one
	 * 
	 * @param file
	 */
	public void closeFile(File file) {
		file.setLoaded(false);

		if (file.wasModified()) {
			// TODO ask to save file
		}

		Iterator<Node> it = filesTab.getChildren().iterator();

		while (it.hasNext()) {
			Node node = it.next();
			if (node.getUserData() == file) {
				it.remove();
				break;
			}
		}

		// Selects the first tab
		if (filesTab.getChildren().size() > 0) {
			Node node = filesTab.getChildren().get(0);
			loadFileInCanvas((File) node.getUserData());
		} else {
			hideSrollBar();
			stopRendering();
		}

	}

	public void refrestFileList() {
		fileList.refresh();
	}

	private void hideSrollBar() {
		updateScrollY(0); // 0 hides it
	}

	public void updateScrollBar(float x, float y) {
		updateScrollX(x);
		updateScrollY(y);
	}

	private void updateScrollX(float x) {
		// TODO scroll X
	}

	private void updateScrollY(float y) {
		double heightProportion = rightScrollBar.getHeight() / y;

		if (heightProportion <= 1) {
			rightScrollInside.setVisible(true);
			rightScrollInside.setHeight(rightScrollBar.getHeight() * heightProportion);
		} else { // Hides it
			rightScrollInside.setVisible(false);
		}

		// Moves it to the correct position
		rightScrollInside.setLayoutY((canvasManager.getScrollY() / y) * rightScrollBar.getHeight());

	}

	private void stopRendering() {
		canvas.setCursor(Cursor.DEFAULT);
		canvasManager.stopRenderThread();
	}

	/**
	 * @return <code>true</code> if at least 1 file is selected
	 */
	public boolean isFileSelected() {
		return fileList.getSelectionModel().getSelectedIndices().isEmpty() ? false : true;
	}

	/**
	 * Adds a <code>*</code> to the file name to indicate it's been modified
	 * 
	 * @param file
	 */
	public void setFileModified(File file) {
		for (Node node : filesTab.getChildren()) {
			Tab tab = (Tab) node;
			if (tab.getUserData() == file) {
				tab.setModified(true);
			}
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

		// 23 is right margin
		// 238 is file list on the left
		canvasBox.setPrefWidth(width - 238 - 23);

		canvas.setWidth(canvasBox.getPrefWidth());

		rightScrollBar.setLayoutX(width - 23);

	}

	@Override
	public void onHeightResize(int height) {

		canvasBox.setPrefHeight(height - secundaryMenu.getHeight() - menuBar.getHeight());

		// 25 = canvasInformationBar Height
		canvas.setHeight(canvasBox.getPrefHeight() - 25 - filesTab.getHeight());
		rightScrollBar.setPrefHeight(canvas.getHeight());
	}

}
