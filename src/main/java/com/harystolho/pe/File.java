package com.harystolho.pe;

import com.harystolho.Main;
import com.harystolho.canvas.CanvasManager;
import com.harystolho.pe.Word.TYPES;
import com.harystolho.pe.linkedList.IndexLinkedList;

import javafx.scene.input.KeyEvent;

/**
 * A class that behaves similarly to a normal file. It contains a list of the
 * words that are drawn by the {@link CanvasManager}.
 * 
 * @author Harystolho
 *
 */
public class File {

	private Object drawLock;

	private java.io.File diskFile;
	private boolean isLoaded;

	private String name;
	private IndexLinkedList<Word> words;

	private float cursorX;
	private float cursorY;

	// Reference to the last word typed
	private Word lastWord;

	public File(String name) {
		this.name = name;

		diskFile = null;
		isLoaded = false;

		drawLock = new Object();

		cursorX = 0;
		cursorY = 0;

		words = new IndexLinkedList<>();
	}

	/**
	 * This method is called when the user presses a key that can be typed in a
	 * file. For example if the user presses <code>F3</code> this method won't be
	 * called.
	 * 
	 * @param e
	 */
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

			addKeyToFile(e);
		}

	}

	/**
	 * Used when a file is loaded from the system.
	 * 
	 * @param c
	 */
	public void type(char c) {

		switch (c) {
		case ' ':
			words.addLast(new Word(' ', TYPES.SPACE));
			resetLastWord();
			return;
		case '\n': // Ignore
			return;
		case '\r':
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

	/**
	 * Adds word to file and updates the cursor position
	 * 
	 * @param word
	 */
	public void addWord(Word word) {
		words.add(word);
		updateCursorPosition(word.getWord()[0], true);
	}

	public void removeCharAtCursor() {

		if (words.isEmpty()) {
			return;
		}

		Word wordToRemove = words.get(getCursorX() - 1, getCursorY()); // Gets the word before the cursor.

		if (wordToRemove == null) { // If it's the beginning of a line, it will return null and will remove the last
									// word in the line above
			removeLastWordAtTheLineAbove();
			return;
		}

		// Remove the last char in the word
		removeCharAtCursor(wordToRemove);

		// If the word has no chars left
		if (!wordToRemove.hasChars()) {
			words.remove(wordToRemove);

			if (wordToRemove == lastWord) {
				lastWord = null;
			}

			// Updates the cursor position
			switch (wordToRemove.getType()) {
			case NEW_LINE:
				Main.getApplication().getCanvasManager().lineUp();
				setCursorX(-1);
				break;
			case TAB:
				updateCursorPosition(wordToRemove, false);
				break;
			default:
				break;
			}

		}

	}

	private void removeCharAtCursor(Word word) {
		double cursorXInWWord = getCursorX() - word.getX(); // Cursor' X in relation to word's X
		double wordWidthPosition = 0;

		for (int x = 0; x < word.getSize(); x++) {
			char ch = word.getWord()[x];

			wordWidthPosition += Word.computeCharWidth(ch);

			if (wordWidthPosition >= cursorXInWWord) {
				char removed = word.removeCharAt(x); // Removes only 1 char in the word
				updateCursorPosition(removed, false);
				break;
			}
		}
	}

	/**
	 * If the user presses <code>backspace</code> at the beginning of a line, it has
	 * to remove the <code>new line</code> at the line above
	 */
	private void removeLastWordAtTheLineAbove() {
		// The new line will always be the last word in a line
		Word newLine = words.findLastWordIn(getCursorY() - Main.getApplication().getCanvasManager().getLineHeight());
		if (newLine != null) {
			words.remove(newLine);
		}

		Main.getApplication().getCanvasManager().lineUp();
		setCursorX(-1);
	}

	/**
	 * Sets the <code>word</code> position to the cursor position.
	 * 
	 * @param word
	 */
	private void setWordPosition(Word word) {
		if (Main.getApplication().getMainController() != null) {

			CanvasManager cm = Main.getApplication().getCanvasManager();

			if (getCursorX() != 0) {
				word.setX((float) cm.getCursorX() + 1);
				word.setY((float) cm.getCursorY());
			} else {
				word.setX((float) cm.getCursorX());
				word.setY((float) cm.getCursorY());
			}
		}

	}

	private void forceLineDown() {
		if (Main.getApplication().getMainController() != null) {
			cursorY = (getCursorY() + Main.getApplication().getCanvasManager().getLineHeight());
		}
	}

	/**
	 * Increases/Decreases the cursor position by <code>c</code>'s width
	 * 
	 * @param c   the char typed or deleted
	 * @param add If <code>true</code> it will add the char's width to cursor X, if
	 *            <code>false</code> it will subtract.
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

	/**
	 * Increases/Decreases the cursor position by <code>word</code>'s
	 * {@link Word#getDrawingSize() getDrawingSize()}
	 * 
	 * @param word the word's width
	 * @param add  If <code>true</code> it will add the width of this char to the
	 *             cursor X, if <code>false</code> it will subtract.
	 */
	public void updateCursorPosition(Word word, boolean add) {
		if (words.size() == 0) {
			setCursorX(0);
			return;
		}

		if (add) {
			cursorX = cursorX + word.getDrawingSize();
		} else {
			cursorX = cursorX - word.getDrawingSize();
		}
	}

	private void addKeyToFile(KeyEvent key) {
		String keyString = key.getText(); // Get String representing this key

		// If SHIFT is pressed, it's keyString.lenght is 0
		if (keyString.length() <= 0) {
			return;
		}

		if (key.isShiftDown()) {
			addCharToFile(keyString.toUpperCase().charAt(0));
		} else {
			addCharToFile(keyString.charAt(0));
		}

	}

	/**
	 * Adds the <code>char</code> to this file at the cursor position. If there is a
	 * word at that position, it will add the char to that word, otherwise it will
	 * create a new word
	 * 
	 * @param c
	 */
	private void addCharToFile(char c) {

		Word wrd = words.get(getCursorX(), getCursorY());

		if (wrd != null) {
			if (wrd.getType() == TYPES.SPACE || wrd.getType() == TYPES.NEW_LINE || wrd.getType() == TYPES.TAB) {
				lastWord = null;
			} else {
				addCharToExistingWord(wrd, c);
				updateCursorPosition(c, true);
				return;
			}
		}

		if (lastWord == null) {
			Word word = new Word(c);
			lastWord = word;

			setWordPosition(word);
			addWord(word);
		} else {
			lastWord.addChar(c);
			updateCursorPosition(c, true);
		}

	}

	/**
	 * Adds this char to an existing word
	 * 
	 * @param wrd word
	 * @param c   char
	 */
	private void addCharToExistingWord(Word wrd, char c) {
		double cursorXInWWord = getCursorX() - wrd.getX(); // Cursor' X in relation to word's X
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

		Word wordAtCursor = words.get(getCursorX(), getCursorY());

		if (wordAtCursor != null) {
			double cursorXInWWord = getCursorX() - wordAtCursor.getX(); // Cursor' X in relation to word's X
			double wordWidthPosition = 0;

			for (int x = 0; x < wordAtCursor.getSize(); x++) {
				wordWidthPosition += Word.computeCharWidth(wordAtCursor.getWord()[x]);

				if (wordWidthPosition > cursorXInWWord) { // If the cursor is in the middle of a word
					createSpaceInTheMiddleOfTheWord(wordAtCursor, x);
					return;
				}
			}

			createSpaceAtTheEndOfTheWord(); // If the cursor is at the and of the word

		} else { // It shouldn't get here, but in case it does
			createSpaceAtTheEndOfTheWord();
		}

	}

	private void createSpaceInTheMiddleOfTheWord(Word word, int charIndex) {
		Word newWord = word.split(charIndex); // Split the current word in 2

		Word space = new Word(' ', TYPES.SPACE);
		setWordPosition(space);
		addWord(space);

		newWord.setX(space.getX() + space.getDrawingSize());
		newWord.setY(word.getY());

		words.add(newWord); // Add new word after space
		resetLastWord();
	}

	private void createSpaceAtTheEndOfTheWord() {
		Word space = new Word(' ', TYPES.SPACE);
		setWordPosition(space);
		addWord(space);
		resetLastWord();
	}

	private void createNewLine() {

		Word wordAtCursor = words.get(getCursorX(), getCursorY());

		if (wordAtCursor != null) {
			double cursorXInWWord = getCursorX() - wordAtCursor.getX(); // Cursor' X in relation to word's X
			double wordWidthPosition = 0;

			for (int x = 0; x < wordAtCursor.getSize(); x++) {
				wordWidthPosition += Word.computeCharWidth(wordAtCursor.getWord()[x]);

				if (wordWidthPosition > cursorXInWWord) { // If the cursor is in the middle of a word
					createNewLineInTheMiddleOfTheWord(wordAtCursor, x);
					return;
				}
			}

			createNewLineAtTheEndOfTheWord(); // If the cursor is at the and of the word

		} else { // It shouldn't get here, but in case it does
			createNewLineAtTheEndOfTheWord();
		}
	}

	private void createNewLineInTheMiddleOfTheWord(Word word, int charIndex) {
		Word newWord = word.split(charIndex); // Split the current word in 2

		createNewLineAtTheEndOfTheWord();

		newWord.setX(word.getX() + word.getDrawingSize());
		newWord.setY(word.getY());

		words.add(newWord); // Add new word after space
		resetLastWord();
	}

	private void createNewLineAtTheEndOfTheWord() {
		Word new_line = new Word(TYPES.NEW_LINE);
		setWordPosition(new_line);
		new_line.setX(new_line.getX() - 2);
		addWord(new_line);

		// Move cursor at the beginning of the line below
		forceLineDown();
		setCursorX(0);

		resetLastWord();
	}

	private void createTab() {
		Word tab = new Word(TYPES.TAB);
		setWordPosition(tab);
		tab.setDrawingSize(CanvasManager.TAB_SIZE);

		addWord(tab);
		updateCursorPosition(tab, true);
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

	public IndexLinkedList<Word> getWords() {
		return words;
	}

	public float getCursorX() {
		return cursorX;
	}

	public void setCursorX(float cursorX) {

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
			Word lastWord = words.findLastWordIn(getCursorY());
			if (lastWord != null) {
				this.cursorX = lastWord.getX() + lastWord.getDrawingSize();
			}
		}

	}

	public void setCursorY(float cursorY) {

		float biggestY = Main.getApplication().getCanvasManager().getLineHeight();

		if (!words.isEmpty()) {
			biggestY = words.getLast().getY();

			if (words.getLast().getType() == TYPES.NEW_LINE) {
				biggestY += Main.getApplication().getCanvasManager().getLineHeight();
			}

			if (cursorY > biggestY) {
				this.cursorY = biggestY;
			} else {
				this.cursorY = cursorY;
			}
		} else {
			this.cursorY = biggestY;
		}

		resetLastWord();

	}

	public float getCursorY() {
		return cursorY;
	}

	public void moveCursorLeft() {

		Word word = getWords().get(getCursorX() - 1, getCursorY());

		if (word != null) {
			double cursorXInWWord = getCursorX() - word.getX(); // Cursor' X in relation to word's X
			double wordWidthPosition = 0;

			int x = 0;
			for (x = 0; x < word.getSize(); x++) {
				wordWidthPosition += Word.computeCharWidth(word.getWord()[x]);

				if (wordWidthPosition > cursorXInWWord) { // If the cursor is in the middle of a word
					if (getCursorX() > 0) {
						setCursorX(getCursorX() - Word.computeCharWidth(word.getWord()[x - 1]));
					}
					return;
				}
			}

			if (getCursorX() > 0) {
				if (word.getType() != TYPES.TAB) {
					setCursorX(getCursorX() - Word.computeCharWidth(word.getWord()[x - 1]));
				}
			} else { // Move line up
				Main.getApplication().getCanvasManager().lineUp();
				setCursorX(-1);
			}

		}

	}

	public void moveCursorRight() {
		Word word = getWords().get(getCursorX(), getCursorY());

		if (word != null) {
			double cursorXInWWord = getCursorX() - word.getX(); // Cursor' X in relation to word's X
			double wordWidthPosition = 0;

			int x = 0;
			for (x = 0; x < word.getSize(); x++) {
				wordWidthPosition += Word.computeCharWidth(word.getWord()[x]);

				if (wordWidthPosition > cursorXInWWord) { // If the cursor is in the middle of a word
					setCursorX(getCursorX() + Word.computeCharWidth(word.getWord()[x]));
					return;
				}
			}

			Word nextWord = getWords().get(word.getX() + word.getDrawingSize() + 1, getCursorY());

			if (nextWord != null) {
				setCursorX(getCursorX() + Word.computeCharWidth(nextWord.getWord()[0]));
			} else {
				Main.getApplication().getCanvasManager().lineDown();
			}

		}
	}

	@Override
	public String toString() {
		return name;
	}

	public Object getDrawLock() {
		return drawLock;
	}

	public Word getLastWord() {
		return lastWord;
	}

	public java.io.File getDiskFile() {
		return diskFile;
	}

	public void setDiskFile(java.io.File f) {
		this.diskFile = f;
	}

	/**
	 * @return <code>true</code> is the file has already been loaded from disk
	 */
	public boolean isLoaded() {
		return isLoaded;
	}

	public void setLoaded(boolean loaded) {
		this.isLoaded = loaded;
	}

}
