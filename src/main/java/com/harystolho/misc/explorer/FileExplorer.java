package com.harystolho.misc.explorer;

import com.harystolho.pe.File;

import javafx.geometry.Insets;
import javafx.scene.control.ScrollPane;

public class FileExplorer extends ScrollPane {

	public FileExplorer() {
		super();
		setId("fileExplorer");
		setPadding(new Insets(0, 0, 0, 5));
	}

	public void remove(File file) {
		// TODO Auto-generated method stub
	}

}
