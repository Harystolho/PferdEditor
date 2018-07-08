package com.harystolho.canvas;

import com.harystolho.canvas.eventHandler.CMKeyEventHandler;
import com.harystolho.canvas.eventHandler.CMMouseEventHandler;
import com.harystolho.utils.RenderThread;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class CanvasManager {

	private Canvas canvas;

	private GraphicsContext gc;

	private File currentFile;

	private double cursorX;
	private double cursorY;

	private int cursorCount;

	private Thread renderThread;

	private CMMouseEventHandler mouseHandler;
	private CMKeyEventHandler keyHandler;

	public CanvasManager(Canvas canvas) {
		this.canvas = canvas;

		gc = canvas.getGraphicsContext2D();

		cursorCount = 0;

		renderThread = new Thread(new RenderThread());

		mouseHandler = new CMMouseEventHandler(this);

	}

	public void update() {

		// Clears the canvas.
		gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());

		gc.setFill(Color.WHITE);
		gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());

		draw();
	}

	public void draw() {

		drawCursor();

		if (currentFile != null) {

		}

	}

	private void drawCursor() {

		if (cursorCount == -15) {
			cursorCount = 15;
		}

		cursorCount--;

		if (cursorCount > 0) {

			gc.setFill(Color.BLACK);

			gc.strokeLine(cursorX, cursorY, cursorX, cursorY + 13);
		}

	}

	public void initRenderThread() {
		if (!renderThread.isAlive()) {
			renderThread.start();
		}
	}

	public void stopRenderThread() {
		if (renderThread.isAlive()) {
			renderThread.interrupt();
		}
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

	public double getCursorX() {
		return cursorX;
	}

	public double getCursorY() {
		return cursorY;
	}

	public void setCursorX(double d) {
		this.cursorX = d;
	}

	public void setCursorY(double cursorY) {
		this.cursorY = cursorY;
	}

}
