package com.harystolho;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import com.harystolho.pe.Word;
import com.harystolho.pe.Word.TYPES;

public class WordTest {

	Word word;

	@Before
	public void init() {
		word = new Word('t');
		word.addChar('e');
		word.addChar('m');
		word.addChar('p');
	}

	@Test
	public void defaultWord() {
		Word w = new Word(TYPES.NORMAL);

		assertEquals(w.getSize(), 0);
		assertNull(w.getWordAsString());
	}

	@Test
	public void testGetSize() {
		assertEquals(4, word.getSize());
	}

	@Test
	public void removeCharAndGetSize() {
		Word w = new Word('t');
		w.addChar('e');

		assertEquals(2, w.getSize());

		w.removeLastChar();

		assertEquals(1, w.getSize());

		w.removeLastChar();

		assertEquals(0, w.getSize());

	}

	@Test
	public void removeEmptyChar() {
		Word w = new Word(TYPES.NORMAL);

		w.removeLastChar();

		assertEquals(0, w.getSize());
	}

	@Test
	public void testRemoveChar() {
		Word w = new Word('t');
		w.addChar('e');

		assertEquals(2, w.getSize());

		w.removeLastChar();

		assertEquals(1, w.getSize());

		assertEquals(w.getWordAsString(), "t");

	}

	@Test
	public void testGetWordAsString() {
		Word w = new Word('t');
		w.addChar('e');
		w.addChar('m');
		w.addChar('p');

		assertEquals(w.getWordAsString(), "temp");

		w.removeLastChar();
		w.removeLastChar();

		assertEquals(w.getWordAsString(), "te");

	}

	@Test
	public void testGrowArraySize() {
		Word w = new Word(TYPES.NEW_LINE);

		for (int x = 0; x < 100; x++) {
			w.addChar((char) x);
		}

		assertEquals(100, w.getSize());

		assertTrue(w.getWord().length > 100);

	}

	@Test
	public void testDefaultType() {
		assertEquals(word.getType(), TYPES.NORMAL);
	}

	@Test
	public void testNewLineType() {
		Word w = new Word(TYPES.NEW_LINE);
		assertEquals(w.getType(), TYPES.NEW_LINE);
	}

}
