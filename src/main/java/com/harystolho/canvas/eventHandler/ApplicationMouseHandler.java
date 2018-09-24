package com.harystolho.canvas.eventHandler;

import com.harystolho.misc.PropertiesWindowFactory;

import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;

/**
 * Mouse event handler for this application.
 * 
 * @author Harystolho
 *
 */
public class ApplicationMouseHandler {

	private Scene scene;

	public ApplicationMouseHandler(Scene scene) {
		this.scene = scene;
		init();
	}

	private void init() {

		scene.setOnMousePressed((e) -> {
			mousePressed(e);
		});

		scene.setOnMouseReleased((e) -> {
			mouseRelease(e);
		});

		scene.setOnScroll((e) -> {
			scrollMoved(e);
		});

	}

	private void mouseRelease(MouseEvent e) {

	}

	private void mousePressed(MouseEvent e) {
		PropertiesWindowFactory.removeOpenWindow(e.getSceneX(), e.getSceneY());
	}

	private void scrollMoved(ScrollEvent e) {

	}

}
