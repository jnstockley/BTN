//Auth.java
package com.github.jnstockley;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 * Helps with setting up the JSON config file with API key(s) for the services required
 * by BTTN.
 * 
 * @author Jack Stockley
 * 
 * @version 0.14-beta
 *
 */
public class Auth {

	/**
	 * Adds the Twitch Auth Key and Client ID to the JSON config file
	 * @param filepath The location of the Config file, in JSON  format
	 * @param reader A BufferedReader which reads user input from the console
	 * @param update Variable used to determine if function was called from update function
	 */
	@SuppressWarnings("unchecked")
	private static void twitchAddAuth(String filepath, BufferedReader reader, boolean update) {
		System.out.print(Bundle.getString("twitchClient"));
		// Get the Twitch client ID from console
		String clientID = null;
		try {
			clientID = reader.readLine();
		} catch (IOException e) {
			Logger.logError(Bundle.getString("badClient"));
		}
		// Get the Twitch Authentication Key from console
		System.out.print(Bundle.getString("twitchAuth"));
		String authorization = null;
		try {
			authorization = reader.readLine();
		} catch (IOException e) {
			Logger.logError(Bundle.getString("badAuth"));
		}
		// Create JSON Object with Twitch keys
		JSONObject twitchAuthJSON = new JSONObject();
		twitchAuthJSON.put("clientID", clientID);
		twitchAuthJSON.put("authorization", authorization);
		JSONParser parser = new JSONParser();
		JSONObject json = new JSONObject();
		// Checks if config file is empty
		File file = new File(filepath);
		if(file.length() != 0) {
			try {
				json = (JSONObject) parser.parse(new FileReader(filepath));
			} catch (IOException | ParseException e) {
				Logger.logError(Bundle.getString("badJSON", filepath));
			}
		}
		// Checks if config file has auth key in JSON object and builds appropriate JSON object
		if(json.containsKey("auth")) {
			if(((JSONObject)json.get("auth")).containsKey("twitch")){
				Logger.logError(Bundle.getString("twitchKeys"));
			}
			JSONObject authJson = (JSONObject)json.get("auth");
			authJson.put("twitch", twitchAuthJSON);
			json.put("auth", authJson);
		} else {
			JSONObject twitch = new JSONObject();
			twitch.put("twitch", twitchAuthJSON);
			json.put("auth", twitch);
		}
		// Writes the JSON object to the file
		FileWriter writer;
		try {
			writer = new FileWriter(filepath);
			writer.write(json.toJSONString());
			writer.flush();
			writer.close();
		} catch (IOException e) {
			Logger.logError(Bundle.getString("badWrite", filepath));
		}
		// Checks if functions was called from update function
		if(update) {
			Logger.logInfo(Bundle.getString("updateTwitch"));
		} else {
			Logger.logInfo(Bundle.getString("addTwitch"));
		}
	}

