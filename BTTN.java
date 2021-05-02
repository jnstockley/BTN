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
		System.out.println("What do you need to setup?\nEnter the opotion number below!");
		System.out.println("1. Add/Remove Channel");
		System.out.println("2. Add/Remove Authentication Keys");
		System.out.print("Option Number: ");
		BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
		int option = -1;
		try {
			option = Integer.parseInt(reader.readLine());
		} catch (NumberFormatException | IOException e) {
			System.err.println("Channel.java - invalid number provided or trouble reading the number provided!");
			System.exit(1);
		}
		switch(option) {
		case 1:
			Auth.setupAuth(filepath);
			break;
		case 2:
			Channel.setupChannels(filepath);
			break;
		default:
			System.err.println("Invalid option inputted!");
			System.exit(1);
		}
	}

	/**
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		if(args.length < 1 || args.length > 2) {
			System.err.println("Invalid arguments, make sure you are only providing the full path to your JSON config file!");
			System.exit(1);
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
			if(!nowLive.isEmpty()) {
				Collections.sort(nowLive);
				Notifications.sendLiveNotification(nowLive, auth);
			} else {
				System.out.println("BTTN Checked all channels, no updates!");
			}
			Helper.updateStatusFile(currStatus, args[0]);
		} else if(args[1].contains("setup")) {
			setup(args[0]);
		} else {
			System.err.println("Invalid Argument provided!");
			System.exit(1);
		}
		/* TODO
		 * Improve notifications with stream name and game???
		 * Add comments and javadoc
		 * Logging
		 */
	}
}