package com.harystolho.controllers;

import com.harystolho.Main;

import javafx.fxml.FXML;

public class MainController implements ResizableInterface {

	@FXML
	void initialize() {
		Main.getApplication().setMainController(this);
		
		
		
	}

	@Override
	public void onWidthResize() {

	}

	@Override
	public void onHeightResize() {

	}

}
