package com.harystolho;

import static org.junit.Assert.assertNotNull;

import org.junit.Before;
import org.junit.Test;

import com.harystolho.utils.PEStyleSheet;

public class PEStyleSheetTest {

	PEStyleSheet pss;

	@Before
	public void init() {
		pss = new PEStyleSheet("file.css");
	}

	@Test
	public void getRules() {
		assertNotNull(pss.getRules("#line"));
	}

	@Test
	public void getRule() {
		assertNotNull(pss.getRule("#line", "background-color"));
	}
	
}
