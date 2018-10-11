package com.harystolho.thread;

import java.util.ListIterator;

import com.harystolho.PEApplication;
import com.harystolho.canvas.CanvasManager;
import com.harystolho.pe.File;
import com.harystolho.pe.Word;
import com.harystolho.utils.PEUtils;

/**
 * Class that scans through the whole file to calculate information about the
 * file such as biggestX and biggestY
 * 
 * @author Harystolho
 *
 */
public class FileUpdaterThread implements Runnable {

	private static float biggestX = 0;
	private static float biggestY = 0;

	@Override
	public void run() {
		try {
			calculate(CanvasManager.getInstance().getCurrentFile());
		} catch (NullPointerException e) {
			// Do nothing
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
		file.getDrawLock().readLock().lock();

		ListIterator<Word> i = file.getWords().listIterator();

		float biggestX = 0;
		float biggestY = CanvasManager.getInstance().getLineHeight();

		while (i.hasNext()) {
			Word wordObj = i.next();

			switch (wordObj.getType()) {
			case NEW_LINE:
				biggestY += CanvasManager.getInstance().getLineHeight();
				continue;
			default:
				break;
			}

			float wordWidth = wordObj.getX() + wordObj.getDrawingSize();
			if (wordWidth > biggestX) {
				biggestX = wordWidth;
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
	public static float getBiggestY() {
		return biggestY;
	}

	public static void incrementBiggestYBy(float increment) {
		biggestY += increment;
	}

	public static void decrementBiggestYBy(float increment) {
		biggestY -= increment;
	}

}
