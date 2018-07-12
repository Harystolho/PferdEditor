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

	public static String getProperty(String key) {
		return prop.getProperty(key);
	}

}
