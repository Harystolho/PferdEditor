package com.harystolho.canvas.eh;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.harystolho.utils.PEHelper;

import javafx.scene.Scene;
import javafx.scene.layout.VBox;

public class ApplicationKeyHandlerTest {

	@BeforeClass
	public static void init() {
		PEHelper.init();
	}

	private Scene scene;

	@Before
	public void initialize() {
		scene = new Scene(new VBox());
	}

	@Test
	public void pressLeftKeyWhenTextIsSelected() {

	}

}
