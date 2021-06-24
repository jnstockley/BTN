package com.github.jnstockley;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * 
 * Helps with logging all messages to the log file and clear the log when needed
 * 
 * @author Jack Stockley
 * 
 * @version 1.51
 *
 */
public class Logging {

	/**
	 * Name of the user directory to store the log file
	 */
	private static final String USERDIR = System.getProperty("user.dir");

	/**
	 *  Name of the file used to log data
	 */
	private static File LOGFILE = new File(USERDIR + System.getProperty("file.separator") + "BTTN.log");

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
	 * Clears the log after being 7 days old
	 */
	private static void clearLog() {
		// Gets the current date and checks if the log file exists
		Date curDate = new Date();
		if(LOGFILE.exists()) {
			try {
				// Reads the first line of the log file
				BufferedReader reader = new BufferedReader(new FileReader(LOGFILE));
				String firstLine = reader.readLine();
				reader.close();
				if(firstLine == null) {
					return;
				}
				// Parses the date from the log file
				DateFormat dateFormat = new SimpleDateFormat("EEE MMM d HH:mm:ss Z yyyy");
				Date d = dateFormat.parse(firstLine);
				// Checks if log file is older then 7 days and deletes it and creates a new file
				if((d.getTime() + TimeUnit.DAYS.toMillis(7)) < curDate.getTime()) {
					if(LOGFILE.delete()) {
						if(!LOGFILE.createNewFile()) {
							System.err.println(curDate.toString() + ": " + Logging.class.getName() + " " + Bundle.getBundle("error") + Bundle.getBundle("deleteLogError"));
						}
					} else {
						System.err.println(curDate.toString() + ": " + Logging.class.getName() + " " + Bundle.getBundle("error") + Bundle.getBundle("deleteLogError"));
					}
				}
			} catch (IOException | ParseException e) {
				// Prints more data if BTTN is in debug mode
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
}
