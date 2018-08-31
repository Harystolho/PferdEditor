package com.harystolho.thread;

import java.util.logging.Logger;

import com.harystolho.Main;

import javafx.animation.AnimationTimer;

public class RenderThread2 extends AnimationTimer {

	private static final Logger logger = Logger.getLogger(RenderThread2.class.getName());

	private static final int FPS = 15;
	private static final double timeF = 1000000000 / FPS;
	private static long previousTime = 0;

	@Override
	public void handle(long now) {
		if (previousTime == 0) {
			previousTime = now;
			return;
		}

		double elepsed = now - previousTime;
		// double elepsed = (now - previousTime) / timeF;

		// if (elepsed > 1) {
		if (elepsed > FPS) {
			Main.getApplication().getMainController().getCanvasManager().update();
		}

		previousTime = now;
	}

	@Override
	public void stop() {
		logger.info("Stopping Render Thread");
		super.stop();

		previousTime = 0;
		Main.getApplication().getMainController().getCanvasManager().makeCanvasTransparent();
	}

}
