import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

//Updater.java

/**
 * 
 * @author Jack Stockely
 * 
 * @version 0.9-beta
 *
 */
public class Updater {

	//
	private final static double version = 0.9;
	
	/**
	 * 
	 * @param auth
	 */
	protected static void checkUpdate(HashMap<String, String> auth, int delay) {
		HashMap<String, String> response = HTTP.get("https://raw.githubusercontent.com/jnstockley/BTTN/main/version");
		if(response.get("statusCode").equals("200")) {
			double serverVersion = Double.parseDouble(response.get("data"));
			if(serverVersion > version) {
				timeToSendAlert(30, serverVersion, auth);
				System.out.println("Update avaiable please update!");
				System.exit(0);
			}
		} else {
			System.err.println("Updater.java - error checking for new version!");
			System.exit(1);
		}
	}
	
	/**
	 * 
	 * @param delay
	 * @param serverVersion
	 * @param auth
	 */
	public static void timeToSendAlert(int delay, double serverVersion, HashMap<String, String> auth) {
		Date checkTime = new Date(getDateTime() + TimeUnit.MINUTES.toMillis(delay));
		Date curDate = new Date();
		System.out.println(checkTime);
		System.out.println(curDate);
		if(checkTime.compareTo(curDate) < 0) {
			Notifications.sendUpdateNotification(serverVersion, auth);
			writeDateTime(curDate.getTime());
		} else {
			System.out.println("Update but notif would not be sent");
		}
	}
	
	/**
	 * 
	 * @param time
	 */
	private static void writeDateTime(long time) {
		File timeFile = new File("lastSent.txt");
		FileWriter writer = null;
		try {
			writer = new FileWriter(timeFile);
			writer.write(Long.toString(time));
			writer.flush();
			writer.close();
		} catch (IOException e) {
			System.err.println("Updater.java - Error writing to time file!");
			System.exit(1);
		}
	}
	
	/**
	 * 
	 * @return
	 */
	private static long getDateTime() {
		File timeFile = new File("lastSent.txt");
		try {
			BufferedReader br = new BufferedReader(new FileReader(timeFile));
			Long time = Long.parseLong(br.readLine());
			br.close();
			return time;
		} catch (IOException | NumberFormatException e) {
			System.err.println("Updater.java - Error reading time file!");
			System.exit(1);
			return -1;
		} 
	}
}
