package com.harystolho.misc;

import java.util.function.Consumer;

import com.harystolho.utils.PEUtils;

import javafx.scene.Parent;
import javafx.scene.layout.Pane;

/**
 * Class to manage windows inside the application.<br>
 *
 * Call {@link #setMainPane(Parent)} before using it.
 * 
 * @author Harystolho
 *
 */
public class PropertiesWindowFactory {

	// FILE - Window that opens when the user right clicks on a file
	public static enum window_type {
		FILE, CANVAS, FOLDER
	}

	public static Pane mainPane;

	private static Parent openWindow;

	/**
	 * Loads a node and displays it at the cursor's position.
	 * 
	 * @param type       the window's type
	 * @param x          window's x inside the scene
	 * @param y          window's y inside the scene
	 * @param controller the controller object that belongs to that scene
	 * @throws NullPointerException if the {@link #mainPane} is null
	 */
	public static void open(window_type type, double x, double y, Consumer<Object> controller) {

		removeOpenWindow(); // If there's an open window, close it

		Parent p = null;

		switch (type) {
		case FILE:
			p = PEUtils.loadFXML("fileRightClick.fxml", (c) -> {
				controller.accept(c);
			});
			break;
		case CANVAS:
			p = PEUtils.loadFXML("canvasRightClick.fxml", (c) -> {
				controller.accept(c);
			});
			break;
		case FOLDER:
			p = PEUtils.loadFXML("folderRightClick.fxml", (c) -> {
				controller.accept(c);
			});
			break;
		default:
			return;
		}

		p.setLayoutX(x + 4); // Opens it a little to the right
		p.setLayoutY(y + 1); // Opens it a little down

		getMainPane().getChildren().add(p);
		openWindow = p;
	}

	/**
	 * Closes the opened window if there is one
	 */
	public static void removeOpenWindow() {
		if (openWindow != null) {
			getMainPane().getChildren().remove(openWindow);
		}
	}

	/**
	 * Closes the opened window if there cursor in not inside it
	 * 
	 * @param x cursor's x
	 * @param y cursor's y
	 */
	public static void removeOpenWindow(double x, double y) {
		if (openWindow != null) {
			if (x > openWindow.getLayoutX() && x < openWindow.getLayoutX() + openWindow.prefWidth(0)
					&& y > openWindow.getLayoutY() && y < openWindow.getLayoutY() + openWindow.prefHeight(0)) {

			} else {
				removeOpenWindow();
			}

		}
	}

	public static Parent getOpenWindow() {
		return openWindow;
	}

	private static Pane getMainPane() {
		if (mainPane == null) {
			throw new NullPointerException();
		}
		return mainPane;
	}

	@BeforeUsing
	public static void setMainPane(Parent p) {
		mainPane = (Pane) p;
	}

}
