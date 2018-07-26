package com.harystolho.canvas.eventHandler;

import com.harystolho.canvas.CanvasManager;

import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;

public class PEMouseEventHandler {

	private CanvasManager cm;

	public PEMouseEventHandler(CanvasManager cm) {
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

		cm.getCanvas().setOnScroll((e) -> {
			scrollMoved(e);
		});

	}

	private void mouseRelease(MouseEvent e) {
		cm.getCanvas().requestFocus();

	}

	private void mousePressed(MouseEvent e) {
		cm.getCanvas().requestFocus();

		cm.setCursorY((float) e.getY()); // setCursorY MUST come first
		cm.setCursorX((float) e.getX());

		cm.setCursorCount(CanvasManager.CURSOR_DELAY);

	}

	private void scrollMoved(ScrollEvent e) {
		if (e.getDeltaY() > 0) {
			cm.scrollUp();
		} else {
			cm.scrollDown();
		}

	}

}
