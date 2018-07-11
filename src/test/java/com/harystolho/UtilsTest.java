package com.harystolho;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.harystolho.utils.PEUtils;

public class UtilsTest {

	@Test
	public void checkVersion() {
		assertEquals(PEUtils.VERSION, "0.2");
	}

}
