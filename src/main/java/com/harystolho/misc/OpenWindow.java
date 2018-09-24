package com.harystolho.misc;

import java.util.function.Consumer;

import com.harystolho.utils.PEUtils;

import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 * Every time a common window is opened, this object should be used
 * 
 * @author Harystolho
 *
 */
public class OpenWindow {

	private Stage stage;
	private Parent parent;
	private Scene scene;

	public OpenWindow(String title) {
		stage = new Stage();

		stage.initModality(Modality.APPLICATION_MODAL);

		stage.setTitle(title);
	}

	public void load(String fxmlName, Consumer<Object> controller) {
		parent = PEUtils.loadFXML(fxmlName, controller);

		scene = new Scene(parent);
		scene.getStylesheets().add(ClassLoader.getSystemResource("style.css").toExternalForm());

		scene.setOnKeyPressed((e) -> {
			if (e.getCode() == KeyCode.ESCAPE) {
				stage.close();
			}
		});

		stage.setScene(scene);
	}

	public void openWindow() {
		stage.show();

	}

	public Stage getStage() {
		return stage;
	}

}
