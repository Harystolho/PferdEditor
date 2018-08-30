package com.harystolho;

import com.harystolho.canvas.CanvasManager;
import com.harystolho.canvas.eventHandler.ApplicationKeyHandler;
import com.harystolho.canvas.eventHandler.ApplicationMouseHandler;
import com.harystolho.controllers.MainController;
import com.harystolho.misc.PropertiesWindowFactory;
import com.harystolho.utils.PEUtils;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;

public class PEApplication extends Application {

	private Stage window;

	private Scene scene;

	private MainController mainController;

	private ApplicationKeyHandler keyHandler;
	private ApplicationMouseHandler mouseHandler;

	@Override
	public void start(Stage window) throws Exception {
		setup();

		setWindow(window);
		window.getIcons().add(new Image(ClassLoader.getSystemResourceAsStream("icon.png")));
		window.setTitle("Pferd Editor");

		window.setScene(scene);

		PropertiesWindowFactory.setMainPane(Main.getApplication().getWindow().getScene().getRoot());

		loadEventHandlers();

		window.show();
	}

	public void setup() {
		Main.setApplication(this);
		loadMainScene();

		keyHandler = new ApplicationKeyHandler(scene, mainController.getCanvasManager());
		mouseHandler = new ApplicationMouseHandler(scene);
	}

	private void loadEventHandlers() {
		window.setOnCloseRequest((e) -> {
			PEUtils.exit();
		});

		// When ALT is pressed it shouldn't focus on the menu bar
		scene.addEventHandler(KeyEvent.KEY_PRESSED, (e) -> {
			if (e.getCode() == KeyCode.ALT) {
				e.consume();
			}
		});

	}

	private void loadMainScene() {
		scene = new Scene(PEUtils.loadFXML("main.fxml", (c) -> {
			mainController = (MainController) c;
		}));

		scene.getStylesheets().add(ClassLoader.getSystemResource("style.css").toExternalForm());

		PEUtils.addResizeHandler(scene, mainController);
	}

	public MainController getMainController() {
		return mainController;
	}

	public CanvasManager getCanvasManager() {
		return mainController.getCanvasManager();
	}

	public static void init(String[] args) {
		launch(args);
	}

	private void setWindow(Stage window) {
		this.window = window;
	}

	public Stage getWindow() {
		return window;
	}

}
