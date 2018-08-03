package com.harystolho.utils;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import com.harystolho.utils.PropertiesWindowFactory.window_type;

import javafx.embed.swing.JFXPanel;
import javafx.scene.layout.Pane;

public class PropertiesWindowFactoryTest {

	@Before
	public void init() {
		new JFXPanel();
		setMainPane();
	}

	@Test
	public void openWindow() {
		PropertiesWindowFactory.open(window_type.FILE, 15, 20, (c) -> {
			assertNotNull(c);
		});

		assertNotNull(PropertiesWindowFactory.getOpenWindow());
	}

	@Test
	public void mainPaneChildren() {
		Pane pane = new Pane();
		PropertiesWindowFactory.setMainPane(pane);
		PropertiesWindowFactory.open(window_type.FILE, 15, 20, (c) -> {
		});

		// It must have at least 1 children
		assertTrue(pane.getChildren().size() >= 1);
	}

	@Test
	public void setMainPane() {
		Pane pane = new Pane();
		PropertiesWindowFactory.setMainPane(pane);
		PropertiesWindowFactory.removeOpenWindow();
	}

	@Test(expected = NullPointerException.class)
	public void setMainPaneNull() {
		// If the main pane is null it must throw an exception
		PropertiesWindowFactory.setMainPane(null);
		PropertiesWindowFactory.removeOpenWindow();
	}

}
