package com.harystolho.pe;

import com.harystolho.canvas.CanvasManager;
import com.harystolho.canvas.Drawable;
import com.sun.javafx.tk.Toolkit;

import javafx.scene.canvas.GraphicsContext;

public class Word implements Drawable {

	private char[] word;

	// This acts as a cache, otherwise It'd have to create 2 String objects every
	// time it draws a word
	private String wordAsString;

	private int x;
	private int y;

	private TYPES type;

	public static enum TYPES {
		NORMAL, NEW_LINE
	}

	public Word() {
		type = TYPES.NORMAL;
	}

	public Word(TYPES type) {
		this.type = type;
	}

	public Word(char[] word) {
		this();
		setWord(word);
	}

	public char[] getWord() {
		return word;
	}

	public String getWordAsString() {
		return wordAsString;
	}

	public void setWord(char[] word) {
		this.word = word;
		wordAsString = new String(word);
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

	public float getSize() {
		return Toolkit.getToolkit().getFontLoader().computeStringWidth(getWordAsString(), CanvasManager.getFont());
	}

	@Override
	public void draw(GraphicsContext gc) {

	}

}
