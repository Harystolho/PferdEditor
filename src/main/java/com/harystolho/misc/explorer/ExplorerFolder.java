package com.harystolho.misc.explorer;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import com.harystolho.PEApplication;

import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

/**
 * A class to represent a disk folder in the file explorer
 * 
 * @author Harystolho
 * @see FileExplorer
 */
public class ExplorerFolder extends VBox implements FileInterface {

	private File diskFile;
	private List<Node> hiddenNodes; // If the folder is closed this list contains all the folders

	public ExplorerFolder(File diskFile) {
		this.diskFile = diskFile;

		hiddenNodes = new ArrayList<>();

		ExplorerFolderName folder = new ExplorerFolderName(diskFile.getName(), this);
		getChildren().add(folder);

		eventHandler();
	}

	private void eventHandler() {
		// Update the width to draw the correct shadow around the file name
		setOnMouseEntered((e) -> {
			setPrefWidth(PEApplication.getInstance().getMainController().getLeftPaneWidth());
		});
	}

	/**
	 * Shows or hides the files inside this folder
	 * 
	 * @param show
	 */
	protected void showFile(boolean show) {
		if (show) {
			hiddenNodes.forEach((node) -> {
				getChildren().add(node);
			});
			hiddenNodes.clear();
		} else { // Hide
			// Start at 1 because the first Node is the folder's name
			ListIterator<Node> nodes = getChildren().listIterator(1);
			while (nodes.hasNext()) {
				hiddenNodes.add(nodes.next());
				nodes.remove();
			}
		}
	}

	public void add(Pane file) {
		file.setPadding(new Insets(1, 0, 1, 10));

		// TODO when file is closed, add it to hiddenNodes
		getChildren().add(getFileNameIndex((FileInterface) file), file);
	}

	/**
	 * To add files in alphabetical order it has to compare their names and return
	 * the index where the new file should go
	 * 
	 * @param fi
	 * @return
	 */
	private int getFileNameIndex(FileInterface fi) {
		// Start at 1 because the first Node is the folder's name
		int idx = 1;
		ListIterator<Node> nodes = getChildren().listIterator(idx);
		while (nodes.hasNext()) {
			if (fi.compareTo((FileInterface) nodes.next()) < 0) { // Comes before
				return idx;
			} else { // Comes after
				idx++;
			}
		}

		return idx;
	}

	public void remove(Pane file) {
		getChildren().remove(file);
	}

	@SuppressWarnings("unchecked")
	public List<FileInterface> getFiles() {
		List<? extends Object> list = getChildren();
		return (List<FileInterface>) list;
	}

	@Override
	public File getDiskFile() {
		return diskFile;
	}

	@Override
	public String getName() {
		return getDiskFile().getName();
	}

	@Override
	public String toString() {
		return getName();
	}

}
