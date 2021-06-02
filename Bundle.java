//Bundle.java
package com.github.jnstockley;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * Helper class used to help when using the resource bundle to help
 * support multiple languages
 * 
 * @author Jack Stockley
 * 
 * @version 1.0
 *
 */
public class Bundle {

	/**
	 * Gets the locale for the user
	 */
	private static final Locale LOCALE = new Locale(Locale.getDefault().getLanguage()); 
	/**
	 * Gets the bundle to be used based on the users locale
	 */
	private static final ResourceBundle bundle = getBundle("bundle");

	/**
	 * Helper function to help with getting the resource file when running
	 * in the real world or in an IDE
	 * @param bundle The String name of the resource bundle
	 * @return The resource bundle that best fits to the user's locale
	 */
	private static ResourceBundle getBundle(String bundle) {
		// Tries to get the real world resource bundle first then the IDE
		try {
			return ResourceBundle.getBundle("resources/" + bundle, LOCALE);
		} catch(Exception e) {
			return ResourceBundle.getBundle(bundle, LOCALE);
		}
	}

	/**
	 * Helper function used to retrieve the bundle key from resource file
	 * @param key String representation of the key
	 * @return The value associated with the key provided
	 */
	protected static String getString(String key) {
		return bundle.getString(key);
	}

	/**
	 * Helper function used to retrieve the bundle key from the resource file,
	 * also replaces '%f% with the 'value' passed into
	 * @param key String representation of the key
	 * @param value The String representation of a value to replace '%f%'
	 * @return The value associated with the key provided with '%f%' replaced with value passed
	 */
	protected static String getString(String key, String value) {
		return bundle.getString(key).replace("%f%", value);
	}

}
