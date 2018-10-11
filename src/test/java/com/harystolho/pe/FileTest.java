package com.harystolho.pe;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.harystolho.canvas.CanvasManager;
import com.harystolho.pe.Word.TYPES;
import com.harystolho.utils.PEUtils;

import javafx.embed.swing.JFXPanel;
import javafx.scene.canvas.Canvas;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

public class FileTest {

	@BeforeClass
	public static void init() {

		new JFXPanel();
		PEUtils.start();

		Canvas canvas = new Canvas(1280, 720);
		CanvasManager.setCanvas(canvas);
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

		assertEquals(f.getScrollX(), 0, 1);
		assertEquals(f.getScrollY(), 0, 1);

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
		CanvasManager.getInstance().setCurrentFile(f);

		typeSpace(f);

		assertEquals(f.getWords().getLast().getType(), TYPES.SPACE);
	}

	@Test
	public void createSpaceBeforeAnotherWord() {

		CanvasManager cm = CanvasManager.getInstance();

		File f = new File("f");
		cm.setCurrentFile(f);

		f.type('w');
		f.type('o');
		f.type('r');
		f.type('d');
		cm.update();
		cm.setCursorX(0);

		typeSpace(f);

		assertTrue(f.getWords().get(0).getWordAsString().equals(" "));
		assertTrue(f.getWords().get(1).getWordAsString().equals("word"));
	}

	@Test
	public void createSpaceAfterAnotherWord() {
		CanvasManager cm = CanvasManager.getInstance();

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
		CanvasManager cm = CanvasManager.getInstance();

		File f = new File("f3");
		cm.setCurrentFile(f);
		cm.setCursorY(0);

		f.type(new KeyEvent(null, null, null, null, "w", KeyCode.UNDEFINED, false, false, false, false));
		f.type(new KeyEvent(null, null, null, null, "o", KeyCode.UNDEFINED, false, false, false, false));
		f.type(new KeyEvent(null, null, null, null, "r", KeyCode.UNDEFINED, false, false, false, false));
		f.type(new KeyEvent(null, null, null, null, "d", KeyCode.UNDEFINED, false, false, false, false));
		cm.draw();

		cm.setCursorX(Word.computeCharWidth('w'));

		typeSpace(f);

		assertTrue(f.getWords().get(0).getWordAsString().equals("w"));
		assertTrue(f.getWords().get(1).getWordAsString().equals(" "));
		assertTrue(f.getWords().get(2).getWordAsString().equals("ord"));
	}

	@Test
	public void deleteSpace_And_TypeSpaceAgain() {
		File f = new File("fDelete");
		CanvasManager cm = CanvasManager.getInstance();
		cm.setCurrentFile(f);

		typeStringToFile(f, "newlife");

		assertEquals(f.getWords().size(), 1);

		cm.setCursorX(0);
		assertEquals(cm.getCursorX(), 0, 0);

		cm.moveCursorRight();
		cm.moveCursorRight();
		cm.moveCursorRight();

		typeSpace(f);

		assertEquals(f.getWords().size(), 3);

		f.removeCharBeforeCursor();
		cm.draw();

		assertEquals(f.getWords().size(), 2);

		typeSpace(f);

		assertEquals(f.getWords().size(), 3);
	}

	@Test
	public void typeSpaceBetweenTwoOneCharWords() {
		File f = new File("fOneChar");
		CanvasManager cm = CanvasManager.getInstance();
		cm.setCurrentFile(f);

		typeStringToFile(f, "1");
		typeSpace(f);
		typeStringToFile(f, "2");

		cm.moveCursorLeft();

		// Delete space between words
		typeDelete(f);

		assertEquals(f.getWords().size(), 2);

		assertEquals(f.getCursorX(), f.getWords().get(1).getX(), 0);

		typeSpace(f);

		assertEquals(TYPES.SPACE, f.getWords().get(1).getType());
		assertEquals("2", f.getWords().get(2).getWordAsString());
	}

	@Test
	public void typeTabBeforeWord() {

	}

	@Test
	public void typeTabInsideWord() {

	}

	@Test
	public void typeTabAfterWord() {

	}

	@Test
	public void typeCharInsideWord() {
		File f = new File("fcharInTheMiddle");
		CanvasManager cm = CanvasManager.getInstance();
		cm.setCurrentFile(f);

		typeStringToFile(f, "middle");

		cm.setCursorX(0);
		cm.moveCursorRight();

		typeStringToFile(f, "1");

		assertEquals(f.getWords().get(0).getWordAsString(), "m1iddle");

		cm.moveCursorRight();
		cm.moveCursorRight();

		typeStringToFile(f, "2");

		assertEquals(f.getWords().get(0).getWordAsString(), "m1id2dle");

		cm.moveCursorRight();
		cm.moveCursorRight();
		cm.moveCursorRight();

		typeStringToFile(f, "3");

		assertEquals(f.getWords().get(0).getWordAsString(), "m1id2dle3");
	}

