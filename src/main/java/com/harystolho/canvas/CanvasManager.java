package com.harystolho.canvas;

import java.util.ListIterator;

import com.harystolho.Main;
import com.harystolho.canvas.eventHandler.CanvasMouseHandler;
import com.harystolho.misc.StyleLoader;
import com.harystolho.pe.File;
import com.harystolho.pe.Word;
import com.harystolho.pe.linkedList.IndexLinkedList.Node;
import com.harystolho.thread.FileUpdaterThread;
import com.harystolho.thread.RenderThread;
import com.harystolho.utils.PEUtils;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;

/**
 * This class manages the canvas, it holds a reference to the file that is being
 * shown in the canvas, it also draws the text, the cursor and the background
 * line
 * 
 * @author Harystolho
 *
 */
public class CanvasManager {

	// Higher numbers mean higher delay
	public static final int CURSOR_DELAY = 8;
	public static final int TAB_SIZE = 35;

	private Canvas canvas;

	private GraphicsContext gc;

	private File currentFile;

	private int cursorCount = 0;
	private int lineHeight;

	private double drawingDisplacementY = 0;

	private boolean showWhiteSpaces;

	// The canvas begins to draw at this word
	@SuppressWarnings("rawtypes")
	private Node pivotNode;

	public CanvasManager(Canvas canvas) {
		this.canvas = canvas;

		gc = canvas.getGraphicsContext2D();

		updateFontAndLineHeight();

		showWhiteSpaces = false;

		pivotNode = null;

		new CanvasMouseHandler(this);

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

	@SuppressWarnings("rawtypes")
	private void drawWords() {
		if (currentFile != null && pivotNode != null) {
			if (currentFile.getDrawLock().writeLock().tryLock()) {
				// In order not to draw the whole file, the pivotNode keeps a reference to a
				// node some lines above the first visible line. The pivotNode is updated every
				// time setCursorY() is called
				Node node = pivotNode;

				float x = node.getData().getX();
				float y = node.getData().getY();
				while (node != null) {

					Word wordObj = node.getData();

					switch (wordObj.getType()) {
					case NEW_LINE:
						wordObj.setX(x);
						wordObj.setY(y);

						x = 0;
						y += getLineHeight();
						node = node.getRight();
						continue;
					case TAB:
						wordObj.setX(x);
						wordObj.setY(y);

						if (showWhiteSpaces) {
							gc.setFill(StyleLoader.getWhiteSpacesColor());
							gc.fillRect(x + getScrollX(), y - getScrollY() - lineHeight, TAB_SIZE, lineHeight);
						}

						x += TAB_SIZE;
						node = node.getRight();
						continue;
					default:
						break;
					}

					gc.setFill(wordObj.getColor());
					gc.fillText(wordObj.getWordAsString(), x + getScrollX(), y - getScrollY() - drawingDisplacementY);

					wordObj.setX(x);
					wordObj.setY(y);

					x += wordObj.getDrawingSize();

					// Stop drawing if it's out of the canvas
					if (y > canvas.getHeight() + getScrollY()) {
						break;
					}

					node = node.getRight();
				}

				// TODO if a file is being rendered and it's tab is closed it will throw an
				// exception because the canvas manager is going to try to unlock the lock in
				// another file
				currentFile.getDrawLock().writeLock().unlock();
			}

			updateSrollBar();
		}

	}

	private void drawBackgroundLine() {
		gc.setFill(StyleLoader.getBackgroundLineColor());
		gc.fillRect(getScrollX(), getCursorY() - lineHeight - getScrollY(), canvas.getWidth(), getLineHeight());

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
			gc.fillRect(getCursorX() + getScrollX(), getCursorY() - lineHeight - getScrollY(), 2, lineHeight); // 2 is
																												// cursor's
			// width
		}

	}

