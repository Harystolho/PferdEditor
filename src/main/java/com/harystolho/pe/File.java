package com.harystolho.pe;

import com.harystolho.Main;
import com.harystolho.canvas.CanvasManager;
import com.harystolho.pe.Word.TYPES;

import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

public class File {

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
				createSpace();
				return;
			case ENTER:
				createNewLine();
				return;
			case BACK_SPACE:
				removeCharAtCursor();
				return;
			case TAB:
				createTab();
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
			words.addLast(new Word(' ', TYPES.SPACE));
			return;
		case '\n':
			words.addLast(new Word(TYPES.NEW_LINE));
			return;
		case '\t':
			words.addLast(new Word(TYPES.TAB));
			return;
		default:
			break;
		}
		words.addLast(new Word(c));
	}

	public void addWord(Word word) {
		words.add(word);

		updateCursorPosition(word.getWord()[0], true);

	}

	public void removeCharAtCursor() {

		if (words.size() <= 0) {
			return;
		}

		Word wordToRemove = words.get(getCursorX() - 1, getCursorY());

		if (wordToRemove == null) {
			Word wordToRemoveAgain = words.get(getCursorX(), getCursorY());

			if (wordToRemoveAgain != null) {
				words.remove(wordToRemoveAgain);
			}

			Main.getApplication().getCanvasManager().lineUp();
			setCursorX(-1);

			return;
		}

		char charRemoved = wordToRemove.removeLastChar();

		if (wordToRemove.getSize() == 0) {
			words.remove(wordToRemove);

			if (wordToRemove.getType() == TYPES.NEW_LINE) {
				Main.getApplication().getCanvasManager().lineUp();
				setCursorX(-1);
			}
		}

		updateCursorPosition(charRemoved, false);

	}

	private void setWordPosition(Word word) {
		if (Main.getApplication().getMainController() != null) {

			CanvasManager cm = Main.getApplication().getCanvasManager();

			if (getCursorX() != 0 || word.getType() == TYPES.NEW_LINE) {
				word.setX((float) cm.getCursorX() - 1);
				word.setY((float) cm.getCursorY());
			} else {
				word.setX((float) cm.getCursorX() + 1);
				word.setY((float) cm.getCursorY());
			}

			/*
			 * if (word.getType() != TYPES.NEW_LINE) { word.setX((float) cm.getCursorX());
			 * word.setY((float) cm.getCursorY()); } else { word.setX((float)
			 * cm.getCursorX() - 1); word.setY((float) cm.getCursorY()); }
			 */

		}

	}

	private void forceLineDown() {
		if (Main.getApplication().getMainController() != null) {
			cursorY = (getCursorY() + Main.getApplication().getCanvasManager().getLineHeight());
		}
	}

	public void setCursorXToZero() {
		setCursorX(0);
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

		if (add) {
			cursorX = cursorX + Word.computeCharWidth(c);
		} else {
			cursorX = cursorX - Word.computeCharWidth(c);
		}

	}

	private void createSpace() {
		Word space = new Word(' ', TYPES.SPACE);
		setWordPosition(space);
		addWord(space);
	}

	private void createNewLine() {
		Word new_line = new Word(TYPES.NEW_LINE);
		setWordPosition(new_line);
		addWord(new_line);
		forceLineDown();
		setCursorXToZero();
	}

	private void createTab() {
		Word tab = new Word(TYPES.TAB);
		setWordPosition(tab);
		addWord(tab);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public WordLinkedList getWords() {
		return words;
	}

	public double getCursorX() {
		return cursorX;
	}

	public double getCursorY() {
		return cursorY;
	}

	public void setCursorX(double cursorX) {

		Word word = getWords().get(cursorX, getCursorY());

		if (word != null) {
			double cursorXInWWord = cursorX - word.getX(); // Cursor' X in relation to word's X

			double wordWidthPosition = 0;
			for (char c : word.getWord()) {
				float charWidth = Word.computeCharWidth(c);

				if (wordWidthPosition + charWidth > cursorXInWWord) {
					if (cursorXInWWord - wordWidthPosition < charWidth - cursorXInWWord) {
						cursorX -= cursorXInWWord - wordWidthPosition;
					} else {
						cursorX += wordWidthPosition + charWidth - cursorXInWWord;
					}
					break;
				}
				wordWidthPosition += charWidth;
			}

			this.cursorX = cursorX;
		} else {

			float biggestX = 0;

			for (Word w : words) {
				if (w.getY() == getCursorY()) {
					if (w.getX() + w.getDrawingSize() >= biggestX) {
						biggestX = w.getX() + w.getDrawingSize();
					}
				}
			}
			this.cursorX = biggestX;
		}

	}

	public void setCursorY(double cursorY) {

		float biggestY = Main.getApplication().getCanvasManager().getLineHeight();

		if (!words.isEmpty()) {
			biggestY = words.getLast().getY();

			if (cursorY > biggestY) {
				this.cursorY = biggestY;
			} else {
				this.cursorY = cursorY;
			}
		} else {
			this.cursorY = biggestY;
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
