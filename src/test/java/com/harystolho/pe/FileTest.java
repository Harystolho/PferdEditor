package com.harystolho.pe;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.harystolho.Main;
import com.harystolho.PEApplication;
import com.harystolho.canvas.CanvasManager;
import com.harystolho.pe.Word.TYPES;
import com.harystolho.utils.PEUtils;

import javafx.embed.swing.JFXPanel;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

public class FileTest {

	@BeforeClass
	public static void init() {

		new JFXPanel();
		PEUtils.start();

		PEApplication app = new PEApplication();
		app.setup();
	}

	@Test
	public void defaultConstructor() {
		File f = new File("name123");

		assertTrue("name123".equals(f.getName()));

		assertNotNull(f.getDrawLock());

		assertNull(f.getDiskFile());
		assertFalse(f.isLoaded());

		assertEquals(f.getCursorX(), 0, 0);
		assertEquals(f.getCursorY(), 0, 0);

		assertEquals(f.getScrollX(), -2);
		assertEquals(f.getScrollY(), 0);

	}

	@Test
	public void setName() {
		String name = "unicorns and monsters";

		File f = new File("abc's and 123's");

		assertFalse(name.equals(f.getName()));

		f.setName(name);
		assertTrue(name.equals(f.getName()));
	}

	@Test
	public void createSpace() {
		File f = new File("f");
		Main.getApplication().getCanvasManager().setCurrentFile(f);

		KeyEvent kv = new KeyEvent(null, null, null, null, null, KeyCode.SPACE, false, false, false, false);

		f.type(kv);

		assertEquals(f.getWords().getLast().getType(), TYPES.SPACE);
	}

	@Test
	public void createSpaceBeforeAnotherWord() {

		CanvasManager cm = Main.getApplication().getCanvasManager();

		File f = new File("f");
		cm.setCurrentFile(f);

		f.type('w');
		f.type('o');
		f.type('r');
		f.type('d');
		cm.update();
		cm.setCursorX(0);

		KeyEvent kv = new KeyEvent(null, null, null, null, null, KeyCode.SPACE, false, false, false, false);
		f.type(kv);

		assertTrue(f.getWords().get(0).getWordAsString().equals(" "));
		assertTrue(f.getWords().get(1).getWordAsString().equals("word"));
	}

	@Test
	public void createSpaceAfterAnotherWord() {
		CanvasManager cm = Main.getApplication().getCanvasManager();

		File f = new File("f2");
		cm.setCurrentFile(f);
		cm.setCursorY(0);

		f.type(new KeyEvent(null, null, null, null, "w", KeyCode.UNDEFINED, false, false, false, false));
		f.type(new KeyEvent(null, null, null, null, "o", KeyCode.UNDEFINED, false, false, false, false));
		f.type(new KeyEvent(null, null, null, null, "r", KeyCode.UNDEFINED, false, false, false, false));
		f.type(new KeyEvent(null, null, null, null, "d", KeyCode.UNDEFINED, false, false, false, false));
		cm.update();

		f.type(new KeyEvent(null, null, null, null, null, KeyCode.SPACE, false, false, false, false));
		cm.update();

		assertTrue(f.getWords().get(0).getWordAsString().equals("word"));
		assertTrue(f.getWords().get(1).getWordAsString().equals(" "));
	}

	@Test
	public void creatSpaceInTheMiddleOfAWord() {
		CanvasManager cm = Main.getApplication().getCanvasManager();

		File f = new File("f3");
		cm.setCurrentFile(f);
		cm.setCursorY(0);

		f.type(new KeyEvent(null, null, null, null, "w", KeyCode.UNDEFINED, false, false, false, false));
		f.type(new KeyEvent(null, null, null, null, "o", KeyCode.UNDEFINED, false, false, false, false));
		f.type(new KeyEvent(null, null, null, null, "r", KeyCode.UNDEFINED, false, false, false, false));
		f.type(new KeyEvent(null, null, null, null, "d", KeyCode.UNDEFINED, false, false, false, false));
		cm.update();

		cm.setCursorX(Word.computeCharWidth('w'));

		f.type(new KeyEvent(null, null, null, null, null, KeyCode.SPACE, false, false, false, false));
		cm.update();

		assertTrue(f.getWords().get(0).getWordAsString().equals("w"));
		assertTrue(f.getWords().get(1).getWordAsString().equals(" "));
		assertTrue(f.getWords().get(2).getWordAsString().equals("ord"));
	}

	@AfterClass
	public static void exit() {
		PEUtils.exit();
	}

}