	/**
	 * Moves the pivot node up or down
	 */
	// TODO fix >= and <=
	// TODO call this only when needed, fix values to improve performance
	public void updatePivotNode() {
		if (pivotNode == null) {
			return;
		}

		if (pivotNode.getData().getY() + (lineHeight * 2) >= getScrollY()) {
			while (pivotNode.getData().getY() + (lineHeight * 4) >= getScrollY()) {
				if (pivotNode.getLeft() != null) {
					pivotNode = pivotNode.getLeft();
				} else {
					break;
				}
			}
		} else if (pivotNode.getData().getY() + (lineHeight * 5) < getScrollY()) {
			while (pivotNode.getData().getY() + (lineHeight * 3) <= getScrollY()) {
				if (pivotNode.getRight() != null) {
					pivotNode = pivotNode.getRight();
				} else {
					break;
				}
			}
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
		PEUtils.getExecutor().execute(new RenderThread());
	}

	public void stopRenderThread() {
		RenderThread.stop();
	}

	public void resetPivotNode() {
		pivotNode = null;
	}

	public File getCurrentFile() {
		return currentFile;
	}

	public void setCurrentFile(File currentFile) {
		if (currentFile != null) {
			this.currentFile = currentFile;
			pivotNode = currentFile.getWords().getFirstNode(); // Starts drawing at the first word
			preRender();
		}
	}

	/**
	 * When a file is loaded all the word's positions are wrong, this method fixes
	 * them by calculating their x and y. This method must be called once before
	 * calling {@link #drawWords()}
	 */
	private void preRender() {
		if (currentFile != null) {

			// First word, First line
			float x = 0;
			float y = getLineHeight();

			if (currentFile.getDrawLock().writeLock().tryLock()) {
				ListIterator<Word> i = currentFile.getWords().listIterator();

				// Iterate through all the words fixing their positions
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

					wordObj.setX(x);
					wordObj.setY(y);

					x += wordObj.getDrawingSize();
				}

				currentFile.getDrawLock().writeLock().unlock();
			}
		}
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
		if (getCursorY() <= lineHeight) {
			setScrollY((int) (getCursorY() - (getCursorY() % lineHeight) - lineHeight));
		} else {
			currentFile.setCursorY(getCursorY() - getLineHeight());
			currentFile.setCursorX(getCursorX()); // Moves the cursor to the end of the life if the line above is
													// shorter.

			if (getCursorY() < getScrollY() + lineHeight) { // If cursor is at the first line in the canvas
				if (getScrollY() % lineHeight == 0) { // If the scroll is aligned to the line
					scrollUp();
				} else { // Else align the scroll
					setScrollY((int) (getCursorY() - (getCursorY() % lineHeight) - lineHeight));
				}
			}
		}
	}

	public void lineDown() {
		currentFile.setCursorY(getCursorY() + getLineHeight());
		currentFile.setCursorX(getCursorX()); // Moves the cursor to the end of the life if the line below is shorter.

		// TODO fix scroll alignment
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

	public void moveCursorToBeginningOfTheLine() {
		if (currentFile != null) {
			currentFile.setCursorX(0);
			resetCursorCount();
		}
	}

	public void moveCursorToEndOfTheLine() {
		if (currentFile != null) {
			setCursorX(-1 - getScrollX());
			resetCursorCount();
		}
	}

	public void moveCursorToFirstLine() {
		setScrollY(0);
		setCursorY(lineHeight);
		moveCursorToBeginningOfTheLine();
	}

	public void moveCursorToLastLine() {
		if (FileUpdaterThread.getBiggestY() > canvas.getHeight()) {
			currentFile.setScrollY((int) (FileUpdaterThread.getBiggestY() - canvas.getHeight()));
		}

		currentFile.setCursorY(FileUpdaterThread.getBiggestY());
		moveCursorToEndOfTheLine();
	}

	public void scrollLeft() {
		/*
		 * if (getScrollX() >= SCROLL_CHANGE) { setScrollX(getScrollX() -
		 * SCROLL_CHANGE); }
		 */
		// TODO scroll left
	}

	public void scrollRight() {

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
				if (y >= 0) {
					currentFile.setScrollY(y);
				} else {
					currentFile.setScrollY(0);
				}
			} else {
				if (y <= FileUpdaterThread.getBiggestY() - canvas.getHeight()) {
					currentFile.setScrollY(y);
				} else {
					currentFile.setScrollY((int) (FileUpdaterThread.getBiggestY() - canvas.getHeight()));
				}
			}

			updatePivotNode();
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

	public boolean showWhiteSpaces() {
		return showWhiteSpaces;
	}

	public void toggleShowWhiteSpaces() {
		if (showWhiteSpaces) {
			showWhiteSpaces = false;
		} else {
			showWhiteSpaces = true;
		}
	}

	public void updateFontAndLineHeight() {
		gc.setFont(StyleLoader.getFont());
		setLineHeight((int) StyleLoader.getFontSize() + 3);
		drawingDisplacementY = StyleLoader.getFontSize() * 0.235;
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