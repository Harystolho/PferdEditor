package com.harystolho.canvas;

import java.util.Arrays;

import com.harystolho.misc.Rectangle;

/**
 * Class used to manage the selection area in the canvas
 * 
 * @author Harystolho
 *
 */
public class SelectionManager {

	private static SelectionManager instance;

	private static CanvasManager cm;

	private double lastX;
	private double lastY;

	private SelectionManager() {
		instance = this;
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

	public void setLastX(double x) {
		lastX = x + cm.getScrollX();
	}

	public void setLastY(double y) {
		y += cm.getLineHeight() - 1 + cm.getScrollY(); // Centralize on cursor
		lastY = y - (y % cm.getLineHeight());
	}

	public Rectangle[] getSelectionBounds() {
		double initX = cm.getCursorX();
		double initY = cm.getCursorY();

		Rectangle[] bounds = new Rectangle[3]; // There will be at most 3 rectangles
		Arrays.fill(bounds, new Rectangle());

		Rectangle bound1 = bounds[0];
		Rectangle bound2 = bounds[1];
		Rectangle bound3 = bounds[2];

		if (lastY > initY) {
			bound1.x = initX;
			bound1.y = initY;
			bound1.width = cm.getCanvas().getWidth() - bound1.x;
			bound1.height = cm.getLineHeight();

			bound2.x = 0;
			bound2.y = bound1.y + cm.getLineHeight();

			if (lastY - initY == cm.getLineHeight()) { // 2 line selection
				bound2.width = lastX;
				bound2.height = cm.getLineHeight();
			} else { // More than 2 lines selected
				bound2.width = cm.getCanvas().getWidth();
				bound2.height = lastY - bound2.y;

				bound3.x = 0;
				bound3.y = lastY;
				bound3.width = lastX;
				bound3.height = cm.getLineHeight();
			}

		} else if (lastY < initY) {

		} else if (lastY == initY) {
			bound1.x = initX;
			bound1.y = initY;
			bound1.width = lastX - initX;
			bound1.height = cm.getLineHeight();
		}

		return null;
	}

}
