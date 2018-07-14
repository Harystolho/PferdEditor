package com.harystolho.canvas;

import com.harystolho.utils.PEStyleSheet;

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

	private static PEStyleSheet peStyleSheet;

	private static Font defaultFont = new Font("Arial", 14);

	private static Color lineColor;
	private static Color bgColor;
	private static Color textColor;
	private static Color cursorColor;

	static {

		peStyleSheet = new PEStyleSheet("file.css");

		loadColors();
	}

	private static void loadColors() {
		lineColor = Color.rgb(179, 179, 179, 0.44);
		bgColor = Color.web(peStyleSheet.getRule("#background", "background-color"));
		textColor = Color.web(peStyleSheet.getRule("#text", "color"));
		cursorColor = Color.rgb(255, 255, 255, 0.8);
	}

	public static void setFont(Font font) {
		StyleLoader.defaultFont = font;
	}

	public static Font getFont() {
		return defaultFont;
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

}
