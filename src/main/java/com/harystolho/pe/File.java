package com.harystolho.pe;

import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import com.harystolho.PEApplication;
import com.harystolho.canvas.CanvasManager;
import com.harystolho.pe.Word.TYPES;
import com.harystolho.pe.linkedList.IndexLinkedList;
import com.harystolho.thread.FileUpdaterThread;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

/**
 * A class that behaves similarly to a normal file. It contains a list of the
 * words that are drawn by the {@link CanvasManager}, the cursor position and
 * scroll position of the file.
 * 
 * @author Harystolho
 *
 */
public class File {

	// Locked used when iterating the LinkedList
	private ReadWriteLock drawLock;

	private java.io.File diskFile;
	private boolean isLoaded;
	private boolean wasModified;
	private boolean wasPreRendered;

	private String name;
	private IndexLinkedList<Word> words;

	// TODO FIX repalce float by double
	private float cursorX;
	private float cursorY;

	private float scrollX;
	private float scrollY;

	private Word lastWordTyped;

	public File(String name) {
		this.name = name;

		diskFile = null;
		isLoaded = false;
		wasPreRendered = false;

		drawLock = new ReentrantReadWriteLock();

		wasModified = false;

		cursorX = 0;
		cursorY = 0;

		scrollX = 0;
		scrollY = 0;

		words = new IndexLinkedList<>();
	}

	/**
	 * Method used to type in the file using a {@link KeyEvent}. The method's
	 * behaviour will depend on the key's {@link KeyCode}. It will also update the
	 * cursor position when necessary
	 * 
	 * @param keyEvent
	 */
	public void type(KeyEvent keyEvent) {

		setWasModified(true);
		drawLock.writeLock().lock();

		switch (keyEvent.getCode()) {
		case SPACE:
			createSpace();
			break;
		case ENTER:
			createNewLine();
			break;
		case BACK_SPACE:
			removeCharBeforeCursor();
			break;
		case DELETE:
			removeCharAtCursor();
			break;
		case TAB:
			createTab();
			break;
		default:
			addKeyToFile(keyEvent);
			break;
		}

		drawLock.writeLock().unlock();
	}

	/**
	 * Inserts the char in the file at the last word. Used when a file is loaded
	 * from the system.
	 * 
	 * @param c
	 */
	public void type(char c) {

		switch (c) {
		case ' ':
			words.addLast(new Word(' ', TYPES.SPACE));
			resetLastWord();
			break;
		case '\n': // Ignore
			return;
		case '\r':
			words.addLast(new Word(TYPES.NEW_LINE));
			resetLastWord();
			break;
		case '\t':
			Word tab = new Word(TYPES.TAB);
			tab.setDrawingSize(CanvasManager.TAB_SIZE);
			words.addLast(tab);
			resetLastWord();
			break;
		case '(': // Add these chars as new words instead of appending them to the last word
		case ')':
		case '{':
		case '}':
		case '[':
		case ']':
		case '<':
		case '>':
		case ':':
		case ';':
		case '.':
		case ',':
			words.addLast(new Word(c, TYPES.NORMAL));
			resetLastWord();
			break;
		default:
			if (lastWordTyped == null) {
				lastWordTyped = new Word(c);
				words.addLast(lastWordTyped);
			} else {
				lastWordTyped.addChar(c);
			}
			break;
		}
	}

	/**
	 * Adds word to file and updates the cursor position
	 * 
	 * @param word
	 */
	public void addWordAndUpdateCursorPosition(Word word) {
		words.add(word);
		updateCursorPosition(word.getCharAt(0), true);
	}

