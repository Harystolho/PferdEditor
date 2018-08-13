package com.harystolho.misc;

import com.harystolho.controllers.MainController;
import com.harystolho.pe.File;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;

public class Tab extends HBox {

	private MainController mainController;
	private File file;

	private Label modified;

	public Tab(File file, MainController mc) {
		super();
		mainController = mc;

		this.file = file;

		modified = new Label("*");
		modified.setVisible(false);

		Label fileLabel = new Label(file.getName());
		fileLabel.setOnMouseClicked((e) -> {
			mc.loadFileInCanvas(file);
		});

		Label close = new Label("x");
		close.getStyleClass().add("closeTab");
		close.setPadding(new Insets(0, 0, 0, 7));
		close.setOnMouseClicked((e) -> {
			mc.closeFile(file);
		});

		setAlignment(Pos.CENTER);
		setPadding(new Insets(0, 5, 0, 5));
		getChildren().addAll(modified, fileLabel, close);
	}

	public void setModified(boolean modified) {
		if (modified) {
			this.modified.setVisible(true);
		} else {
			this.modified.setVisible(false);
			file.setWasModified(false);
		}
	}

	@Override
	public Object getUserData() {
		return file;
	}

	public void setSelected(boolean b) {
		if (b) {
			if (!getStyleClass().contains("fileTabItem")) { // If it already contains it, don't add again
				getStyleClass().add("fileTabItem");
			}
		} else {
			getStyleClass().remove("fileTabItem");
		}
	}

}
