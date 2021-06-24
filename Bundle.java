package com.github.jnstockley;

import java.util.Locale;
import java.util.ResourceBundle;

/**
 * 
 * Helps with getting the user's current language and deciding which resource bundle to use
 * 
 * @author Jack Stockley
 * 
 * @version 1.51
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
}
