package com.harystolho.utils;

import com.harystolho.Main;
import com.harystolho.pe.File;

/**
 * A class that runs over the whole file every {@value #THREAD_INTERVAL}
 * milliseconds to calculate information about the file that's to expensive to
 * calculate when drawing it
 * 
 * @author Harystolho
 *
 */
public class FileUpdaterThread implements Runnable {

	private static final int THREAD_INTERVAL = 2 * 1000;
	private static volatile boolean running = false;

	private static int biggestX = 0;
	private static int biggestY = 0;

	@Override
	public void run() {

		while (running) {

			if (Main.getApplication().getMainController() != null) {
				if (Main.getApplication().getMainController().getCanvasManager() != null) {
					if (Main.getApplication().getCanvasManager().getCurrentFile() != null) {
						calculate(Main.getApplication().getCanvasManager().getCurrentFile());
					}
				}
			}

			try {
				Thread.sleep(THREAD_INTERVAL);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

	}

	private void calculate(File file) {
		
	}

	public static void setRunning(boolean b) {
		if (!running) {
			running = true;
			PEUtils.getExecutor().submit(new FileUpdaterThread());
		}

		running = b;
	}

	public static void stop() {
		running = false;
	}

	public static int getBiggestX() {
		return biggestX;
	}

	public static int getBiggestY() {
		return biggestY;
	}

}
