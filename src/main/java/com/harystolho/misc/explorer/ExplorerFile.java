package com.harystolho.misc.explorer;

import com.harystolho.PEApplication;
import com.harystolho.misc.PropertiesWindowFactory;
import com.harystolho.pe.File;

import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.HBox;

/**
 * A class to represent a disk file in the file explorer
 * 
 * @author Harystolho
 * @see FileExplorer
 */
public class ExplorerFile extends HBox implements FileInterface {

	private File file;

	private Label fileName;

	public ExplorerFile(String name) {
		getStyleClass().add("commonFile");

		ImageView icon = new ImageView(ClassLoader.getSystemResource("icons/common_file.png").toExternalForm());
		icon.setFitWidth(16);
		icon.setFitHeight(16);
		setMargin(icon, new Insets(0, 5, 0, 0));

		fileName = new Label(name);

		getChildren().addAll(icon, fileName);

		eventHandler();
	}

	protected void eventHandler() {
		setOnMouseClicked((mouse) -> {
			PropertiesWindowFactory.removeOpenWindow();

			if (mouse.getButton() == MouseButton.PRIMARY) {
				if (mouse.getClickCount() >= 2) { // Double click
					PEApplication.getInstance().getMainController().loadFileInCanvas(file);
				}
			} else if (mouse.getButton() == MouseButton.SECONDARY) {
				PEApplication.getInstance().getMainController().openFileRightClickWindow(file, mouse.getSceneX(),
						mouse.getSceneY());
			}
		});
	}

	public void setFile(File file) {
		this.file = file;
	}

	public File getFile() {
		return file;
	}

	@Override
	public java.io.File getDiskFile() {
		return file.getDiskFile();
	}

	public void update() {
		fileName.setText(file.getName());
	}

	@Override
	public String getName() {
		return getFile().getName();
	}

}
