package com.harystolho.pe;

import java.util.Arrays;

import com.harystolho.canvas.Drawable;
import com.harystolho.canvas.StyleLoader;
import com.sun.javafx.tk.Toolkit;

import javafx.scene.canvas.GraphicsContext;

public class Word implements Drawable {

	private static final int INITIAL_WORD_LENGTH = 10;

	private char[] word;
	private int size;

	// This acts as a cache, otherwise It'd have to create 2 String objects every
	// time it draws a word
	private String wordAsString;

	private float drawingSize;

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

		x = 0;
		y = 0;

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

	public char[] getWord() {
		return word;
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

	public void removeLastChar() {
		if (size > 0) {
			word[--size] = '\0';
		}

		updateWordAsString();
	}

	private void resizeWordArray() {
		word = Arrays.copyOf(word, (int) (size * 1.5));
	}

	private void updateWordAsString() {
		wordAsString = new String(word, 0, size);
		updateDrawingSize();
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

	public void setX(float x2) {
		this.x = x2;
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

	/**
	 * Updates the word's width
	 */
	public void updateDrawingSize() {
		drawingSize = Toolkit.getToolkit().getFontLoader().computeStringWidth(getWordAsString(), StyleLoader.getFont());
	}

	/**
	 * @return A number representing this word's witdh
	 */
	public float getDrawingSize() {
		return drawingSize;
	}

	@Override
	public void draw(GraphicsContext gc) {

	}

}
