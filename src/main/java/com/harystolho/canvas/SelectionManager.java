package com.harystolho.canvas;

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
			// TODO IMPL upward selection
		} else if (lastY == initY) { // Selection is only 1 line
			bound1.x = initX;
			bound1.y = initY;
			bound1.width = lastX - initX;
			bound1.height = cm.getLineHeight();
		}

		return bounds;
	}

}