	/**
	 * Adds the Spontit Auth Key(s) and User ID to the JSON config file
	 * Allows multiple keys and userIDs to overcome Spontit issues with multi-device
	 * support
	 * @param filepath The location of the Config file, in JSON  format
	 * @param reader A BufferedReader which reads user input from the console
	 * @param update Variable used to determine if function was called from update function
	 */
	@SuppressWarnings("unchecked")
	private static void spontitAddAuth(String filepath, BufferedReader reader, boolean update) {
		// Asks the user how many Spontit API keys they want to add, used to
		// overcome issue with Spontit and multi-device support
		System.out.println(Bundle.getString("numUsers"));
		System.out.print(Bundle.getString("number"));
		int numKeys = -1;
		try {
			numKeys = Integer.parseInt(reader.readLine());
		} catch (NumberFormatException | IOException e1) {
			Logger.logError(Bundle.getString("invalidNum"));
		}
		//List<String> authKeys = new ArrayList<String>();
		//List<String> userIDs = new ArrayList<String>();
		JSONArray authKeys = new JSONArray();
		JSONArray userIDs = new JSONArray();

		// Allows user to enter X number of userIDs and API Key(s)
		for(int i=0; i< numKeys; i++) {
			// Gets the Spontit auth key
			System.out.print(Bundle.getString("spontitAuth"));
			String authorization = null;
			try {
				authorization = reader.readLine();
			} catch (IOException e) {
				Logger.logError(Bundle.getString("badAuth"));
			}
			// Gets the Spontit User ID
			System.out.print(Bundle.getString("spontitUser"));
			String userID = null;
			try {
				userID = reader.readLine();
			} catch (IOException e) {
				Logger.logError(Bundle.getString("badUser"));
			}
			authKeys.add(authorization);
			userIDs.add(userID);
		}
		// Builds the Spontit JSON Object
		JSONObject spontitAuthJSON = new JSONObject();
		spontitAuthJSON.put("userID", userIDs);
		spontitAuthJSON.put("authorization", authKeys);
		JSONParser parser = new JSONParser();
		JSONObject json = new JSONObject();
		// Checks if the config file is empty
		File file = new File(filepath);
		if(file.length() !=0) {
			try {
				json = (JSONObject) parser.parse(new FileReader(filepath));
			} catch (IOException | ParseException e) {
				Logger.logError(Bundle.getString("badJSON", filepath));
			}
		}
		// Checks if config file has auth key in JSON object and builds appropriate JSON object
		if(json.containsKey("auth")) {
			if(((JSONObject)json.get("auth")).containsKey("spontit")){
				Logger.logError(Bundle.getString("spontitKeys"));
			}
			JSONObject authJson = (JSONObject)json.get("auth");
			authJson.put("spontit", spontitAuthJSON);
			json.put("auth", authJson);
		} else {
			JSONObject spontit = new JSONObject();
			spontit.put("spontit", spontitAuthJSON);
			json.put("auth", spontit);
		}
		// Writes the JSON object to the config file
		FileWriter writer;
		try {
			writer = new FileWriter(filepath);
			writer.write(json.toJSONString());
			writer.flush();
			writer.close();
		} catch (IOException e) {
			Logger.logError(Bundle.getString("badWrite", filepath));
		}
		// Checks if functions was called from update function 
		if(update) {
			Logger.logInfo(Bundle.getString("updateSpontit"));
		} else {
			Logger.logInfo(Bundle.getString("addSpontit"));
		}
	}

	/**
	 * Removes the Twitch Auth Key and Client ID to the JSON config file
	 * @param filepath The location of the Config file, in JSON  format
	 * @param reader A BufferedReader which reads user input from the console
	 * @param update Variable used to determine if function was called from update function
	 */
	private static void twitchRemoveAuth(String filepath, BufferedReader reader, boolean update) {
		JSONParser parser = new JSONParser();
		JSONObject json = new JSONObject();
		// Checks if config file is empty
		File file = new File(filepath);
		if(file.length() ==0) {
			Logger.logError(Bundle.getString("emptyAuth", filepath));
		}
		// Reads JSON file
		try {
			json = (JSONObject) parser.parse(new FileReader(filepath));
		} catch (IOException | ParseException e) {
			Logger.logError(Bundle.getString("badJSON", filepath));
		}
		// Checks JSON object for required JSON keys and removes the Twitch JSON key
		if(json.containsKey("auth") && ((JSONObject)json.get("auth")).containsKey("twitch")){
			((JSONObject)json.get("auth")).remove("twitch");
			FileWriter writer;
			try {
				writer = new FileWriter(filepath);
				writer.write(json.toJSONString());
				writer.flush();
				writer.close();
			} catch (IOException e) {
				Logger.logError(Bundle.getString("badWrite", filepath));
			}
			// Checks if functions was called from update function 
			if(!update) {
				Logger.logWarn(Bundle.getString("removeTwitch"));
			}
		}else {
			Logger.logWarn(Bundle.getString("noTwitch", filepath));
		}
	}

