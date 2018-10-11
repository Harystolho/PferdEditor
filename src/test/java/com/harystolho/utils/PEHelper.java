package com.harystolho.utils;

import com.harystolho.canvas.CanvasManager;
import com.harystolho.pe.File;

import javafx.embed.swing.JFXPanel;
import javafx.scene.canvas.Canvas;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

public class PEHelper {

	public static void init() {
		new JFXPanel();
		PEUtils.start();
		CanvasManager.setCanvas(new Canvas(500, 200));
	}

	public void typeStringToFile(File file, String string) {
		for (char c : string.toCharArray()) {
			file.type(new KeyEvent(null, null, null, null, String.valueOf(c), KeyCode.UNDEFINED, false, false, false,
					false));
			CanvasManager.getInstance().draw();
		}
	}

	public void typeSpace(File file) {
		file.type(new KeyEvent(null, null, null, null, null, KeyCode.SPACE, false, false, false, false));
		CanvasManager.getInstance().draw();
	}

	public void typeEnter(File file) {
		file.type(new KeyEvent(null, null, null, null, null, KeyCode.ENTER, false, false, false, false));
		CanvasManager.getInstance().draw();
	}

	public void typeDelete(File file) {
		file.type(new KeyEvent(null, null, null, null, null, KeyCode.BACK_SPACE, false, false, false, false));
		CanvasManager.getInstance().draw();
	}

}
