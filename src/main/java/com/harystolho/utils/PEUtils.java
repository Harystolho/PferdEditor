package com.harystolho.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;
import java.util.logging.Logger;

import com.harystolho.controllers.MainController;
import com.harystolho.controllers.ResizableInterface;

import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;

public class PEUtils {

	private static final Logger logger = Logger.getLogger(PEUtils.class.getName());

	private static ExecutorService executor = Executors.newFixedThreadPool(1);

	private static final File saveFolder = new File("files");

	public static final String VERSION = "0.3";

	/**
	 * Loads a FXML File.
	 * 
	 * @param file       full file name. (Eg: main.fxml)
	 * @param controller a consumer interface that will accept the controller
	 * @return the <code>Parent</code> object containing the file, or
	 *         <code>null</code> if it can't find the file.
	 */
	public static <T> Parent loadFXML(String file, Consumer<T> controller) {

		try {
			FXMLLoader fxml = new FXMLLoader(ClassLoader.getSystemResource(file));
			Parent p = fxml.load();
			controller.accept(fxml.getController());
			return p;

		} catch (NullPointerException | IOException e) {
			logger.severe("Couldn't load file " + file + " // " + ClassLoader.getSystemResource(file));
			return null;
		}

	}

	/**
	 * Handler for window resize.
	 * 
	 * @param scene
	 * @param resize a JavaFX Controller that implements
	 *               {@link #com.harystolho.controllers.ResizableInterface
	 *               ResizableInterface }
	 */
	public static void addResizeHandler(Scene scene, ResizableInterface resize) {

		scene.widthProperty().addListener((obv, oldValue, newValue) -> {
			resize.onWidthResize(newValue.intValue());
		});

		scene.heightProperty().addListener((obv, oldValue, newValue) -> {
			resize.onHeightResize(newValue.intValue());
		});

	}

	public static void saveFiles(List<com.harystolho.pe.File> files) {
		files.stream().forEach((fileToSave) -> {
			File f = new File(saveFolder + "/" + fileToSave.getName());

			try (FileWriter fw = new FileWriter(f)) {
				fileToSave.getWords().stream().forEach((word) -> {
					try {
						switch (word.getType()) {
						case NORMAL:
						case SPACE:
							fw.write(word.getWordAsString());
							break;
						case NEW_LINE:
							fw.write("\n");
							break;
						case TAB:
							fw.write("\t");
							break;
						default:
							break;
						}

					} catch (IOException e) {
						logger.severe("Couldn't write {" + word.getWordAsString() + "} to " + f.getName());
					}
				});
				fw.flush();
			} catch (IOException e) {
				logger.severe("Couldn't save file " + f.getName() + " to " + saveFolder.getAbsolutePath());
			}

		});
	}

	public static void loadFiles(MainController controller) {
		if (saveFolder.exists()) {
			for (File file : saveFolder.listFiles()) {
				controller.addNewFile(createFileFromSourceFile(file));
			}
		}
	}

	/**
	 * Loads a file from disk, creates a new {@link com.harystolho.pe.File File} and
	 * adds chars to it.
	 * 
	 * @param f the file to be read
	 * @return a <code>File</code> object containing the characters read
	 */
	private static com.harystolho.pe.File createFileFromSourceFile(File f) {

		com.harystolho.pe.File file = new com.harystolho.pe.File(f.getName());

		try (FileReader fr = new FileReader(f)) {

			int i;
			while ((i = fr.read()) != -1) {
				char c = (char) i;
				file.type(c);
			}

		} catch (IOException e) {
			logger.severe("Couldn't read bytes from " + f.getName());
		}

		return file;

	}

	public static ExecutorService getExecutor() {
		return executor;
	}

	/**
	 * Initializes resources for this application.
	 */
	public static void start() {
		logger.info("Initializing application");

		PEConfiguration.loadProperties();

	}

	public static void exit() {
		logger.info("Closing application.");

		executor.shutdown();

		RenderThread.stop();
	}

}
