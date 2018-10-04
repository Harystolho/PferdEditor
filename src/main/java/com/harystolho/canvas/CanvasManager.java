package com.harystolho.canvas;

import java.util.Collections;
import java.util.List;
import java.util.ListIterator;

import com.harystolho.PEApplication;
import com.harystolho.canvas.SelectionManager.SELECTION_DIRECTION;
import com.harystolho.canvas.eventHandler.CanvasMouseHandler;
import com.harystolho.misc.BeforeUsing;
import com.harystolho.misc.Rectangle;
import com.harystolho.misc.StyleLoader;
import com.harystolho.pe.File;
import com.harystolho.pe.Word;
import com.harystolho.pe.linkedList.IndexLinkedList.Node;
import com.harystolho.thread.FileUpdaterThread;
import com.harystolho.thread.RenderThread;

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

	private static CanvasManager instance;

	// Higher numbers mean higher delay
	public static final int CURSOR_DELAY = 8;
	public static final int TAB_SIZE = 35;

	private static Canvas canvas;

	private GraphicsContext gc;

	private File currentFile;

	private int cursorCount = 0;
	private int lineHeight;

	private double drawingDisplacementY = 0;

	private boolean showWhiteSpaces;
	private boolean showSelection;

	// The canvas begins to draw at this word
	@SuppressWarnings("rawtypes")
	private Node pivotNode;

	private CanvasManager() {
		instance = this;

		gc = canvas.getGraphicsContext2D();

		updateFontAndLineHeight();

		showWhiteSpaces = false;

		pivotNode = null;

		new CanvasMouseHandler(this);

	}

	/**
	 * Call {@link #setCanvas(Canvas)} once before calling this method
	 * 
	 * @return
	 */
	public static CanvasManager getInstance() {
		if (canvas == null) {
			throw new NullPointerException("canvas object is null. Call setCanvas() before using this method");
		}

		if (instance == null) {
			new CanvasManager();
		}
		return instance;
	}

	/**
	 * This method must be called once before calling {@link #getInstance()}
	 * 
	 * @param canvas
	 */
	@BeforeUsing
	public static void setCanvas(Canvas canvas) {
		CanvasManager.canvas = canvas;
	}

	/**
	 * Don't call this method when testing because the canvas can't be focused.
	 */
	public void update() {
		if (canvas.isFocused()) {
			clear();
			draw();
		}
	}

	public void clear() {
		gc.setFill(StyleLoader.getBgColor());
		gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
	}

	public void makeCanvasTransparent() {
		gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
	}

	public void draw() {
		drawBackgroundLineOrSelection();
		drawWords();
		drawCursor();
	}

	@SuppressWarnings("rawtypes")
	private void drawWords() {
		if (currentFile != null && pivotNode != null) {
			if (currentFile.getDrawLock().readLock().tryLock()) {
				// In order not to draw the whole file, the pivotNode keeps a reference to a
				// node some lines above the first visible line. The pivotNode is updated every
				// time setCursorY() is called
				Node node = pivotNode;

				// Biggest X position in the file
				// Used to calculate the horizontal scroll bar's width
				float biggestX = 0;

				float x = node.getData().getX() >= 0 ? node.getData().getX() : 0;
				float y = node.getData().getY();
				while (node != null) {

					Word word = node.getData();

					switch (word.getType()) {
					case NEW_LINE:
						word.setX(x);
						word.setY(y);

						if (x > biggestX) {
							biggestX = x;
						}

						x = 0;
						y += getLineHeight();
						node = node.getRight();
						continue;
					case TAB:
						word.setX(x);
						word.setY(y);

						if (showWhiteSpaces) {
							gc.setFill(StyleLoader.getWhiteSpacesColor());
							gc.fillRect(x - getScrollX(), y - getScrollY() - lineHeight, TAB_SIZE, lineHeight);
						}

						x += TAB_SIZE;

						if (x > biggestX) {
							biggestX = x;
						}

						node = node.getRight();
						continue;
					default:
						break;
					}

					gc.setFill(word.getColor());
					gc.fillText(word.getWordAsString(), x - getScrollX(), y - getScrollY() - drawingDisplacementY);

					word.setX(x);
					word.setY(y);

					x += word.getDrawingSize();

					if (x > biggestX) {
						biggestX = x;
					}

					// Stop drawing if it's out of the canvas
					if (y > canvas.getHeight() + getScrollY()) {
						break;
					}

					node = node.getRight();
				}

				FileUpdaterThread.setBiggestX(biggestX);

				currentFile.getDrawLock().readLock().unlock();
			}

			updateSrollBar();
		}

	}

	private void drawBackgroundLineOrSelection() {
		if (isShowingSelection()) {
			drawSelection();

			// If selection is more than 1 line, don't draw background line
			SELECTION_DIRECTION selDir = SelectionManager.getInstance().getSelectionDirection();
			if (selDir != SELECTION_DIRECTION.SIDEWAYS_LEFT || selDir != SELECTION_DIRECTION.SIDEWAYS_RIGHT) {
				return;
			}
		}

		// Draw Background Line
		gc.setFill(StyleLoader.getBackgroundLineColor());
		gc.fillRect(0, getCursorY() - lineHeight - getScrollY(), canvas.getWidth(), getLineHeight());
	}

	private void drawSelection() {
		Rectangle[] bounds = SelectionManager.getInstance().getSelectionBounds();

		gc.setFill(StyleLoader.getSelectionColor());

		for (Rectangle r : bounds) {
			// If the rectangle's width is the same as the canvas's width, it means that the
			// selection spans the whole line. Because a line is sometimes bigger than
			// the canvas's width(that's when you can scroll) it needs to update the
			// rectangle size
			if (r.width == canvas.getWidth()) {
				if (FileUpdaterThread.getBiggestX() > canvas.getWidth()) {
					r.width = FileUpdaterThread.getBiggestX();
				}
			}

			gc.fillRect(r.x - getScrollX(), r.y - lineHeight - getScrollY(), r.width, r.height);
		}

	}

	private void drawCursor() {
		if (cursorCount <= -CURSOR_DELAY) {
			cursorCount = CURSOR_DELAY;
		}

		cursorCount--;

		if (cursorCount > 0) {
			gc.setFill(StyleLoader.getCursorColor());
			// 2 is the cursor's width
			gc.fillRect(getCursorX() - getScrollX(), getCursorY() - lineHeight - getScrollY(), 2, lineHeight);
		}

	}

	/**
	 * Moves the pivot node backward or forward.
	 */
	public void updatePivotNode() {
		if (pivotNode == null) {
			return;
		}

		if (pivotNode.getData().getY() + (lineHeight * 2) >= getScrollY()) { // Move pivot left
			while (pivotNode.getData().getY() + (lineHeight * 4) >= getScrollY()) {
				if (pivotNode.getLeft() != null) {
					pivotNode = pivotNode.getLeft();
				} else {
					break;
				}
			}
		} else if (pivotNode.getData().getY() + (lineHeight * 5) < getScrollY()) { // Move pivot right
			while (pivotNode.getData().getY() + (lineHeight * 3) <= getScrollY()) {
				if (pivotNode.getRight() != null) {
					pivotNode = pivotNode.getRight();
				} else {
					break;
				}
			}
		}
	}

	public void movePivotNodeLeft() {
		if (pivotNode.getLeft() != null) {
			pivotNode = pivotNode.getLeft();
		}
	}

	public void movePivotNodeRight() {
		if (pivotNode.getRight() != null) {
			pivotNode = pivotNode.getRight();
		}
	}

	private void updateSrollBar() {
		if (PEApplication.getInstance().getMainController() != null) {
			PEApplication.getInstance().getMainController().updateScrollBar(FileUpdaterThread.getBiggestX(),
					FileUpdaterThread.getBiggestY());
		}
	}

	public void initRenderThread() {
		new RenderThread();
		RenderThread.instance.start();
	}

	public void stopRenderThread() {
		if (RenderThread.instance != null) {
			RenderThread.instance.stop();
		}
	}

	public void resetPivotNode() {
		pivotNode = null;
	}

	@SuppressWarnings("rawtypes")
	public void setPivotNode(Node node) {
		pivotNode = node;
	}

	public File getCurrentFile() {
		return currentFile;
	}

	public void setCurrentFile(File currentFile) {
		if (currentFile != null) {
			this.currentFile = currentFile;
			pivotNode = currentFile.getWords().getFirstNode(); // Starts drawing at the first word
			showSelection(false);

			if (!currentFile.wasPreRendered()) {
				preRender();
			}

			canvas.requestFocus();
		}
	}

	/**
	 * When a file is loaded all the word's positions are wrong, this method fixes
	 * them by calculating their x and y. This method must be called once before
	 * calling {@link #drawWords()}
	 */
	public void preRender() {
		if (currentFile != null) {

			// First word, First line
			float x = 0;
			float y = getLineHeight();

			currentFile.getDrawLock().readLock().lock();

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

			currentFile.getDrawLock().readLock().unlock();

			currentFile.setPreRendered(true);
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

			SelectionManager.getInstance().setLastX(getCursorX());
		}
	}

	public void setCursorY(float cursorY) {
		if (currentFile != null) {
			cursorY += lineHeight - 1 + getScrollY(); // Centralize on cursor
			currentFile.setCursorY(cursorY - (cursorY % lineHeight));

			SelectionManager.getInstance().setLastY(getCursorY());
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
			currentFile.setCursorX(getCursorX()); // Moves the cursor to the end of the line if the line above is
													// shorter.

			if (getCursorY() < getScrollY() + lineHeight) { // If cursor is at the first line in the canvas
				if (getScrollY() % lineHeight == 0) { // If the scroll is aligned to the line
					scrollUp();
				} else { // Else align the scroll
					setScrollY((int) (getCursorY() - (getCursorY() % lineHeight) - lineHeight));
				}
			} /*
				 * else if (getCursorY() + getLineHeight() == getScrollY() + canvas.getHeight())
				 * { // If the cursor is at the last line in the canvas }
				 */
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

	public void moveCursorToBeginningOfTheLine() {
		if (currentFile != null) {
			currentFile.setCursorX(0);
			resetCursorCount();
		}
	}

	public void moveCursorToEndOfTheLine() {
		if (currentFile != null) {
			// TODO IMPL scrollX if end of line is bigger than screen size
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

			if (x < 0) {
				currentFile.setScrollX(0);
			} else {
				if (x <= FileUpdaterThread.getBiggestX() - canvas.getWidth()) {
					currentFile.setScrollX(x);
				}
			}
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

	public boolean isShowingSelection() {
		return showSelection;
	}

	public void showSelection(boolean showSelection) {
		this.showSelection = showSelection;

		if (!showSelection) {
			SelectionManager.getInstance().reset();
		}

	}

	public void selectWholeFile() {
		SelectionManager sm = SelectionManager.getInstance();

		sm.setInitX(0);
		sm.setInitY(getLineHeight());

		Word lastWord = currentFile.getWords().findLastWordIn(FileUpdaterThread.getBiggestY());
		sm.setLastX(lastWord.getX() + lastWord.getDrawingSize());
		sm.setLastY(FileUpdaterThread.getBiggestY());

		showSelection(true);
	}

	public List<Word> getWordsInsideSelectionBound() {
		if (currentFile != null) {
			return currentFile.getWordsInsideSelectionBound();
		} else {
			return Collections.emptyList();
		}
	}

	/**
	 * If the text color or size has been changed, it must updates all the words
	 * color and size
	 */
	public void reRenderWords() {

		if (currentFile != null) {
			currentFile.getDrawLock().readLock().lock();

			for (Word word : currentFile.getWords()) {
				word.updateDrawingColor();
				word.updateDrawingSize();
			}

			currentFile.getDrawLock().readLock().unlock();
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