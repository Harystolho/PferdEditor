package com.harystolho.canvas;

import java.util.ListIterator;

import com.harystolho.Main;
import com.harystolho.canvas.eventHandler.CanvasMouseEventHandler;
import com.harystolho.pe.File;
import com.harystolho.pe.Word;
import com.harystolho.thread.FileUpdaterThread;
import com.harystolho.thread.RenderThread;
import com.harystolho.utils.PEUtils;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.text.Font;

public class CanvasManager {

	public static final int CURSOR_DELAY = 8;
	public static final int TAB_SIZE = 35;
	public static final int SCROLL_CHANGE = 5;

	private Canvas canvas;

	private GraphicsContext gc;

	private File currentFile;

	private int cursorCount = 0;
	private int lineHeight; // in pixels

	private CanvasMouseEventHandler mouseHandler;

	public CanvasManager(Canvas canvas) {
		this.canvas = canvas;

		gc = canvas.getGraphicsContext2D();

		setLineHeight(30);

		StyleLoader.setFont(new Font("Arial", lineHeight - 2));
		gc.setFont(StyleLoader.getFont());

		mouseHandler = new CanvasMouseEventHandler(this);

	}

	// TODO don't render when the canvas is not focused.
	public void update() {
		clear();
		draw();
	}

	public void clear() {
		gc.setFill(StyleLoader.getBgColor());
		gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
	}

	public void makeCanvasTransparent() {
		gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
	}

	public void draw() {
		drawBackgroundLine();
		drawWords();
		drawCursor();

	}

	private void drawWords() {
		if (currentFile != null) {

			float x = 0;
			float y = getLineHeight();

			synchronized (currentFile.getDrawLock()) {
				ListIterator<Word> i = currentFile.getWords().listIterator();

				while (i.hasNext()) {
					Word wordObj = i.next();

					switch (wordObj.getType()) {
					case NEW_LINE:
						wordObj.setX(x);
						wordObj.setY(y);

						x = 0;
						y += getLineHeight();
						continue;
					case TAB:
						wordObj.setX(x);
						wordObj.setY(y);

						x += TAB_SIZE;
						continue;
					default:
						break;
					}

					gc.setFill(StyleLoader.getTextColor());
					gc.fillText(wordObj.getWordAsString(), x - getScrollX(), y - getScrollY());

					wordObj.setX(x);
					wordObj.setY(y);

					x += wordObj.getDrawingSize();

					// Stop drawing if it's out of the canvas
					if (y > canvas.getHeight() + getScrollY()) {
						break;
					}

				}

				updateSrollBar();

			}
		}
	}

	private void drawBackgroundLine() {
		gc.setFill(StyleLoader.getBackgroundLineColor());
		gc.fillRect(0, getCursorY() - lineHeight - getScrollY(), canvas.getWidth(), getLineHeight());

	}

	private void drawCursor() {

		if (!canvas.isFocused()) {
			return;
		}

		if (cursorCount <= -CURSOR_DELAY) {
			cursorCount = CURSOR_DELAY;
		}

		cursorCount--;

		if (cursorCount > 0) {
			gc.setFill(StyleLoader.getCursorColor());
			gc.fillRect(getCursorX() - getScrollX(), getCursorY() - lineHeight - getScrollY(), 2, lineHeight); // 2 is
																												// cursor's
			// width
		}

	}

	private void updateSrollBar() {
		if (Main.getApplication().getMainController() != null) {
			Main.getApplication().getMainController().updateScrollBar(FileUpdaterThread.getBiggestX(),
					FileUpdaterThread.getBiggestY());
		}
	}

