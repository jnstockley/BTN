//Updater.java
package com.github.jnstockley;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;


/**
 * Helps with checking for updates and letting the user know that an update is available
 * 
 * @author Jack Stockley
 * 
 * @version 1.01
 *
 */
public class Updater {

	/**
	 * Private double representing the current version of the BTTN program
	 */
	private final static double version = 1.01;

	/**
	 * Makes HTTP request to server to get the latest version number publicly available
	 * @param auth HashMap with API keys used to send update notification if update is available
	 * @param delay Integer representing amount of delay between update notifications
	 */
	protected static void checkUpdate(HashMap<String, String> auth, int delay) {
		// HTTP request to see if newer version is available
		HashMap<String, String> response = HTTP.get("https://raw.githubusercontent.com/jnstockley/BTTN/main/version");
		// Makes sure HTTP request returned a status code of 200
		if(response.get("statusCode").equals("200")) {
			// Checks if server version is newer then current version
			double serverVersion = Double.parseDouble(response.get("data"));
			// Sends notification if its been longer then update delay
			if((serverVersion > version) && timeToSendAlert(delay)) {
				Notifications.sendUpdateNotification(serverVersion, auth);
			}
			Logger.logWarn(Bundle.getString("updateAvailable"));
		} else {
			Logger.logError(Bundle.getString("errCheck"));
		}
	}

	/**
	 * Checks the time an update notification was last sent is greater or equal to delay
	 * @param delay Integer representing amount of delay between update notifications
	 * @return True if notification should be sent otherwise false
	 */
	private static boolean timeToSendAlert(int delay) {
		// Gets time last sent and adds delay to it
		Date checkTime = new Date(getDateTime() + TimeUnit.MINUTES.toMillis(delay));
		// Gets current date
		Date curDate = new Date();
		// Checks if last time plus delay is less then current time and updates last sent file
		if(checkTime.compareTo(curDate) < 0) {
			writeDateTime(curDate.getTime());
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Updates a text file with the time the last update notification was sent out
	 * @param time The time in milliseconds since January 1st 1970 the last update notification was sent
	 */
	private static void writeDateTime(long time) {
		// Writes current time to last sent text file
		File timeFile = new File("lastSent.txt");
		FileWriter writer = null;
		try {
			writer = new FileWriter(timeFile);
			writer.write(Long.toString(time));
			writer.flush();
			writer.close();
		} catch (IOException e) {
			Logger.logError(Bundle.getString("timeWriteErr"));
		}
	}

	/**
	 * Gets the last time an update notification was sent from the last sent text file.
	 * @return The time in milliseconds since January 1st 1970 the last update notification
	 * was sent or -1 if no file exists or it is empty
	 */
	private static long getDateTime() {
		// Reads the long integer from the last sent text file and returns it
		File timeFile = new File("lastSent.txt");
		if(timeFile.exists() && timeFile.length() != 0) {
			try {
				BufferedReader br = new BufferedReader(new FileReader(timeFile));
				Long time = Long.parseLong(br.readLine());
				br.close();
				return time;
			} catch (IOException | NumberFormatException e) {
				e.printStackTrace();
				Logger.logError(Bundle.getString("timeReadErr"));
				return -1;
			} 
		} else {
			return -1;
		}
	}
}
