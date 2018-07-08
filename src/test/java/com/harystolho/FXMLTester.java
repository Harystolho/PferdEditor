package com.harystolho;

import static org.junit.Assert.assertNotNull;

import org.junit.Test;

public class FXMLTester {

	@Test
	public void checkMainFXMLFile() {
		assertNotNull(ClassLoader.getSystemResource("main.fxml"));
	}

}
