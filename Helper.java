//Helper.java
package com.github.jnstockley;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 * Helper class, which focuses on retrieving data required throughout the program
 * 
 * @author Jack Stockley
 * 
 * @version 1.0-RC1
 *
 */
public class Helper {

	/**
	 * Private variable that describes the latest version of the JSON config file
	 */
	private static final double jsonVersion = 0.14;

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
	 * Uses the Twitch API to get the current live status, and game channel is streaming of the channels in the JSON config file
	 * @param channels Set of all the channels to check
	 * @param auth HashMap with the API Keys
	 * @return HashMap with the channel and another HashMap with String representation of live status and game streaming
	 */
	protected static HashMap<String, HashMap<String, String>> getStatus(Set<String> channels, HashMap<String, String> auth) {
		// Get the Twitch API keys and converts them to one the Twitch API can easily understand
		HashMap<String, HashMap<String, String>> currStatus = new HashMap<String, HashMap<String, String>>();
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
					HashMap<String, String> chanData = new HashMap<String, String>();
					JSONObject channelJson = null;
					try {
						channelJson = (JSONObject)parser.parse(channelData);
					} catch (ParseException e) {
						Logger.logError(Bundle.getString("badJSONForm"));
					}
					// Makes sure channels name match and checks if live status is true
					if(channelJson.get("display_name").equals(channel) && channelJson.get("is_live").toString().equals("true")) {
						chanData.put("live", "true");
						chanData.put("game", channelJson.get("game_name").toString());
						chanData.put("title", channelJson.get("title").toString());
						currStatus.put(channel, chanData);
					}
				}
				if(!currStatus.containsKey(channel)) {
					HashMap<String, String> chanData = new HashMap<String, String>();
					chanData.put("live", "false");
					currStatus.put(channel, chanData);
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
	protected static void updateStatusFile(HashMap<String, HashMap<String, String>> currStatus, String filepath) {
		HashMap<String, Boolean> liveStatus = new HashMap<String, Boolean>();
		Set<String> channels = currStatus.keySet();
		for(String channel: channels) {
			liveStatus.put(channel, Boolean.parseBoolean(currStatus.get(channel).get("live")));
		}
		JSONParser parser = new JSONParser();
		JSONObject json = new JSONObject();
		// Reads data from JSON config file
		try {
			json = (JSONObject) parser.parse(new FileReader(filepath));
		} catch (IOException | ParseException e1) {
			Logger.logError(Bundle.getString("badJSON", filepath));
		}
		// Update live status
		JSONObject channelJSON = new JSONObject(liveStatus);
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
			Logger.logError(Bundle.getString("badJSON", filepath));
		}
		return jsonSection;
	}

