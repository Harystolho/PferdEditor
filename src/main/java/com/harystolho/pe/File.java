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

	private Word lastWord;

	public File(String name) {
		this.name = name;

		drawLock = new Object();

		cursorX = 0;
		cursorY = 0;

		words = new WordLinkedList();
	}

	public void type(KeyEvent e) {

		synchronized (drawLock) {

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

			addCharToFile(key.charAt(0));
		}

	}

	public void type(char c) {

		switch (c) {
		case ' ':
			words.addLast(new Word(' ', TYPES.SPACE));
			resetLastWord();
			return;
		case '\n':
			words.addLast(new Word(TYPES.NEW_LINE));
			resetLastWord();
			return;
		case '\t':
			words.addLast(new Word(TYPES.TAB));
			resetLastWord();
			return;
		default:
			break;
		}

		if (lastWord == null) {
			lastWord = new Word(c);
			words.addLast(lastWord);
		} else {
			lastWord.addChar(c);
		}

	}

	public void addWord(Word word) {
		words.add(word);

		updateCursorPosition(word.getWord()[0], true);

	}

	public void removeCharAtCursor() {

		if (words.size() == 0) {
			return;
		}

		Word wordToRemove = words.get(getCursorX() - 1, getCursorY()); // Get the word before the cursor.

		if (wordToRemove == null) { // If it's the beginning of a line, it will return null.
			removeWordAtTheBeginningOfTheLine(wordToRemove);
			return;
		}

		double cursorXInWWord = cursorX - wordToRemove.getX(); // Cursor' X in relation to word's X
		double wordWidthPosition = 0;

		for (int x = 0; x < wordToRemove.getSize(); x++) {
			char ch = wordToRemove.getWord()[x];

			wordWidthPosition += Word.computeCharWidth(ch);

			if (wordWidthPosition >= cursorXInWWord) {
				char removed = wordToRemove.removeCharAt(x);
				updateCursorPosition(removed, false);
				break;
			}
		}

		if (wordToRemove.getSize() == 0) {
			words.remove(wordToRemove);

			if (wordToRemove == lastWord) {
				lastWord = null;
			}

			if (wordToRemove.getType() == TYPES.NEW_LINE) {
				Main.getApplication().getCanvasManager().lineUp();
				setCursorX(-1);
			}
		}

	}

	private void removeWordAtTheBeginningOfTheLine(Word wordToRemove) {
		Word wordToRemoveAgain = words.get(getCursorX(), getCursorY()); // Try to remove the word at the
		// cursor.
		if (wordToRemoveAgain != null) {
			words.remove(wordToRemoveAgain);
		}

		Main.getApplication().getCanvasManager().lineUp();
		setCursorX(-1);
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

	private void addCharToFile(char c) {

		Word wrd = words.get(getCursorX(), getCursorY());

		if (wrd != null) {
			if (wrd.getType() == TYPES.SPACE || wrd.getType() == TYPES.NEW_LINE || wrd.getType() == TYPES.TAB) {
			} else {
				addCharToExistingWord(wrd, c);
				updateCursorPosition(c, true);
				return;
			}
		}

		if (lastWord == null) {
			Word word = new Word(c);
			this.lastWord = word;

			setWordPosition(word);
			addWord(word);
		} else {
			lastWord.addChar(c);
			updateCursorPosition(c, true);
		}

	}

	private void addCharToExistingWord(Word wrd, char c) {
		double cursorXInWWord = cursorX - wrd.getX(); // Cursor' X in relation to word's X

		double wordWidthPosition = 0;

		for (int x = 0; x < wrd.getSize(); x++) {
			char ch = wrd.getWord()[x];

			wordWidthPosition += Word.computeCharWidth(ch);

			if (wordWidthPosition > cursorXInWWord) {
				wrd.addBeforeChar(c, x);
				return;
			}
		}

		wrd.addChar(c); // Add the char to the end of the word

	}

	private void createSpace() {
		Word space = new Word(' ', TYPES.SPACE);
		setWordPosition(space);
		addWord(space);
		resetLastWord();
	}

	private void createNewLine() {
		Word new_line = new Word(TYPES.NEW_LINE);
		setWordPosition(new_line);
		addWord(new_line);
		forceLineDown();
		setCursorXToZero();
		resetLastWord();
	}

	private void createTab() {
		Word tab = new Word(TYPES.TAB);
		setWordPosition(tab);
		addWord(tab);
		resetLastWord();
	}

	public void resetLastWord() {
		lastWord = null;
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
					if (cursorXInWWord - wordWidthPosition <= charWidth / 2) {
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

	public String toString() {
		return name;
	}

	public Object getDrawLock() {
		return drawLock;
	}

	public Word getLastWord() {
		return lastWord;
	}

}
