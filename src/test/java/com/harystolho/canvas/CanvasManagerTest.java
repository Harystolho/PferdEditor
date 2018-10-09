package com.harystolho.canvas;

import org.junit.Before;
import org.junit.Test;

import com.harystolho.canvas.CanvasManager;
import com.harystolho.pe.File;
import com.harystolho.utils.PEUtils;

import javafx.embed.swing.JFXPanel;
import javafx.scene.canvas.Canvas;

public class CanvasManagerTest {

	@Before
	public void init() {
		new JFXPanel();
		PEUtils.start();
		
		CanvasManager.setCanvas(new Canvas(500, 200));
	}

	@Test
	public void updateHorizontalScrollBar_If_LineWidthIsBiggerThanCanvas() {
		CanvasManager cm = CanvasManager.getInstance();
		File file = new File("updateHBar");
		
		
		
	}

	@Test
	public void updateHorizontalScrollBar_If_LineWidthIsSmallerThanCanvas() {
		
	}
	
}
