package com.harystolho.misc.dictionary;

import java.util.HashSet;
import java.util.Set;

import com.harystolho.pe.Word;
import com.harystolho.utils.PEUtils;

/**
 * In order not to update the word color every time a key is pressed, the timer
 * waits a bit and updates many words together improving performance. Two
 * scenarios are possible: If the {@link #timer} is NULL it creates and
 * initializes a new {@link Timer}, if it's not not it resets the existing one.
 * 
 * @see Timer#reset()
 * 
 * @author Harystolho
 *
 */
public class WordDictionaryTimer {

	public static final int TIMER_DELAY = 750; // ms

	private static Timer timer;

	private static final Set<Word> words = new HashSet<>();

	public static synchronized void addWord(Word word) {
		words.add(word);
		resetTimer();
	}

	private static synchronized void resetTimer() {
		if (timer == null) {
			timer = new Timer(WordDictionaryTimer::updateWords);
			PEUtils.getExecutor().submit(timer);
		} else {
			timer.reset();
		}
	}

	/**
	 * This method is called by the {@link #timer}
	 */
	private static synchronized void updateWords() {
		for (Word word : words) {
			word.updateDrawingColor();
		}

		words.clear();
		timer = null;
	}

}
