package com.harystolho.misc;

import com.harystolho.Main;
import com.harystolho.controllers.MainController;
import com.harystolho.pe.File;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;

/**
 * Class that behaves similarly to a tab
 * 
 * @author Harystolho
 *
 */
public class Tab extends HBox {

	private static MainController mainController = Main.getApplication().getMainController();
	private File file;

	private Label modified;

	public Tab(File file) {
		super();

		this.file = file;

		modified = new Label("*");
		modified.setVisible(false);

		Label fileLabel = new Label(file.getName());
		fileLabel.setOnMouseClicked((e) -> {
			mainController.loadFileInCanvas(file);
		});

		Label close = new Label("x");
		close.getStyleClass().add("closeTab");
		close.setPadding(new Insets(0, 0, 0, 7));
		close.setOnMouseClicked((e) -> {
			mainController.closeFile(file);
		});

		setAlignment(Pos.CENTER);
		setPadding(new Insets(0, 5, 0, 5));
		getChildren().addAll(modified, fileLabel, close);
	}

	/**
	 * If <code>true</code> will show the '*' before the file other
	 * 
	 * @param modified
	 */
	public void setModified(boolean modified) {
		if (modified) {
			this.modified.setVisible(true);
		} else {
			this.modified.setVisible(false);
			file.setWasModified(false);
		}
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

	@Override
	public Object getUserData() {
		return file;
	}

}
