package com.github.jnstockley;

import java.util.Locale;
import java.util.ResourceBundle;

/**
 *
 * Helps with getting the user's current language and deciding which resource bundle to use
 *
 * @author Jack Stockley
 *
 * @version 1.62
 *
 */
public class Bundle {

	/**
	 *  Name of the Class used for Logging error
	 */
	private static final String CLASSNAME = Bundle.class.getName();

	/**
	 *  The user's Locale used for getting correct resource bundle 
	 */
	private static final Locale LOCALE = new Locale(Locale.getDefault().getLanguage());

	/**
	 *  The resource bundle based on user's locale
	 */
	private static final ResourceBundle BUNDLE = ResourceBundle.getBundle("bundle", LOCALE);

	/**
	 * If the resource bundle contains the key, it returns it from the resource bundle if not returns the key
	 * @param key The key to represent the bundle
	 * @return The value of the key if it exists or the key
	 */
	public static String getBundle(String key) {
		// Checks if key is in the bundle
		if(BUNDLE.containsKey(key)) {
			return BUNDLE.getString(key);
			// Returns key and logs missing key
		} else {
			Logging.logWarn(CLASSNAME, BUNDLE.getString("missingKey") + key + " " + LOCALE);
			return key;
		}
	}

	/**
	 * Uses getBundle(key) to get the key from the resource bundle and then replaces %f% with value passed
	 * @param key The key to represent the bundle
	 * @param value A string that replaces %f% with
	 * @return The value of the key with %f% replaced with value
	 */
	public static String getBundle(String key, String value) {
		// Gets the bundle from the resource bundle
		String message = getBundle(key);
		// Checks if %f% is in the string and replaces it with value
		if(message.contains("%f%")) {
			message = message.replace("%f%", value);
		}
		return message;
	}
	
	/**
	 * Helper function used to verify resource bundles work in all supported languages
	 */
	protected static void bundleTest() {
		int keys = 23;
		String[] auth = {"missingAPIKey", "noAPIKeys", "addTwitchAPI", "twitchSecret", "twitchClient", "addAlertzy", "numAlertzyKeys", "noAlertzyKeys", "enterAlertzyKey",
				"noAPIKeysAdded", "APIKeysAdded", "removeTwitchAPI", "removeAlertzyAPI", "APIKeysRemoved", "yes", "y", "no", "n", "isEmpty", "noAPIKeysSelRem",
				"invalidTwitchKeys", "validAlertzyKeys", "invalidAlertzyKeys", "twitchReAuth"};
		String[] BTTN = {"debugMode", "BTTNTesting", "BTTNRelease", "build", "statusUnchanged", "invalidArgs", "invalidConfig", "chanNotFound"};
		String[] bundle = {"missingKey"};
		String[] channel = {"numChanAdd", "invalidChanNum", "chanName", "addedChan", "channelsChecked", "chanRemove", "removedChan", "noChannels", "invalidChan"};
		String[] failover = {"failoverSubject", "failoverSent", "failoverTextEmail", "enterPhoneNumber", "selectProvider", "provider", "enterEmailAddress", "toName",
				"fromEmail", "smtpPassword", "smtpServer", "smtpPort", "failoverEnabled", "failoverRemoved", "failoverDisabled", "text", "email", "customProvider",
				"cellProviderExtension", "badSuffix", "badEmail", "badPhoneNumber"};
		String[] helper = {"errorCheckingStatus", "noChannels", "socketErrorRetry", "socketError", "expiredToken"};
		String[] logging = {"info", "warn", "error", "deleteLogError"};
		String[] notifications = {"encounteredError", "notSentEveryone", "notSent", "newVersion", "downloadUpdate", "updateTo", "isLive", "areLive", "notificationSent",
				"errorNotificationSent", "testTitle", "testMessage"};
		String[] setupManager = {"setupWelcome", "modifyKeys", "modifyChans", "modifyFailover", "wikiHelp", "option", "invalidOption", "howModifyKeys", "addKeys",
				"removeKeys", "reauthKeys", "howModifyChan", "addChan", "removeChan", "howModifyFailover", "addFailover", "removeFailover"};
		String[] updater = {"updateAvailable"};
		String[] global = {"viewUpdate", "openStream", "failoverNotEnabled"};
		String[] flags = {"setup", "setupDesc", "version", "versionDesc", "debug", "debugDesc", "config", "configDesc", "help", "helpDesc", "BTTNUsage"};
		System.out.println(LOCALE.getDisplayLanguage());
		System.out.println("-------------------------------------------");
		System.out.println("AUTH.java");
		for(String key: auth) {
			System.out.println(key + " - \"" + getBundle(key) + "\"");
			keys++;
		}
		System.out.println("-------------------------------------------");
		System.out.println("BTTN.java");
		for(String key: BTTN) {
			System.out.println(key + " - \"" + getBundle(key) + "\"");
			keys++;
		}
		System.out.println("-------------------------------------------");
		System.out.println("BUNDLE.java");
		for(String key: bundle) {
			System.out.println(key + " - \"" + getBundle(key) + "\"");
			keys++;
		}
		System.out.println("-------------------------------------------");
		System.out.println("CHANNEL.java");
		for(String key: channel) {
			System.out.println(key + " - \"" + getBundle(key) + "\"");
			keys++;
		}
		System.out.println("-------------------------------------------");
		System.out.println("FAILOVER.java");
		for(String key: failover) {
			System.out.println(key + " - \"" + getBundle(key) + "\"");
			keys++;
		}
		System.out.println("-------------------------------------------");
		System.out.println("HELPER.java");
		for(String key: helper) {
			System.out.println(key + " - \"" + getBundle(key) + "\"");
			keys++;
		}
		System.out.println("-------------------------------------------");
		System.out.println("LOGGING.java");
		for(String key: logging) {
			System.out.println(key + " - \"" + getBundle(key) + "\"");
			keys++;
		}
		System.out.println("-------------------------------------------");
		System.out.println("NOTIFICIATIONS.java");
		for(String key: notifications) {
			System.out.println(key + " - \"" + getBundle(key) + "\"");
			keys++;
		}
		System.out.println("-------------------------------------------");
		System.out.println("SETUPMANAGER.java");
		for(String key: setupManager) {
			System.out.println(key + " - \"" + getBundle(key) + "\"");
			keys++;
		}
		System.out.println("-------------------------------------------");
		System.out.println("UPDATER.java");
		for(String key: updater) {
			System.out.println(key + " - \"" + getBundle(key) + "\"");
			keys++;
		}
		System.out.println("-------------------------------------------");
		System.out.println("GLOBAL");
		for(String key: global) {
			System.out.println(key + " - \"" + getBundle(key) + "\"");
			keys++;
		}
		System.out.println("-------------------------------------------");
		System.out.println("FLAGS");
		for(String key: flags) {
			System.out.println(key + " - \"" + getBundle(key) + "\"");
			keys++;
		}
		System.out.println("-------------------------------------------");
		System.out.println("Keys: " + keys);
	}
}