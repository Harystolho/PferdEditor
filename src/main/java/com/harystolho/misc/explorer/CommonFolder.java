package com.harystolho.misc.explorer;

import java.io.File;
import java.util.List;

import javafx.geometry.Insets;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

public class CommonFolder extends VBox {

	private File diskFile;

	public CommonFolder(File diskFile) {
		this.diskFile = diskFile;

		CommonFile folder = new CommonFile(diskFile.getName(), true);
		getChildren().add(folder);

		// 219 is the File Explorer's width
		setPrefWidth(219);
	}

	public void add(Pane file) {
		file.setPadding(new Insets(1, 0, 1, 10));

		getChildren().add(file);
	}

	public void remove(Pane file) {

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
