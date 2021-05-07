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
	private final static double version = 0.10;
	
	/**
	 * 
	 * @param auth
	 */
	protected static void checkUpdate(HashMap<String, String> auth, int delay) {
		HashMap<String, String> response = HTTP.get("https://raw.githubusercontent.com/jnstockley/BTTN/main/version");
		if(response.get("statusCode").equals("200")) {
			double serverVersion = Double.parseDouble(response.get("data"));
			if((serverVersion > version) && timeToSendAlert(30)) {
				Notifications.sendUpdateNotification(serverVersion, auth);
				Logger.logInfo(Bundle.getString("updateAvailable"));
			}
		} else {
			Logger.logError(Bundle.getString("errCheck"));
		}
	}
	
	/**
	 * 
	 * @param delay
	 * @param serverVersion
	 * @param auth
	 */
	private static boolean timeToSendAlert(int delay) {
		Date checkTime = new Date(getDateTime() + TimeUnit.MINUTES.toMillis(delay));
		Date curDate = new Date();
		System.out.println(checkTime);
		System.out.println(curDate);
		if(checkTime.compareTo(curDate) < 0) {
			writeDateTime(curDate.getTime());
			return true;
		} else {
			return false;
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
			Logger.logError(Bundle.getString("timeWriteErr"));
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
			Logger.logError(Bundle.getString("timeReadErr"));
			return -1;
		} 
	}
}
