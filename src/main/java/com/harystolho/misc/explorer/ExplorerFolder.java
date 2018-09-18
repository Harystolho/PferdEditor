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

public class ExplorerFolder extends VBox {

	private File diskFile;
	private List<Node> hiddenNodes;

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
		} else {
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
