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
import java.util.List;
import java.util.Set;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 * Helps with modifying channels to be checked for changed live status
 * 
 * @author Jack Stockley
 * 
 * @version 0.14-beta
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
			if(!channels.containsKey(channel.toLowerCase())) {
				channels.put(channel.toLowerCase(), false);
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
}
