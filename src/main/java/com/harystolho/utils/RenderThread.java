package com.harystolho.utils;

import com.harystolho.Main;

public class RenderThread implements Runnable {

	private static final int FPS = 15;
	public static volatile boolean running = false;

	@Override
	public void run() {

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

		Main.getApplication().getMainController().getCanvasManager().clear();

	}

	public static boolean isRunning() {
		return running;
	}

	public static void stop() {
		running = false;
	}

}
