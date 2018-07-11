package com.harystolho.canvas;

import java.util.Iterator;
import java.util.ListIterator;

import com.harystolho.canvas.eventHandler.CMMouseEventHandler;
import com.harystolho.pe.File;
import com.harystolho.pe.Word;
import com.harystolho.utils.PEStyleSheet;
import com.harystolho.utils.PEUtils;
import com.harystolho.utils.RenderThread;
import com.sun.javafx.tk.FontMetrics;
import com.sun.javafx.tk.Toolkit;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class CanvasManager {

	public static final int CURSOR_DELAY = 15;

	private Canvas canvas;

	private GraphicsContext gc;

	PEStyleSheet peStyleSheet;

	private File currentFile;

	private int cursorCount;
	private int lineHeight; // px

	private Color lineColor;

	private FontMetrics fm;

	private CMMouseEventHandler mouseHandler;

	public CanvasManager(Canvas canvas) {
		this.canvas = canvas;

		gc = canvas.getGraphicsContext2D();

		peStyleSheet = new PEStyleSheet("file.css");

		setCursorCount(0);
		setLineHeight(16);

		loadColors();

		fm = Toolkit.getToolkit().getFontLoader().getFontMetrics(gc.getFont());

		mouseHandler = new CMMouseEventHandler(this);

	}

	public void update() {

		clear();

		gc.setFill(Color.WHITE);
		gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());

		draw();
	}

	public void draw() {

		drawLineBackground();

		drawCursor();

		if (currentFile != null) {

			int x = 0;
			int y = getLineHeight();

			ListIterator<Word> i = currentFile.getWords().listIterator();

			while (i.hasNext()) {
				gc.setFill(Color.BLACK);
				String word = new String(i.next().getWord());
				gc.fillText(word, x, y);
				x += fm.computeStringWidth(word) + 1.2;
				
				if (x >= canvas.getWidth()) {
					x = 0;
					y += getLineHeight();
				}

			}

		}

	}

	public void clear() {
		gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
	}

	private void drawLineBackground() {
		gc.setFill(lineColor);
		gc.fillRect(0, getCursorY(), canvas.getWidth(), getLineHeight());

	}

	private void drawCursor() {

		if (cursorCount == -CURSOR_DELAY) {
			cursorCount = CURSOR_DELAY;
		}

		cursorCount--;

		if (cursorCount > 0) {

			gc.setFill(Color.BLACK);

			gc.strokeLine(getCursorX(), getCursorY(), getCursorX(), getCursorY() + getLineHeight());
		}

	}

	private void loadColors() {
		lineColor = Color.web(peStyleSheet.getRule("#line", "background-color"));

	}

	public void initRenderThread() {

		RenderThread.running = true;

		PEUtils.getExecutor().execute(new RenderThread());
	}

	public void stopRenderThread() {
		RenderThread.stop();
	}

	public File getCurrentFile() {
		return currentFile;
	}

	public void setCurrentFile(File currentFile) {
		this.currentFile = currentFile;
	}

	public Canvas getCanvas() {
		return canvas;
	}

	public void setCursorCount(int count) {
		cursorCount = count;
	}

	public double getCursorX() {
		return currentFile.getCursorX();
	}

	public double getCursorY() {
		return currentFile.getCursorY();
	}

	public void setCursorX(double d) {
		if (currentFile != null) {
			currentFile.setCursorX(d);
		}

	}

	public void setCursorY(double cursorY) {
		if (currentFile != null) {
			cursorY += lineHeight - 1; // Centralize on cursor

			currentFile.setCursorY(cursorY - (cursorY % lineHeight) - lineHeight);
		}
	}

	public int getLineHeight() {
		return lineHeight;
	}

	public void setLineHeight(int lineHeight) {
		this.lineHeight = lineHeight;
	}

	public void lineUp() {
		currentFile.setCursorY(getCursorY() - getLineHeight());
	}

	public void lineDown() {
		currentFile.setCursorY(getCursorY() + getLineHeight());
	}

}
