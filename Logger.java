//Logger.java
package com.github.jnstockley;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;

/**
 * Logs info, warnings and error messages to a log file. Also clear log file after a day of being created
 * 
 * @author Jack Stockley
 * 
 * @version 0.14-beta
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
		// Makes sure log file exists
		if(new File("BTTN.log").exists()) {
			// Clears the log after log file has 500 or more lines
			emptyLog();
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
	 * Deletes the log file after the file contains for then 500 lines
	 * @return True if log was cleared, false otherwise
	 */
	public static boolean emptyLog() {
		// Get log file and path
		File logFile = new File("BTTN.log");
		Path logPath = Paths.get("BTTN.log");
		// Checks if log has more then 500 lines and clears it
		try {
			long lines = Files.lines(logPath).count();
			if(lines >= 500) {
				if(logFile.delete()) {
					if(logFile.createNewFile()) {
						return true;
					}
				}
			}
			return false;
		} catch (IOException e) {
			Logger.logError(Bundle.getString("logClear"));
			return false;
		}
	}
}
