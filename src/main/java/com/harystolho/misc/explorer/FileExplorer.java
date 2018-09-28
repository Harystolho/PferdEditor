package com.harystolho.misc.explorer;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.function.Consumer;

import com.harystolho.pe.File;
import com.harystolho.utils.PEUtils;

import javafx.geometry.Insets;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.Pane;

/**
 * Wrapper for a file explorer. You can use this class to add, remove or get a
 * list of the files in the working directory.
 * 
 * @author Harystolho
 *
 */
public class FileExplorer extends ScrollPane {

	public FileExplorer() {
		super();
		setId("fileExplorer");
		setPadding(new Insets(0, 0, 0, 5));
	}

	public void removeFile(File file) {
		Queue<ExplorerFolder> folders = new ArrayDeque<>();

		ExplorerFolder currentFolder = (ExplorerFolder) getContent();

		while (currentFolder != null) {
			for (FileInterface p : currentFolder.getFiles()) {
				if (p instanceof ExplorerFile) {
					ExplorerFile cFile = (ExplorerFile) p;
					if (cFile.getFile() == file) {
						currentFolder.getChildren().remove(p);
						return;
					}
				} else if (p instanceof ExplorerFolder) {
					folders.add((ExplorerFolder) p);
				}
			}
			currentFolder = folders.poll();
		}
	}

	public void removeFolder(java.io.File folder) {
		Queue<ExplorerFolder> folders = new ArrayDeque<>();

		ExplorerFolder currentFolder = (ExplorerFolder) getContent();

		while (currentFolder != null) {
			for (FileInterface p : currentFolder.getFiles()) {
				if (p instanceof ExplorerFolder) {
					ExplorerFolder eFolder = (ExplorerFolder) p;
					if (eFolder.getDiskFile().equals(folder)) {
						currentFolder.remove(eFolder);
						return;
					} else {
						folders.add((ExplorerFolder) p);
					}
				}
			}
			currentFolder = folders.poll();
		}
	}

	public void add(Pane file) {
		if (getContent() != null) {
			ExplorerFolder root = (ExplorerFolder) getContent();

			addFileToCorrectFolder(root, (FileInterface) file);
		} else {
			setContent(file);
		}
	}

	/**
	 * If the file is at the same directory as the <code>folder</code> it will add
	 * the <code>file</code> to that folder, if it's not then it will loop through
	 * all the folders in the <code>folder</code> directory and call this method
	 * until it finds the correct one.
	 * 
	 * @param folder
	 * @param file
	 * @return
	 */
	// TODO fix this to stop when the file is added
	private void addFileToCorrectFolder(ExplorerFolder folder, FileInterface file) {
		// If folder is parent to file
		if (folder.getDiskFile().equals(file.getDiskFile().getParentFile())) {
			folder.add((Pane) file);
			return;
		} else {
			for (FileInterface fFile : folder.getFiles()) {
				if (fFile instanceof ExplorerFolder) {
					addFileToCorrectFolder((ExplorerFolder) fFile, file);
				}
			}
		}
	}

	/**
	 * 
	 * 
	 * @param file
	 */
	public void updateFile(File file) {
		findFileAndApply(file, (cFile) -> {
			cFile.update();
		});
	}

	/**
	 * Finds the {@link ExplorerFile} that corresponds to this <code>file</code> and
	 * calls <code>function.accept()</code>
	 * 
	 * @param file
	 * @param function when it finds the file it will call this function passing the
	 *                 ExplorerFile that was found
	 */
	private void findFileAndApply(File file, Consumer<ExplorerFile> function) {
		Queue<ExplorerFolder> folders = new ArrayDeque<>();

		ExplorerFolder currentFolder = (ExplorerFolder) getContent();

		while (currentFolder != null) {
			for (FileInterface p : currentFolder.getFiles()) {
				if (p instanceof ExplorerFile) {
					ExplorerFile cFile = (ExplorerFile) p;
					if (cFile.getFile() == file) {
						function.accept(cFile);
						return;
					}
				} else if (p instanceof ExplorerFolder) {
					folders.add((ExplorerFolder) p);
				}
			}
			currentFolder = folders.poll();
		}
	}

	/**
	 * @return A list containing all the files under the save folder
	 * @see {@link PEUtils#getWorkspaceFolder()}
	 */
	public List<File> getFiles() {
		List<File> files = new ArrayList<>();

		ExplorerFolder root = (ExplorerFolder) getContent();

		addFilesToList(files, root);

		return files;
	}

	/**
	 * If <code>p</code> is a file, add it to the <code>list</code>, if it's a
	 * folder call this method for each file under it.
	 * 
	 * @param list
	 * @param p
	 */
	private void addFilesToList(List<File> list, Pane p) {
		if (p instanceof ExplorerFolderName) {
			ExplorerFile cFile = (ExplorerFile) p;
			list.add(cFile.getFile());
		} else if (p instanceof ExplorerFolder) {
			ExplorerFolder cFolder = (ExplorerFolder) p;
			for (FileInterface pp : cFolder.getFiles()) {
				addFilesToList(list, (Pane) pp);
			}
		}
	}

}
