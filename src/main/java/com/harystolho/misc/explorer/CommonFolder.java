package com.harystolho.misc.explorer;

import java.io.File;
import java.util.List;

import com.harystolho.PEApplication;

import javafx.geometry.Insets;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

public class CommonFolder extends VBox {

	private File diskFile;
	private boolean isOpened;

	public CommonFolder(File diskFile) {
		this.diskFile = diskFile;
		this.isOpened = false;

		CommonFolderFile folder = new CommonFolderFile(diskFile.getName());
		getChildren().add(folder);

		eventHandler();
	}

	private void eventHandler() {
		// Update the width to draw the correct shadow around the file name
		setOnMouseEntered((e) -> {
			setPrefWidth(PEApplication.getInstance().getMainController().getLeftPaneWidth());
		});

	}

	public void add(Pane file) {
		file.setPadding(new Insets(1, 0, 1, 10));

		getChildren().add(file);
	}

	public void remove(Pane file) {
		getChildren().remove(file);
	}

	@SuppressWarnings("unchecked")
	public List<Pane> getFiles() {
		List<? extends Object> list = getChildren();
		return (List<Pane>) list;
	}

	public File getDiskFile() {
		return diskFile;
	}

}
