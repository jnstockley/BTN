//BTTN.java
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 
 * @author Jack 
 * 
 * @version 0.9-beta
 * 
 */
public class BTTN {

	/**
	 * 
	 * @param filepath
	 */
	private static void setup(String filepath) {
		System.out.println(Bundle.getString("setupQ"));
		System.out.println(Bundle.getString("modChan"));
		System.out.println(Bundle.getString("modKeys"));
		System.out.print(Bundle.getString("option"));
		BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
		int option = -1;
		try {
			option = Integer.parseInt(reader.readLine());
		} catch (NumberFormatException | IOException e) {
			Logger.logError(Bundle.getString("invalidNum"));
		}
		switch(option) {
		case 1:
			Auth.setupAuth(filepath);
			break;
		case 2:
			Channel.setupChannels(filepath);
			break;
		default:
			Logger.logError(Bundle.getString("invalidOpt"));
		}
	}

	/**
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		if(args.length < 1 || args.length > 2) {
			Logger.logError(Bundle.getString("argsError"));
		} else if(args.length == 1){
			HashMap<String, Boolean> oldStatus = new HashMap<String, Boolean>();
			HashMap<String, Boolean> currStatus = new HashMap<String, Boolean>();
			HashMap<String, String> auth = Helper.getAuth(args[0]);
			int delay = Helper.getDelay(args[0]);
			Updater.checkUpdate(auth, delay);
			List<String> nowLive = new ArrayList<String>();
			Set<String> channels = new HashSet<String>();
			oldStatus = Helper.getOldStatus(args[0]);
			channels = oldStatus.keySet();
			currStatus = Helper.getStatus(channels, auth);
			for(String channel: currStatus.keySet()) {
				if(currStatus.get(channel) && !oldStatus.get(channel)) {
					nowLive.add(channel);
				}
			}
			Helper.updateStatusFile(currStatus, args[0]);
			if(!nowLive.isEmpty()) {
				Collections.sort(nowLive);
				Notifications.sendLiveNotification(nowLive, auth);
			} else {
				Logger.logInfo(Bundle.getString("noUpdates"));
			}
		} else if(args[1].contains(Bundle.getString("setup"))) {
			setup(args[0]);
		} else {
			Logger.logError(Bundle.getString("argsError"));
		}
		/* TODO
		 * Improve notifications with stream name and game???
		 * Add comments and javadoc
		 * Fix daily delete of log file
		 * Fix error when exporting???
		 * Check for bugs before 1.0 release, especially in file modifying
		 */
	}
}