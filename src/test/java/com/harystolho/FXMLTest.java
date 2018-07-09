package com.harystolho;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.junit.Before;
import org.junit.Test;

import com.harystolho.utils.PEUtils;

import javafx.embed.swing.JFXPanel;

public class FXMLTest {

	@Before
	public void init() {
		new JFXPanel();
	}

	@Test
	public void checkMainFXMLFile() {
		assertNotNull(ClassLoader.getSystemResource("main.fxml"));
	}

	@Test
	public void loadMainFXMLFile() {
		//assertNotNull(PEUtils.loadFXML("main.fxml"));
	}

	@Test
	public void loadNonExistentFXMLFile() {
		assertNull(PEUtils.loadFXML("thisFileDoesntExist.fxml"));
	}

}
