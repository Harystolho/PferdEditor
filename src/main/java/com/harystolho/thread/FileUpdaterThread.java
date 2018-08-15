package com.harystolho.thread;

import java.util.ListIterator;

import com.harystolho.Main;
import com.harystolho.pe.File;
import com.harystolho.pe.Word;
import com.harystolho.utils.PEUtils;

/**
 * A class that scans through the whole file to calculate information about the
 * file such as biggestX and biggestY
 * 
 * @author Harystolho
 *
 */
public class FileUpdaterThread implements Runnable {

	private static int biggestX = 0;
	private static int biggestY = 0;

	@Override
	public void run() {

		/*
		 * private static final int THREAD_INTERVAL = 2 * 1000; while (running) {
		 * 
		 * if (Main.getApplication().getMainController() != null) { if
		 * (Main.getApplication().getMainController().getCanvasManager() != null) { if
		 * (Main.getApplication().getCanvasManager().getCurrentFile() != null) {
		 * calculate(Main.getApplication().getCanvasManager().getCurrentFile()); } } }
		 * 
		 * try { Thread.sleep(THREAD_INTERVAL); } catch (InterruptedException e) {
		 * e.printStackTrace(); } }
		 */

		if (Main.getApplication().getMainController() != null) {
			if (Main.getApplication().getMainController().getCanvasManager() != null) {
				if (Main.getApplication().getCanvasManager().getCurrentFile() != null) {
					calculate(Main.getApplication().getCanvasManager().getCurrentFile());
				}
			}
		}

	}

	public static void calculate() {
		PEUtils.getExecutor().submit(new FileUpdaterThread());
	}

	/**
	 * Calculates information about the file
	 * 
	 * @param file
	 */
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
			
			System.out.println("$#$");
		}
		
	}

	public static int getBiggestX() {
		return biggestX;
	}

	public static void incrementBiggestYBy(int increment) {
		biggestY += increment;
	}

	public static void decrementBiggestYBy(int increment) {
		biggestY -= increment;
	}

	public static int getBiggestY() {
		return biggestY;
	}

}
