package com.harystolho.canvas;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;

import com.harystolho.misc.StyleLoader;

import javafx.scene.paint.Color;

public class StyleLoaderTest {

	@Test
	public void assertLoadWasCorrect() {
		assertNotNull(StyleLoader.getBackgroundLineColor());
		assertNotNull(StyleLoader.getBgColor());
		assertNotNull(StyleLoader.getCursorColor());
		assertNotNull(StyleLoader.getTextColor());
	}
	
	@Test
	public void setCursorColor() {
		Color c = Color.CYAN;
		StyleLoader.setCursorColor(c);
		assertEquals(StyleLoader.getCursorColor(), c);
	}
	
}
