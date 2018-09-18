package com.harystolho.misc.explorer;

import com.harystolho.misc.PropertiesWindowFactory;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;

public class CommonFolderFile extends CommonFile {

	public CommonFolderFile(String name) {
		super(name);

		ImageView img = (ImageView) getChildren().get(0);
		img.setImage(new Image(ClassLoader.getSystemResource("icons/common_folder.png").toExternalForm()));
	}

	@Override
	protected void eventHandler() {
		setOnMouseClicked((e) -> {
			PropertiesWindowFactory.removeOpenWindow();

			if (e.getButton() == MouseButton.PRIMARY) {
				if (e.getClickCount() == 2) { // Double click
					
				}
			}
		});
	}

}
