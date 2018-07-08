package com.harystolho;

import com.harystolho.controllers.MainController;
import com.harystolho.utils.PEUtils;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class PEApplication extends Application {

	private Stage window;

	private MainController mainController;

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

	}

	private void loadMainScene() {

		Scene scene = new Scene(PEUtils.loadFXML("main.fxml"));

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
