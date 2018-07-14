package com.harystolho.canvas;

import java.util.ListIterator;

import com.harystolho.canvas.eventHandler.CMMouseEventHandler;
import com.harystolho.pe.File;
import com.harystolho.pe.Word;
import com.harystolho.utils.PEUtils;
import com.harystolho.utils.RenderThread;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

public class CanvasManager {

	public static final int CURSOR_DELAY = 15;
	public static final int TAB_SIZE = 35;

	private Canvas canvas;

	private GraphicsContext gc;

	private File currentFile;

	private int cursorCount;
	private int lineHeight; // px

	private int scrollX;
	private int scrollY;

	private CMMouseEventHandler mouseHandler;

	public CanvasManager(Canvas canvas) {
		this.canvas = canvas;

		gc = canvas.getGraphicsContext2D();

		setCursorCount(0);
		setLineHeight(18);

		StyleLoader.setFont(new Font("Arial", lineHeight - 2));
		gc.setFont(StyleLoader.getFont());

		scrollX = 0;
		scrollY = 0;

		mouseHandler = new CMMouseEventHandler(this);

	}

	// TODO don't render when the canvas is not focused.
	public void update() {

		clear();

		gc.setFill(StyleLoader.getBgColor());
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

					switch (wordObj.getType()) {
					case NEW_LINE:
						x = 0;
						y += getLineHeight();

						wordObj.setY(y);
						continue;
					case TAB:
						x += TAB_SIZE;

						wordObj.setX(x);
						wordObj.setY(y);
						continue;
					default:
						break;
					}

					gc.setFill(StyleLoader.getTextColor());
					gc.fillText(wordObj.getWordAsString(), x - scrollX, y - scrollY);

					wordObj.setX(x);
					wordObj.setY(y);

					x += wordObj.getDrawingSize();

				}
			}

			// If a new char was typed, updated the cursor
			if (currentFile.isTyped()) {
				currentFile.setTyped(false);

				currentFile.updateCursorPosition();
			}

		}

		drawCursor();

	}

	public void clear() {
		gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
	}

	private void drawLineBackground() {
		gc.setFill(StyleLoader.getLineColor());
		gc.fillRect(0, getCursorY() - lineHeight - scrollY, canvas.getWidth(), getLineHeight());

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

			gc.strokeLine(getCursorX() - scrollX, getCursorY() - scrollY, getCursorX() - scrollX,
					getCursorY() - getLineHeight() - scrollY);
		}

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
		updateWordsWidth();
	}

	/**
	 * When a file is loaded it calculates each word's width using the default font.
	 * When the {@link CanvasManager} object is created, it may change the font and
	 * then It has to recalculate the each word's width again.
	 */
	private void updateWordsWidth() {
		synchronized (currentFile.getDrawLock()) {
			for (Word word : currentFile.getWords()) {
				word.updateDrawingSize();
			}
		}

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
			currentFile.setCursorX(d + scrollX);
		}

	}

	public void setCursorY(double cursorY) {
		if (currentFile != null) {
			cursorY += lineHeight - 1 + scrollY; // Centralize on cursor

			currentFile.setCursorY(cursorY - (cursorY % lineHeight));
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
		if (getCursorY() <= lineHeight) {
			return;
		}

		currentFile.setCursorY(getCursorY() - getLineHeight());
	}

	public void lineDown() {
		currentFile.setCursorY(getCursorY() + getLineHeight());
	}

	public void scrollLeft() {
		if (scrollX >= 5) {
			scrollX -= 5;
		}
	}

	public void scrollRight() {
		scrollX += 5;
	}

	public void scrollUp() {
		if (scrollY >= lineHeight) {
			scrollY -= lineHeight;
		}
	}

	public void scrollDown() {
		scrollY += lineHeight;
	}

	public void printDebugMessage() {
		if (currentFile != null) {
			System.out.println(String.format("File - Words: %d | cursorX: %.1f | cursorY: %.1f",
					currentFile.getWords().size(), currentFile.getCursorX(), currentFile.getCursorY()));
		}
	}

}