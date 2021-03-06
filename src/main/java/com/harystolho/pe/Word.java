package com.harystolho.pe;

import java.util.Arrays;

import com.harystolho.misc.StyleLoader;
import com.harystolho.misc.dictionary.WordDictionary;
import com.harystolho.misc.dictionary.WordDictionaryTimer;
import com.sun.javafx.tk.Toolkit;

import javafx.scene.paint.Color;

public class Word implements Comparable<Word> {

	private static final int INITIAL_WORD_LENGTH = 10;

	private char[] word;
	private int size;

	// This acts as a cache, otherwise It'd have to create 2 String objects every
	// time it draws a word
	private String wordAsString;

	private float drawingSize;

	private Color color;

	private float x;
	private float y;

	private TYPES type;

	public static enum TYPES {
		NORMAL, SPACE, NEW_LINE, TAB
	}

	private Word() {
		word = new char[INITIAL_WORD_LENGTH];
		size = 0;

		drawingSize = 0f;

		color = StyleLoader.getTextColor();

		x = Float.MAX_VALUE;
		y = Float.MAX_VALUE;

		type = TYPES.NORMAL;
	}

	public Word(TYPES type) {
		this();
		this.type = type;
	}

	public Word(char c) {
		this();
		addChar(c);
	}

	public Word(char c, TYPES type) {
		this();

		this.type = type;
		addChar(c);
	}

	/**
	 * @return An array containing all the chars and null slots. If a word has 4
	 *         chars, it will return an array containing 4 chars plus 6 null
	 *         elements because by default the {@link #word word array} size is 10.
	 *         As the word grows the size also grows.
	 * @see #resizeWordArray()
	 */
	public char[] getWord() {
		return word;
	}

	public char getCharAt(int idx) {
		return getWord()[idx];
	}

	public String getWordAsString() {
		return wordAsString;
	}

	public void addChar(char c) {
		if (size == word.length) {
			resizeWordArray();
		}

		word[size++] = c;

		updateWordAsString();
	}

	public void addBeforeIndex(char charToAdd, int index) {
		if (size == word.length) {
			resizeWordArray();
		}

		for (int x = size - 1; x >= index; x--) {
			word[x + 1] = word[x];
		}

		word[index] = charToAdd;
		size++;

		updateWordAsString();
	}

	public char removeLastChar() {

		char charRemoved = '\0';

		if (size > 0) {
			charRemoved = word[size - 1];
			word[--size] = '\0';
		}

		updateWordAsString();

		return charRemoved;
	}

	public char removeCharAt(int index) {
		char removed = word[index];

		for (int x = index; x < size - 1; x++) { //
			word[x] = word[x + 1];
		}

		word[--size] = '\0';

		updateWordAsString();

		return removed;
	}

	/**
	 * Creates a new array that is 50% bigger and copies the old elements into the
	 * new one.
	 */
	private void resizeWordArray() {
		word = Arrays.copyOf(word, (int) (size * 1.5));
	}

	/**
	 * Every time a new char is added to the char array, it creates a new String
	 * representing the char array. This method also updates the word's width
	 */
	private void updateWordAsString() {
		wordAsString = new String(word, 0, size);
		updateDrawingSize();
		WordDictionaryTimer.addWord(this);
	}

	/**
	 * @return The number of chars in this Word
	 */
	public int getSize() {
		return size;
	}

	public float getX() {
		return x;
	}

	public void setX(float x) {
		this.x = x;
	}

	public float getY() {
		return y;
	}

	public void setY(float y) {
		this.y = y;
	}

	public TYPES getType() {
		return type;
	}

	public void setType(TYPES type) {
		this.type = type;
	}

	public boolean hasChars() {
		return getSize() > 0;
	}

	/**
	 * Updates the word's width. This is used when drawing the word or when moving
	 * the cursor
	 */
	public void updateDrawingSize() {
		drawingSize = Toolkit.getToolkit().getFontLoader().computeStringWidth(getWordAsString(), StyleLoader.getFont());
	}

	public static float computeCharWidth(char c) {
		return Toolkit.getToolkit().getFontLoader().computeStringWidth(String.valueOf(c), StyleLoader.getFont());
	}

	/**
	 * @return A number representing this word's witdh
	 */
	public float getDrawingSize() {
		return drawingSize;
	}

	public void setDrawingSize(float drawingSize) {
		this.drawingSize = drawingSize;
	}

	public Color getColor() {
		return color;
	}

	public void setColor(Color color) {
		this.color = color;
	}

	/**
	 * Updates the word color. It checks in the {@link WordDictionary} to get it's
	 * color, if it can't find a match it will return the default color specified in
	 * {@link StyleLoader#getTextColor()}
	 * 
	 * @see WordDictionary
	 */
	public void updateDrawingColor() {
		color = WordDictionary.getWordColor(getWordAsString());
	}

	/**
	 * Removes all chars after the char at index(inclusive) and add them to another
	 * {@link Word}. In the end there will be 2 words each one containing a part of
	 * the initial chars.
	 * 
	 * @param ch
	 * @return The word that was created
	 */
	public Word split(int charIndex) {
		Word otherWord = new Word(TYPES.NORMAL);

		for (int x = charIndex; x < size; x++) {
			otherWord.addChar(word[x]);
			word[x] = '\0';
		}

		size = size - otherWord.getSize();

		updateWordAsString();
		return otherWord;
	}

	@Override
	public int compareTo(Word w) {

		if (this == w) {
			return 0;
		}

		if (this.getY() < w.getY()) {
			return -1;
		} else if (this.getY() == w.getY()) {
			if (this.getX() < w.getX()) {
				return -1;
			} else if (this.getX() == w.getX()) {
				return 0;
			} else {
				if (this.getX() < w.getX() + w.getDrawingSize()) {
					return 0;
				} else {
					if (w.getType() == TYPES.NEW_LINE) {
						return -1;
					}
					return 1;
				}
			}
		} else if (this.getY() > w.getY()) {
			return 1;
		}

		return 0;
	}

	public String toString() {
		if (getType() == TYPES.TAB || getType() == TYPES.NEW_LINE) {
			return '"' + getType().name() + '"' + " {" + getX() + "," + getY() + "}";
		} else {
			return '"' + getWordAsString() + '"' + " {" + getX() + "," + getY() + "}";
		}

	}

}
