package com.harystolho.utils;

import java.util.function.Consumer;

import com.harystolho.Main;

import javafx.scene.Parent;
import javafx.scene.layout.Pane;

public class PropertiesWindowFactory {

	public static enum window_type {
		FILE
	}

	private static Parent lastNode;

	/**
	 * Loads a node and displays it the cursor's position
	 * 
	 * @param type       the window's type
	 * @param x          window's x inside the scene
	 * @param y          window's y inside the scene
	 * @param controller the controller object that belongs to that scene
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

		default:
			return;
		}

		p.setLayoutX(x + 4); // Opens it a little to the right
		p.setLayoutY(y + 1); // Opens it a little down

		((Pane) Main.getApplication().getWindow().getScene().getRoot()).getChildren().add(p);
		lastNode = p;
	}

	/**
	 * Closes the opened window if there is one
	 */
	public static void removeOpenWindow() {
		if (lastNode != null) {
			((Pane) Main.getApplication().getWindow().getScene().getRoot()).getChildren().remove(lastNode);
		}
	}

	/**
	 * Closes the opened window if there cursor in not inside it
	 * 
	 * @param x cursor's x
	 * @param y cursor's y
	 */
	public static void removeOpenWindow(double x, double y) {
		if (lastNode != null) {
			if (x > lastNode.getLayoutX() && x < lastNode.getLayoutX() + lastNode.prefWidth(0)
					&& y > lastNode.getLayoutY() && y < lastNode.getLayoutY() + lastNode.prefHeight(0)) {

			} else {
				removeOpenWindow();
			}

		}
	}

}
