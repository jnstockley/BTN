//Helper.java
package com.github.jnstockley;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 * 
 * @author Jack Stockley
 * 
 * @version 0.11-beta
 *
 */
public class Helper {

	/**
	 * Reads the JSON config file and returns the old live status for each channel
	 * @param filepath Location of JSON config file
	 * @return HashMap with a channel and a boolean representing live state
	 */
	protected static HashMap<String, Boolean> getOldStatus(String filepath) {
		// Create HashMap
		HashMap<String, Boolean> oldStatus = new HashMap<String, Boolean>();
		// Reads the JSON file and gets the channels sections
		JSONObject statuses = getSection(filepath, "channels");
		// Converts JSON to HashMap
		for(Object channel: statuses.keySet()) {
			oldStatus.put(channel.toString(), (Boolean) statuses.get(channel.toString()));
		}
		return oldStatus;
	}

	/**
	 * Reads the JSON config file and gets the API Keys required for the program to run and returns them
	 * @param filepath Location of JSON config file
	 * @return HashMap with all the API keys used in the program
	 */
	protected static HashMap<String, String> getAuth(String filepath) {
		// Makes sure the JSON config file isn't empty
		HashMap<String, String> auth = new HashMap<String, String>();
		File file = new File(filepath);
		if(file.length() == 0) {
			Logger.logError(Bundle.getString("emptyFile", filepath));
		}
		// Gets the auth section from the JSON object
		JSONObject authJSON = getSection(filepath, "auth");
		// Makes sure the JSON auth section has Twitch key and get keys and convert them to HashMap
		if(authJSON.containsKey("twitch")) {
			JSONObject twitch = (JSONObject) authJSON.get("twitch");
			if(twitch.containsKey("clientID") && twitch.containsKey("authorization")) {
				auth.put("twitch-client-id", twitch.get("clientID").toString());
				auth.put("twitch-authorization", "Bearer " + twitch.get("authorization").toString());
			} else {
				Logger.logError(Bundle.getString("noTwitchKey"));
			}
		} else {
			Logger.logError(Bundle.getString("noTwitch", filepath));
		}
		// Makes sure the JSON auth section has Spontit key and get keys and convert them to HashMap
		if(authJSON.containsKey("spontit")) {
			JSONObject spontit = (JSONObject) authJSON.get("spontit");
			if(spontit.containsKey("userID") && spontit.containsKey("authorization")) {
				auth.put("spontit-user-id", spontit.get("userID").toString());
				auth.put("spontit-authorization", spontit.get("authorization").toString());
			} else {
				Logger.logError(Bundle.getString("noSpontitKey"));
			}
		} else {
			Logger.logError(Bundle.getString("noSpontit", filepath));
		}
		return auth;
	}

