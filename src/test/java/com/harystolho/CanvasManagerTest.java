package com.harystolho;

import org.junit.Before;

import com.harystolho.canvas.CanvasManager;

import javafx.embed.swing.JFXPanel;
import javafx.scene.canvas.Canvas;

public class CanvasManagerTest {

	CanvasManager cm;

	@Before
	public void init() {
		new JFXPanel();

		CanvasManager.setCanvas(new Canvas());
		cm = CanvasManager.getInstance();
	}

}
