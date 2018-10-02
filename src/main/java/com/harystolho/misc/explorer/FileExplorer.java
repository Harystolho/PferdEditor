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

			addFileToFolder(root, (FileInterface) file);
		} else {
			setContent(file);
		}
	}

	/**
	 * Adds the <code>file</code> under the correct folder
	 * 
	 * @param baseFolder
	 * @param file
	 */
	private void addFileToFolder(ExplorerFolder baseFolder, FileInterface file) {
		Queue<ExplorerFolder> folders = new ArrayDeque<>();

		ExplorerFolder currentFolder = baseFolder;

		while (currentFolder != null) {
			if (file.isParent(currentFolder)) {
				currentFolder.add((Pane) file);
				return;
			}

			for (FileInterface eFile : currentFolder.getFiles()) {
				if (eFile instanceof ExplorerFolder) {
					folders.add((ExplorerFolder) eFile);
				}
			}
			currentFolder = folders.poll();
		}

	}

	/**
	 * Finds the <code>file</code> and calls {@link ExplorerFile#update()}
	 * 
	 * @param file
	 */
	public void updateFile(File file) {
		findFileAndApply(file, (cFile) -> {
			cFile.update();
		});
	}

	/**
	 * Finds the {@link ExplorerFile} that corresponds to the <code>file</code> and
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
	 * @return A list containing all the files under the workspace directory
	 * @see {@link PEUtils#getWorkspaceFolder()}
	 */
	public List<File> getFiles() {
		List<File> files = new ArrayList<>();

		ExplorerFolder root = (ExplorerFolder) getContent();

		addFilesToList(files, root);

		return files;
	}

	/**
	 * If <code>fi</code> is a file, add it to the <code>list</code>, if it's a
	 * folder call this method for each file under it.
	 * 
	 * @param list
	 * @param fi
	 */
	private void addFilesToList(List<File> list, FileInterface fi) {
		if (fi instanceof ExplorerFile) {
			ExplorerFile cFile = (ExplorerFile) fi;
			if(cFile.getFile() != null) {
				list.add(cFile.getFile());	
			}
		} else if (fi instanceof ExplorerFolder) {
			ExplorerFolder cFolder = (ExplorerFolder) fi;
			for (FileInterface pp : cFolder.getFiles()) {
				addFilesToList(list, pp);
			}
		}
	}

}
