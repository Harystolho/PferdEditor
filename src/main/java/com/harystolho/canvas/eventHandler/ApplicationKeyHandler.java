package com.harystolho.canvas.eventHandler;

import java.util.HashMap;
import java.util.function.Consumer;

import com.harystolho.PEApplication;
import com.harystolho.canvas.CanvasManager;
import com.harystolho.controllers.CanvasRightClickController;
import com.harystolho.controllers.FileRightClickController;

import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

/**
 * Key event handler for this application
 * 
 * @author Harystolho
 *
 */
public class ApplicationKeyHandler {

	private CanvasManager cm;
	private Scene scene;

	// Maps a key to a function that will get called when the key is pressed
	private HashMap<KeyCode, Consumer<KeyEvent>> keyMap;

	public ApplicationKeyHandler(Scene scene, CanvasManager cm) {
		this.cm = cm;
		this.scene = scene;

		keyMap = new HashMap<>();

		init();

	}

	private void init() {

		loadKeyMap();

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

		Consumer<KeyEvent> key = keyMap.get(e.getCode());
		if (key != null) { // If there's a function for this key
			key.accept(e);
			return;
		}

		pressKeyOnCanvas(e);

	}

	private void pressKeyOnCanvas(KeyEvent e) {
		if (cm.getCanvas().isFocused()) {
			if (cm.getCurrentFile() != null) {
				cm.getCurrentFile().type(e);
				e.consume();
			}
		}
	}

