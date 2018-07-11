package com.harystolho.pe;

import com.harystolho.canvas.Drawable;

import javafx.scene.canvas.GraphicsContext;

public class Word implements Drawable {

	private char[] word;
	private int x;
	private int y;

	public Word() {

	}

	public Word(char[] word) {
		this.word = word;
	}

	public char[] getWord() {
		return word;
	}

	public void setWord(char[] word) {
		this.word = word;
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

	@Override
	public void draw(GraphicsContext gc) {
		
	}

}
