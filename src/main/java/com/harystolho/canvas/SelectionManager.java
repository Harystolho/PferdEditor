package com.harystolho.canvas;

import java.util.Collections;
import java.util.List;

import com.harystolho.misc.Rectangle;
import com.harystolho.pe.Word;
import com.harystolho.pe.Word.TYPES;

/**
 * Class used to manage the selection area in the canvas
 * 
 * @author Harystolho
 *
 */
public class SelectionManager {

	public static enum SELECTION_DIRECTION {
		SIDEWAYS_LEFT, SIDEWAYS_RIGHT, DOWNWARD, UPWARD
	}

	private static SelectionManager instance;

	private static CanvasManager cm;

	private double initX;
	private double initY;

	private double lastX;
	private double lastY;

	private SelectionManager() {
		instance = this;
		initX = 0;
		initY = 0;
		lastX = 0;
		lastY = 0;
	}

	public static SelectionManager getInstance() {
		if (instance == null) {
			cm = CanvasManager.getInstance();
			new SelectionManager();
		}
		return instance;
	}

	public void setInitX(double initX) {
		this.initX = initX;
	}

	public void setInitY(double initY) {
		this.initY = initY;
	}

	public void setLastX(double x) {
		lastX = x;
	}

	public void setLastY(double y) {
		lastY = y;
	}

	public double getInitX() {
		switch (getSelectionDirection()) {
		case UPWARD:
		case SIDEWAYS_LEFT:
			return initX;
		case DOWNWARD:
		case SIDEWAYS_RIGHT:
			return lastX;
		default:
			return 0;
		}
	}

	public double getInitY() {
		switch (getSelectionDirection()) {
		case UPWARD:
		case SIDEWAYS_LEFT:
			return initY;
		case DOWNWARD:
		case SIDEWAYS_RIGHT:
			return lastY;
		default:
			return 0;
		}
	}

	public double getLastX() {
		switch (getSelectionDirection()) {
		case UPWARD:
		case SIDEWAYS_LEFT:
			return lastX;
		case DOWNWARD:
		case SIDEWAYS_RIGHT:
			return initX;
		default:
			return 0;
		}
	}

	public double getLastY() {
		switch (getSelectionDirection()) {
		case UPWARD:
		case SIDEWAYS_LEFT:
			return lastY;
		case DOWNWARD:
		case SIDEWAYS_RIGHT:
			return initY;
		default:
			return 0;
		}
	}

	public void reset() {
		initX = 0;
		initY = 0;
		lastX = 0;
		lastY = 0;
	}

	/**
	 * Calculates the bounds for the current selected area
	 * 
	 * @return
	 */
	public Rectangle[] getSelectionBounds() {
		Rectangle[] bounds = new Rectangle[3]; // There will be at most 3 rectangles
		Rectangle bound1 = new Rectangle();
		Rectangle bound2 = new Rectangle();
		Rectangle bound3 = new Rectangle();

		bounds[0] = bound1;
		bounds[1] = bound2;
		bounds[2] = bound3;

		if (lastY > initY) { // Selection is downward
			bound1.x = initX;
			bound1.y = initY;
			bound1.width = cm.getCanvas().getWidth() - bound1.x;
			bound1.height = cm.getLineHeight();

			bound2.x = 0;
			bound2.y = bound1.y + cm.getLineHeight();

			if (lastY - initY == cm.getLineHeight()) { // 2 lines selection
				bound2.width = lastX;
				bound2.height = cm.getLineHeight();

				bound1.width = cm.getCanvas().getWidth();
			} else { // More than 2 lines selected
				bound2.width = cm.getCanvas().getWidth();
				bound2.height = lastY - bound2.y;

				bound3.x = 0;
				bound3.y = lastY;
				bound3.width = lastX;
				bound3.height = cm.getLineHeight();

				bound1.width = cm.getCanvas().getWidth();
			}
		} else if (lastY < initY) { // Selection is upward
			bound1.x = lastX;
			bound1.y = lastY;
			bound1.width = cm.getCanvas().getWidth() - lastX;
			bound1.height = cm.getLineHeight();

			bound2.x = 0;
			bound2.y = bound1.y + cm.getLineHeight();

			if (initY - lastY == cm.getLineHeight()) { // 2 lines selection
				bound2.width = initX;
				bound2.height = cm.getLineHeight();

				bound1.width = cm.getCanvas().getWidth();
			} else { // More than 2 lines selected
				bound2.width = cm.getCanvas().getWidth();
				bound2.height = initY - bound2.y;

				bound3.x = 0;
				bound3.y = initY;
				bound3.width = initX;
				bound3.height = cm.getLineHeight();

				bound1.width = cm.getCanvas().getWidth();
			}
		} else if (lastY == initY) { // Selection is only 1 line
			if (initX < lastX) { // Selection is towards the left
				bound1.x = initX;
				bound1.y = initY;
				bound1.width = lastX - initX;
				bound1.height = cm.getLineHeight();
			} else { // Selection is towards the right
				bound1.x = lastX;
				bound1.y = lastY;
				bound1.width = initX - lastX;
				bound1.height = cm.getLineHeight();
			}
		}

		return bounds;
	}