	public void initRenderThread() {
		RenderThread.running = true;
		FileUpdaterThread.setRunning(true);
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

	private void resetCursorCount() {
		setCursorCount(CURSOR_DELAY);
	}

	public float getCursorX() {
		return currentFile.getCursorX();
	}

	public float getCursorY() {
		return currentFile.getCursorY();
	}

	public void setCursorX(float x) {
		if (currentFile != null) {
			currentFile.setCursorX(x + getScrollX());
		}
	}

	public void setCursorY(float cursorY) {
		if (currentFile != null) {
			cursorY += lineHeight - 1 + getScrollY(); // Centralize on cursor

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
		currentFile.setCursorX(getCursorX()); // Moves the cursor to the end of the life if the line above is shorter.

		if (getCursorY() <= getScrollY()) {
			scrollUp();
		}

	}

	public void lineDown() {
		currentFile.setCursorY(getCursorY() + getLineHeight());
		currentFile.setCursorX(getCursorX()); // Moves the cursor to the end of the life if the line below is shorter.

		if (getCursorY() > canvas.getHeight() + getScrollY()) {
			scrollDown();
		}

	}

	public void moveCursorLeft() {
		if (currentFile != null) {
			currentFile.moveCursorLeft();
			resetCursorCount();
		}

	}

	public void moveCursorRight() {
		if (currentFile != null) {
			currentFile.moveCursorRight();
			resetCursorCount();
		}
	}

	public void moveCursorToStartOfTheLine() {
		if (currentFile != null) {
			currentFile.setCursorX(0);
			resetCursorCount();
		}
	}

	public void moveCursorToEndOfTheLine() {
		if (currentFile != null) {
			setCursorX(-1);
			resetCursorCount();
		}
	}

	public void moveCursorToFirstLine() {
		setScrollY(0);
		setCursorY(lineHeight);
		moveCursorToStartOfTheLine();
	}

	public void moveCursorToLastLine() {
		currentFile.setScrollY((int) (FileUpdaterThread.getBiggestY() - canvas.getHeight()));
		currentFile.setCursorY(FileUpdaterThread.getBiggestY());
		moveCursorToEndOfTheLine();
	}

	public void scrollLeft() {
		if (getScrollX() >= SCROLL_CHANGE) {
			setScrollX(getScrollX() - SCROLL_CHANGE);
		}
	}

	public void scrollRight() {
		setScrollX(getScrollX() + SCROLL_CHANGE);
	}

	public void scrollUp() {
		scrollUp(false);
	}

	/**
	 * @param twoLines if <code>true</code> scrolls 2 lines up
	 */
	public void scrollUp(boolean twoLines) {
		setScrollY(getScrollY() - lineHeight);

		if (twoLines) {
			// Try to scroll up again
			scrollUp(false);
		}
	}

	public void scrollDown() {
		scrollDown(false);
	}

	/**
	 * @param twoLines if <code>true</code> scrolls 2 lines down
	 */
	public void scrollDown(boolean twoLines) {
		setScrollY(getScrollY() + lineHeight);

		if (twoLines) {
			scrollDown(false);
		}
	}

	public void setScrollX(int x) {
		if (currentFile != null) {
			currentFile.setScrollX(x);
		}
	}

	public void setScrollY(int y) {
		if (currentFile != null) {

			if (y < lineHeight) { // First Line
				if (getScrollY() >= lineHeight) {
					currentFile.setScrollY(y);
				}
			} else {
				if (y <= FileUpdaterThread.getBiggestY() - canvas.getHeight() + lineHeight) {
					currentFile.setScrollY(y);
				}
			}

		}
	}

	public int getScrollX() {
		if (currentFile != null) {
			return currentFile.getScrollX();
		}
		return 0;
	}

	public int getScrollY() {
		if (currentFile != null) {
			return currentFile.getScrollY();
		}
		return 0;
	}

	public void printDebugMessage() {
		if (currentFile != null) {
			System.out.println("---- DEBUG ------");
			System.out.println(String.format("File - Words: %d | cursorX: %.1f | cursorY: %.1f",
					currentFile.getWords().size(), currentFile.getCursorX(), currentFile.getCursorY()));
			System.out.println(String.format("File - lastWord: %s", currentFile.getLastWord()));
		}
	}

}