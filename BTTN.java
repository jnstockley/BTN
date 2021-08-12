package com.github.jnstockley;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.json.simple.JSONObject;

/**
 * 
 * The main function for BTTN which passes arguments
 * and controls the flow of BTTN
 * 
 * @author Jack Stockley
 *
 * @version 1.6
 *
 */
public class BTTN {

	/**
	 *  Boolean used to determine if the program is ran in setup mode
	 */
	private static boolean setupMode = false;

	/**
	 *  Name of the Class used for Logging error
	 */
	private static final String CLASSNAME = BTTN.class.getName();

	/**
	 * File containing all of the configuration used for
	 * BTTN and the old status of channels being checked
	 */
	public static File configFile;

	/**
	 * Auth Object which holds all the users API Keys
	 */
	public static Auth auth;

	/**
	 * Boolean used to determine if BTTN should display
	 * more information when throwing errors
	 */
	public static boolean debug = false;

	/**
	 * The main function which runs BTTN
	 * @param args The command line arguments used to run the program
	 */
	public static void main(String[] args) {
		// Checks for test flag to test resource bundle
		for(String arg: args) {
			if(arg.equalsIgnoreCase("-test") || arg.equalsIgnoreCase("-t")) {
				Bundle.bundleTest();
				System.exit(0);
			}
		}
		// Read the arguments passed
		argsHandler(args);
		// Program not in setup mode
		if(!setupMode) {
			if(!Helper.validConfig()) {
				Logging.logError(CLASSNAME, Bundle.getBundle("invalidConfig", BTTN.configFile.getName()));
			}
			// Get API Keys from config file
			auth = new Auth(configFile);
			// Check for update
			Updater.updateAvailable();
			// Get Old Twitch.tv Live status
			List<Channel> allChannels = new ArrayList<Channel>();
			HashMap <String, Boolean> channels = Helper.getOldStatus(configFile);
			// Gets current Twitch.tv Live Status
			Set<String> channelNames = channels.keySet();
			for(String name: channelNames) {
				JSONObject liveStatus = Helper.curLiveStatus(auth, name);
				if(liveStatus == null) {
					Logging.logWarn(CLASSNAME, Bundle.getBundle("chanNotFound", name));
					Notifications.sendErrorNotification(Bundle.getBundle("chanNotFound", name), auth.getAlertzyAccountKey());
				}else {
					Channel channel = new Channel(liveStatus, channels.get(name));
					allChannels.add(channel);
				}
			}
			// Determine which channels have just gone live
			List<Channel> nowLive = new ArrayList<Channel>();
			for(Channel chan: allChannels) {
				if(chan.statusChange()) {
					nowLive.add(chan);
				}
			}
			// Update config file with new live statuses
			Helper.updateStatus(allChannels, configFile);
			// Send live notification for channels that have just gone live
			if(!nowLive.isEmpty()) {
				Notifications.sendLiveNotification(nowLive, auth.getAlertzyAccountKey());

			} else {
				Logging.logInfo(CLASSNAME, 	Bundle.getBundle("statusUnchanged"));
			}
			// Program in setup mode
		} else {
			SetupManager.setup(configFile);
		}
	}

	/**
	 * Helper functions which handles all the CLI arguments that could be passed to BTTN
	 * @param args String array of all the CLI arguments
	 */
	private static void argsHandler(String[] args) {
		// Adding all the possible arguments that could be passed
		Options options = new Options();
		options.addOption(Bundle.getBundle("config"), true, Bundle.getBundle("configDesc"));
		options.addOption(Bundle.getBundle("setup"), false, Bundle.getBundle("setupDesc"));
		options.addOption(Bundle.getBundle("version"), false, Bundle.getBundle("versionDesc"));
		options.addOption(Bundle.getBundle("debug"), false, Bundle.getBundle("debugDesc"));
		options.addOption(Bundle.getBundle("help"), false, Bundle.getBundle("helpDesc"));
		// Reading in all the CLI arguments
		CommandLineParser parser = new DefaultParser();
		CommandLine cmd = null;
		try {
			cmd = parser.parse(options, args);
		} catch (ParseException e) {
			Logging.logError(CLASSNAME, e);
		}
		// Help argument was passed
		if(cmd.hasOption(Bundle.getBundle("help"))) {
			HelpFormatter formatter = new HelpFormatter();
			formatter.printHelp(Bundle.getBundle("BTTNUsage"), options);
			System.exit(0);
		}
		// Version argument has passed, used to display which 'branch' BTTN is in
		if(cmd.hasOption(Bundle.getBundle("version"))) {
			if(Updater.TESTING) {
				System.out.println(Bundle.getBundle("BTTNTesting", Double.toString(Updater.VERSION)));
			} else {
				System.out.println(Bundle.getBundle("BTTNRelease", Double.toString(Updater.VERSION)));
			}
			System.out.println(Bundle.getBundle("build", Updater.BUILD));
			System.exit(0);
		}
		// Config argument was passed which has the path to the JSON config file
		if(cmd.hasOption(Bundle.getBundle("config"))) {
			BTTN.configFile = new File(cmd.getOptionValue("config"));
		}
		// Debug argument was passed which puts BTTN in debug mode
		if(cmd.hasOption(Bundle.getBundle("debug"))) {
			System.out.println(Bundle.getBundle("debugMode"));
			debug = true;
		}
		// Setup argument was passed, which puts BTTN in setup mode
		if(cmd.hasOption(Bundle.getBundle("setup"))) {
			setupMode = true;
		}
	}

	/**
	 * TODO 
	 * Function to check for expired or invalid OAuth token and alert user every 30 minutes
	 */
}
