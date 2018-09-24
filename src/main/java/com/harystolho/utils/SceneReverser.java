package com.harystolho.utils;

import java.util.logging.Logger;

import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Hides the main scene and shows another scene
 * 
 * @author Harystolho
 *
 */
public class SceneReverser {

	private static final Logger logger = Logger.getLogger(SceneReverser.class.getName());

	private static Stage window;

	private static Scene mainScene;

	public static void reverse(Scene newScene) {
		if (window == null) {
			logger.severe("Window is null");
		} else {
			if (mainScene == null) {
				mainScene = window.getScene();
				window.setScene(newScene);
			} else {
				window.setScene(mainScene);
				mainScene = null;
			}
		}
	}

	public static void setWindow(Stage window) {
		SceneReverser.window = window;
	}

}