	private void addKeyToFile(KeyEvent key) {
		String keyString = key.getText(); // Get string representing this key

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
	 * Removes the char before the cursor and updates the cursor postiion
	 */
	public void removeCharBeforeCursor() {
		if (words.isEmpty()) {
			return;
		}

		Word wordToRemove = words.get(getCursorX() - 1, getCursorY()); // Gets the word before the cursor.

		// If it's the beginning of a line, it will return null and will remove the last
		// word in the line above
		if (wordToRemove == null) {
			// If it's the first line
			if (getCursorX() == 0 && getCursorY() == CanvasManager.getInstance().getLineHeight()) {
				return;
			}

			removeLastWordAtTheLineAbove();

			// Update file's biggest Y
			FileUpdaterThread.decrementBiggestYBy(CanvasManager.getInstance().getLineHeight());
		} else { // It is not at the beginning of a line

			// Remove the last char in the word and update cursor position
			removeCharBeforeCursor(wordToRemove);

			// If the word has no chars left
			if (!wordToRemove.hasChars()) {
				words.remove(wordToRemove); // Remove word from file

				if (wordToRemove == lastWordTyped) {
					lastWordTyped = null;
				}

				// Updates the cursor position
				switch (wordToRemove.getType()) {
				case NEW_LINE:
					CanvasManager.getInstance().lineUp();
					CanvasManager.getInstance().moveCursorToEndOfTheLine();
					break;
				case TAB:
					updateCursorPosition(wordToRemove, false);
					break;
				default:
					break;
				}
			}
		}
	}

	/**
	 * Removes the last char of the word and updates the cursor position
	 * 
	 * @param word
	 */
	private void removeCharBeforeCursor(Word word) {
		double cursorXInWWord = getCursorX() - word.getX(); // Cursor's X in relation to word's X
		double wordWidthPosition = 0;

		for (int charPosition = 0; charPosition < word.getSize(); charPosition++) { // Find char to remove
			char charAtPosition = word.getCharAt(charPosition);

			wordWidthPosition += Word.computeCharWidth(charAtPosition);

			if (wordWidthPosition >= cursorXInWWord) {
				char removed = word.removeCharAt(charPosition); // Removes only 1 char in the word
				updateCursorPosition(removed, false);
				break;
			}
		}
	}

	/**
	 * Removes the char at the cursor and updates the cursor position
	 */
	private void removeCharAtCursor() {
		if (words.isEmpty()) {
			return;
		}

		Word wordToRemove = words.get(getCursorX(), getCursorY()); // Gets the word at the cursor

		if (wordToRemove != null) {
			// Removes the char at the cursor
			removeCharAtCursor(wordToRemove);

			// If the word has no chars left
			if (!wordToRemove.hasChars()) {
				words.remove(wordToRemove);

				if (wordToRemove == lastWordTyped) {
					lastWordTyped = null;
				}
			}

			if (wordToRemove.getType() == TYPES.NEW_LINE) {
				// Update file's biggest Y
				FileUpdaterThread.decrementBiggestYBy(CanvasManager.getInstance().getLineHeight());

				// If the word removed is a NEW_LINE and is in the first line, move the
				// word after it 1 line above
				if (wordToRemove.getY() == CanvasManager.getInstance().getLineHeight()) {
					getWords().getFirst().setY(CanvasManager.getInstance().getLineHeight());
				}
			}
		}

	}

	private void removeCharAtCursor(Word word) {
		double cursorXInWWord = getCursorX() - word.getX(); // Cursor X in relation to word X
		double wordWidthPosition = 0;

		for (int charPosition = 0; charPosition < word.getSize(); charPosition++) {
			char charAtPosition = word.getWord()[charPosition];

			if (wordWidthPosition >= cursorXInWWord) {
				word.removeCharAt(charPosition); // Removes only 1 char in the word
				break;
			}
			wordWidthPosition += Word.computeCharWidth(charAtPosition);
		}
	}

	/**
	 * If the user presses <code>backspace</code> at the beginning of a line, it has
	 * to remove the <code>new line</code> at the line above
	 */
	private void removeLastWordAtTheLineAbove() {
		// The NEW_LINE will always be the last word in a line
		Word newLine = words.findLastWordIn(getCursorY() - CanvasManager.getInstance().getLineHeight());
		if (newLine != null) {
			words.remove(newLine);

			CanvasManager.getInstance().lineUp();
			CanvasManager.getInstance().moveCursorToEndOfTheLine();
		}
	}

	/**
	 * Sets the <code>word</code> position to the cursor position.
	 * 
	 * @param word
	 */
	private void setWordPosition(Word word) {
		CanvasManager cm = CanvasManager.getInstance();

		// TODO FIX why add scrollX?
		word.setX(cm.getCursorX() + scrollX - 1);
		word.setY(cm.getCursorY());
	}

	/**
	 * Moves the cursor 1 line down
	 */
	private void forceLineDown() {
		// Update file's biggest Y
		FileUpdaterThread.incrementBiggestYBy(CanvasManager.getInstance().getLineHeight());

		// Increase cursor position
		cursorY = getCursorY() + CanvasManager.getInstance().getLineHeight();

		// Scroll down if necessary
		if (getCursorY() > PEApplication.getInstance().getMainController().getCanvas().getHeight() + getScrollY()) {
			CanvasManager.getInstance().scrollDown();
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
			cursorX += Word.computeCharWidth(c);
		} else {
			// If the cursor is at the biggest X in the file, it has to move the scroll left
			if (cursorX == FileUpdaterThread.getBiggestX()) {
				setScrollX(getScrollX() - Word.computeCharWidth(c));
			}

			cursorX -= Word.computeCharWidth(c);
		}
	}

	/**
	 * Increases/Decreases the cursor position by <code>word</code>'s
	 * {@link Word#getDrawingSize() getDrawingSize()}
	 * 
	 * @param word the word
	 * @param add  If <code>true</code> it will add the width of this char to the
	 *             cursor X, if <code>false</code> it will subtract.
	 */
	public void updateCursorPosition(Word word, boolean add) {
		if (words.size() == 0) {
			setCursorX(0);
			return;
		}

		if (add) {
			cursorX += word.getDrawingSize();
		} else {
			cursorX -= word.getDrawingSize();
		}
	}

	/**
	 * Adds the <code>char</code> to this file at the cursor position. If there is a
	 * word at that position, it will append the char to that word, otherwise it
	 * will create a new word
	 * 
	 * @param c
	 */
	public void addCharToFile(char c) {
		if (isCharPunctuation(c)) {
			Word word = new Word(c);

			setWordPosition(word);
			addWordAndUpdateCursorPosition(word);
			resetLastWord();
			return;
		}

		Word wrd = words.get(getCursorX() - 1, getCursorY());
		if (wrd != null) {
			if (wrd.getType() == TYPES.SPACE || wrd.getType() == TYPES.NEW_LINE || wrd.getType() == TYPES.TAB) {
				createWordAfterSpecialWord(wrd, c);
			} else { // Append to word before the cursor
				addCharToExistingWord(wrd, c);
				updateCursorPosition(c, true);
			}
			return;
		} else {
			if (lastWordTyped == null || getCursorX() == 0) {
				Word word = new Word(c);
				lastWordTyped = word;

				setWordPosition(word);
				addWordAndUpdateCursorPosition(word);
			} else {
				lastWordTyped.addChar(c);
				updateCursorPosition(c, true);
			}
		}
	}

	/**
	 * If a char is a punctuation it shouldn't be appended to another word, it
	 * should be a word on its own
	 * 
	 * @param c
	 * @return
	 */
	private boolean isCharPunctuation(char c) {
		switch (c) {
		case '(':
		case ')':
		case '{':
		case '}':
		case '[':
		case ']':
		case '<':
		case '>':
		case ':':
		case ';':
		case '.':
		case ',':
			return true;
		default:
			return false;
		}
	}

	/**
	 * A special word is either a {@link TYPES#TAB}, {@link TYPES#NEW_LINE} or a
	 * {@link TYPES#SPACE}
	 * 
	 * @param wrd the word before the cursor
	 * @param ch  the char to be added
	 */
	private void createWordAfterSpecialWord(Word wrd, char ch) {
		resetLastWord();

		Word word = new Word(ch);
		lastWordTyped = word;

		word.setX(wrd.getX() + 1);
		word.setY(wrd.getY());

		addWordAndUpdateCursorPosition(word);
	}

	/**
	 * Appends this char to an existing word
	 * 
	 * @param wrd word
	 * @param c   char
	 */
	private void addCharToExistingWord(Word wrd, char c) {
		double cursorXInWWord = getCursorX() - wrd.getX(); // Cursor' X in relation to word's X
		double wordWidthPosition = 0;

		for (int charPosition = 0; charPosition < wrd.getSize(); charPosition++) {
			char charAtPosition = wrd.getWord()[charPosition];

			wordWidthPosition += Word.computeCharWidth(charAtPosition);

			if (wordWidthPosition > cursorXInWWord) {
				wrd.addBeforeIndex(c, charPosition);
				return;
			}
		}

		// If it couldn't append the char in the loop above, append it to the end of the
		// word
		wrd.addChar(c);
	}

	/**
	 * Creates a space and updates the cursor position
	 */
	private void createSpace() {

		Word wordAtCursor = words.get(getCursorX(), getCursorY());

		if (wordAtCursor != null) {
			double cursorXInWWord = getCursorX() - wordAtCursor.getX(); // Cursor' X in relation to word's X
			double wordWidthPosition = 0;

			for (int charPosition = 0; charPosition < wordAtCursor.getSize(); charPosition++) {
				wordWidthPosition += Word.computeCharWidth(wordAtCursor.getCharAt(charPosition));

				if (wordWidthPosition > cursorXInWWord) { // If the cursor is in the middle of a word
					if (charPosition > 0) {
						createSpaceInTheMiddleOfTheWord(wordAtCursor, charPosition);// If the cursor is in the middle of
																					// a word
					} else {
						createSpaceBeforeWord(); // If the cursor is at the beginning of a word
					}
					return;
				}
			}

			createSpaceAtTheEndOfTheWord(); // If the cursor is at the and of the word

		} else { // Create space after word
			createSpaceAtTheEndOfTheWord();
		}

	}

	private void createSpaceBeforeWord() {
		Word space = new Word(' ', TYPES.SPACE);

		space.setX(CanvasManager.getInstance().getCursorX() + scrollX - 1);
		space.setY(CanvasManager.getInstance().getCursorY());

		addWordAndUpdateCursorPosition(space);

		resetLastWord();
	}

	private void createSpaceInTheMiddleOfTheWord(Word word, int charIndex) {
		Word newWord = word.split(charIndex); // Split the current word in 2

		Word space = new Word(' ', TYPES.SPACE);
		setWordPosition(space);
		addWordAndUpdateCursorPosition(space);

		newWord.setX(space.getX() + space.getDrawingSize());
		newWord.setY(word.getY());

		words.add(newWord); // Add new word after space
		resetLastWord();
	}

	private void createSpaceAtTheEndOfTheWord() {
		Word space = new Word(' ', TYPES.SPACE);
		setWordPosition(space);
		addWordAndUpdateCursorPosition(space);
		resetLastWord();
	}

	/**
	 * Creates a new line and updates the cursor position
	 */
	private void createNewLine() {

		Word wordAtCursor = words.get(getCursorX(), getCursorY());

		if (wordAtCursor != null) {
			double cursorXInWWord = getCursorX() - wordAtCursor.getX(); // Cursor' X in relation to word's X
			double wordWidthPosition = 0;

			for (int charPosition = 0; charPosition < wordAtCursor.getSize(); charPosition++) {
				wordWidthPosition += Word.computeCharWidth(wordAtCursor.getCharAt(charPosition));

				if (wordWidthPosition > cursorXInWWord) {
					if (charPosition > 0) {
						createNewLineInTheMiddleOfTheWord(wordAtCursor, charPosition);// If the cursor is in the middle
																						// of a word
					} else {
						createNewLineAtTheEndOfTheWord(); // If the cursor is at the beginning of a word
					}
					return;
				}
			}

			createNewLineAtTheEndOfTheWord(); // If the cursor is at the and of the word

		} else { // Create new line after word
			createNewLineAtTheEndOfTheWord();
		}
	}

	private void createNewLineInTheMiddleOfTheWord(Word word, int charIndex) {
		Word newWord = word.split(charIndex); // Split the current word in 2

		newWord.setX(word.getX() + word.getDrawingSize());
		newWord.setY(word.getY());
		words.add(newWord);

		createNewLineAtTheEndOfTheWord(); // Create New Line between words

		resetLastWord();
	}

	private void createNewLineAtTheEndOfTheWord() {
		Word new_line = new Word(TYPES.NEW_LINE);
		setWordPosition(new_line);
		addWordAndUpdateCursorPosition(new_line);

		// Move cursor to the beginning of the line below
		forceLineDown();
		CanvasManager.getInstance().moveCursorToBeginningOfTheLine();

		resetLastWord();
	}

	/**
	 * Creates a tab and updates the cursor position
	 */
	private void createTab() {
		Word tab = new Word(TYPES.TAB);
		setWordPosition(tab);
		tab.setDrawingSize(CanvasManager.TAB_SIZE);

		addWordAndUpdateCursorPosition(tab);
		updateCursorPosition(tab, true);
		resetLastWord();
	}

	public void resetLastWord() {
		lastWordTyped = null;
	}

	public IndexLinkedList<Word> getWords() {
		return words;
	}

	public float getCursorX() {
		return cursorX;
	}

	/**
	 * Sets the cursor's x position to the beginning or end of a char position. If
	 * the user clicks near the beginning of a char, it will place the cursor at the
	 * beginning of the char.
	 * 
	 * @param cursorX if -1 will set the cursor's position to the end of the line
	 *                otherwise near <code>cursorX</code>
	 */
	public void setCursorX(float cursorX) {
		Word word = getWords().get(cursorX, getCursorY());

		if (word != null) {
			double cursorXInWWord = cursorX - word.getX(); // Cursor' X in relation to word's X

			if (word.getType() == TYPES.TAB) {
				if (word.getDrawingSize() / 2 > cursorXInWWord) { // Move cursor to the beginning of the word
					this.cursorX = word.getX();
				} else { // Move cursor to the end of the word
					this.cursorX = word.getX() + word.getDrawingSize();
				}
				return;
			} else {
				double wordWidthPosition = 0;

				for (int charPosition = 0; charPosition < word.getSize(); charPosition++) {
					char c = word.getCharAt(charPosition);

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
			}

			this.cursorX = cursorX;
		} else { // Move cursor to the end of the last word in the cursor Y
			Word lastWord = words.findLastWordIn(getCursorY());
			if (lastWord != null) {
				this.cursorX = lastWord.getX() + lastWord.getDrawingSize();
			} else {
				this.cursorX = 0;
			}
		}

	}

	public void setCursorY(float cursorY) {

		float biggestY = CanvasManager.getInstance().getLineHeight();

		if (!words.isEmpty()) {
			biggestY = words.getLast().getY();

			if (words.getLast().getType() == TYPES.NEW_LINE) {
				biggestY += CanvasManager.getInstance().getLineHeight();
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
			if (word.getType() == TYPES.TAB) {
				this.cursorX = word.getX();
				return;
			}

			double cursorXInWWord = getCursorX() - word.getX(); // Cursor' X in relation to word's X
			double wordWidthPosition = 0;

			int x = 0;
			for (x = 0; x < word.getSize(); x++) {
				wordWidthPosition += Word.computeCharWidth(word.getWord()[x]);

				if (wordWidthPosition > cursorXInWWord) { // If the cursor is in the middle of a word
					if (getCursorX() > 0) {
						setCursorX(getCursorX() - Word.computeCharWidth(word.getCharAt(x - 1)));
					}
					return;
				}
			}

			// If the word has only 1 char it gets here
			if (getCursorX() > 0) {
				setCursorX(getCursorX() - Word.computeCharWidth(word.getWord()[x - 1]));
			} else { // Move line up
				CanvasManager.getInstance().lineUp();
				CanvasManager.getInstance().moveCursorToEndOfTheLine();
			}
		} else { // If the cursor is at the beginning of a line
			CanvasManager.getInstance().lineUp();
			CanvasManager.getInstance().moveCursorToEndOfTheLine();
		}

	}

	public void moveCursorRight() {
		Word word = getWords().get(getCursorX(), getCursorY());

		if (word != null) {
			double cursorXInWWord = getCursorX() - word.getX(); // Cursor' X in relation to word's X
			double wordWidthPosition = 0;

			if (word.getType() == TYPES.TAB) {
				this.cursorX = word.getX() + word.getDrawingSize();
				return;
			}

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
				CanvasManager.getInstance().lineDown();
				setCursorX(0);
			}

		}
	}

	public Word getWordAtCursor() {
		return getWords().get(getCursorX(), getCursorY());
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
		updateDiskFile(name);
	}

	/**
	 * When a {@link File} is renamed it has also to rename the disk file
	 * 
	 * @param name
	 */
	private void updateDiskFile(String name) {
		if (diskFile == null) {
			return;
		}

		java.io.File newFile = new java.io.File(diskFile.getParent() + "/" + name);
		boolean succeed = diskFile.renameTo(newFile);

		if (!succeed) {
			Alert error = new Alert(AlertType.ERROR);
			error.setContentText("Can't rename file");
			error.showAndWait();
		} else {
			diskFile = newFile;
		}
	}

	@Override
	public String toString() {
		return name;
	}

	public ReadWriteLock getDrawLock() {
		return drawLock;
	}

	public Word getLastWord() {
		return lastWordTyped;
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
		if (!loaded) {
			unload();
		} else {
			isLoaded = true;
		}
	}

	public void unload() {
		getWords().clear();
		resetLastWord();

		words = new IndexLinkedList<>();
		scrollX = 0;
		scrollY = 0;
		cursorX = 0;
		cursorY = 0;

		lastWordTyped = null;

		isLoaded = false;
		wasModified = false;
		wasPreRendered = false;

		System.out.println("Unloading file from disk: " + getName());
	}

	public float getScrollX() {
		return scrollX;
	}

	public void setScrollX(float scrollX) {
		if (scrollX > 0) {
			this.scrollX = scrollX;
		} else {
			this.scrollX = 0;
		}

	}

	public double getScrollY() {
		return scrollY;
	}

	public void setScrollY(float scrollY) {
		this.scrollY = scrollY;
	}

	public boolean wasModified() {
		return wasModified;
	}

	public void setWasModified(boolean wasModified) {
		if (!this.wasModified && wasModified) {
			PEApplication.getInstance().getMainController().getFileTabManager().setFileModified(this);
		}
		this.wasModified = wasModified;

	}

	public boolean wasPreRendered() {
		return wasPreRendered;
	}

	public void setPreRendered(boolean wasPreRendered) {
		this.wasPreRendered = wasPreRendered;
	}

}
