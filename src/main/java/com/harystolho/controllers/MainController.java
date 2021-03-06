package com.harystolho.controllers;

import java.awt.Desktop;
import java.awt.Desktop.Action;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import com.harystolho.PEApplication;
import com.harystolho.canvas.CanvasManager;
import com.harystolho.canvas.tab.FileTabManager;
import com.harystolho.canvas.tab.Tab;
import com.harystolho.misc.OpenWindow;
import com.harystolho.misc.PropertiesWindowFactory;
import com.harystolho.misc.PropertiesWindowFactory.window_type;
import com.harystolho.misc.ResizableInterface;
import com.harystolho.misc.explorer.ExplorerFile;
import com.harystolho.misc.explorer.ExplorerFolder;
import com.harystolho.misc.explorer.FileExplorer;
import com.harystolho.pe.File;
import com.harystolho.thread.FileUpdaterThread;
import com.harystolho.thread.RenderThread;
import com.harystolho.utils.PEUtils;

import javafx.fxml.FXML;
import javafx.scene.Cursor;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;

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
	private FileTabManager filesTab;
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

		loadWorkspaceDirectory();
	}

	private void loadEventHandlers() {

		loadMenuBarItemHandler();

		newFile.setOnMouseClicked((e) -> {
			createNewFile(null);
		});

		saveFile.setOnMouseClicked((e) -> {
			saveOpenedFile();
		});

		refresh.setOnMouseClicked((e) -> {
			loadWorkspaceDirectory();
		});

		pilcrow.setOnMouseClicked((e) -> {
			CanvasManager.getInstance().toggleShowWhiteSpaces();
		});

		browser.setOnMouseClicked((e) -> {
			// TODO IMPL browser button
		});

		donate.setOnMouseClicked((e) -> {
			// TODO IMPL donate button
		});

		addNewIcon.setOnMouseClicked((e) -> {
			// TODO IMPL custom buttons
		});

		rightScrollBar.setOnMousePressed((e) -> {
			lastY = e.getY();
		});

		rightScrollBar.setOnMouseDragged((mouse) -> {
			// If cursor is inside scroll bar
			if (mouse.getY() >= rightScrollInside.getLayoutY()
					&& mouse.getY() <= rightScrollInside.getLayoutY() + rightScrollInside.getHeight()) {
				double displacement = lastY - mouse.getY();

				CanvasManager.getInstance().setScrollY(CanvasManager.getInstance().getScrollY()
						- (FileUpdaterThread.getBiggestY() * (float) (displacement / rightScrollBar.getHeight())));

				lastY = mouse.getY();
			}
		});

		bottomScrollBar.setOnMousePressed((e) -> {
			lastX = e.getX();
		});

		bottomScrollBar.setOnMouseDragged((mouse) -> {
			// If cursor is inside scroll bar
			if (mouse.getX() >= bottomScrollInside.getLayoutX()
					&& mouse.getX() <= bottomScrollInside.getLayoutX() + bottomScrollInside.getWidth()) {
				double displacement = lastX - mouse.getX();

				CanvasManager.getInstance().setScrollX((int) (CanvasManager.getInstance().getScrollX()
						- (FileUpdaterThread.getBiggestX() * (displacement / bottomScrollBar.getWidth()))));

				lastX = mouse.getX();
			}
		});

	}

	/**
	 * Sets the event handler for items on the menu
	 */
	private void loadMenuBarItemHandler() {

		menuNewFile.setOnAction((e) -> {
			createNewFile(null);
		});

		menuSave.setOnAction((e) -> {
			saveOpenedFile();
		});

		menuSaveAs.setOnAction((e) -> {
			PEUtils.saveFileAs(CanvasManager.getInstance().getCurrentFile());
			removeFileTabModified(CanvasManager.getInstance().getCurrentFile());
		});

		menuChangeWorkspace.setOnAction((e) -> {
			PEUtils.showWorkspaceLoader();
		});

		menuExit.setOnAction((e) -> {
			PEApplication.getInstance().getWindow().close();
			PEUtils.exit();
		});

		menuSearch.setOnAction((e) -> {
			// TODO IMPL search
		});

		menuReplace.setOnAction((e) -> {
			// TODO IMPL replace
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
			// TODO IMPL check for updates
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
		File file = CanvasManager.getInstance().getCurrentFile();
		if (file != null) {
			PEUtils.saveFile(file, file.getDiskFile());

			removeFileTabModified(file);
		}
	}

	/**
	 * 
	 * @param folder <code>null</code> sets the initial folder to the workspace
	 *               folder
	 */
	public static void createNewFile(java.io.File folder) {
		OpenWindow ow = new OpenWindow("New File");
		ow.load("newFile.fxml", (controller) -> {
			NewFileController c = (NewFileController) controller;
			c.setStage(ow.getStage());
			c.setInitialFolder(folder);
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

		ExplorerFile eFile = new ExplorerFile(fileName);
		eFile.setFile(file);

		fileExplorer.add(eFile);
	}

	public void deleteFile(File file) {
		if (file != null) {
			fileExplorer.removeFile(file);

			if (file.isLoaded()) {
				filesTab.closeFile(file);
			}

			file.getDiskFile().delete();
		}
	}

	public void deleteFolder(java.io.File folder) {
		if (folder != null) {
			fileExplorer.removeFolder(folder);
		}
	}

	/**
	 * Loads the <code>file</code> in the canvas and updates the selected file tab
	 */
	public void loadFileInCanvas(File file) {
		if (file != null) {
			if (!file.isLoaded()) {
				PEUtils.loadFileFromDisk(file);
				filesTab.addTab(file);
			}

			CanvasManager.getInstance().setCurrentFile(file);

			FileUpdaterThread.calculate();

			if (RenderThread.instance == null) {
				canvas.setCursor(Cursor.TEXT);
				CanvasManager.getInstance().initRenderThread();
			}

			updateFileTabSelection(file);
		}
	}

	/**
	 * Opens a file properties window (this is the window that opens when you press
	 * right click) at the cursor's position
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
	 * Updates the workspace directory
	 * 
	 * @param dir
	 */
	public void updateWorkspaceDirectory(java.io.File dir) {
		if (dir != null && dir.isDirectory()) {
			PEUtils.setWorkspaceFolder(dir); // Updates the save folder
			loadWorkspaceDirectory(); // Loads the new directory
		}
	}

	/**
	 * Clears the {@link #fileExplorer} and loads the files from the workspace
	 * directory
	 */
	private void loadWorkspaceDirectory() {
		fileExplorer.setContent(null);

		filesTab.closeTabs();

		loadFileNames();
	}

	public FileTabManager getFileTabManager() {
		return filesTab;
	}

	/**
	 * Selects the tab and loads the file at the index
	 * 
	 * @param idx
	 */
	public void selectTab(int idx) {
		if (idx > filesTab.getTabs().size() - 1) {
			return;
		}

		Tab tab = filesTab.getTabs().get(idx);

		if (tab != null) {
			loadFileInCanvas(tab.getFile());
		}
	}

	/**
	 * Changes the tab's color to indicate it's selected
	 * 
	 * @param file
	 */
	private void updateFileTabSelection(File file) {
		filesTab.select(file);
	}

	/**
	 * Removes the '*' before the tab's file name
	 */
	private void removeFileTabModified(File file) {
		if (file != null) {
			filesTab.removeModified(file);
		}
	}

	/**
	 * Loads the files name from the workspace directory
	 */
	private void loadFileNames() {
		java.io.File workspace = PEUtils.getWorkspaceFolder();
		if (workspace.exists()) {
			ExplorerFolder root = new ExplorerFolder(workspace.getAbsoluteFile());
			for (java.io.File file : workspace.listFiles()) {
				PEUtils.createFileFromSourceFile(root, file);
			}
			fileExplorer.add(root);
		}
	}

	public void selectFirstTabOnFileTab() {
		if (!filesTab.isEmpty()) {
			Tab tab = filesTab.getTabs().get(0);
			loadFileInCanvas(tab.getFile());
		} else { // If there're no tabs left
			hideSrollBar();
			stopRendering();
		}
	}

	public void openSaveChangesWindow(File file) {
		OpenWindow ow = new OpenWindow("Save Changes");

		ow.load("saveChanges.fxml", (controller) -> {
			SaveChangesController c = (SaveChangesController) controller;
			c.setStage(ow.getStage());
			c.setPEFile(file);
		});

		ow.openWindow();
	}

	/**
	 * If the file name or some visible information about the file is modified, this
	 * method must be called to update the information in the file explorer
	 * 
	 * @param file
	 */
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

		if (widthProportion <= 1) {
			bottomScrollInside.setVisible(true);
			bottomScrollInside.setWidth(bottomScrollBar.getWidth() * widthProportion);

			// Updates the scroll position
			CanvasManager.getInstance().updateHorizontalScroll();
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