	/**
	 * Helper function witch runs at the start of the main program to make sure the version
	 * number of the JSON config file is up to date
	 * @param filepath Location of JSON config file
	 * @return True if the config file is up to date, otherwise false
	 */
	protected static boolean configUpToDate(String filepath) {
		// Reads the config file
		JSONObject json = new JSONObject();
		JSONParser parser = new JSONParser();
		try {
			json = (JSONObject) parser.parse(new FileReader(filepath));
		} catch (IOException | ParseException e) {
			Logger.logError(Bundle.getString("badJSON", filepath));
		}
		// Makes sure JSON config file has version key and is up to date
		if(json.containsKey("version") && Double.parseDouble(json.get("version").toString()) == jsonVersion) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Performs upgrade to JSON config file to add new features and fix bugs
	 * @param filepath Location of the JSON config file
	 */
	@SuppressWarnings("unchecked")
	protected static void configUpgrade(String filepath) {
		// Reads the config file
		JSONObject json = new JSONObject();
		JSONParser parser = new JSONParser();
		try {
			json = (JSONObject) parser.parse(new FileReader(filepath));
		} catch (IOException | ParseException e1) {
			Logger.logError(Bundle.getString("badJSON", filepath));
		}
		// Makes sure the JSON config file has required keys and known version number
		if(json.containsKey("version") && Double.parseDouble(json.get("version").toString()) == jsonVersion) {
			Logger.logInfo(Bundle.getString("upToDate", filepath));
		}else if(json.containsKey("version") && Double.parseDouble(json.get("version").toString()) == 0.9) {
			if(json.containsKey("auth") && ((JSONObject) (json.get("auth"))).containsKey("spontit")) {
				// Performs conversion from single entry to JSON Array and update version number
				JSONObject spontit = (JSONObject)((JSONObject)json.get("auth")).get("spontit");
				String authorization = spontit.get("authorization").toString();
				String userID = spontit.get("userID").toString();
				JSONObject newSpontit = new JSONObject();
				newSpontit.put("authorization", "[" + authorization + "]");
				newSpontit.put("userID", "[" + userID + "]");
				((JSONObject)json.get("auth")).put("spontit", newSpontit);
				json.put("version", 0.13);
				// Writes changes to file and informs user of upgrade!
				try {
					FileWriter writer = new FileWriter(filepath);
					writer.write(json.toJSONString());
					writer.flush();
					writer.close();
				} catch (IOException e) {
					Logger.logError(Bundle.getString("badWrite", filepath));
				}
				configUpgrade(filepath);
			} else {
				Logger.logError(Bundle.getString("noSpontit"));
			}
		} else if(json.containsKey("version") && Double.parseDouble(json.get("version").toString()) == 0.13){
			if(json.containsKey("auth") && ((JSONObject) (json.get("auth"))).containsKey("spontit")){
				// Performs upgrades to fix JSONArray bug in version 0.13
				JSONObject auth = new JSONObject();
				auth.put("twitch", (JSONObject)((JSONObject)json.get("auth")).get("twitch"));
				JSONObject spontit = (JSONObject)((JSONObject)json.get("auth")).get("spontit");
				JSONArray userIDs = new JSONArray();
				JSONArray authKeys = new JSONArray();
				String keyStr = spontit.get("authorization").toString().substring(1, spontit.get("authorization").toString().length()-1);
				String userStr = spontit.get("userID").toString().substring(1, spontit.get("userID").toString().length()-1);
				List<String> userList = Arrays.asList(userStr);
				List<String> keyList = Arrays.asList(keyStr);
				for(int i=0; i<userList.size(); i++) {
					userIDs.add(userList.get(i));
					authKeys.add(keyList.get(i));
				}
				spontit.put("authorization", authKeys);
				spontit.put("userID", userIDs);

				auth.put("spontit", spontit);
				json.put("auth", auth);
				json.put("version", 0.14);
				// Writes changes to file and informs user of upgrade!
				try {
					FileWriter writer = new FileWriter(filepath);
					writer.write(json.toJSONString());
					writer.flush();
					writer.close();
				} catch (IOException e) {
					Logger.logError(Bundle.getString("badWrite", filepath));
				}
				System.out.println(Bundle.getString("updateComplete"));
			} else {
				Logger.logError(Bundle.getString("noSpontit"));
			}
		} else {
			Logger.logError(Bundle.getString("noVersion"));
		}
	}

	/**
	 * Helper function which makes sure the miscellaneous JSON keys are in the JSON config file
	 * and if not adds them to the file
	 * @param filepath Location of the JSON config file
	 */
	@SuppressWarnings("unchecked")
	protected static void addMisc(String filepath) {
		// Reads the JSON config file
		JSONObject json = new JSONObject();
		JSONParser parser = new JSONParser();
		try {
			json = (JSONObject) parser.parse(new FileReader(filepath));
		} catch (IOException | ParseException e1) {
			Logger.logError(Bundle.getString("badJSON", filepath));
		}
		// Makes sure version key is in and if not adds current JSON version number
		if(!json.containsKey("version")) {
			json.put("version", jsonVersion);
			// Writes changes to JSON file
			try {
				FileWriter writer = new FileWriter(filepath);
				writer.write(json.toJSONString());
				writer.flush();
				writer.close();
			} catch (IOException e) {
				Logger.logError(Bundle.getString("badWrite", filepath));
			}
		}
		// Makes sure update delay key is in and if not asks user to enter a value
		if(!json.containsKey("updateDelay")) {
			System.out.print(Bundle.getString("updateDelay"));
			BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
			int delay = -1;
			try {
				delay = Integer.parseInt(reader.readLine());
				json.put("updateDelay", delay);
				// Writes changes to JSON file
				try {
					FileWriter writer = new FileWriter(filepath);
					writer.write(json.toJSONString());
					writer.flush();
					writer.close();
				} catch (IOException e) {
					Logger.logError(Bundle.getString("badWrite", filepath));
				}
			} catch (NumberFormatException | IOException e) {
				Logger.logError(Bundle.getString("invalidNum"));
			}
		}
	}
}
