package com.harystolho.utils;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;
import java.util.logging.Logger;

import com.harystolho.Main;
import com.harystolho.controllers.ResizableInterface;
import com.harystolho.thread.RenderThread;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.FileChooser;

public class PEUtils {

	private static final Logger logger = Logger.getLogger(PEUtils.class.getName());

	private static final ExecutorService executor = Executors.newFixedThreadPool(3);

	private static File saveFolder;

	public static final String VERSION = "0.5";

	/**
	 * Loads a FXML File.
	 * 
	 * @param file       file name with extension. (Eg: main.fxml)
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
			logger.severe("Can't load file: " + file + " // " + ClassLoader.getSystemResource(file));
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
			if (fileToSave.isLoaded()) {
				File f = new File(saveFolder + "/" + fileToSave.getName());
				saveFile(fileToSave, f);
			}
		});
	}

	public static void saveFile(com.harystolho.pe.File fileToSave, File f) {

		// If a new PE file was created the disk file is null
		if (f == null) {
			f = new File(getSaveFolder() + "/" + fileToSave.getName());
			fileToSave.setDiskFile(f);
		}

		if (!f.exists()) {
			try {
				f.createNewFile();
			} catch (IOException e) {
				logger.severe("Couldn't create file: " + f.getAbsolutePath());
			}
		}

		try (FileWriter fw = new FileWriter(f)) {
			fileToSave.getWords().stream().forEach((word) -> {
				try {
					switch (word.getType()) {
					case NORMAL:
					case SPACE:
						fw.write(word.getWordAsString());
						break;
					case NEW_LINE:
						fw.write(System.getProperty("line.separator"));
						break;
					case TAB:
						fw.write("\t");
						break;
					}

				} catch (IOException e) {
					logger.severe("Couldn't write {" + word.getWordAsString() + "} to " + fileToSave.getName());
				}
			});
			fw.flush();
		} catch (IOException e) {
			logger.severe("Couldn't save file " + f.getName() + " to " + saveFolder.getAbsolutePath());
		}
	}

	public static void saveFileAs(com.harystolho.pe.File currentFile) {
		FileChooser fc = new FileChooser();
		java.io.File file = fc.showSaveDialog(Main.getApplication().getWindow());

		if (file != null) {
			saveFile(currentFile, file);
		}
	}

	/**
	 * Loads the file names from disk but not the content inside it, to load the
	 * content use {@link #loadFileFromDisk(com.harystolho.pe.File)}
	 * 
	 * @param f file to be read
	 * @return a <code>File</code> object containing the file name
	 */
	public static com.harystolho.pe.File createFileFromSourceFile(File f) {
		com.harystolho.pe.File file = new com.harystolho.pe.File(f.getName());
		file.setDiskFile(f);

		return file;
	}

	public static void loadFileFromDisk(com.harystolho.pe.File file) {
		System.out.println("Loading from disk: " + file.getName());

		if (file.getDiskFile() != null) {
			try (FileReader fr = new FileReader(file.getDiskFile())) {

				int i;
				while ((i = fr.read()) != -1) {
					char c = (char) i;
					file.type(c);
				}

			} catch (IOException e) {
				logger.severe("Can't read bytes from \"" + file.getName() + "\"");
			}
		}

		file.setLoaded(true);

	}

	public static File getSaveFolder() {
		if (!saveFolder.exists()) {
			saveFolder.mkdir();
		}

		return saveFolder;
	}

	public static void setSaveFolder(File dir) {
		saveFolder = dir;
		PEConfiguration.setProperty("PROJ_FOLDER", dir.getAbsolutePath());
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

		saveFolder = new File(PEConfiguration.getProperty("PROJ_FOLDER"));

	}

	public static void exit() {
		logger.info("Closing application.");

		executor.shutdown();

		PEConfiguration.saveProperties();

		RenderThread.stop();
	}

}