	/**
	 * Uses the Twitch API to get the current live status of the channels in the JSON config file
	 * @param channels Set of all the channels to check
	 * @param auth HashMap with the API Keys
	 * @return HashMap with the channel and boolean representing the live status
	 */
	protected static HashMap<String, Boolean> getStatus(Set<String> channels, HashMap<String, String> auth) {
		// Get the Twitch API keys and converts them to one the Twitch API can easily understand
		HashMap<String, Boolean> currStatus = new HashMap<String, Boolean>();
		HashMap<String, String> twitchAuth = new HashMap<String, String>();
		twitchAuth.put("client-id", auth.get("twitch-client-id"));
		twitchAuth.put("Authorization", auth.get("twitch-authorization"));
		JSONParser parser = new JSONParser();
		HashMap<String, String> response;
		// Makes and hTTP get request for each channel to get live status
		for(String channel: channels) {
			response = HTTP.get("https://api.twitch.tv/helix/search/channels?query=" + channel, twitchAuth);
			// Makes sure HTTP request returned 200 status code
			if(Integer.parseInt(response.get("statusCode"))!=200) {
				Notifications.sendErrorNotification(response, auth);
				Logger.logError(Bundle.getString("twitchStatus", response.toString()));
			} else {
				//TODO Make sure comments are actuate
				// Gets JSON data from HTTP request
				JSONObject json = null;
				try {
					json = (JSONObject) parser.parse(response.get("data"));
				} catch (ParseException e1) {
					Logger.logError(Bundle.getString("badJSONForm"));
				}
				// Parse JSON from HTTP request
				Object[] jsonArr = ((JSONArray) json.get("data")).toArray();
				List<String> data = new ArrayList<String>();
				for(int i=0; i<jsonArr.length; i++) {
					data.add(jsonArr[i].toString());
				}
				// Parses JSON data
				for(String channelData: data) {
					JSONObject channelJson = null;
					try {
						channelJson = (JSONObject)parser.parse(channelData);
					} catch (ParseException e) {
						Logger.logError(Bundle.getString("badJSONForm"));
					}
					// Makes sure channels name match and checks if live status is true
					if(channelJson.get("display_name").equals(channel) && channelJson.get("is_live").toString().equals("true")) {
						currStatus.put(channel, true);
					}
				}
				if(!currStatus.containsKey(channel)) {
					currStatus.put(channel, false);
				}	
			}
		}
		return currStatus;
	}

	/**
	 * Writes live status changes for the channels to the JSON config file
	 * @param currStatus HashMap with update live status for channels
	 * @param filepath Location of JSON config file
	 */
	@SuppressWarnings("unchecked")
	protected static void updateStatusFile(HashMap<String, Boolean> currStatus, String filepath) {
		JSONParser parser = new JSONParser();
		JSONObject json = new JSONObject();
		// Reads data from JSON config file
		try {
			json = (JSONObject) parser.parse(new FileReader(filepath));
		} catch (IOException | ParseException e1) {
			Logger.logError(Bundle.getString("badJSON", filepath));
		}
		// Update live status
		JSONObject channelJSON = new JSONObject(currStatus);
		json.remove("channels");
		json.put("channels", channelJSON);
		// Writes changes to JSON config file
		FileWriter writer;
		try {
			writer = new FileWriter(filepath);
			writer.write(json.toJSONString());
			writer.flush();
			writer.close();
		} catch (IOException e) {
			Logger.logError(Bundle.getString("badWrite", filepath));
		}
	}

	/**
	 * Reads the JSON config file and get update notification delay
	 * @param filepath Location of JSON config file
	 * @return An integer representing number of minutes between update notification being sent
	 */
	protected static int getDelay(String filepath) {
		// Set default delay
		int delay = 30;
		JSONParser parser = new JSONParser();
		JSONObject json = new JSONObject();
		// Makes sure JSON config file isn't empty
		File file = new File(filepath);
		if(file.length() != 0) {
			try {
				json = (JSONObject) parser.parse(new FileReader(filepath));
				// Reads update delay value if present
				if(json.containsKey("updateDelay")) {
					delay = Integer.parseInt(json.get("updateDelay").toString());
				}
			} catch (IOException | ParseException e) {
				Logger.logError(Bundle.getString("badWrite", filepath));
			}
		}
		return delay;
	}

	/**
	 * Reads the JSON config file and returns a specified JSON key 'section'
	 * @param filepath Location of JSON config file
	 * @param section String representing the JSON key to be returned
	 * @return JSON Object containing the value from the specified key
	 */
	private static JSONObject getSection(String filepath, String section) {
		JSONParser parser = new JSONParser();
		JSONObject jsonSection = new JSONObject();
		// Reads the JSON key from the JSON config file and returns it
		try {
			jsonSection = (JSONObject) ((JSONObject) parser.parse(new FileReader(filepath))).get(section);
		} catch (IOException | ParseException e) {
			Logger.logError(Bundle.getString("badWrite", filepath));
		}
		return jsonSection;
	}
}
