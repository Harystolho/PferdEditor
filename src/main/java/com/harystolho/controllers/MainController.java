package com.harystolho.controllers;

import java.awt.Desktop;
import java.awt.Desktop.Action;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ListIterator;
import java.util.regex.Pattern;

import com.harystolho.PEApplication;
import com.harystolho.canvas.CanvasManager;
import com.harystolho.misc.OpenWindow;
import com.harystolho.misc.PropertiesWindowFactory;
import com.harystolho.misc.PropertiesWindowFactory.window_type;
import com.harystolho.misc.ResizableInterface;
import com.harystolho.misc.Tab;
import com.harystolho.misc.explorer.ExplorerFile;
import com.harystolho.misc.explorer.ExplorerFolder;
import com.harystolho.misc.explorer.FileExplorer;
import com.harystolho.pe.File;
import com.harystolho.thread.FileUpdaterThread;
import com.harystolho.thread.RenderThread;
import com.harystolho.utils.PEUtils;

import javafx.fxml.FXML;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import javafx.stage.StageStyle;

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
	private MenuItem menuChangeWorkspace;
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
	private HBox bottomBox;

	@FXML
	private HBox filesTab;
	@FXML
	private VBox canvasBox;
	@FXML
	private Canvas canvas;
	@FXML
	private Pane canvasInformationBar;
	@FXML
	private Pane rightScrollBar;
	private double lastY = 0;

	@FXML
	private Pane bottomScrollBar;
	private double lastX = 0;

	@FXML
	private Rectangle rightScrollInside;
	@FXML
	private Rectangle bottomScrollInside;

	boolean showWhiteSpaces = false;

	private FileExplorer fileExplorer;

	@FXML
	void initialize() {
		loadEventHandlers();

		CanvasManager.setCanvas(canvas);

		addFileExplorer();

		loadSaveDirectory();
		setCurrentDirectoryLabel(PEUtils.getSaveFolder());
	}

	private void loadEventHandlers() {

		loadMenuBarItemHandler();

		newFile.setOnMouseClicked((e) -> {
			createNewFile();
		});

		saveFile.setOnMouseClicked((e) -> {
			PEUtils.saveFiles(fileExplorer.getFiles());
		});

		refresh.setOnMouseClicked((e) -> {
			loadSaveDirectory();
		});

		pilcrow.setOnMouseClicked((e) -> {
			CanvasManager.getInstance().toggleShowWhiteSpaces();
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

		rightScrollBar.setOnMousePressed((e) -> {
			lastY = e.getY();
		});

		rightScrollBar.setOnMouseDragged((e) -> {
			// If cursor is inside scroll bar
			if (e.getY() >= rightScrollInside.getLayoutY()
					&& e.getY() <= rightScrollInside.getLayoutY() + rightScrollInside.getHeight()) {
				double displacement = lastY - e.getY();

				CanvasManager.getInstance().setScrollY((int) (CanvasManager.getInstance().getScrollY()
						- (FileUpdaterThread.getBiggestY() * (displacement / rightScrollBar.getHeight()))));

				lastY = e.getY();
			}
		});

		bottomScrollBar.setOnMousePressed((e) -> {
			lastX = e.getX();
		});

		bottomScrollBar.setOnMouseDragged((e) -> {
			// If cursor is inside scroll bar
			if (e.getX() >= bottomScrollInside.getLayoutX()
					&& e.getX() <= bottomScrollInside.getLayoutX() + bottomScrollInside.getWidth()) {
				double displacement = lastX - e.getX();

				CanvasManager.getInstance().setScrollX((int) (CanvasManager.getInstance().getScrollX()
						- (FileUpdaterThread.getBiggestX() * (displacement / bottomScrollBar.getWidth()))));

				lastX = e.getX();
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
			PEUtils.saveFiles(fileExplorer.getFiles());
		});

		menuSaveAs.setOnAction((e) -> {
			if (CanvasManager.getInstance().getCurrentFile() != null)
				PEUtils.saveFileAs(CanvasManager.getInstance().getCurrentFile());
		});

		menuChangeWorkspace.setOnAction((e) -> {
			showWorkspaceLoader();
		});

		menuExit.setOnAction((e) -> {
			PEApplication.getInstance().getWindow().close();
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
	 * The {@link FileExplorer} is used to navigate folders.
	 */
	private void addFileExplorer() {
		fileExplorer = new FileExplorer();

		fileExplorer.setPrefHeight(canvas.getHeight());

		leftPane.getChildren().add(fileExplorer);
	}

	/**
	 * Saves the file that is being drawn
	 */
	public void saveOpenedFile() {
		if (CanvasManager.getInstance().getCurrentFile() != null) {
			PEUtils.saveFile(CanvasManager.getInstance().getCurrentFile(),
					CanvasManager.getInstance().getCurrentFile().getDiskFile());

			setFileTabModified(CanvasManager.getInstance().getCurrentFile());
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
	 * Creates a new {@link File} inside <code>folder</code> and adds it to
	 * {@link #fileList}
	 * 
	 * @param folder
	 * @param fileName
	 */
	public void createNewFile(java.io.File folder, String fileName) {
		File file = new File(fileName);
		file.setDiskFile(new java.io.File(folder + java.io.File.separator + fileName));

		ExplorerFile cFile = new ExplorerFile(fileName);
		cFile.setFile(file);

		fileExplorer.add(cFile);
	}

	public void deleteFile(File file) {
		if (file != null) {
			fileExplorer.remove(file);

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

		CanvasManager.getInstance().setCurrentFile(file);

		FileUpdaterThread.calculate();

		if (RenderThread.instance == null) {
			canvas.setCursor(Cursor.TEXT);
			CanvasManager.getInstance().initRenderThread();
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
			if (tab.getUserData() == CanvasManager.getInstance().getCurrentFile()) {
				tab.setModified(false);
			}
		}
	}

	public void setCurrentDirectoryLabel(java.io.File directory) {
		// Split the full directory name
		String fullDirName[] = directory.getPath().split(Pattern.quote(java.io.File.separator));
		// Get only the last part (Eg: C:/file_to/folder/LAST_PART)
		String lastDirName = fullDirName[fullDirName.length - 1];
	}

	/**
	 * Opens a properties window( this is the window that opens when you press right
	 * click) at the cursor's position
	 * 
	 * @param file
	 * @param x
	 * @param y
	 */
	public void openFileRightClickWindow(File file, double x, double y) {
		PropertiesWindowFactory.open(window_type.FILE, x, y, (controller) -> {
			FileRightClickController fileController = (FileRightClickController) controller;
			fileController.setFile(file);
		});
	}

	/**
	 * Opens a new window to select the new save directory
	 * 
	 * @param dir
	 */
	public void changeDirectory(java.io.File dir) {
		if (dir != null && dir.isDirectory()) {
			PEUtils.setSaveFolder(dir); // Updates the save folder
			setCurrentDirectoryLabel(dir); // Updates the directory label
			loadSaveDirectory(); // Loads the new directory
		}
	}

	private void showWorkspaceLoader() {
		OpenWindow ow = new OpenWindow("Workspace Loader");

		ow.load("workspaceLoader.fxml", (controller) -> {
			WorkspaceLoaderController c = (WorkspaceLoaderController) controller;
			c.setStage(ow.getStage());
		});

		ow.getStage().initStyle(StageStyle.UNDECORATED);

		PEApplication.getInstance().getWindow().hide();
		ow.openWindow();
	}

	/**
	 * Clears the file list and loads the files from the save directory
	 */
	private void loadSaveDirectory() {
		fileExplorer.setContent(null);

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
			ExplorerFolder root = new ExplorerFolder(saveFolder);
			for (java.io.File file : saveFolder.listFiles()) {
				PEUtils.createFileFromSourceFile(root, file);
			}
			fileExplorer.add(root);
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
			openSaveChangesWindow(file);
		} else {
			it.remove();
			CanvasManager.getInstance().resetPivotNode();

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

	private void openSaveChangesWindow(File file) {
		OpenWindow ow = new OpenWindow("Save Changes");

		ow.load("saveChanges.fxml", (controller) -> {
			SaveChangesController c = (SaveChangesController) controller;
			c.setStage(ow.getStage());
			c.setPEFile(file);
		});

		ow.openWindow();
	}

	public void refreshFile(File file) {
		fileExplorer.updateFile(file);
	}

	private void hideSrollBar() {
		updateScrollX(1); // 1 hides it
		updateScrollY(1); // 1 hides it
	}

	public void updateScrollBar(float x, float y) {
		updateScrollX(x);
		updateScrollY(y);
	}

	private void updateScrollX(float x) {
		double widthProportion = bottomScrollBar.getWidth() / x;

		// System.out.println(widthProportion);

		if (widthProportion <= 1) {
			bottomScrollInside.setVisible(true);
			bottomScrollInside.setWidth(bottomScrollBar.getWidth() * widthProportion);
		} else { // Hides it
			bottomScrollInside.setVisible(false);
		}

		// Moves it to the correct position
		bottomScrollInside.setLayoutX((CanvasManager.getInstance().getScrollX() / x) * bottomScrollBar.getWidth());
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
		rightScrollInside.setLayoutY((CanvasManager.getInstance().getScrollY() / y) * rightScrollBar.getHeight());

	}

	private void stopRendering() {
		canvas.setCursor(Cursor.DEFAULT);
		CanvasManager.getInstance().stopRenderThread();
	}

	/**
	 * @return <code>true</code> if at least 1 file is selected
	 */
	public boolean isFileSelected() {
		return true;
		// return fileList.getSelectionModel().getSelectedIndices().isEmpty() ? false :
		// true;
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

	public double getLeftPaneWidth() {
		return leftPane.getWidth();
	}

	@Override
	public void onWidthResize(int width) {
		menuBar.setPrefWidth(width);
		secundaryMenu.setPrefWidth(width);
		bottomBox.setPrefWidth(width);

		leftPane.setPrefWidth(width * 0.17d);

		// 42 is right margin + left margin + some spacing
		canvasBox.setPrefWidth(width - leftPane.getPrefWidth() - 42);

		canvas.setWidth(canvasBox.getPrefWidth());

		rightScrollBar.setLayoutX(width - 23);
	}

	@Override
	public void onHeightResize(int height) {
		bottomBox.setPrefHeight(height - menuBar.getHeight() - secundaryMenu.getHeight());

		canvasBox.setPrefHeight(height - secundaryMenu.getHeight() - menuBar.getHeight());

		// 25 = canvasInformationBar Height
		canvas.setHeight(canvasBox.getPrefHeight() - 20 - filesTab.getHeight() - bottomScrollBar.getHeight());
		rightScrollBar.setPrefHeight(canvas.getHeight());

		// Scrolls the canvas up when the window resized to a bigger size
		if (CanvasManager.getInstance().getScrollY() >= FileUpdaterThread.getBiggestY() - canvas.getHeight()) {
			if (CanvasManager.getInstance().getCurrentFile() != null) {
				CanvasManager.getInstance().getCurrentFile()
						.setScrollY((int) (FileUpdaterThread.getBiggestY() - canvas.getHeight()));
				CanvasManager.getInstance().updatePivotNode();
			}
		}
	}

}
