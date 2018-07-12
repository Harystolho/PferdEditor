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
		word = new Word('t');
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
