package com.harystolho.thread;

import java.util.ListIterator;

import com.harystolho.PEApplication;
import com.harystolho.canvas.CanvasManager;
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

	private static float biggestX = 0;
	private static int biggestY = 0;

	@Override
	public void run() {
		if (PEApplication.getInstance().getMainController() != null) {
			if (CanvasManager.getInstance() != null) {
				if (CanvasManager.getInstance().getCurrentFile() != null) {
					calculate(CanvasManager.getInstance().getCurrentFile());
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

		int lineHeight = CanvasManager.getInstance().getLineHeight();

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

			if (wordObj.getX() + wordObj.getDrawingSize() >= biggestX) {
				biggestX = (int) (wordObj.getX() + wordObj.getDrawingSize());
			}
		}

		FileUpdaterThread.biggestX = biggestX;
		FileUpdaterThread.biggestY = biggestY;

		file.getDrawLock().readLock().unlock();
	}

	public static float getBiggestX() {
		return biggestX;
	}

	public static void setBiggestX(float x) {
		biggestX = x;
	}

	public static void incrementBiggestXBy(float increment) {
		biggestX += increment;
		System.out.println("> " + biggestX);
	}

	public static void decrementBiggestXBy(float increment) {
		biggestX -= increment;
		System.out.println("< " + biggestX);
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
