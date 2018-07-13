package com.harystolho.pe;

import java.util.LinkedList;
import java.util.List;

import com.harystolho.Main;
import com.harystolho.pe.Word.TYPES;

import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

public class File {

	private static final Word SPACE = new Word(' ', TYPES.SPACE);
	private static final Word TAB = new Word(TYPES.TAB);
	public static final Word NEW_LINE = new Word(TYPES.NEW_LINE);

	private Object drawLock;

	private String name;
	private List<Word> words;

	private Word lastWord;

	private double cursorX;
	private double cursorY;

	// TRUE when the user types something.
	private boolean typed;

	public File(String name) {
		this.name = name;

		drawLock = new Object();

		cursorX = 0;
		cursorY = 0;

		words = new LinkedList<>();
	}

	public void type(KeyEvent e) {

		synchronized (drawLock) {

			setTyped(true);

			switch (e.getCode()) {
			case SPACE:
				addWord(SPACE);
				addLastWord();
				return;
			case ENTER:
				addWord(NEW_LINE);
				addLastWord();
				return;
			case BACK_SPACE:
				removeCharAtCursor();
				return;
			case TAB:
				addWord(TAB);
				addLastWord();
				return;
			default:
				break;
			}

			String key = e.getText();

			if (key.length() <= 0) {
				return;
			}

			if (lastWord == null) {
				lastWord = new Word(key.charAt(0));
				addWord(lastWord);
			} else {
				lastWord.addChar(key.charAt(0));
			}

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

	/**
	 * When the user presses SPACE or ENTER, it will set {@link #lastWord} to null,
	 * so that if he types a new char it will create a new word instead of appending
	 * to this one.
	 */
	public void addLastWord() {
		if (lastWord != null) {
			lastWord = null;
		}
	}

	public void addWord(Word word) {
		words.add(word);
	}

	public void removeCharAtCursor() {

		if (words.size() <= 0) {
			return;
		}

		Word w = getLastWordInWordsList();

		if (w.getType() != TYPES.SPACE) {
			w.removeLastChar();

			if (w.getSize() == 0) {
				words.remove(words.size() - 1);
				addLastWord();

				if (w.getType() == TYPES.NEW_LINE) {
					Main.getApplication().getCanvasManager().lineUp();
				}
			}

		} else {
			words.remove(words.size() - 1);
			addLastWord();
		}

	}

	public Word getLastWordInWordsList() {
		return words.get(words.size() - 1);
	}

	/**
	 * Sets the position of the cursor to the last typed char.
	 */
	public void updateCursorPosition() {

		if (words.size() == 0) {
			setCursorX(0);
			return;
		}

		Word lastWord = getLastWordInWordsList();

		// 1 is beauty purposes.
		setCursorX(lastWord.getX() + lastWord.getDrawingSize() + 1);
		Main.getApplication().getCanvasManager().setCursorY(lastWord.getY());
		Main.getApplication().getCanvasManager().update();
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
		this.cursorX = cursorX;
	}

	public void setCursorY(double cursorY) {
		this.cursorY = cursorY;
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
