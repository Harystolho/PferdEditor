package com.harystolho.thread;

import java.util.logging.Logger;

import com.harystolho.Main;

public class RenderThread implements Runnable {

	private static final int FPS = 15;
	public static volatile boolean running = false;
	private static final Logger logger = Logger.getLogger(RenderThread.class.getName());

	@Override
	public void run() {

		logger.info("Started Render Thread");
		
		long initialTime = System.nanoTime();
		final double timeF = 1000000000 / FPS;
		double deltaF = 0;

		while (running) {

			long currentTime = System.nanoTime();
			deltaF += (currentTime - initialTime) / timeF;
			initialTime = currentTime;

			if (deltaF >= 1) {
				Main.getApplication().getMainController().getCanvasManager().update();
				deltaF--;
			}

		}

		Main.getApplication().getMainController().getCanvasManager().makeCanvasTransparent();

	}

	public static boolean isRunning() {
		return running;
	}

	public static void stop() {
		logger.info("Stopping Render Thread");
		running = false;
	}

}
