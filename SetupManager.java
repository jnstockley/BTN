package com.github.jnstockley;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * 
 * Class that manages the program when in setup mode, and controls the flow of the program
 * 
 * @author Jack Stockley
 * 
 * @version 1.5
 *
 */
public class SetupManager {

	/**
	 *  Name of the file used to log data
	 */
	private static final String CLASSNAME = SetupManager.class.getName();

	/**
	 * Displays the setup welcome message and asks what the user wants to modify
	 * @param file The JSON config file
	 */
	public static void setup(File file) {
		// Display the welcome message and asks user what they want to modify
		System.out.println(Bundle.getBundle("setupWelcome"));
		System.out.println(Bundle.getBundle("modifyKeys"));
		System.out.println(Bundle.getBundle("modifyChans"));
		System.out.println(Bundle.getBundle("modifyFailover"));
		System.out.println(Bundle.getBundle("wikiHelp"));
		System.out.print(Bundle.getBundle("option"));
		// Gets the users input
		BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
		int option = -1;
		try {
			option = Integer.parseInt(reader.readLine());
		} catch (NumberFormatException | IOException e) {
			Logging.logError(CLASSNAME, e);
		}
		switch(option) {
		// Modify API Keys
		case 1:
			setupAPI(reader);
			break;
			// Modify Channels
		case 2:
			setupChannels(reader);
			break;
			// Modify failover
		case 3:
			setupFailover(reader);
			break;
			// View WiKi
		case 4:
			accessWiki();
			break;
			// Invalid option
		default:
			Logging.logError(CLASSNAME, Bundle.getBundle("invalidOption", Integer.toString(option)));
			break;
		}
	}

	/**
	 * Asks user how they want to modify their API keys
	 * @param reader BufferedReader used to get user input from the console
	 */
	private static void setupAPI(BufferedReader reader) {
		// Asks user how they want to modify the API keys
		System.out.println(Bundle.getBundle("howModifyKeys"));
		System.out.println(Bundle.getBundle("addKeys"));
		System.out.println(Bundle.getBundle("removeKeys"));
		System.out.print(Bundle.getBundle("option"));
		// Gets the users input
		int option = -1;
		try {
			option = Integer.parseInt(reader.readLine());
		} catch (NumberFormatException | IOException e) {
			Logging.logError(CLASSNAME, e);
		}
		switch(option) {
		// Add Keys
		case 1:
			Auth.addAPIKeys(reader);
			break;
			// Remove Keys
		case 2:
			Auth.removeAPIKeys(reader);
			break;
			// Invalid option
		default:
			Logging.logError(CLASSNAME, Bundle.getBundle("invalidOption", Integer.toString(option)));
			break;
		}
	}

	/**
	 * Asks user how they want to modify the twitch channels being checked
	 * @param reader BufferedReader used to get user input from the console
	 */
	private static void setupChannels(BufferedReader reader) {
		// Asks user how they want to modify the twitch channels
		System.out.println(Bundle.getBundle("howModifyChan"));
		System.out.println(Bundle.getBundle("addChan"));
		System.out.println(Bundle.getBundle("removeChan"));
		System.out.print(Bundle.getBundle("option"));
		// Gets the users input
		int option = -1;
		try {
			option = Integer.parseInt(reader.readLine());
		} catch (NumberFormatException | IOException e) {
			Logging.logError(CLASSNAME, e);
		}
		switch(option) {
		// Add Channel(s)
		case 1:
			Channel.addChannels(reader);
			break;
			// Remove Channel(s)
		case 2:
			Channel.removeChannels(reader);
			break;
		default:
			// Invalid option
			Logging.logError(CLASSNAME, Bundle.getBundle("invalidOption", Integer.toString(option)));
			break;
		}
	}

	/**
	 * Asks user how they want to modify the failover method
	 * @param reader BufferedReader used to get user input from the console
	 */
	private static void setupFailover(BufferedReader reader) {
		// Asks user how they want to modify the failover method
		System.out.println(Bundle.getBundle("howModifyFailover"));
		System.out.println(Bundle.getBundle("addFailover"));
		System.out.println(Bundle.getBundle("removeFailover"));
		System.out.print(Bundle.getBundle("option"));
		// Gets the users input
		int option = -1;
		try {
			option = Integer.parseInt(reader.readLine());
		} catch (NumberFormatException | IOException e) {
			Logging.logError(CLASSNAME, e);
		}
		switch(option) {
		// Add failover
		case 1:
			Failover.addFailover(reader);
			break;
			// Remove failover
		case 2:
			Failover.removeFailover(reader);
			break;
		default:
			// Invalid option
			Logging.logError(CLASSNAME, Bundle.getBundle("invalidOption", Integer.toString(option)));
			break;
		}
	}

	/**
	 * Opens the GitHub WiKi page in the use's default web browser
	 */
	private static void accessWiki() {
		URI webpage;
		try {
			webpage = new URI("https://github.com/jnstockley/BTTN/wiki");
			java.awt.Desktop.getDesktop().browse(webpage);
		} catch (URISyntaxException | IOException e) {
			Logging.logError(CLASSNAME, e);
		}
	}

}
