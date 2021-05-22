//BTTN.java
package com.github.jnstockley;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

/**
 * 
 * The main class of the program which runs the program, helps
 * with setting up the JSON config file, and reading the JSON
 * file to get user specific data.
 * 
 * @author Jack Stockley
 * 
 * @version 0.13-beta
 * 
 */
public class BTTN {

	/**
	 * A Helper Function which determines how the program
	 * needs to proceeded while setting up the config file.
	 * Take a String which is the file path to an already created
	 * JSON file.
	 * @param filepath Path to JSON file, which already exists
	 */
	private static void setup(String filepath) {
		// Setup Questions
		System.out.println(Bundle.getString("setupQ"));
		System.out.println(Bundle.getString("modChan"));
		System.out.println(Bundle.getString("modKeys"));
		System.out.println(Bundle.getString("updateConfig"));
		System.out.print(Bundle.getString("option"));
		// Gets user input and makes sure it's valid
		BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
		int option = -1;
		try {
			option = Integer.parseInt(reader.readLine());
		} catch (NumberFormatException | IOException e) {
			Logger.logError(Bundle.getString("invalidNum"));
		}
		// Calls the corresponding option selected by the user
		switch(option) {
		case 1:
			Auth.setupAuth(filepath);
			Helper.addMisc(filepath);
			break;
		case 2:
			Channel.setupChannels(filepath);
			Helper.addMisc(filepath);
			break;
		case 3:
			Helper.configUpgrade(filepath);
			Helper.addMisc(filepath);
			break;
		default:
			Logger.logError(Bundle.getString("invalidOpt"));
		}
	}

	/**
	 * The main function of the program which setups all the variables
	 * used throughout the program. If user provided, runs the setup
	 * part of the program to help with configuration.
	 * @param args Either 1 or 2 arguments. The first one should
	 * always be the JSON config file, the second, optional argument, must be 
	 * a keyword 'setup' or the equivalent in the users language.
	 */
	public static void main(String[] args) {
		// Checks to make sure arguments provided are valid
		if(args.length == 1){
			if(!Helper.configUpToDate(args[0])) {
				Logger.logError(Bundle.getString("badJSONVersion"));
			}
			// Creates 'dictionaries' used throughout the program
			HashMap<String, Boolean> oldStatus = Helper.getOldStatus(args[0]);
			HashMap<String, HashMap<String, String>> currStatus = new HashMap<String, HashMap<String, String>>();
			HashMap<String, String> auth = Helper.getAuth(args[0]);
			// Gets how often update notifications are sent out
			int delay = Helper.getDelay(args[0]);
			//Checks for updates
			Updater.checkUpdate(auth, delay);
			// Creates lists of channels and channels that are live
			HashMap<String, HashMap<String, String>> nowLive = new HashMap<String, HashMap<String, String>>();
			Set<String> channels = new HashSet<String>();
			channels = oldStatus.keySet();
			currStatus = Helper.getStatus(channels, auth);
			// Loops through all the channels and sees if the channels has gone live
			for(String channel: currStatus.keySet()) {
				if(currStatus.get(channel).get("live").equals("true") && !oldStatus.get(channel)) {
					nowLive.put(channel, currStatus.get(channel));
				}
			}
			// Updates the config file with new status
			Helper.updateStatusFile(currStatus, args[0]);
			// Sends out live notification is channels are now live
			if(!nowLive.isEmpty()) {
				Notifications.sendLiveNotification(nowLive, auth);
			} else {
				Logger.logInfo(Bundle.getString("noUpdates"));
			}
			// Runs the setup functions
		} else if(args.length == 2 && args[1].contains(Bundle.getString("setup"))) {
			setup(args[0]);
		// Displays an error if the args are invalid
		} else {
			Logger.logError(Bundle.getString("argsError"));
		}
		/* TODO
		 * Improve notifications with stream name and game???
		 * Check for bugs before 1.0 release, especially in file modifying
		 */
	}
}