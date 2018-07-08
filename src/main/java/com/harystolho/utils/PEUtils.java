package com.harystolho.utils;

import java.util.logging.Logger;

public class PEUtils {

	private static final Logger logger = Logger.getLogger(PEUtils.class.getName());

	public static final String VERSION = "0.1";

	/**
	 * Initializes resources for this application.
	 */
	public static void start() {
		logger.info("Initializing application.");

		PEConfiguration.loadProperties();

	}

	public static void exit() {
		logger.info("Closing application.");
	}

}
