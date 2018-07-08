package com.harystolho.utils;

import java.io.IOException;
import java.util.logging.Logger;

import com.harystolho.controllers.ResizableInterface;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;

public class PEUtils {

	private static final Logger logger = Logger.getLogger(PEUtils.class.getName());

	public static final String VERSION = "0.1";

	/**
	 * Loads a FXML File.
	 * 
	 * @param file
	 *            full file name. (Eg: main.fxml)
	 * @return the <code>Parent</code> object containing the file, or
	 *         <code>null</code> if it can't find the file.
	 */
	public static Parent loadFXML(String file) {

		try {
			return FXMLLoader.load(ClassLoader.getSystemResource(file));
		} catch (IOException | NullPointerException e) {
			logger.severe("Couldn't load file=" + file);
			return null;
		}

	}

	/**
	 * Handler for window resize.
	 * 
	 * @param scene
	 * @param resize
	 *            a JavaFX Controller that implements
	 *            {@link #com.harystolho.controllers.ResizableInterface
	 *            ResizableInterface }
	 */
	public static void addResizeHandler(Scene scene, ResizableInterface resize) {

		scene.widthProperty().addListener((obv, oldValue, newValue) -> {
			resize.onWidthResize();
		});

		scene.heightProperty().addListener((obv, oldValue, newValue) -> {
			resize.onHeightResize();
		});

	}

	/**
	 * Initializes resources for this application.
	 */
	public static void start() {
		logger.info("Initializing application.");

		PEConfiguration.loadProperties();

	}

	public static void exit() {
		logger.info("Closing application.");
	}

}
