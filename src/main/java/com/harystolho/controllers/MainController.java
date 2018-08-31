package com.harystolho.controllers;

import java.awt.Desktop;
import java.awt.Desktop.Action;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ListIterator;
import java.util.regex.Pattern;

import javax.xml.stream.XMLInputFactory;

import com.harystolho.Main;
import com.harystolho.canvas.CanvasManager;
import com.harystolho.misc.OpenWindow;
import com.harystolho.misc.PropertiesWindowFactory;
import com.harystolho.misc.PropertiesWindowFactory.window_type;
import com.harystolho.misc.Tab;
import com.harystolho.pe.File;
import com.harystolho.thread.FileUpdaterThread;
import com.harystolho.thread.RenderThread;
import com.harystolho.utils.PEUtils;

import javafx.fxml.FXML;
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

public class MainController implements ResizableInterface {

	@FXML
	private Pane pane;
	@FXML
	private VBox leftPane;

	@FXML
	private MenuBar menuBar;
	@FXML
	private MenuItem menuNewFile;
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
	private ImageView pilcrow;
	@FXML
	private ImageView browser;
	@FXML
	private ImageView donate;
	@FXML
	private ImageView addNewIcon;

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

	boolean showWhiteSpaces = false;

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
			loadSaveDirectory();
		});

		pilcrow.setOnMouseClicked((e) -> {
			canvasManager.toggleShowWhiteSpaces();
		});

		browser.setOnMouseClicked((e) -> {
			// TODO browser button
		});

		donate.setOnMouseClicked((e) -> {
			// TODO donate button
		});

		addNewIcon.setOnMouseClicked((e) -> {
			// TODO custom buttons
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

		menuNewFile.setOnAction((e) -> {
			createNewFile();
		});

		menuSave.setOnAction((e) -> {
			PEUtils.saveFiles(fileList.getItems());
		});

		menuSaveAs.setOnAction((e) -> {
			if (canvasManager.getCurrentFile() != null)
				PEUtils.saveFileAs(canvasManager.getCurrentFile());
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
			OpenWindow ow = new OpenWindow("Settings");
			ow.load("settings.fxml", (c) -> {
				SettingsController controller = (SettingsController) c;
				controller.setStage(ow.getStage());
			});
			ow.openWindow();
		});

		menuCheckForUpdates.setOnAction((e) -> {

		});

		menuAbout.setOnAction((e) -> {
			openAboutPage();
		});

	}

	/**
	 * Saves the file that is being drawn
	 */
	public void saveOpenedFile() {
		if (canvasManager.getCurrentFile() != null) {
			PEUtils.saveFile(canvasManager.getCurrentFile(), canvasManager.getCurrentFile().getDiskFile());

			setFileTabModified(canvasManager.getCurrentFile());
		}
	}

	private void createNewFile() {
		OpenWindow ow = new OpenWindow("New File");

		ow.load("newFile.fxml", (controller) -> {
			NewFileController c = (NewFileController) controller;
			c.setStage(ow.getStage());
		});

		ow.openWindow();
	}

	/**
	 * Creates a new {@link File} and adds it to {@link #fileList}
	 */
	public void createNewFile(String fileName) {
		File file = new File(fileName);
		addFileToList(file);
	}

	public void addFileToList(File file) {
		fileList.getItems().add(file);
	}

	private void deleteFile(File file) {
		if (file != null) {
			fileList.getItems().remove(file);

			if (file.isLoaded()) {
				closeFile(file);
			}

			file.getDiskFile().delete();
		}
	}

	/**
	 * Loads the <code>file</code> in the canvas and updates the selected file tab
	 */
	public void loadFileInCanvas(File file) {
		if (file == null) {
			return;
		}

		if (!file.isLoaded()) {
			PEUtils.loadFileFromDisk(file);
			createFileTabLabel(file);
		}

		canvasManager.setCurrentFile(file);

		FileUpdaterThread.calculate();

		if (!RenderThread.isRunning()) {
			canvas.setCursor(Cursor.TEXT);
			canvasManager.initRenderThread();
		}

		updateFileTabSelection(file);

	}

	/**
	 * Adds a CSS class to the select file tab to show it's select
	 * 
	 * @param file
	 */
	private void updateFileTabSelection(File file) {
		for (Node node : filesTab.getChildren()) {
			Tab tab = (Tab) node;
			if (tab.getUserData() != file) {
				tab.setSelected(false);
			} else {
				tab.setSelected(true);
			}

		}
	}

	/**
	 * Updates the '*' before the tab's file name
	 */
	private void setFileTabModified(File currentFile) {
		for (Node node : filesTab.getChildren()) {
			Tab tab = (Tab) node;
			if (tab.getUserData() == canvasManager.getCurrentFile()) {
				tab.setModified(false);
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
	 * Opens a new window to select the new save directory
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

	/**
	 * Clears the file list and loads the files from the save directory
	 */
	private void loadSaveDirectory() {
		fileList.getItems().clear();

		// Closes the opened tabs
		ListIterator<Node> it = filesTab.getChildren().listIterator();
		while (it.hasNext()) {
			Node node = it.next();
			closeFile((File) node.getUserData(), it);
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
					addFileToList(PEUtils.createFileFromSourceFile(file));
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
		Tab tab = new Tab(file);
		filesTab.getChildren().add(tab);
	}

	/**
	 * Removes the <code>file</code> from the {@link #filesTab} and selects the
	 * first tab if there's one
	 * 
	 * @param file
	 * @param node
	 */
	public void closeFile(File file) {
		ListIterator<Node> it = filesTab.getChildren().listIterator();
		while (it.hasNext()) {
			Node node = it.next();
			if (node.getUserData() == file) {
				closeFile(file, it);
				break;
			}
		}
	}

	private void closeFile(File file, ListIterator<Node> it) {
		if (file.wasModified()) {
			openSaveChangesWidow(file);
		} else {
			it.remove();
			canvasManager.resetPivotNode();

			selectFirstTabOnFileTab();

			file.unload();
		}
	}

	public void selectFirstTabOnFileTab() {
		if (filesTab.getChildren().size() > 0) {
			Node node = filesTab.getChildren().get(0);
			loadFileInCanvas((File) node.getUserData());
		} else { // If there's no tab left
			hideSrollBar();
			stopRendering();
		}
	}

	/**
	 * Removes this <code>file</code>'s tab from {@link #filesTab}
	 * 
	 * @param file
	 */
	public void removeFileFromFileTab(File file) {
		ListIterator<Node> it = filesTab.getChildren().listIterator();
		while (it.hasNext()) {
			Node node = it.next();
			if (node.getUserData() == file) {
				filesTab.getChildren().remove(node);
				break;
			}
		}
	}

	private void openSaveChangesWidow(File file) {
		OpenWindow ow = new OpenWindow("Save Changes");

		ow.load("saveChanges.fxml", (controller) -> {
			SaveChangesController c = (SaveChangesController) controller;
			c.setStage(ow.getStage());
			c.setPEFile(file);
		});

		ow.openWindow();
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
				break;
			}
		}
	}

	/**
	 * If a file is renamed it has to update its name in the File Tab too, this
	 * method does that
	 * 
	 * @param file
	 */
	public void updateFileNameOnFileTab(File file) {
		for (Node node : filesTab.getChildren()) {
			Tab tab = (Tab) node;
			if (tab.getUserData() == file) {
				tab.setTabName(file.getName());
				break;
			}
		}
	}

	public Canvas getCanvas() {
		return canvas;
	}

	public CanvasManager getCanvasManager() {
		return canvasManager;
	}

	public boolean showWhiteSpaces() {
		return showWhiteSpaces;
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
		canvas.setHeight(canvasBox.getPrefHeight() - 20 - filesTab.getHeight());
		rightScrollBar.setPrefHeight(canvas.getHeight());

		// Scrolls the canvas up when the window resized to a bigger size
		if (canvasManager.getScrollY() >= FileUpdaterThread.getBiggestY() - canvas.getHeight()) {
			if (canvasManager.getCurrentFile() != null) {
				canvasManager.getCurrentFile().setScrollY((int) (FileUpdaterThread.getBiggestY() - canvas.getHeight()));
				canvasManager.updatePivotNode();
			}
		}
	}

}
