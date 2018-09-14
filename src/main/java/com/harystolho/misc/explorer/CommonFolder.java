package com.harystolho.misc.explorer;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.harystolho.misc.PropertiesWindowFactory;

import javafx.geometry.Insets;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

public class CommonFolder extends VBox {

	private File diskFile;
	List<Pane> files;

	public CommonFolder(File diskFile) {
		this.diskFile = diskFile;
		files = new ArrayList<>();

		CommonFile folder = new CommonFile(diskFile.getName(), true);
		getChildren().add(folder);

		eventHandler();
	}

	public void add(Pane file) {
		setMargin(file, new Insets(1, 0, 1, 10));
		getChildren().add(file);
	}

	public void remove(Pane file) {

	}

	@SuppressWarnings("unchecked")
	public List<Pane> getFiles() {
		List<? extends Object> list = getChildren();
		return (List<Pane>) list;
	}

	private void eventHandler() {
		setOnMouseClicked((e) -> {
			PropertiesWindowFactory.removeOpenWindow();
			if (e.getButton() == MouseButton.PRIMARY) {
				if (e.getClickCount() == 2) { // Double click

				}
			} else if (e.getButton() == MouseButton.SECONDARY) {

			}

		});
	}

}
