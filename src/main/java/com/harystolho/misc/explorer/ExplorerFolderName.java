package com.harystolho.misc.explorer;

import com.harystolho.controllers.FolderRightClickController;
import com.harystolho.misc.PropertiesWindowFactory;
import com.harystolho.misc.PropertiesWindowFactory.window_type;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;

/**
 * Similar to {@link ExplorerFile} but instead of naming a file it names a
 * directory
 * 
 * @author Harystolho
 *
 */
public class ExplorerFolderName extends ExplorerFile {

	ExplorerFolder parent;

	private ImageView img;

	private boolean isOpened;

	public ExplorerFolderName(String name, ExplorerFolder parent) {
		super(name);

		this.parent = parent;
		this.isOpened = true;

		img = (ImageView) getChildren().get(0);
		img.setImage(new Image(ClassLoader.getSystemResource("icons/common_folder_open.png").toExternalForm()));
	}

	@Override
	protected void eventHandler() {
		setOnMouseClicked((e) -> {
			PropertiesWindowFactory.removeOpenWindow();

			if (e.getButton() == MouseButton.PRIMARY) {
				if (e.getClickCount() >= 2) { // Double click
					if (isOpened) {
						showOpenIcon(false);
						getParentFolder().showFile(false);
					} else {
						showOpenIcon(true);
						getParentFolder().showFile(true);
					}
				}
			} else if (e.getButton() == MouseButton.SECONDARY) {
				PropertiesWindowFactory.open(window_type.FOLDER, e.getSceneX(), e.getSceneY(), (c) -> {
					FolderRightClickController controller = (FolderRightClickController) c;
					controller.setFolder(parent);
				});
			}
		});
	}

	private void showOpenIcon(boolean show) {
		if (show) {
			isOpened = true;
			img.setImage(new Image(ClassLoader.getSystemResource("icons/common_folder_open.png").toExternalForm()));
		} else {
			isOpened = false;
			img.setImage(new Image(ClassLoader.getSystemResource("icons/common_folder_closed.png").toExternalForm()));
		}
	}

	public ExplorerFolder getParentFolder() {
		return parent;
	}

	public void setFolderParent(ExplorerFolder parent) {
		this.parent = parent;
	}

	public boolean isOpened() {
		return isOpened;
	}

}
