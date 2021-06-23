package com.github.jnstockley;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.file.Files;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * 
 * Helps with logging all messages to the log file and clear the log when needed
 * 
 * @author Jack Stockley
 * 
 * @version 1.5
 *
 */
public class Logging {

	/**
	 *  Name of the file used to log data
	 */
	private static final File LOGFILE = new File("BTTN.log");

	/**
	 * Logs information to the log file, used when successful operations are performed
	 * @param className The name of the file the message if from
	 * @param message The message to log to the log file
	 */
	public static void logInfo(String className, String message) {
		// Clears the log after 7 days of being created
		clearLog();
		// Gets the current date and time and builds the message to log
		Date curDate = new Date();
		String log = curDate.toString() + ": " + className + " " + Bundle.getBundle("info") + message;
		// Writes the log message to the log
		try {
			FileWriter writer = new FileWriter(LOGFILE, true);
			writer.write(log + '\n');
			writer.flush();
			writer.close();
		} catch (IOException e) {
			// Prints more data if BTTN is in debug mode
			if(BTTN.debug) {
				StringWriter error = new StringWriter();
				e.printStackTrace(new PrintWriter(error));
				System.err.println(curDate.toString() + ": " + Logging.class.getName() + " " + Bundle.getBundle("error") + error.toString());
			} else {
				System.err.println(curDate.toString() + ": " + Logging.class.getName() + " " + Bundle.getBundle("error") + e.getLocalizedMessage());
			}
		}
		// Prints more data if BTTN is in debug mode
		if(BTTN.debug) {
			System.out.println(log);
		} else {
			System.out.println(message);
		}
	}

	/**
	 * Logs warning messages to the log file, used when an operation was not successful but the program should run fine
	 * @param className The name of the file the message if from
	 * @param message The message to log to the log file
	 */
	public static void logWarn(String className, String message) {
		// Gets the current date and time and builds the message to log
		Date curDate = new Date();
		String log = curDate.toString() + ": " + className + " " + Bundle.getBundle("warn") + message;
		// Writes the log message to the log
		try {
			FileWriter writer = new FileWriter(LOGFILE, true);
			writer.write(log + '\n');
			writer.flush();
			writer.close();
		} catch (IOException e) {
			// Prints more data if BTTN is in debug mode
			if(BTTN.debug) {
				StringWriter error = new StringWriter();
				e.printStackTrace(new PrintWriter(error));
				System.err.println(curDate.toString() + ": " + Logging.class.getName() + " " + Bundle.getBundle("error") + error.toString());
			} else {
				System.err.println(curDate.toString() + ": " + Logging.class.getName() + " " + Bundle.getBundle("error") + e.getLocalizedMessage());
			}
		}
		// Prints more data if BTTN is in debug mode
		if(BTTN.debug) {
			System.err.println(log);
		} else {
			System.err.println(message);
		}
		System.exit(1);
	}

	/**
	 * Logs error messages to the log file, used when an operation was not successful and the program should exit
	 * @param className The name of the file the message if from
	 * @param message The message to log to the log file
	 */
	public static void logError(String className, String message) {
		// Gets the current date and time and builds the message to log
		Date curDate = new Date();
		String log = curDate.toString() + ": " + className + " " + Bundle.getBundle("error") + message;
		// Writes the log message to the log
		try {
			FileWriter writer = new FileWriter(LOGFILE, true);
			writer.write(log + '\n');
			writer.flush();
			writer.close();
		} catch (IOException e) {
			// Prints more data if BTTN is in debug mode
			if(BTTN.debug) {
				StringWriter error = new StringWriter();
				e.printStackTrace(new PrintWriter(error));
				System.err.println(curDate.toString() + ": " + Logging.class.getName() + " " + Bundle.getBundle("error") +error.toString());
			} else {
				System.err.println(curDate.toString() + ": " + Logging.class.getName() + " " + Bundle.getBundle("error") + e.getLocalizedMessage());
			}
		}
		// Prints more data if BTTN is in debug mode
		if(BTTN.debug) {
			System.err.println(log);
		} else {
			System.err.println(message);
		}
		System.exit(1);
	}

	/**
	 * Logs error messages to the log file, used when an operation was not successful and the program should exit
	 * @param className The name of the file the message if from
	 * @param message The exception thrown that is being logged
	 */
	public static void logError(String className, Exception message) {
		// Gets the current date and time and builds the message to log
		Date curDate = new Date();
		String log = "";
		// Prints more data if BTTN is in debug mode
		if(BTTN.debug) {
			StringWriter error = new StringWriter();
			message.printStackTrace(new PrintWriter(error));
			log = curDate.toString() + ": " + className + " " + Bundle.getBundle("error") + error.toString();
		} else {
			log = curDate.toString() + ": " + className + " " + Bundle.getBundle("error") + message.toString();
		}
		// Writes the log message to the log
		try {
			FileWriter writer = new FileWriter(LOGFILE, true);
			writer.write(log + '\n');
			writer.flush();
			writer.close();
		} catch (IOException e) {
			// Prints more data if BTTN is in debug mode
			if(BTTN.debug) {
				StringWriter error = new StringWriter();
				e.printStackTrace(new PrintWriter(error));
				System.err.println(curDate.toString() + ": " + Logging.class.getName() + " " + Bundle.getBundle("error") + error.toString());
			} else {
				System.err.println(curDate.toString() + ": " + Logging.class.getName() + " " + Bundle.getBundle("error") + e.getLocalizedMessage());
			}
		}
		// Prints more data if BTTN is in debug mode
		if(BTTN.debug) {
			System.err.println(log);
		} else {
			System.err.println(message);
		}
		System.exit(1);
	}

	/**
	 * Clears the log file after being 7 days old
	 */
	private static void clearLog() {
		// Gets the current date
		Date curDate = new Date();
		try {
			// Gets the date the file was created
			BasicFileAttributes attrs = Files.readAttributes(LOGFILE.toPath(), BasicFileAttributes.class);
			long time = attrs.creationTime().toMillis();
			// Checks if date if older then 7 days and deletes it
			if((time + TimeUnit.DAYS.toMillis(7)) < curDate.getTime()) {
				if(!LOGFILE.delete()) {
					if(!LOGFILE.createNewFile()) {
						System.err.println(curDate.toString() + ": " + Logging.class.getName() + " " + Bundle.getBundle("error") + Bundle.getBundle("deleteLogError"));
					}
					System.err.println(curDate.toString() + ": " + Logging.class.getName() + " " + Bundle.getBundle("error") + Bundle.getBundle("deleteLogError"));
				}
			}
		} catch (IOException e) {
			if(BTTN.debug) {
				StringWriter error = new StringWriter();
				e.printStackTrace(new PrintWriter(error));
				System.err.println(curDate.toString() + ": " + Logging.class.getName() + " " + Bundle.getBundle("error") + error.toString());
			} else {
				System.err.println(curDate.toString() + ": " + Logging.class.getName() + " " + Bundle.getBundle("error") + e.getLocalizedMessage());
			}
		}
	}

}
