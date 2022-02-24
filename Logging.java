package com.github.jnstockley;

import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class Logging {

	protected static Logger logger = Logger.getLogger("Logger");

	protected static SimpleFormatter formatter = new SimpleFormatter();

	protected static FileHandler fh = setupFileHandler();

	public static FileHandler setupFileHandler() {
		try {
			fh = new FileHandler(System.getProperty("user.home") + "/BTTN/BTTN.log", true);
			fh.setFormatter(formatter);
			logger.addHandler(fh);
			return fh;
		} catch (SecurityException | IOException e) {
			e.printStackTrace();
			return null;
		}
	}
}