	@Test
	public void typeCharBeforeFirstWord() {
		File f = new File("fCharBefore");
		CanvasManager cm = CanvasManager.getInstance();
		cm.setCurrentFile(f);

		typeStringToFile(f, "before");

		cm.setCursorX(0);

		typeStringToFile(f, "-");

		assertEquals(f.getWords().get(0).getWordAsString(), "-");
		assertEquals(f.getWords().get(1).getWordAsString(), "before");
	}

	@Test
	public void typeCharAtTheBegginnigOfAWord() {
		File f = new File("fCharBefore");
		CanvasManager cm = CanvasManager.getInstance();
		cm.setCurrentFile(f);

		typeStringToFile(f, "fWord");
		typeSpace(f);
		typeStringToFile(f, "sWord");

		assertEquals(f.getWords().get(0).getWordAsString(), "fWord");
		assertEquals(f.getWords().get(1).getWordAsString(), " ");
		assertEquals(f.getWords().get(2).getWordAsString(), "sWord");

		cm.setCursorX(0);
		cm.moveCursorRight();
		cm.moveCursorRight();
		cm.moveCursorRight();
		cm.moveCursorRight();
		cm.moveCursorRight();
		cm.moveCursorRight(); // Cursor before sWord

		typeStringToFile(f, "+");

		assertEquals(f.getWords().get(2).getWordAsString(), "+");
		assertEquals(f.getWords().get(3).getWordAsString(), "sWord");
	}

	@Test
	public void typeCharAtTheBegginnigOfAWord2() {
		File f = new File("fCharBefore");
		CanvasManager cm = CanvasManager.getInstance();
		cm.setCurrentFile(f);

		typeStringToFile(f, "a");
		typeSpace(f);
		typeStringToFile(f, "b");
		typeSpace(f);
		typeStringToFile(f, "c");
		typeSpace(f);

		cm.setCursorX(0);
		cm.moveCursorRight();
		cm.moveCursorRight();

		typeStringToFile(f, "d");

		assertEquals(f.getWords().get(0).getWordAsString(), "a");
		assertEquals(f.getWords().get(1).getWordAsString(), " ");
		assertEquals(f.getWords().get(2).getWordAsString(), "d");
		assertEquals(f.getWords().get(3).getWordAsString(), "b");
	}

	@Test
	public void typeCharBeforeSpace() {
		File f = new File("fCharBeforeSpace");
		CanvasManager cm = CanvasManager.getInstance();
		cm.setCurrentFile(f);

		typeStringToFile(f, "my");
		typeSpace(f);
		typeStringToFile(f, "life");

		cm.setCursorX(0);
		cm.moveCursorRight();
		cm.moveCursorRight();

		typeStringToFile(f, "-");

		assertEquals(f.getWords().get(0).getWordAsString(), "my-");
		assertEquals(f.getWords().get(1).getWordAsString(), " ");
		assertEquals(f.getWords().get(2).getWordAsString(), "life");

	}

	@Test
	public void moveCursorRightAtTheEndOfLine() {
		File f = new File("fCharBeforeSpace");
		CanvasManager cm = CanvasManager.getInstance();
		cm.setCurrentFile(f);

		typeStringToFile(f, "line1");
		typeEnter(f);
		typeStringToFile(f, "line2");

		cm.setCursorX(-1);
		cm.setCursorY(0);

		f.getWords().printDebug();

		assertEquals(f.getWordAtCursor().getType(), TYPES.NEW_LINE);

		cm.moveCursorRight();

		assertEquals(f.getWordAtCursor().getWordAsString(), "line2");
	}

	public void moveCursorLeftAtTheBeginningOfLine() {
		File f = new File("fCharBeforeSpace");
		CanvasManager cm = CanvasManager.getInstance();
		cm.setCurrentFile(f);

		typeStringToFile(f, "line1");
		typeEnter(f);
		typeStringToFile(f, "line2");

		cm.setCursorX(0);

		assertEquals(f.getWordAtCursor().getWordAsString(), "line2");

		cm.moveCursorLeft();

		assertEquals(f.getWordAtCursor().getType(), TYPES.NEW_LINE);
	}

	private void typeStringToFile(File file, String string) {
		for (char c : string.toCharArray()) {
			file.type(new KeyEvent(null, null, null, null, String.valueOf(c), KeyCode.UNDEFINED, false, false, false,
					false));
			CanvasManager.getInstance().draw();
		}
	}

	private void typeSpace(File file) {
		file.type(new KeyEvent(null, null, null, null, null, KeyCode.SPACE, false, false, false, false));
		CanvasManager.getInstance().draw();
	}

	private void typeEnter(File file) {
		file.type(new KeyEvent(null, null, null, null, null, KeyCode.ENTER, false, false, false, false));
		CanvasManager.getInstance().draw();
	}

	private void typeDelete(File file) {
		file.type(new KeyEvent(null, null, null, null, null, KeyCode.BACK_SPACE, false, false, false, false));
		CanvasManager.getInstance().draw();
	}

	@AfterClass
	public static void exit() {
		PEUtils.exit();
	}

}
