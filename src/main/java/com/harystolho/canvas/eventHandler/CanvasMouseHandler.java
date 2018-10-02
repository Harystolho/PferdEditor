package com.harystolho.canvas.eventHandler;

import com.harystolho.canvas.CanvasManager;
import com.harystolho.canvas.SelectionManager;
import com.harystolho.controllers.CanvasRightClickController;
import com.harystolho.misc.PropertiesWindowFactory;
import com.harystolho.misc.PropertiesWindowFactory.window_type;

import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;

/**
 * Mouse event handler for the canvas, it only handles the events that happen
 * inside the canvas
 * 
 * @author Harystolho
 *
 */
public class CanvasMouseHandler {

	private CanvasManager cm;

	public CanvasMouseHandler(CanvasManager cm) {
		this.cm = cm;

		init();
	}

	private void init() {

		cm.getCanvas().setOnMousePressed((e) -> {
			mousePressed(e);
		});

		cm.getCanvas().setOnMouseReleased((e) -> {
			mouseRelease(e);
		});

		cm.getCanvas().setOnMouseDragged((e) -> {
			mouseDragged(e);
		});

		cm.getCanvas().setOnScroll((e) -> {
			scrollMoved(e);
		});

	}

	private void mouseRelease(MouseEvent e) {
		e.consume();
	}

	private void mousePressed(MouseEvent e) {
		e.consume();

		PropertiesWindowFactory.removeOpenWindow();

		switch (e.getButton()) {
		case PRIMARY:
			cm.getCanvas().requestFocus();
			cm.setCursorY((float) e.getY()); // setCursorY MUST come first
			cm.setCursorX((float) e.getX());

			cm.setCursorCount(CanvasManager.CURSOR_DELAY);
			break;
		case SECONDARY:
			openCanvasProperties(e);
			break;
		default:
			break;
		}
	}

	private void mouseDragged(MouseEvent e) {
		SelectionManager.getInstance().setLastX(e.getX());
		SelectionManager.getInstance().setLastY(e.getY());
		CanvasManager.getInstance().showSelection(true);
	}

	private void scrollMoved(ScrollEvent e) {
		if (e.getDeltaY() > 0) {
			cm.scrollUp(true);
		} else {
			cm.scrollDown(true);
		}

	}

	/**
	 * Opens a window at the cursor position to cut, copy, save, ... the file
	 * 
	 * @param e
	 */
	private void openCanvasProperties(MouseEvent e) {
		if (CanvasManager.getInstance().getCurrentFile() != null) {
			PropertiesWindowFactory.open(window_type.CANVAS, e.getSceneX(), e.getSceneY(), (controller) -> {
				CanvasRightClickController canvasController = (CanvasRightClickController) controller;
			});
		}
	}
}
