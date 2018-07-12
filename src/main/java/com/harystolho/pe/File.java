package com.harystolho.pe;

import java.util.LinkedList;
import java.util.List;

import com.harystolho.pe.Word.TYPES;

import javafx.scene.input.KeyEvent;

public class File {

	private static final Word SPACE = new Word(' ', TYPES.SPACE);
	public static final Word NEW_LINE = new Word(TYPES.NEW_LINE);

	private Object drawLock;

	private String name;
	private List<Word> words;

	private Word lastWord;

	private double cursorX;
	private double cursorY;

	public File(String name) {
		this.name = name;

		drawLock = new Object();

		cursorX = 0;
		cursorY = 0;

		words = new LinkedList<>();
	}

	public void type(KeyEvent e) {

		synchronized (drawLock) {
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
			}

		} else {
			words.remove(words.size() - 1);
			addLastWord();
		}

	}

	public void removeWord(Word word) {
		words.remove(word);
	}

	public Word getLastWordInWordsList() {
		return words.get(words.size() - 1);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String toString() {
		return name;
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

	public Object getDrawLock() {
		return drawLock;
	}

}
