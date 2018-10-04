package com.harystolho.misc.dictionary;

public class Timer implements Runnable {

	private volatile boolean resetTimer;

	private Runnable function;

	public Timer(Runnable function) {
		this.function = function;
	}

	@Override
	public void run() {
		try {
			resetTimer = false;
			// Sleeps for some time
			Thread.sleep(WordDictionaryTimer.TIMER_DELAY);

			// If the timer was reset, call this again
			if (resetTimer) {
				run();
			} else { // Else run the function
				function.run();
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public void reset() {
		resetTimer = true;
	}

}
