package com.harystolho;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import com.harystolho.pe.File;
import com.harystolho.pe.Word;

public class FileTest {

	File file;

	@Before
	public void init() {
		file = new File("temp_0");
	}

	@Test
	public void testName() {
		assertEquals("temp_0", file.getName());
	}

	@Test
	public void notNullLock() {
		assertNotNull(file.getDrawLock());
	}

	@Test
	public void ensureEmpty() {

		File f = new File("temp_1");

		assertEquals(f.getWords().size(), 0);

	}

	@Test
	public void checkSize() {

		Word w1 = new Word("123");
		Word w2 = new Word("abc");
		Word w3 = new Word("def");
		Word w4 = new Word("321");

		File f = new File("temp_2");

		f.addWord(w1);
		f.addWord(w2);
		f.addWord(w3);
		f.addWord(w4);

		assertEquals(f.getWords().size(), 4);
	}

	@Test
	public void checkIfListContainsWord() {

		Word w1 = new Word("123");

		File f = new File("temp_3");

		f.addWord(w1);

		assertTrue(f.getWords().contains(w1));

	}

	@Test
	public void checkIfListContainsWordAfterItIsRemoved() {

		Word w1 = new Word("123");

		File f = new File("temp_3");

		f.addWord(w1);

		f.removeWord(w1);

		assertFalse(f.getWords().contains(w1));

	}

	@Test
	public void testCursorPosition() {
		assertEquals(0, file.getCursorX(), 0);
		assertEquals(0, file.getCursorY(), 0);
	}

	@Test
	public void setCursor() {
		file.setCursorX(15);

		assertEquals(15, file.getCursorX(), 0);
	}

}