	/**
	 * @return A list containing all the words inside the selection bound. If the
	 *         selection starts or ends at the middle of a word it will return that
	 *         word too
	 */
	public List<Word> getWordsInsideSelectionBound() {
		return cm.getCurrentFile().getWords().getWordsFrom(getInitX(), getInitY(), getLastX(), getLastY());
	}

	/**
	 * @return the text
	 */
	public List<Word> getTextInsideBound() {
		List<Word> selectedWords = getWordsInsideSelectionBound();
		// TODO FIX return only chars inside selection bound

		// The method above returns all the words that are inside the selection bound
		// even if the selection only start at the middle of the word.
		// The methods below remove the portion of the word that is not selected

		splitLastWord(selectedWords);
		splitFirstWord(selectedWords);

		return selectedWords;
	}

	private void splitFirstWord(List<Word> selectedWords) {
		Word firstWord = selectedWords.get(0);

		double initX = getInitX();

		if (firstWord.getX() != initX) { // Remove some portion of the first word
			double width = firstWord.getX();
			int idx = 0;

			while (idx < firstWord.getWord().length) {
				if (width == initX) {
					break;
				} else {
					width += Word.computeCharWidth(firstWord.getWord()[idx]);
				}
				idx++;
			}

			String selectedWordPortion = firstWord.getWordAsString().substring(idx);
			Word wordPortion = new Word(TYPES.NORMAL);
			wordPortion.setX(firstWord.getX());
			wordPortion.setY(firstWord.getY());

			for (char c : selectedWordPortion.toCharArray()) {
				wordPortion.addChar(c);
			}

			// Replace first word
			selectedWords.set(0, wordPortion);
		}
	}

	private void splitLastWord(List<Word> selectedWords) {
		Word lastWord = selectedWords.get(selectedWords.size() - 1);

		double lastX = getLastX();

		if (lastWord.getX() + lastWord.getDrawingSize() > lastX) { // Remove some portion of the last word
			double width = lastWord.getX();
			int idx = 0;

			while (idx < lastWord.getWord().length) {
				if (width == lastX) {
					break;
				} else {
					width += Word.computeCharWidth(lastWord.getWord()[idx]);
				}
				idx++;
			}

			String selectedWordPortion = lastWord.getWordAsString().substring(0, idx);

			Word wordPortion = new Word(TYPES.NORMAL);
			wordPortion.setX(lastWord.getX());
			wordPortion.setY(lastWord.getY());

			for (char c : selectedWordPortion.toCharArray()) {
				wordPortion.addChar(c);
			}

			// Replace last word
			selectedWords.set(selectedWords.size() - 1, wordPortion);
		}
	}

	public SELECTION_DIRECTION getSelectionDirection() {
		if (initY < lastY) {
			return SELECTION_DIRECTION.UPWARD;
		} else if (initY == lastY) {
			if (initX < lastX) {
				return SELECTION_DIRECTION.SIDEWAYS_LEFT;
			} else {
				return SELECTION_DIRECTION.SIDEWAYS_RIGHT;
			}
		} else {
			return SELECTION_DIRECTION.DOWNWARD;
		}
	}

}
