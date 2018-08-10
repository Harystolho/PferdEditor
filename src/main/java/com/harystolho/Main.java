package com.harystolho;

import com.harystolho.utils.PEUtils;

/**
 * Text Editor Application in Java.
 *
 */
public class Main {

	private static PEApplication application;

	public static void main(String[] args) {

		PEUtils.start();
		PEApplication.init(args);

	}

	public static PEApplication getApplication() {
		return application;
	}

	public static void setApplication(PEApplication application) {
		Main.application = application;
	}

}
