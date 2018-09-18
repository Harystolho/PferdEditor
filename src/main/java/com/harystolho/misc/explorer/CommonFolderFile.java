package com.harystolho.misc.explorer;

import com.harystolho.misc.PropertiesWindowFactory;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;

public class CommonFolderFile extends CommonFile {

	CommonFolder parent;

	private boolean isOpened;

	public CommonFolderFile(String name, CommonFolder parent) {
		super(name);

		this.parent = parent;
		this.isOpened = true;

		ImageView img = (ImageView) getChildren().get(0);
		img.setImage(new Image(ClassLoader.getSystemResource("icons/common_folder.png").toExternalForm()));
	}

	@Override
	protected void eventHandler() {
		setOnMouseClicked((e) -> {
			PropertiesWindowFactory.removeOpenWindow();

			if (e.getButton() == MouseButton.PRIMARY) {
				if (e.getClickCount() == 2) { // Double click
					if (isOpened) {
						isOpened = false;
						getFolderParent().showFile(false);
					} else {
						isOpened = true;
						getFolderParent().showFile(true);
					}
				}
			}
		});
	}

	public CommonFolder getFolderParent() {
		return parent;
	}

	public void setFolderParent(CommonFolder parent) {
		this.parent = parent;
	}

}
