package com.harystolho.misc;

import com.harystolho.PEApplication;
import com.harystolho.canvas.CanvasManager;
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

	private static final String defaultFontName = "Inconsolata";

	private static Font defaultFont;

	private static Color lineColor;
	private static Color bgColor;
	private static Color textColor;
	private static Color cursorColor;
	private static Color whiteSpacesColor;
	private static double fontSize;

	static {
		loadColors();
	}

	private static void loadColors() {
		lineColor = Color.web(PEConfiguration.getProperty("LINE_COLOR"), 0.4);
		bgColor = Color.web(PEConfiguration.getProperty("CANVAS_BACKGROUND_COLOR"));
		textColor = Color.web(PEConfiguration.getProperty("TEXT_COLOR"));
		cursorColor = Color.web(PEConfiguration.getProperty("CURSOR_COLOR"));
		whiteSpacesColor = Color.web(PEConfiguration.getProperty("WHITESPACE_COLOR"), 0.7);
		fontSize = Double.valueOf(PEConfiguration.getProperty("FONT_SIZE"));
		createDefaultFonts();
	}

	private static void createDefaultFonts() {
		defaultFont = new Font(defaultFontName, fontSize);
	}

	public static void setFont(Font font) {
		StyleLoader.defaultFont = font;
	}

	public static Font getFont() {
		return defaultFont;
	}

	public static void setFontSize(double size) {
		defaultFont = new Font(defaultFontName, size);
		PEConfiguration.setProperty("FONT_SIZE", String.valueOf(size));

		if (PEApplication.getInstance().getMainController() != null) {
			if (CanvasManager.getInstance() != null) {
				CanvasManager.getInstance().updateFontAndLineHeight();
			}
		}

	}

	public static double getFontSize() {
		return defaultFont.getSize();
	}

	public static Color getBackgroundLineColor() {
		return lineColor;
	}

	public static void setLineColor(Color lineColor) {
		StyleLoader.lineColor = lineColor;
		PEConfiguration.setProperty("LINE_COLOR", getColorHexFromString(lineColor));
	}

	public static Color getBgColor() {
		return bgColor;
	}

	public static void setBgColor(Color bgColor) {
		StyleLoader.bgColor = bgColor;
		PEConfiguration.setProperty("CANVAS_BACKGROUND_COLOR", getColorHexFromString(bgColor));
	}

	public static Color getTextColor() {
		return textColor;
	}

	public static void setTextColor(Color textColor) {
		StyleLoader.textColor = textColor;
		PEConfiguration.setProperty("TEXT_COLOR", getColorHexFromString(textColor));
	}

	public static Color getCursorColor() {
		return cursorColor;
	}

	public static void setCursorColor(Color cursorColor) {
		StyleLoader.cursorColor = cursorColor;
		PEConfiguration.setProperty("CURSOR_COLOR", getColorHexFromString(cursorColor));
	}

	public static Color getWhiteSpacesColor() {
		return whiteSpacesColor;
	}

	public static void setWhiteSpacesColor(Color whiteSpacesColor) {
		StyleLoader.whiteSpacesColor = whiteSpacesColor;
		PEConfiguration.setProperty("WHITESPACE_COLOR", getColorHexFromString(whiteSpacesColor));
	}

	/**
	 * <code>Color.toString()</code> returns 0x------ff(for exemple 0x000000ff), It
	 * has to remove the first two and the last two chars to save it as a hex color
	 * value
	 * 
	 * @param color
	 * @return
	 */
	public static String getColorHexFromString(Color color) {
		return color.toString().substring(2, color.toString().length() - 2);
	}

}
