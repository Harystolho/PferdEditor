package com.harystolho.canvas.eventHandler;

import com.harystolho.canvas.CanvasManager;

import javafx.scene.Scene;
import javafx.scene.input.KeyEvent;

public class PEKeyEventHandler {

	private CanvasManager cm;
	private Scene scene;

	public PEKeyEventHandler(Scene scene, CanvasManager cm) {
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
		case UP:
			cm.lineUp();
			e.consume();
			return;
		case DOWN:
			cm.lineDown();
			e.consume();
			return;
		case LEFT:
			cm.moveCursorLeft();
			e.consume();
			return;
		case RIGHT:
			cm.moveCursorRight();
			e.consume();
			return;
		case HOME:
			cm.moveCursorToStartOfTheLine();
			return;
		case END:
			cm.moveCursorToEndOfTheLine();
			return;
		case F3:
			cm.printDebugMessage();
			return;
		case F4:
			cm.getCurrentFile().getWords().printDebug();
			return;
		default:
			break;
		}

		if (cm.getCanvas().isFocused()) {
			if (cm.getCurrentFile() != null) {
				e.consume();
				cm.getCurrentFile().type(e);
			}
		}

	}

}
