package com.harystolho.canvas;

import java.lang.Thread.State;

import com.harystolho.canvas.eventHandler.CMKeyEventHandler;
import com.harystolho.canvas.eventHandler.CMMouseEventHandler;
import com.harystolho.utils.PEUtils;
import com.harystolho.utils.RenderThread;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class CanvasManager {

	public static final int CURSOR_DELAY = 15;

	private Canvas canvas;

	private GraphicsContext gc;

	private File currentFile;

	private int cursorCount;

	private CMMouseEventHandler mouseHandler;
	private CMKeyEventHandler keyHandler;

	public CanvasManager(Canvas canvas) {
		this.canvas = canvas;

		gc = canvas.getGraphicsContext2D();

		cursorCount = 0;

		mouseHandler = new CMMouseEventHandler(this);

	}

	public void update() {

		clear();

		gc.setFill(Color.WHITE);
		gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());

		draw();
	}

	public void draw() {

		drawCursor();

		if (currentFile != null) {

		}

	}

	public void clear() {
		gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
	}

	private void drawCursor() {

		if (cursorCount == -CURSOR_DELAY) {
			cursorCount = CURSOR_DELAY;
		}

		cursorCount--;

		if (cursorCount > 0) {

			gc.setFill(Color.BLACK);

			// 13 is font size
			gc.strokeLine(getCursorX(), getCursorY(), getCursorX(), getCursorY() + 13);
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
		currentFile.setCursorX(d);
	}

	public void setCursorY(double cursorY) {
		currentFile.setCursorY(cursorY);
	}

}
