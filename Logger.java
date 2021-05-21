//Logger.java
package com.github.jnstockley;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Date;

/**
 * Logs info, warnings and error messages to a log file. Also clear log file after a day of being created
 * 
 * @author Jack Stockley
 * 
 * @version 0.11-beta
 *
 */
public class Logger {

	/**
	 * Updates a file with a new message with what the program just finished processing
	 * @param message Updates that describes the function just performed
	 */
	protected static void logInfo(String message) {
		// Gets current date
		Date curDate = new Date();
		// Clears the log after log file being active for over a day
		try {
			Path log = Paths.get("BTTN.log");
			BasicFileAttributes attr = Files.readAttributes(log, BasicFileAttributes.class);
			Date creation = new Date(attr.creationTime().toMillis());
			long diffTime = curDate.getTime() - creation.getTime();
			double diffDays = diffTime / (1000 * 60 * 60 * 24);
			if(diffDays >= 1) {
				clearLog();
			}
		} catch (IOException e1) {
			System.err.println(Bundle.getString("logClear"));
		}
		// Builds full message with type of log and current date and time
		String fullMsg = curDate.toString() + ": " + Bundle.getString("info") +" - " + message + '\n';
		// Writes full message to log file without overwriting old logs
		try {
			FileWriter writer = new FileWriter("BTTN.log", true);
			writer.write(fullMsg);
			writer.flush();
			writer.close();
		} catch (IOException e) {
			System.err.println(Bundle.getString("logWrite"));
			System.exit(1);
		}
		// Prints message to console and exits program
		System.out.println(message);
		System.exit(0);
	}

	/**
	 * Updates a file with a new message with a warning from the program
	 * @param message Message that describes the warning created from the program
	 */
	protected static void logWarn(String message) {
		// Gets the current date
		Date curDate = new Date();
		// Builds full message with type of log and current date and time
		String fullMsg = curDate.toString() + ": "+ Bundle.getString("warn") + " - " + message + '\n';
		// Writes full message to log file without overwriting old logs
		try {
			FileWriter writer = new FileWriter("BTTN.log", true);
			writer.write(fullMsg);
			writer.flush();
			writer.close();
		} catch (IOException e) {
			System.err.println(Bundle.getString("logWrite"));
			System.exit(1);
		}
		// Prints the warning message to console and exits program
		System.err.println(message);
		System.exit(0);
	}

	/**
	 * Updates a file with a new message with an error from the program
	 * @param message Message that describes the error created from the program
	 */
	protected static void logError(String message) {
		// Gets the current date
		Date curDate = new Date();
		// Builds fill message with type of log and current date and time
		String fullMsg = curDate.toString() + ": " + Bundle.getString("error")  + " - " + message + '\n';
		// Writes full message to the log file without overwriting old logs
		try {
			FileWriter writer = new FileWriter("BTTN.log", true);
			writer.write(fullMsg);
			writer.flush();
			writer.close();
		} catch (IOException e) {
			System.err.println(Bundle.getString("logWrite"));
			System.exit(1);
		}
		// Prints the error message to console and exits the program
		System.err.println(message);
		System.exit(1);
	}

	/**
	 * Deletes the old log file, only ran after log file is older than 1 day
	 */
	private static void clearLog() {
		// Overwrites the old log file with an empty file
		try {
			new FileWriter("BTTN.log",false).close();
		} catch (IOException e) {
			System.err.println(Bundle.getString("logWrite"));
			System.exit(1);
		}
	}

}
