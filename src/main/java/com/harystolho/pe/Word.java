package com.harystolho.pe;

import java.awt.Font;
import java.awt.Shape;
import java.awt.font.GlyphVector;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;

import javax.swing.text.html.CSS;
import javax.swing.text.html.StyleSheet;

import com.harystolho.canvas.Drawable;
import com.sun.javafx.css.parser.CSSParser;

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

		Font f = new Font("arial", 0, 12);

		GlyphVector gv = f.createGlyphVector(null, getWord());

		Shape s = gv.getOutline();

	}

}