	/**
	 * Removes the Spontit Auth Key and User ID to the JSON config file
	 * @param filepath The location of the Config file, in JSON  format
	 * @param reader A BufferedReader which reads user input from the console
	 * @param update Variable used to determine if function was called from update function
	 */
	private static void spontitRemoveAuth(String filepath, BufferedReader reader, boolean update) {
		JSONParser parser = new JSONParser();
		JSONObject json = new JSONObject();
		// Checks if config file is empty
		File file = new File(filepath);
		if(file.length() ==0) {
			Logger.logError(Bundle.getString("emptyAuth", filepath));
		}
		//Reads JSON file
		try {
			json = (JSONObject) parser.parse(new FileReader(filepath));
		} catch (IOException | ParseException e) {
			Logger.logError(Bundle.getString("badJSON", filepath));
		}
		// Checks JSON object for required JSON keys and removes the Spontit JSON key
		if(json.containsKey("auth") && ((JSONObject)json.get("auth")).containsKey("spontit")){
			((JSONObject)json.get("auth")).remove("spontit");
			FileWriter writer;
			try {
				writer = new FileWriter(filepath);
				writer.write(json.toJSONString());
				writer.flush();
				writer.close();
			} catch (IOException e) {
				Logger.logError(Bundle.getString("badWrite", filepath));
			}
			// Checks if functions was called from update function 
			if(!update) {
				Logger.logWarn(Bundle.getString("removeSpontit"));
			}
		}else {
			Logger.logError(Bundle.getString("noSpontit", filepath));
		}
	}

	/**
	 * Gets the service the user wants to modify and returns it
	 * @param reader A BufferedReader which reads user input from the console
	 * @return An integer representing the service number the user wants to modify
	 */
	private static int authSelection(BufferedReader reader) {
		// Prints the services and asks user which service they want to modify
		System.out.println(Bundle.getString("authType"));
		System.out.println(Bundle.getString("twitch"));
		System.out.println(Bundle.getString("spontit"));
		System.out.print(Bundle.getString("option"));
		// Makes sure option number is valid and returns it
		int option = -1;
		try {
			option = Integer.parseInt(reader.readLine());
		} catch (NumberFormatException | IOException e) {
			Logger.logError(Bundle.getString("invalidOpt"));
		}
		if(option > 2 || option < 1) {
			Logger.logError(Bundle.getString("invalidOpt"));
		}
		return option;
	}

	/**
	 * Main function which helps with selecting the service the user wants to modify
	 * and runs the corresponding function
	 * @param filepath The location of the Config file, in JSON  format
	 */
	protected static void setupAuth(String filepath) {
		// Prints the ways the user can modify the config file
		System.out.println(Bundle.getString("selOpt"));
		System.out.println(Bundle.getString("addAuth"));
		System.out.println(Bundle.getString("removeAuth"));
		System.out.println(Bundle.getString("updateAuth"));
		System.out.print(Bundle.getString("option"));
		// Makes sure the option number is valid
		BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
		int option = -1;
		try {
			option = Integer.parseInt(reader.readLine());
		} catch (NumberFormatException | IOException e) {
			Logger.logError(Bundle.getString("invalidOpt"));
		}
		if(option > 3 || option < 1) {
			Logger.logError(Bundle.getString("invalidOpt"));
		}
		// Gets the service the user wants to modify
		int service = authSelection(reader);
		switch(option) {
		// Add functions
		case 1:
			switch(service) {
			case 1:
				// Add Twitch
				twitchAddAuth(filepath, reader, false);
				break;
			case 2:
				// Add Spontit
				spontitAddAuth(filepath, reader, false);
				break;
			}
			break;
		default:
			Logger.logError(Bundle.getString("invalidOpt"));
			// Remove functions
		case 2:
			switch(service) {
			case 1:
				// Remove Twitch
				twitchRemoveAuth(filepath, reader, false);
				break;
			case 2:
				// Remove Spontit
				spontitRemoveAuth(filepath, reader, false);
				break;
			default:
				Logger.logError(Bundle.getString("invalidOpt"));
			}
			break;
			// Update functions
		case 3:
			switch(service) {
			case 1:
				// Update Twitch
				twitchRemoveAuth(filepath, reader, true);
				twitchAddAuth(filepath, reader, true);
				break;
			case 2:
				// Update Spontit
				spontitRemoveAuth(filepath, reader, true);
				spontitAddAuth(filepath, reader, true);
				break;
			default:
				Logger.logError(Bundle.getString("invalidOpt"));
			}
			break;
		}
	}
}
