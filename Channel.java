//Channel.java
package com.github.jnstockley;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 * Helps with modifying channels to be checked for changed live status
 * 
 * @author Jack Stockley
 * 
 * @version 1.0
 *
 */
public class Channel {

	/**
	 * Adds channel(s) to JSON config file
	 * @param filepath Location of JSON config file
	 * @param reader A BufferedReader which reads user input from console
	 */
	@SuppressWarnings("unchecked")
	private static void addChannels(String filepath, BufferedReader reader) {
		// Reads the JSON config file
		JSONParser parser = new JSONParser();
		JSONObject json = new JSONObject();
		JSONObject channels = new JSONObject();
		File file = new File(filepath);
		// Gets the number of current channels in the config file
		int oldSize = 0;
		if(file.exists() && file.length() != 0) {
			try {
				json = (JSONObject) parser.parse(new FileReader(filepath));
			} catch (IOException | ParseException e) {
				Logger.logError(Bundle.getString("badJSON", filepath));
			}
			if(json.containsKey("channels")) {
				channels = (JSONObject) json.get("channels");
				oldSize = channels.size();
			}

		}
		// Asks user to enter new channels
		System.out.print(Bundle.getString("getChan"));
		List<String> newChannels = null;
		try {
			newChannels = Arrays.asList(reader.readLine().split("\\s*,\\s*"));
		} catch (IOException e) {
			Logger.logError(Bundle.getString("badChan"));
		}
		// Converts channels into JSON with non-live state
		for(String channel: newChannels) {
			if(!channels.containsKey(channel.toLowerCase()) && checkChannel(channel)) {
				channels.put(channel.toLowerCase(), false);
			} else {
				Logger.logError(Bundle.getString("invalidChan", channel));
			}
		}
		json.put("channels", channels);
		// Makes sure user actually added new channels and writes them to file
		if(channels.size() != oldSize) {
			FileWriter writer;
			try {
				writer = new FileWriter(filepath);
				writer.write(json.toJSONString());
				writer.flush();
				writer.close();
			} catch (IOException e) {
				Logger.logError(Bundle.getString("badWrite", filepath));
			}
			Logger.logInfo(Bundle.getString("goodAdd"));
		} else {
			Logger.logInfo(Bundle.getString("noChanAdd"));
		}
	}

	/**
	 * Remove channel(s) to JSON config file
	 * @param filepath Location of JSON config file
	 * @param reader A BufferedReader which reads user input from console
	 */
	@SuppressWarnings("unchecked")
	private static void removeChannels(String filepath, BufferedReader reader) {
		// Reads the JSON config file
		JSONParser parser = new JSONParser();
		JSONObject json = new JSONObject();
		try {
			json = (JSONObject) parser.parse(new FileReader(filepath));
		} catch (IOException | ParseException e) {
			Logger.logError(Bundle.getString("badJSON", filepath));
		}
		JSONObject channels = (JSONObject) json.get("channels");
		// Gets the number of current channels in the config file
		int oldSize = channels.size();
		// Converts channels from JSON to a list
		System.out.println(Bundle.getString("selChan"));
		int index = 1;
		Set<String> channelSet = channels.keySet();
		List<String> channelNames = new ArrayList<String>();
		for(Object channel: channelSet) {
			channelNames.add(channel.toString());
		}
		// Sorts channels alphabetically and prints them out
		Collections.sort(channelNames);
		for(String channel: channelNames) {
			System.out.println(index + ": " + channel);
			index++;
		}
		// Asks user to enter index(s) of channels to remove
		System.out.print("Channels: ");
		List<String> indexs = null;
		try {
			indexs = Arrays.asList((reader.readLine().split("\\s*,\\s*")));
		} catch (IOException e) {
			Logger.logError(Bundle.getString("badChan"));
		}
		// Removes channels from Java List
		for(String channelIndex: indexs) {
			channels.remove(channelNames.get(Integer.parseInt(channelIndex)-1));
		}
		json.put("channels", channels);
		// Makes sure user actually is removing channels and writes them to file
		if(channels.size() != oldSize) {
			FileWriter writer;
			try {
				writer = new FileWriter(filepath);
				writer.write(json.toJSONString());
				writer.flush();
				writer.close();
			} catch (IOException e) {
				Logger.logError(Bundle.getString("badWrite"));
			}
			Logger.logInfo(Bundle.getString("goodDel", filepath));
		} else {
			Logger.logInfo(Bundle.getString("noChanDel"));
		}
	}

	/**
	 * Determines how the user wants to modify the channels and run the corresponding function
	 * @param filepath Location of JSON config file
	 */
	protected static void setupChannels(String filepath) {
		// Asks user how they want to modify the channels in the JSON config file
		System.out.println(Bundle.getString("selOpt"));
		System.out.println(Bundle.getString("addChan"));
		System.out.println(Bundle.getString("delChan"));
		System.out.print(Bundle.getString("option"));
		// Reads the user input and makes sure its valid
		BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
		int option = 0;
		try {
			option = Integer.parseInt(reader.readLine());
		} catch (NumberFormatException | IOException e) {
			Logger.logError(Bundle.getString("invalidNum"));
		}
		switch(option) {
		// Add Channels
		case 1:
			addChannels(filepath, reader);
			break;
			// Remove Channels
		case 2:
			removeChannels(filepath, reader);
			break;
		default:
			Logger.logError(Bundle.getString("invalidNum"));
		}
	}

	/**
	 * First checks to make sure the provided channel fits twitch's channel requirements then, makes
	 * an HTTP post request using Twitch's API to see if the username is taken (valid) or available(invalid).
	 * Returns true if channel is valid
	 * @param channel Name of the channel being checked
	 * @return True if channel is valid, otherwise false
	 */
	@SuppressWarnings("unchecked")
	public static boolean checkChannel(String channel) {
		// Uses regex to make sure username fits Twitch's username requirements
		Pattern chanPattern = Pattern.compile("[a-zA-Z0-9]{4,25}");
		Matcher chanMatcher = chanPattern.matcher(channel);
		if(!chanMatcher.find()) {
			return false;
		}
		// Makes HTTP request to see if channel is valid
		HashMap<String, String> headers = new HashMap<String, String>();
		headers.put("Client-ID", "kimne78kx3ncx6brgo4mv6wki5h1ko");
		headers.put("Content-Length", Integer.toString(198 + channel.length()));
		JSONObject data = new JSONObject();
		data.put("operationName", "UsernameValidator_User");
		JSONObject username = new JSONObject();
		username.put("username", channel);
		data.put("variables", username);
		JSONObject extensions = new JSONObject();
		JSONObject persistedQuery = new JSONObject();
		persistedQuery.put("version", 1);
		persistedQuery.put("sha256Hash", "fd1085cf8350e309b725cf8ca91cd90cac03909a3edeeedbd0872ac912f3d660");
		extensions.put("persistedQuery", persistedQuery);
		data.put("extensions", extensions);
		HashMap<String, String> response = HTTP.post("https://gql.twitch.tv/gql#origin=twilight", data.toJSONString(), headers);
		JSONParser parser = new JSONParser();
		JSONObject jsonResponse = new JSONObject();
		try {
			jsonResponse = (JSONObject) parser.parse(response.get("data"));
		} catch (ParseException e) {
			Logger.logError(Bundle.getString("badJSONForm"));
		}
		boolean valid = !(boolean) ((JSONObject) jsonResponse.get("data")).get("isUsernameAvailable");
		return valid;
	}
}
