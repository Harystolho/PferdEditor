package com.harystolho.canvas.eventHandler;

import com.harystolho.canvas.CanvasManager;

import javafx.scene.input.KeyEvent;

public class CMKeyEventHandler {

	private CanvasManager cm;

	public CMKeyEventHandler(CanvasManager cm) {
		this.cm = cm;

		init();

	}

	private void init() {

		cm.getCanvas().setOnKeyPressed((e) -> {
			keyPress(e);
		});

		cm.getCanvas().setOnKeyReleased((e) -> {
			keyRelease(e);
		});

	}

	private void keyRelease(KeyEvent e) {
		
	}

	private void keyPress(KeyEvent e) {

	}

}
