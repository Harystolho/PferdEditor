package com.harystolho;

import com.harystolho.canvas.CanvasManager;
import com.harystolho.canvas.eventHandler.PEKeyEventHandler;
import com.harystolho.canvas.eventHandler.PEMouseEventHandler;
import com.harystolho.controllers.MainController;
import com.harystolho.utils.PEUtils;
import com.harystolho.utils.PropertiesWindowFactory;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class PEApplication extends Application {

	private Stage window;

	private Scene scene;

	private MainController mainController;

	private PEKeyEventHandler keyHandler;
	private PEMouseEventHandler mouseHandler;

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
		
		keyHandler = new PEKeyEventHandler(scene, mainController.getCanvasManager());
		mouseHandler = new PEMouseEventHandler(scene);
	}
	
	private void loadEventHandlers() {
		window.setOnCloseRequest((e) -> {
			PEUtils.exit();
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
