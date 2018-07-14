package com.harystolho.canvas.eventHandler;

import com.harystolho.canvas.CanvasManager;

import javafx.scene.input.MouseEvent;

public class CMMouseEventHandler {

	private CanvasManager cm;

	public CMMouseEventHandler(CanvasManager cm) {
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

	}

	private void mouseRelease(MouseEvent e) {
		cm.getCanvas().requestFocus();

	}

	private void mousePressed(MouseEvent e) {
		cm.getCanvas().requestFocus();

		cm.setCursorY(e.getY()); // setCursorY MUST come first
		cm.setCursorX(e.getX());

		cm.setCursorCount(CanvasManager.CURSOR_DELAY);

	}

}
