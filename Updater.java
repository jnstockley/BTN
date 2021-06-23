package com.github.jnstockley;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Determines if an update is available and when to send a notification alerting of the update!
 * 
 * @author Jack Stockley
 * 
 * @version 1.5
 *
 */
public class Updater {

	/**
	 *  Name of the Class used for Logging error
	 */
	private static final String CLASSNAME = Updater.class.getName();

	/**
	 * The current version number of BTTN
	 */
	public static final double VERSION = 1.5;

	/**
	 * The build version of BTTN
	 */
	public static final String BUILD = "JUN-23-21";

	/**
	 * True if this build is part of the TESTING branch otherwise false
	 */
	public static final boolean TESTING = false;

	/**
	 * Checks if their is an update available for BTTN
	 */
	public static void updateAvailable() {
		// Build the HTTP request
		OkHttpClient client = new OkHttpClient();
		Request request = new Request.Builder()
				.url("https://raw.githubusercontent.com/jnstockley/BTTN/main/version")
				.build();
		// Sends the HTTP request
		try(Response response = client.newCall(request).execute()){
			double serverVersion = Double.parseDouble(response.body().string());
			// Checks if the version on GitHub is newer then the current version and sends the notification
			if(serverVersion > VERSION && sendNotification()) {
				Auth auth = BTTN.auth;
				Notifications.sendUpdateNotification(serverVersion, auth.getAlertzyAccountKey());
				System.exit(0);
			} else if(serverVersion > VERSION){
				Logging.logWarn(CLASSNAME, Bundle.getBundle("updateAvailable"));
				System.exit(0);
			}
		} catch (IOException e) {
			Logging.logError(CLASSNAME, e);
		}
	}

	/**
	 * Makes sure its been at least 30 minutes since the last update notification has been sent
	 * @return True if it's time to send an update notification otherwise false
	 */
	private static boolean sendNotification() {
		// Gets the time the next notification should be sent
		Date nextSend = new Date(getLastSent() + TimeUnit.MINUTES.toMillis(30));
		Date curDate = new Date();
		// Checks if its time to send a new notification
		if(nextSend.compareTo(curDate) < 0) {
			writeTime(curDate.getTime());
			return true;
		}
		return false;
	}

	/**
	 * Reads the lastSent.txt file to get the time the last notification was sent out
	 * @return A long number with the time representation of the time the last notification was sent
	 */
	private static long getLastSent() {
		File timeFile = new File("lastSent.txt");
		// Checks if file exists and has a time in it
		if(timeFile.exists() && timeFile.length() > 0) {
			try {
				// Reads the time and returns it
				BufferedReader fileReader = new BufferedReader(new FileReader(timeFile));
				Long time = Long.parseLong(fileReader.readLine());
				fileReader.close();
				return time;
			} catch(IOException | NumberFormatException e) {
				Logging.logError(CLASSNAME, e);
			}
		}
		return -1;
	}

	/**
	 * Write the new time the last update notification was sent out
	 * @param time The long time the last update notification was sent
	 */
	private static void writeTime(long time) {
		// The file where the time is written to
		File timeFile = new File("lastSent.txt");
		try {
			// Writes the new time
			FileWriter writer = new FileWriter(timeFile);
			writer.write(Long.toString(time));
			writer.flush();
			writer.close();
		} catch (IOException e) {
			Logging.logError(CLASSNAME, e);
		}
	}
}
