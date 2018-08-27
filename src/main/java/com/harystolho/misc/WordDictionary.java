package com.harystolho.misc;

import java.util.HashMap;

import javafx.scene.paint.Color;

/**
 * Class to manage words that have different colors
 * 
 * @author Harystolho
 *
 */
public class WordDictionary {

	/**
	 * A HashMap to store the color associated with a word
	 */
	private static HashMap<String, Color> wordColors;

	static {
		wordColors = new HashMap<>();
		addDefaultColors();
	}

	/**
	 * Adds this color to {@link #wordColors}
	 * 
	 * @param word
	 * @param color
	 */
	public static void addColor(String word, Color color) {
		wordColors.put(word, color);
	}

	/**
	 * Removes this word's color form {@link #wordColors}
	 * 
	 * @param word
	 * @param color
	 */
	public static void removeWordColor(String word) {
		wordColors.remove(word);
	}

	/**
	 * Returns the color for the <code>word</code> specified in the
	 * {@link #wordColors} map or {@link StyleLoader#getTextColor()} if color for
	 * this word isn't defined
	 * 
	 * @param word
	 * @return
	 */
	public static Color getWordColor(String word) {
		return wordColors.getOrDefault(word, StyleLoader.getTextColor());
	}

	private static void addDefaultColors() {
		addColor("###", Color.AQUA);
	}

}
