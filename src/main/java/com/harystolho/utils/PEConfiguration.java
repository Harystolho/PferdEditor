package com.harystolho.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Properties;
import java.util.logging.Logger;

public class PEConfiguration {

	private static final Logger logger = Logger.getLogger(PEConfiguration.class.getName());

	private static final Properties prop = new Properties();

	private static final File propFile = new File("configuration");

	private static void loadDefaultProperties() {

		prop.putIfAbsent("VERSION", PEUtils.VERSION);
		prop.putIfAbsent("LANG", "EN");
		prop.putIfAbsent("PROJ_FOLDER", "files");

		prop.putIfAbsent("TEXT_COLOR", "#fff");
		prop.putIfAbsent("CANVAS_BACKGROUND_COLOR", "#333333");
		prop.putIfAbsent("LINE_COLOR", "#b3b3b3");
		prop.putIfAbsent("CURSOR_COLOR", "#cccccc");
		prop.putIfAbsent("WHITESPACE_COLOR", "007f7f");
		prop.putIfAbsent("FONT_SIZE", "16");

		saveProperties();
	}

	public static void saveProperties() {

		try (OutputStream os = new FileOutputStream(propFile)) {
			prop.store(os, null);
		} catch (FileNotFoundException e) {
			logger.severe("Couldn't find configuration file to save.");
		} catch (IOException e) {
			logger.severe("Couldn't save to configuration file.");
		}

	}

	public static void loadProperties() {

		logger.info("Loading configuration file");

		try (FileInputStream fis = new FileInputStream(propFile)) {
			prop.load(fis);
		} catch (IOException e) {
			logger.severe("Couldn't load configuration file.");
		}

		loadDefaultProperties();

	}

	public static void setProperty(Object string, Object value) {
		prop.put(string, value);
	}

	public static String getProperty(String key) {
		return prop.getProperty(key);
	}

}