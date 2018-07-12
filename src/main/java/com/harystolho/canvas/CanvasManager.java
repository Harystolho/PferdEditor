package com.harystolho.canvas;

import java.util.ListIterator;

import com.harystolho.canvas.eventHandler.CMMouseEventHandler;
import com.harystolho.pe.File;
import com.harystolho.pe.Word;
import com.harystolho.utils.PEStyleSheet;
import com.harystolho.utils.PEUtils;
import com.harystolho.utils.RenderThread;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

public class CanvasManager {

	public static final int CURSOR_DELAY = 15;

	private Canvas canvas;

	private GraphicsContext gc;

	PEStyleSheet peStyleSheet;

	private File currentFile;

	private int cursorCount;
	private int lineHeight; // px

	private int scrollX;
	private int scrollY;

	private Color lineColor;
	private Color bgColor;
	private Color textColor;

	private CMMouseEventHandler mouseHandler;

	public CanvasManager(Canvas canvas) {
		this.canvas = canvas;

		gc = canvas.getGraphicsContext2D();

		peStyleSheet = new PEStyleSheet("file.css");

		setCursorCount(0);
		setLineHeight(18);

		scrollX = 0;
		scrollY = 0;

		loadColors();
		setupFonts();

		mouseHandler = new CMMouseEventHandler(this);

	}

	// TODO don't render when the canvas is not focused.
	public void update() {

		clear();

		gc.setFill(bgColor);
		gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());

		draw();
	}

	public void draw() {

		drawLineBackground();

		if (currentFile != null) {

			float x = 0;
			float y = getLineHeight();

			synchronized (currentFile.getDrawLock()) {
				ListIterator<Word> i = currentFile.getWords().listIterator();

				while (i.hasNext()) {
					Word wordObj = i.next();

					if (wordObj == File.NEW_LINE) {
						x = 0;
						y += getLineHeight();
						continue;
					}

					gc.setFill(textColor);
					gc.fillText(wordObj.getWordAsString(), x - scrollX, y);
					x += wordObj.getDrawingSize();

				}
			}

		}

		drawCursor();

	}

	public void clear() {
		gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
	}

	private void drawLineBackground() {
		gc.setFill(lineColor);
		gc.fillRect(0, getCursorY(), canvas.getWidth(), getLineHeight());

	}

	private void drawCursor() {

		if (!canvas.isFocused()) {
			return;
		}

		if (cursorCount == -CURSOR_DELAY) {
			cursorCount = CURSOR_DELAY;
		}

		cursorCount--;

		if (cursorCount > 0) {

			gc.setFill(Color.BLACK);

			gc.strokeLine(getCursorX(), getCursorY(), getCursorX(), getCursorY() + getLineHeight());
		}

	}

	public void initRenderThread() {
		RenderThread.running = true;

		PEUtils.getExecutor().execute(new RenderThread());
	}

	private void loadColors() {
		lineColor = Color.rgb(179, 179, 179, 0.44);
		bgColor = Color.web(peStyleSheet.getRule("#background", "background-color"));
		textColor = Color.web(peStyleSheet.getRule("#text", "color"));
	}

	public void setupFonts() {
		gc.setFont(new Font("Arial", getLineHeight() - 2));
	}

	public Font getFont() {
		return gc.getFont();
	}

	public void setFont(Font font) {
		gc.setFont(font);
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
		// It can't go above first line.
		if (getCursorY() < 16) {
			return;
		}

		currentFile.setCursorY(getCursorY() - getLineHeight());
	}

	public void lineDown() {
		currentFile.setCursorY(getCursorY() + getLineHeight());
	}

	public void scrollRight() {
		scrollX += 5;
	}

	public void scrollLeft() {
		if (scrollX >= 5) {
			scrollX -= 5;
		}
	}

	public int getScrollX() {
		return scrollX;
	}

	public int getScrollY() {
		return scrollY;
	}

	public void setScrollX(int scrollX) {
		this.scrollX = scrollX;
	}

	public void setScrollY(int scrollY) {
		this.scrollY = scrollY;
	}

	public void printDebugMessage() {
		if (currentFile != null) {
			System.out.println(String.format("File - Words: %d | cursorX: %.1f | cursorY: %.1f",
					currentFile.getWords().size(), currentFile.getCursorX(), currentFile.getCursorY()));
		}
	}

}