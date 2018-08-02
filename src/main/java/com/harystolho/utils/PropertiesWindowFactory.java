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

	public static void get(window_type type, double x, double y, Consumer<Object> controller) {

		removeOpenWindow();

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

		p.setLayoutX(x + 4); // Open it a little to the right
		p.setLayoutY(y + 1); // Open it a little down

		((Pane) Main.getApplication().getWindow().getScene().getRoot()).getChildren().add(p);
		lastNode = p;
	}

	public static void removeOpenWindow() {
		if (lastNode != null) {
			((Pane) Main.getApplication().getWindow().getScene().getRoot()).getChildren().remove(lastNode);
		}
	}

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
