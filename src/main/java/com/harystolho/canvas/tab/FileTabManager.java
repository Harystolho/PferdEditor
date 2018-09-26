package com.harystolho.canvas.tab;

import java.util.ArrayList;
import java.util.List;

import javafx.scene.layout.HBox;

public class FileTabManager extends HBox {

	List<Tab> tabs = new ArrayList<>();

	public FileTabManager() {
		super();
		setPrefHeight(24);
	}

	public void addTab(Tab tab) {
		getChildren().add(tab);
		tabs.add(tab);
	}

	public void removeTab(Tab tab) {
		getChildren().remove(tab);
		tabs.remove(tab);
	}

}
