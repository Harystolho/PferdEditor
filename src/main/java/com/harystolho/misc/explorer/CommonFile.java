package com.harystolho.misc.explorer;

import com.harystolho.PEApplication;
import com.harystolho.misc.PropertiesWindowFactory;
import com.harystolho.pe.File;

import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.HBox;

public class CommonFile extends HBox {

	File file;

	public CommonFile(String name, boolean isDirectory) {
		ImageView icon;

		if (isDirectory) {
			icon = new ImageView(ClassLoader.getSystemResource("icons/common_folder.png").toExternalForm());
		} else {
			icon = new ImageView(ClassLoader.getSystemResource("icons/common_file.png").toExternalForm());
		}

		icon.setFitWidth(16);
		icon.setFitHeight(16);

		setMargin(icon, new Insets(0, 5, 0, 0));

		Label fileName = new Label(name);

		getStyleClass().add("commonFile");

		getChildren().addAll(icon, fileName);

		eventHandler();
	}

	private void eventHandler() {
		setOnMouseClicked((e) -> {
			PropertiesWindowFactory.removeOpenWindow();
			if (e.getButton() == MouseButton.PRIMARY) {
				if (e.getClickCount() == 2) { // Double click
					PEApplication.getInstance().getMainController().loadFileInCanvas(file);
				}
			} else if (e.getButton() == MouseButton.SECONDARY) {
				PEApplication.getInstance().getMainController().openFileRightClickWindow(file, e.getSceneX(),
						e.getSceneY());
			}
		});
	}

	public void setFile(File file) {
		this.file = file;
	}

	public File getFile() {
		return file;
	}

}
