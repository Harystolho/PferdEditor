package com.harystolho.utils;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.logging.Logger;

import javax.swing.text.html.StyleSheet;

/**
 * A StyleSheet wrapper to load CSS files. WARNING: It doesn't work with JavaFX
 * rules.
 * 
 * @author Harystolho
 *
 */
public class PEStyleSheet {

	private static final Logger logger = Logger.getLogger(PEStyleSheet.class.getName());

	private StyleSheet styleSheet;

	// Rules cache
	private Map<String, Map<String, String>> rules;

	public PEStyleSheet(String file) {
		rules = new HashMap<>();

		loadStyleSheet(file);

		loadCssRulesCache();

	}

	/**
	 * Loads a CSS file.
	 * 
	 * @param fileName the name of the file to be loaded. (Eg: style.css);
	 */
	public void loadStyleSheet(String fileName) {
		try {
			styleSheet = new StyleSheet();

			styleSheet.loadRules(new InputStreamReader(ClassLoader.getSystemResourceAsStream(fileName), "UTF-8"), null);

		} catch (IOException | NullPointerException e) {
			logger.severe("Couldn't load stylesheet from file=" + fileName);
		}
	}

	/**
	 * Returns all CSS rules belonging to this <code>selector</code>
	 * 
	 * @param selector
	 * @return
	 */
	public Map<String, String> getRules(String selector) {
		return rules.get(selector);
	}

	/**
	 * Returns a CSS rule
	 * 
	 * @param selector
	 * @param rule
	 * @return
	 */
	public String getRule(String selector, String rule) {
		return getRules(selector).get(rule);
	}

	/**
	 * Maps CSS rules to {@link #rules rules map}
	 */
	private void loadCssRulesCache() {

		// TODO fix rgb in css

		Enumeration<?> cssSelectors = styleSheet.getStyleNames();

		if (cssSelectors != null) {

			// Skip default element
			cssSelectors.nextElement();

			while (cssSelectors.hasMoreElements()) {

				String selector = (String) cssSelectors.nextElement();

				rules.put(selector, loadRules(selector));

			}
		}

	}

	private Map<String, String> loadRules(String selector) {
		String cssSelector = styleSheet.getStyle(selector).toString();

		try {
			// Remove useless part of the string.
			cssSelector = cssSelector.substring(cssSelector.indexOf(",") + 1, cssSelector.length() - 2);

		} catch (StringIndexOutOfBoundsException e) {
			// If the selector has no rules.
			return new HashMap<>();
		}

		String[] cssRules = cssSelector.split(",");

		Map<String, String> rules = new HashMap<>();

		for (String rule : cssRules) {
			String[] r = rule.split("=");
			rules.put(r[0], r[1]);
		}

		return rules;

	}

}
