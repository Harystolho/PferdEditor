package com.harystolho.utils;

import java.util.ListIterator;

import com.harystolho.Main;
import com.harystolho.canvas.CanvasManager;
import com.harystolho.canvas.StyleLoader;
import com.harystolho.pe.File;
import com.harystolho.pe.Word;

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

	public static void calculate(File file) {

		int lineHeight = Main.getApplication().getCanvasManager().getLineHeight();

		synchronized (file.getDrawLock()) {
			ListIterator<Word> i = file.getWords().listIterator();

			int biggestX = 0;
			int biggestY = lineHeight;

			while (i.hasNext()) {
				Word wordObj = i.next();

				switch (wordObj.getType()) {
				case NEW_LINE:
					biggestY += lineHeight;
					continue;
				default:
					break;
				}

				if (wordObj.getX() > biggestX) {
					biggestX = (int) (wordObj.getX() + wordObj.getDrawingSize());
				}
			}

			FileUpdaterThread.biggestX = biggestX;
			FileUpdaterThread.biggestY = biggestY;

		}

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
