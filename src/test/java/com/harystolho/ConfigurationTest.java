package com.harystolho;

import static org.junit.Assert.assertNotNull;

import org.junit.Before;
import org.junit.Test;

import com.harystolho.utils.PEConfiguration;

public class ConfigurationTest {

	@Before
	public void init() {
		PEConfiguration.loadProperties();
	}

	@Test
	public void checkVersionProp() {
		assertNotNull(PEConfiguration.getProperty("VERSION"));
	}

	@Test
	public void checkLanguageProp() {
		assertNotNull(PEConfiguration.getProperty("LANG"));
	}

}
