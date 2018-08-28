package com.harystolho.misc;

import com.harystolho.utils.PEConfiguration;

import javafx.scene.paint.Color;
import javafx.scene.text.Font;

/**
 * A class to manager objects related to colors and sizes used when drawing in
 * the canvas.
 * 
 * @author Harystolho
 *
 */
public class StyleLoader {

	private static Font defaultFont = new Font("Arial", 14);

	private static Color lineColor;
	private static Color bgColor;
	private static Color textColor;
	private static Color cursorColor;
	private static Color whiteSpacesColor;

	static {
		loadColors();
	}

	private static void loadColors() {
		lineColor = Color.web(PEConfiguration.getProperty("LINE_COLOR"), 0.4);
		bgColor = Color.web(PEConfiguration.getProperty("CANVAS_BACKGROUND_COLOR"));
		textColor = Color.web(PEConfiguration.getProperty("TEXT_COLOR"));
		cursorColor = Color.web(PEConfiguration.getProperty("CURSOR_COLOR"));
		whiteSpacesColor = Color.web(PEConfiguration.getProperty("WHITESPACE_COLOR"), 0.7);
	}

	public static void setFont(Font font) {
		StyleLoader.defaultFont = font;
	}

	public static Font getFont() {
		return defaultFont;
	}

	public static double getFontSize() {
		return defaultFont.getSize();
	}

	public static Color getBackgroundLineColor() {
		return lineColor;
	}

	public static void setLineColor(Color lineColor) {
		StyleLoader.lineColor = lineColor;
	}

	public static Color getBgColor() {
		return bgColor;
	}

	public static void setBgColor(Color bgColor) {
		StyleLoader.bgColor = bgColor;
	}

	public static Color getTextColor() {
		return textColor;
	}

	public static void setTextColor(Color textColor) {
		StyleLoader.textColor = textColor;
	}

	public static Color getCursorColor() {
		return cursorColor;
	}

	public static void setCursorColor(Color cursorColor) {
		StyleLoader.cursorColor = cursorColor;
	}

	public static Color getWhiteSpacesColor() {
		return whiteSpacesColor;
	}

	public static void setWhiteSpacesColor(Color whiteSpacesColor) {
		StyleLoader.whiteSpacesColor = whiteSpacesColor;
	}

}
