package com.harystolho;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;

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
		assertEquals(w.getDrawingSize(), 0, 0);
		assertNull(w.getWordAsString());
	}

	@Test
	public void wordConstructorWithChar() {
		Word w = new Word('f');
		assertEquals("f", w.getWordAsString());
	}

	@Test
	public void wordConstructorWithCharAndType() {
		Word w = new Word('d', TYPES.NORMAL);

		assertEquals("d", w.getWordAsString());
		assertEquals(TYPES.NORMAL, w.getType());
	}

	@Test
	public void testGetWord() {
		Word w = new Word('t');

		w.addChar('e');
		w.addChar('m');
		w.addChar('p');

		// w.getWord() returns {'t', 'e', 'm', 'p', '', '', '', '', '', ''}
		assertFalse(Arrays.equals(new char[] { 't', 'e', 'm', 'p' }, w.getWord()));

		char[] c = new char[10];

		c[0] = 't';
		c[1] = 'e';
		c[2] = 'm';
		c[3] = 'p';

		assertTrue(Arrays.equals(c, w.getWord()));

		assertEquals(10, w.getWord().length);
	}

	@Test
	public void testGetSize() {
		assertEquals(4, word.getSize());
	}

	@Test
	public void testGetWordLength() {
		Word w = new Word(TYPES.NORMAL);

		w.addChar('0');
		w.addChar('1');
		w.addChar('2');
		w.addChar('3');
		w.addChar('4');
		w.addChar('5');
		w.addChar('6');
		w.addChar('7');
		w.addChar('8');
		w.addChar('9');

		assertEquals(10, w.getWord().length);

		w.addChar('0');

		assertEquals(10 * 1.5, w.getWord().length, 0);

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

		w.removeLastChar();
		w.removeLastChar();

		assertEquals(w.getWordAsString(), "");
	}

	@Test
	public void testGetWordAsStringNull() {
		Word w = new Word(TYPES.SPACE);
		assertNull(w.getWordAsString());
	}

	@Test
	public void testAddChar() {
		Word w = new Word(TYPES.NORMAL);

		w.addChar('0');
		w.addChar('1');
		w.addChar('2');
		w.addChar('3');
		w.addChar('4');
		w.addChar('5');
		w.addChar('6');
		w.addChar('7');
		w.addChar('8');
		w.addChar('9');

		char[] c = new char[10];

		c[0] = '0';
		c[1] = '1';
		c[2] = '2';
		c[3] = '3';
		c[4] = '4';
		c[5] = '5';
		c[6] = '6';
		c[7] = '7';
		c[8] = '8';
		c[9] = '9';

		assertTrue(Arrays.equals(c, w.getWord()));
		assertEquals("0123456789", w.getWordAsString());
	}

	@Test
	public void testResizeWordArray() {
		Word w = new Word(TYPES.NEW_LINE);

		for (int x = 0; x < 100; x++) {
			w.addChar((char) x);
		}

		assertEquals(100, w.getSize());

		assertTrue(w.getWord().length >= 100);

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