	/**
	 * Maps KeyCodes to functions that will be called when their KeyCode event is
	 * fired
	 */
	private void loadKeyMap() {

		keyMap.put(KeyCode.UP, (e) -> {
			cm.lineUp();
			e.consume();
		});

		keyMap.put(KeyCode.DOWN, (e) -> {
			cm.lineDown();
			e.consume();
		});

		keyMap.put(KeyCode.LEFT, (e) -> {
			cm.moveCursorLeft();
			e.consume();
		});

		keyMap.put(KeyCode.RIGHT, (e) -> {
			cm.moveCursorRight();
			e.consume();
		});

		keyMap.put(KeyCode.S, (e) -> {
			if (e.isControlDown()) {
				PEApplication.getInstance().getMainController().saveOpenedFile();
			} else {
				pressKeyOnCanvas(e);
			}
		});

		keyMap.put(KeyCode.HOME, (e) -> {
			if (e.isControlDown()) {
				cm.moveCursorToFirstLine();
			} else {
				cm.moveCursorToBeginningOfTheLine();
			}
		});

		keyMap.put(KeyCode.END, (e) -> {
			if (e.isControlDown()) {
				cm.moveCursorToLastLine();
			} else {
				cm.moveCursorToEndOfTheLine();
			}
		});

		keyMap.put(KeyCode.ALT, (e) -> {
			// IGNORE
		});

		keyMap.put(KeyCode.SHIFT, (e) -> {
			// IGNORE
		});

		keyMap.put(KeyCode.CONTROL, (e) -> {
			// IGNORE
		});

		keyMap.put(KeyCode.CAPS, (e) -> {
			// IGNORE
		});

		keyMap.put(KeyCode.F3, (e) -> {
			cm.printDebugMessage();
		});

		keyMap.put(KeyCode.F4, (e) -> {
			if (!e.isAltDown()) {
				cm.getCurrentFile().getWords().printDebug();
			}
		});

		// Normal Keys
		keyMap.put(KeyCode.W, (e) -> {
			if (e.isControlDown()) {
				PEApplication.getInstance().getMainController().getFileTabManager().closeFile(cm.getCurrentFile());
			} else {
				pressKeyOnCanvas(e);
			}
		});

		keyMap.put(KeyCode.C, (e) -> {
			if (e.isControlDown()) {
				FileRightClickController.copyFile(CanvasManager.getInstance().getCurrentFile());
			} else {
				pressKeyOnCanvas(e);
			}
		});

		keyMap.put(KeyCode.V, (e) -> {
			if (e.isControlDown()) {
				CanvasRightClickController.pasteFile(CanvasManager.getInstance().getCurrentFile());
			} else {
				pressKeyOnCanvas(e);
			}
		});

		// Shift
		keyMap.put(KeyCode.OPEN_BRACKET, (e) -> {
			if (e.isShiftDown()) {
				KeyEvent ke = new KeyEvent(null, null, KeyEvent.KEY_PRESSED, " ", "{", KeyCode.BRACELEFT, false, false,
						false, false);
				pressKeyOnCanvas(ke);
			} else {
				pressKeyOnCanvas(e);
			}
		});

		keyMap.put(KeyCode.CLOSE_BRACKET, (e) -> {
			if (e.isShiftDown()) {
				KeyEvent ke = new KeyEvent(null, null, KeyEvent.KEY_PRESSED, " ", "}", KeyCode.BRACERIGHT, false, false,
						false, false);
				pressKeyOnCanvas(ke);
			} else {
				pressKeyOnCanvas(e);
			}
		});

		keyMap.put(KeyCode.BACK_QUOTE, (e) -> {
			if (e.isShiftDown()) {
				KeyEvent ke = new KeyEvent(null, null, KeyEvent.KEY_PRESSED, " ", "~", KeyCode.DEAD_TILDE, false, false,
						false, false);
				pressKeyOnCanvas(ke);
			} else {
				pressKeyOnCanvas(e);
			}
		});

		keyMap.put(KeyCode.DIGIT1, (e) -> {
			if (e.isControlDown()) {
				PEApplication.getInstance().getMainController().selectTab(0);
				return;
			} else if (e.isShiftDown()) {
				e = new KeyEvent(null, null, KeyEvent.KEY_PRESSED, " ", "!", KeyCode.EXCLAMATION_MARK, false, false,
						false, false);
			}
			pressKeyOnCanvas(e);
		});

		keyMap.put(KeyCode.DIGIT2, (e) -> {
			if (e.isShiftDown()) {
				e = new KeyEvent(null, null, KeyEvent.KEY_PRESSED, " ", "@", KeyCode.AT, false, false, false, false);
			} else if (e.isControlDown()) {
				PEApplication.getInstance().getMainController().selectTab(1);
				return;
			}
			pressKeyOnCanvas(e);
		});

		keyMap.put(KeyCode.DIGIT3, (e) -> {
			if (e.isShiftDown()) {
				KeyEvent ke = new KeyEvent(null, null, KeyEvent.KEY_PRESSED, " ", "#", KeyCode.NUMBER_SIGN, false,
						false, false, false);
				pressKeyOnCanvas(ke);
			} else {
				pressKeyOnCanvas(e);
			}
		});

		keyMap.put(KeyCode.DIGIT4, (e) -> {
			if (e.isShiftDown()) {
				KeyEvent ke = new KeyEvent(null, null, KeyEvent.KEY_PRESSED, " ", "$", KeyCode.DOLLAR, false, false,
						false, false);
				pressKeyOnCanvas(ke);
			} else {
				pressKeyOnCanvas(e);
			}
		});

		keyMap.put(KeyCode.DIGIT5, (e) -> {
			if (e.isShiftDown()) {
				KeyEvent ke = new KeyEvent(null, null, KeyEvent.KEY_PRESSED, " ", "%", KeyCode.UNDEFINED, false, false,
						false, false);
				pressKeyOnCanvas(ke);
			} else {
				pressKeyOnCanvas(e);
			}
		});

		keyMap.put(KeyCode.DIGIT6, (e) -> {
			if (e.isShiftDown()) {
				KeyEvent ke = new KeyEvent(null, null, KeyEvent.KEY_PRESSED, " ", "^", KeyCode.CIRCUMFLEX, false, false,
						false, false);
				pressKeyOnCanvas(ke);
			} else {
				pressKeyOnCanvas(e);
			}
		});

		keyMap.put(KeyCode.DIGIT7, (e) -> {
			if (e.isShiftDown()) {
				KeyEvent ke = new KeyEvent(null, null, KeyEvent.KEY_PRESSED, " ", "&", KeyCode.AMPERSAND, false, false,
						false, false);
				pressKeyOnCanvas(ke);
			} else {
				pressKeyOnCanvas(e);
			}
		});

		keyMap.put(KeyCode.DIGIT8, (e) -> {
			if (e.isShiftDown()) {
				KeyEvent ke = new KeyEvent(null, null, KeyEvent.KEY_PRESSED, " ", "*", KeyCode.ASTERISK, false, false,
						false, false);
				pressKeyOnCanvas(ke);
			} else {
				pressKeyOnCanvas(e);
			}
		});

		keyMap.put(KeyCode.DIGIT9, (e) -> {
			if (e.isShiftDown()) {
				KeyEvent ke = new KeyEvent(null, null, KeyEvent.KEY_PRESSED, " ", "(", KeyCode.LEFT_PARENTHESIS, false,
						false, false, false);
				pressKeyOnCanvas(ke);
			} else {
				pressKeyOnCanvas(e);
			}
		});

		keyMap.put(KeyCode.DIGIT0, (e) -> {
			if (e.isShiftDown()) {
				KeyEvent ke = new KeyEvent(null, null, KeyEvent.KEY_PRESSED, " ", ")", KeyCode.RIGHT_PARENTHESIS, false,
						false, false, false);
				pressKeyOnCanvas(ke);
			} else {
				pressKeyOnCanvas(e);
			}
		});

		keyMap.put(KeyCode.MINUS, (e) -> {
			if (e.isShiftDown()) {
				KeyEvent ke = new KeyEvent(null, null, KeyEvent.KEY_PRESSED, " ", "_", KeyCode.UNDERSCORE, false, false,
						false, false);
				pressKeyOnCanvas(ke);
			} else {
				pressKeyOnCanvas(e);
			}
		});

		keyMap.put(KeyCode.COMMA, (e) -> {
			if (e.isShiftDown()) {
				KeyEvent ke = new KeyEvent(null, null, KeyEvent.KEY_PRESSED, " ", "<", KeyCode.LESS, false, false,
						false, false);
				pressKeyOnCanvas(ke);
			} else {
				pressKeyOnCanvas(e);
			}
		});

		keyMap.put(KeyCode.PERIOD, (e) -> {
			if (e.isShiftDown()) {
				KeyEvent ke = new KeyEvent(null, null, KeyEvent.KEY_PRESSED, " ", ">", KeyCode.GREATER, false, false,
						false, false);
				pressKeyOnCanvas(ke);
			} else {
				pressKeyOnCanvas(e);
			}
		});

		keyMap.put(KeyCode.SEMICOLON, (e) -> {
			if (e.isShiftDown()) {
				KeyEvent ke = new KeyEvent(null, null, KeyEvent.KEY_PRESSED, " ", ":", KeyCode.COLON, false, false,
						false, false);
				pressKeyOnCanvas(ke);
			} else {
				pressKeyOnCanvas(e);
			}
		});

	}

}
