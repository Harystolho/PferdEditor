package com.harystolho.thread;

import java.util.logging.Logger;

import com.harystolho.canvas.CanvasManager;

import javafx.animation.AnimationTimer;

public class RenderThread extends AnimationTimer {

	private static final Logger logger = Logger.getLogger(RenderThread.class.getName());

	private static final int FPS = 20; // Even though it's 20 here, it only displays around 15 FPS
	private static final double timeF = 1e9 / FPS;
	private static long previousTime = 0;
	private static long previousFPS = 0;
	private static long fpsCount = 0;

	public static RenderThread instance;

	public RenderThread() {
		instance = this;
	}

	@Override
	public void handle(long now) {
		if (previousTime == 0) {
			previousTime = now;
			previousFPS = now;
			return;
		}

		double elepsed = now - previousTime;

		if (elepsed > timeF) {
			CanvasManager.getInstance().update();

			/*
			 * if (previousFPS + 1e9 < now) { fpsCount = 0; previousFPS = now; } else {
			 * fpsCount++; }
			 */

			previousTime = now;
		}
	}

	@Override
	public void stop() {
		logger.info("Stopping Render Thread");
		super.stop();

		instance = null;
		previousTime = 0;
		previousFPS = 0;
		CanvasManager.getInstance().makeCanvasTransparent();
	}

}
