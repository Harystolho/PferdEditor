package com.harystolho.utils;

import java.util.logging.Logger;

import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Class used to hide the {@link #mainScene} and show another scene.
 * 
 * @author Harystolho
 *
 */
public class SceneReverser {

	private static final Logger logger = Logger.getLogger(SceneReverser.class.getName());

	private static Stage window;

	private static Scene mainScene;

	/**
	 * Hides the {@link #mainScene} and shows the <code>newScene</code> when this
	 * method is first called. When this method is called again, it will hide the
	 * window that is being shown and display the {@link #mainScene} again
	 * 
	 * @param newScene
	 */
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
