package com.harystolho.canvas.eventHandler;

import com.harystolho.canvas.CanvasManager;

import javafx.scene.Scene;
import javafx.scene.input.KeyEvent;

public class CMKeyEventHandler {

	private CanvasManager cm;
	private Scene scene;

	public CMKeyEventHandler(Scene scene, CanvasManager cm) {
		this.cm = cm;
		this.scene = scene;

		init();

	}

	private void init() {

		scene.setOnKeyPressed((e) -> {
			keyPress(e);
		});

		scene.setOnKeyReleased((e) -> {
			keyRelease(e);
		});

	}

	private void keyRelease(KeyEvent e) {

	}

	private void keyPress(KeyEvent e) {

		switch (e.getCode()) {
		case NUMPAD8:
			cm.lineUp();
			return;
		case NUMPAD2:
			cm.lineDown();
			return;
		default:
			break;
		}

		if (cm.getCanvas().isFocused()) {
			if (cm.getCurrentFile() != null) {
				System.out.println(e.getText());
				cm.getCurrentFile().type(e.getText());
			}
		}

	}

}
