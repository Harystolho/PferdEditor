package com.harystolho.pe;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import com.harystolho.pe.Word.TYPES;

public class File {

	private static final Word SPACE = new Word(" ".toCharArray());
	public static final Word NEW_LINE = new Word(TYPES.NEW_LINE);

	private Object drawLock;

	private String name;
	private List<Word> words;

	private double cursorX;
	private double cursorY;

	public File(String name) {
		this.name = name;

		drawLock = new Object();

		cursorX = 0;
		cursorY = 0;

		words = new LinkedList<>();
	}

	public void type(String character) {

		synchronized (drawLock) {

			switch (character) {
			case " ":
				addWord(SPACE);
				return;
			case "\r":
				addWord(NEW_LINE);
				return;
			default:
				break;
			}

			Word w = new Word(character.toCharArray());
			addWord(w);
		}
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

	public void addWord(Word word) {
		words.add(word);
	}

	public void removeWord(Word word) {
		words.remove(word);
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
