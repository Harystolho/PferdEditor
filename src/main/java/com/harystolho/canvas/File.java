package com.harystolho.canvas;

import java.util.LinkedList;
import java.util.List;

public class File {

	private String name;
	private List<Word> words;

	private double cursorX;
	private double cursorY;

	public File(String name) {
		this.name = name;

		cursorX = 0;
		cursorY = 0;

		words = new LinkedList<>();
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

}
