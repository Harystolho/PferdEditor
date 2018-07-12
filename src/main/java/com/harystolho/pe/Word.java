package com.harystolho.pe;

import java.util.Arrays;

import com.harystolho.Main;
import com.harystolho.canvas.Drawable;
import com.sun.javafx.tk.Toolkit;

import javafx.scene.canvas.GraphicsContext;

public class Word implements Drawable {

	private static final int INITIAL_WORD_LENGTH = 10;

	private char[] word;
	private int size;

	// This acts as a cache, otherwise It'd have to create 2 String objects every
	// time it draws a word
	private String wordAsString;

	private int x;
	private int y;

	private TYPES type;

	public static enum TYPES {
		NORMAL, SPACE, NEW_LINE
	}

	private Word() {
		word = new char[INITIAL_WORD_LENGTH];
		size = 0;

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

		updateStringChar();
	}

	public void removeLastChar() {
		if (size > 0) {
			word[size--] = '\0';
		}

		updateStringChar();
	}

	private void resizeWordArray() {
		word = Arrays.copyOf(word, (int) (size * 1.5));
	}

	private void updateStringChar() {
		wordAsString = new String(word, 0, size);
	}

	public int getSize() {
		return size;
	}

	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}

	public TYPES getType() {
		return type;
	}

	public void setType(TYPES type) {
		this.type = type;
	}

	public float getDrawingSize() {
		return Toolkit.getToolkit().getFontLoader().computeStringWidth(getWordAsString(),
				Main.getApplication().getMainController().getCanvasManager().getFont());
	}

	@Override
	public void draw(GraphicsContext gc) {

	}

}
