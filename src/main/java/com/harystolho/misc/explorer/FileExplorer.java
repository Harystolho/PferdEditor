package com.harystolho.misc.explorer;

import java.util.ArrayList;
import java.util.List;

import com.harystolho.pe.File;
import com.harystolho.utils.PEUtils;

import javafx.geometry.Insets;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.Pane;

public class FileExplorer extends ScrollPane {

	public FileExplorer() {
		super();
		setId("fileExplorer");
		setPadding(new Insets(0, 0, 0, 5));
	}

	public void remove(File file) {
		// TODO Auto-generated method stub
	}

	public void add(Pane file) {
		if (getContent() != null) {
			CommonFolder cFolder = (CommonFolder) getContent();
			cFolder.add(file);
		} else {
			setContent(file);
		}
	}

	/**
	 * @return a list containing all the files under the save folder
	 * @see {@link PEUtils#getSaveFolder()}
	 */
	public List<File> getFiles() {
		List<File> files = new ArrayList<>();

		CommonFolder root = (CommonFolder) getContent();

		addFilesToList(files, root);

		return files;
	}

	/**
	 * If <code>p</code> is a file add it to the <code>list</code>, if it's a folder
	 * call this method for each file under it.
	 * 
	 * @param list
	 * @param p
	 */
	private void addFilesToList(List<File> list, Pane p) {
		if (p instanceof CommonFile) {
			CommonFile cFile = (CommonFile) p;
			if(!cFile.isDirectory()) {
				list.add(cFile.getFile());	
			}
		} else if (p instanceof CommonFolder) {
			CommonFolder cFolder = (CommonFolder) p;
			for (Pane pp : cFolder.getFiles()) {
				addFilesToList(list, pp);
			}
		}
	}

}
