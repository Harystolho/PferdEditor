package com.harystolho;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import com.harystolho.canvas.CanvasManager;
import com.harystolho.pe.File;
import com.sun.webkit.graphics.RenderTheme;

import javafx.embed.swing.JFXPanel;
import javafx.scene.canvas.Canvas;

public class CanvasManagerTest {

	CanvasManager cm;

	int width = 100;
	int heigh = 50;

	@Before
	public void init() {
		new JFXPanel();

		Canvas canvas = new Canvas(width, heigh);
		cm = new CanvasManager(canvas);
	}

	@Test
	public void testLineHeight() {
		cm.setLineHeight(25);
		assertEquals(cm.getLineHeight(), 25);
	}

	@Test
	public void testCurrentFile() {

		File file = new File("temp");

		cm.setCurrentFile(file);

		assertEquals(file, cm.getCurrentFile());
	}

}
