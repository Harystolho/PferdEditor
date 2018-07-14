package com.harystolho.pe;

import java.util.List;

import com.harystolho.Main;
import com.harystolho.canvas.CanvasManager;
import com.harystolho.pe.Word.TYPES;

import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

public class File {

	private static final Word TAB = new Word(TYPES.TAB);
	private static final Word NEW_LINE = new Word(TYPES.NEW_LINE);

	private Object drawLock;

	private String name;
	private WordLinkedList words;

	private double cursorX;
	private double cursorY;

	// TRUE when the user types something.
	private boolean typed;

	public File(String name) {
		this.name = name;

		drawLock = new Object();

		cursorX = 0;
		cursorY = 0;

		words = new WordLinkedList();
	}

	public void type(KeyEvent e) {

		synchronized (drawLock) {

			setTyped(true);

			switch (e.getCode()) {
			case SPACE:
				Word space = new Word(' ', TYPES.SPACE);
				setWordPosition(space);
				addWord(space);
				return;
			case ENTER:
				addWord(NEW_LINE);
				return;
			case BACK_SPACE:
				removeCharAtCursor();
				return;
			case TAB:
				addWord(TAB);
				return;
			default:
				break;
			}

			String key = e.getText();

			if (key.length() <= 0) {
				return;
			}

			Word lastWord = new Word(key.charAt(0));
			setWordPosition(lastWord);
			addWord(lastWord);
		}

	}

	public void type(char c) {

		switch (c) {
		case ' ':
			type(new KeyEvent(null, null, null, KeyCode.SPACE, false, false, false, false));
			return;
		case '\n':
			type(new KeyEvent(null, null, null, KeyCode.ENTER, false, false, false, false));
			return;
		case '\t':
			type(new KeyEvent(null, null, null, KeyCode.TAB, false, false, false, false));
			return;
		default:
			break;
		}

		type(new KeyEvent(null, String.valueOf(c), String.valueOf(c), KeyCode.UNDEFINED, false, false, false, false));

	}

	public void addWord(Word word) {
		words.add(word);

		updateCursorPosition(word.getWord()[0], true);

	}

	public void removeCharAtCursor() {

		if (words.size() <= 0) {
			return;
		}

		Word word = words.get(getCursorX() - 1, getCursorY());

		if (word == null) {
			return;
		}

		char charRemoved = word.removeLastChar();

		if (word.getSize() == 0) {
			words.remove(word);

			if (word.getType() == TYPES.NEW_LINE) {
				Main.getApplication().getCanvasManager().lineUp();
			}
		}

		updateCursorPosition(charRemoved, false);

	}

	private void setWordPosition(Word lastWord) {
		if (Main.getApplication().getMainController() != null) {

			CanvasManager cm = Main.getApplication().getCanvasManager();

			lastWord.setX((float) cm.getCursorX());
			lastWord.setY((float) cm.getCursorY());

		}

	}

	/**
	 * Updates the cursor position.
	 * 
	 * @param c   the char typed or deleted
	 * @param add If <code>true</code> it will add the width of this char to the
	 *            cursor X, if <code>false</code> it will subtract.
	 */
	public void updateCursorPosition(char c, boolean add) {

		if (words.size() == 0) {
			setCursorX(0);
			return;
		}

		// 1 is for beauty purposes.
		if (add) {
			setCursorX(getCursorX() + Word.computeCharWidth(c) + 1);
		} else {
			setCursorX(getCursorX() - Word.computeCharWidth(c) + 1);
		}

	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<Word> getWords() {
		return words;
	}

	public double getCursorX() {
		return cursorX;
	}

	public double getCursorY() {
		return cursorY;
	}

	public void setCursorX(double cursorX) {
		float biggestX = 0;

		for (Word w : words) {
			if (w.getY() == getCursorY()) {
				if (w.getX() + w.getDrawingSize() > biggestX) {
					biggestX = w.getX() + w.getDrawingSize();
				}
			}
		}

		if (cursorX < biggestX) {
			this.cursorX = cursorX;
		} else {
			this.cursorX = biggestX;
		}

	}

	public void setCursorY(double cursorY) {

		float biggestY = Main.getApplication().getCanvasManager().getLineHeight();

		for (Word w : words) {
			if (w.getY() > biggestY) {
				biggestY = w.getY();
			}
		}

		if (cursorY > biggestY) {
			this.cursorY = biggestY;
		} else {
			this.cursorY = cursorY;
		}

	}

	public boolean isTyped() {
		return typed;
	}

	public void setTyped(boolean typed) {
		this.typed = typed;
	}

	public String toString() {
		return name;
	}

	public Object getDrawLock() {
		return drawLock;
	}

}
