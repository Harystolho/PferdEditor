package com.harystolho;

import com.harystolho.canvas.eventHandler.CMKeyEventHandler;
import com.harystolho.canvas.eventHandler.CMMouseEventHandler;
import com.harystolho.controllers.MainController;
import com.harystolho.utils.PEUtils;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class PEApplication extends Application {

	private Stage window;

	private Scene scene;

	private MainController mainController;

	private CMKeyEventHandler keyHandler;

	@Override
	public void start(Stage window) throws Exception {
		Main.setApplication(this);

		this.window = window;

		window.setTitle("Pferd Editor");

		loadMainScene();

		loadEventHandlers();

		window.show();
	}

	private void loadEventHandlers() {
		window.setOnCloseRequest((e) -> {
			PEUtils.exit();
		});

		keyHandler = new CMKeyEventHandler(scene, mainController.getCanvasManager());

	}

	private void loadMainScene() {

		scene = new Scene(PEUtils.loadFXML("main.fxml"));

		scene.getStylesheets().add(ClassLoader.getSystemResource("style.css").toExternalForm());

		PEUtils.addResizeHandler(scene, mainController);

		window.setScene(scene);
	}

	public MainController getMainController() {
		return mainController;
	}

	public void setMainController(MainController mainController) {
		this.mainController = mainController;
	}

	public static void init(String[] args) {
		launch(args);
	}

}
