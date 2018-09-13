package com.harystolho;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Before;
import org.junit.Test;

import com.harystolho.utils.PEUtils;

import javafx.embed.swing.JFXPanel;
import javafx.scene.Parent;

public class UtilsTest {

	@Before
	public void init() {
		new JFXPanel();
		PEUtils.start();
	}

	@Test
	public void checkVersion() {
		assertEquals(PEUtils.VERSION, "0.7");
	}

	@Test(expected = IllegalStateException.class)
	public void loadInvalidFXML() {
		PEUtils.loadFXML("dwdawddwad.fxml", (c) -> {
		});
	}

	@Test
	public void loadFXML() {
		Parent p = PEUtils.loadFXML("main.fxml", (controller) -> {
			assertNotNull(controller);
		});

		assertNotNull(p);

	}

}
