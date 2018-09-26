package com.harystolho.canvas.tab;

import java.util.ListIterator;

import com.harystolho.PEApplication;
import com.harystolho.canvas.CanvasManager;
import com.harystolho.pe.File;

import javafx.collections.ObservableList;
import javafx.scene.layout.HBox;

public class FileTabManager extends HBox {

	public FileTabManager() {
		super();
		setPrefHeight(24);
	}

	public void addTab(Tab tab) {
		getChildren().add(tab);
	}

	public void addTab(File file) {
		Tab tab = new Tab(file);
		addTab(tab);
	}

	public void removeTab(Tab tab) {
		getChildren().remove(tab);
	}

	/**
	 * Adds a CSS class to the selected file tab to show it's select
	 * 
	 * @param file
	 */
	public void select(File file) {
		for (Tab tab : getTabs()) {
			if (tab.getUserData() != file) {
				tab.setSelected(false);
			} else {
				tab.setSelected(true);
			}
		}
	}

	public void removeModified(File currentFile) {
		for (Tab tab : getTabs()) {
			if (tab.getUserData() == currentFile) {
				tab.setModified(false);
			}
		}
	}

	public void closeTabs() {
		ListIterator<Tab> it = getTabs().listIterator();
		while (it.hasNext()) {
			Tab tab = it.next();
			closeFile((File) tab.getUserData(), it);
		}
	}

	/**
	 * Removes the <code>file</code> from the {@link #filesTab} and selects the
	 * first tab if there's one
	 * 
	 * @param file
	 * @param node
	 */
	public void closeFile(File file) {
		ListIterator<Tab> it = getTabs().listIterator();
		while (it.hasNext()) {
			Tab tab = it.next();
			if (tab.getFile() == file) {
				closeFile(file, it);
				break;
			}
		}
	}

	private void closeFile(File file, ListIterator<Tab> it) {
		if (file.wasModified()) {
			PEApplication.getInstance().getMainController().openSaveChangesWindow(file);
		} else {
			it.remove();
			CanvasManager.getInstance().resetPivotNode();

			PEApplication.getInstance().getMainController().selectFirstTabOnFileTab();

			file.unload();
		}
	}

	public void removeFileFromFileTab(File file) {
		ListIterator<Tab> it = getTabs().listIterator();
		while (it.hasNext()) {
			Tab tab = it.next();
			if (tab.getFile() == file) {
				removeTab(tab);
				break;
			}
		}
	}

	/**
	 * Adds a <code>*</code> to the file name to indicate it's been modified
	 * 
	 * @param file
	 */
	public void setFileModified(File file) {
		for (Tab tab : getTabs()) {
			if (tab.getFile() == file) {
				tab.setModified(true);
				break;
			}
		}
	}

	/**
	 * If a file is renamed it has to update its name in the File Tab too, this
	 * method does that
	 * 
	 * @param file
	 */
	public void updateFileNameOnFileTab(File file) {
		for (Tab tab : getTabs()) {
			if (tab.getFile() == file) {
				tab.setTabName(file.getName());
				break;
			}
		}
	}

	@SuppressWarnings("rawtypes")
	public ObservableList<Tab> getTabs() {
		ObservableList tabs = getChildren();
		return tabs;
	}

	public boolean isEmpty() {
		return getTabs().size() == 0;
	}

}
