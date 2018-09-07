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

		file.getDrawLock().readLock().lock();

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

		file.getDrawLock().readLock().unlock();
	}

	public static int getBiggestX() {
		return biggestX;
	}

	public static void incrementBiggestXBy(int increment) {
		System.out.println(biggestX);
		biggestX += increment;
	}

	public static void decrementBiggestXBy(int increment) {
		biggestX -= increment;
	}

	public static int getBiggestY() {
		return biggestY;
	}

	public static void incrementBiggestYBy(int increment) {
		biggestY += increment;
	}

	public static void decrementBiggestYBy(int increment) {
		biggestY -= increment;
	}

}
