package com.harystolho.utils;

import static org.junit.Assert.assertNotNull;

import org.junit.Before;
import org.junit.Test;

import com.harystolho.utils.PropertiesWindowFactory.window_type;

import javafx.embed.swing.JFXPanel;
import javafx.scene.layout.Pane;

public class PropertiesWindowFactoryTest {

	@Before
	public void init() {
		new JFXPanel();
	}
	
	@Test
	public void testOpenWindow() {
		
		Pane p = new Pane();
		PropertiesWindowFactory.setMainPane(p);
		
		PropertiesWindowFactory.open(window_type.FILE, 15, 20, (c)->{
			
		});
		
		assertNotNull(PropertiesWindowFactory.getOpenWindow());
	}
	
}
