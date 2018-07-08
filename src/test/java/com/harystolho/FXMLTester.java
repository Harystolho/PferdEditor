package com.harystolho;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.junit.Test;

import com.harystolho.utils.PEUtils;

public class FXMLTester {

	@Test
	public void checkMainFXMLFile() {
		assertNotNull(ClassLoader.getSystemResource("main.fxml"));
	}

	@Test
	public void loadMainFXMLFile() {
		assertNotNull(PEUtils.loadFXML("main.fxml"));
	}

	@Test
	public void loadNonExistentFXMLFile() {
		assertNull(PEUtils.loadFXML("thisFileDoesntExist.fxml"));
	}

}
