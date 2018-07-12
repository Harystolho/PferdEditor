package com.harystolho;

import static org.junit.Assert.assertEquals;
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
		word = new Word("temp");
	}

	@Test
	public void constructorWithString() {
		Word w = new Word("temp_1");

		assertEquals(w.getWordAsString(), "temp_1");
	}

	@Test
	public void constructorWithCharArray() {
		Word w = new Word(new char[] { 't', 'e', 'm', 'p', '_', '2' });

		assertEquals(w.getWordAsString(), "temp_2");
	}

	@Test
	public void getWordWithStringConstructor() {
		Word w = new Word("temp_3");

		assertTrue(Arrays.equals(w.getWord(), "temp_3".toCharArray()));
	}

	@Test
	public void getWordWithCharArrayConstructor() {
		Word w = new Word(new char[] { 't', 'e', 'm', 'p', '_', '4' });

		assertTrue(Arrays.equals(w.getWord(), "temp_4".toCharArray()));
	}

	@Test
	public void testWordAsString() {
		Word w = new Word("temp_5");

		assertTrue(w.getWordAsString().equals("temp_5"));

		w.setWord("temp_6");

		assertTrue(Arrays.equals(w.getWord(), "temp_6".toCharArray()));

		assertTrue(w.getWordAsString().equals("temp_6"));
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
