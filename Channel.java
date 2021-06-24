package com.github.jnstockley;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 * 
 * Class that holds the data for each channel and helps with
 * adding and removing channels
 * 
 * @author Jack Stockley
 * 
 * @version 1.51
 *
 */
public class Channel {

	/**
	 *  Name of the Class used for Logging error
	 */
	private static final String CLASSNAME = Channel.class.getName();

	/**
	 *  The name of the channel
	 */
	private String channelName;

	/**
	 *  The stream name of the channel
	 */
	private String streamName;

	/**
	 *  The category the stream is in
	 */
	private String category;

	/**
	 *  True if old live status is live otherwise false
	 */
	private boolean oldLiveStatus;

	/**
	 *  True if current live status is live otherwise false
	 */
	private boolean liveStatus;

	/**
	 * Get the name of the channel
	 * @return The name of the channel
	 */
	public String getChannelName() {
		return channelName;
	}

	/**
	 * Sets the name of the Channel
	 * @param channelName The name of the channel
	 */
	public void setChannelName(String channelName) {
		this.channelName = channelName;
	}

	/**
	 * Get the name of the channels stream
	 * @return The name of the channels stream
	 */
	public String getStreamName() {
		return streamName;
	}

	/**
	 * Sets the name of the channels stream
	 * @param streamName The name of the channels stream
	 */
	public void setStreamName(String streamName) {
		this.streamName = streamName;
	}

	/**
	 * Get the name of the streams category
	 * @return The name of the category
	 */
	public String getCategory() {
		return category;
	}

	/**
	 * Sets the name of streams category
	 * @param category The name of the category
	 */
	public void setCategory(String category) {
		this.category = category;
	}

	/**
	 * Get the channel's old live status
	 * @return The channel's old live status
	 */
	public boolean isOldLiveStatus() {
		return oldLiveStatus;
	}

	/**
	 * Sets the channel's old live status
	 * @param oldLiveStatus The channel's old live status
	 */
	public void setOldLiveStatus(boolean oldLiveStatus) {
		this.oldLiveStatus = oldLiveStatus;
	}

	/**
	 * Get the channel's current live status
	 * @return The channel's current live status
	 */
	public boolean isLiveStatus() {
		return liveStatus;
	}

	/**
	 * Sets the channel's current live status
	 * @param liveStatus The channel's current live status
	 */
	public void setLiveStatus(boolean liveStatus) {
		this.liveStatus = liveStatus;
	}

	/**
	 * Determines if the channel has just gone live
	 * @return True if the channel is live and the channel's
	 * old status was false, otherwise false
	 */
	public boolean statusChange() {
		if(this.liveStatus && this.oldLiveStatus != this.liveStatus) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Prints a string representation of the object with the channel name, stream name and category
	 */
	@Override
	public String toString() {
		return channelName + " [" + streamName + ": " + category + "]";
	}

	/**
	 * Creates a channel object with the JSON response data from Twitch API
	 * and the old live status from the JSON config file
	 * @param json The JSON response data from the Twitch API
	 * @param oldLiveStatus The old live status of the channel from the JSON config file
	 */
	public Channel(JSONObject json, boolean oldLiveStatus) {
		channelName = json.get("display_name").toString();
		streamName = json.get("title").toString();
		category = json.get("game_name").toString();
		this.oldLiveStatus = oldLiveStatus;
		liveStatus = Boolean.parseBoolean(json.get("is_live").toString());
	}

	/**
	 * Helper function used to ask the user how many channels they
	 * want to add and then gets the name, checks they are valid
	 * with regex and Twitch API
	 * @param reader BufferedReader used to get user input from the console
	 */
	@SuppressWarnings("unchecked")
	public static void addChannels(BufferedReader reader) {
		// Asks how many channels the user wants to add
		System.out.print(Bundle.getBundle("numChanAdd"));
		int numChans = -1;
		try {
			numChans = Integer.parseInt(reader.readLine());
		} catch (NumberFormatException | IOException e) {
			Logging.logError(CLASSNAME, e);
		}
		// Makes sure the number of channels is greater then 0
		if(numChans < 0) {
			Logging.logError(CLASSNAME, Bundle.getBundle("invalidChanNum"));
		}
		// Asks user to enter x number of channels
		List<String> channels = new ArrayList<String>();
		for(int i=0; i<numChans; i++) {
			int temp = i + 1;
			System.out.print(Bundle.getBundle("chanName", temp + "/" + numChans));
			try {
				String channel = reader.readLine();
				channels.add(channel);
			} catch (IOException e) {
				Logging.logError(CLASSNAME, e);
			}
		}
		// Builds the JSON from the JSON config file
		JSONParser parser = new JSONParser();
		JSONObject json = new JSONObject();
		try {
			json = (JSONObject) parser.parse(new FileReader(BTTN.configFile));
		} catch (IOException | ParseException e) {
			Logging.logError(CLASSNAME, e);
		}
		// Checks if the current JSON object has any channels
		JSONObject channelsJSON = new JSONObject();
		if(json.containsKey("channels")) {
			channelsJSON = (JSONObject) json.get("channels");
		}
		// Makes sure channel is not already in the JSON config and the channel is valid
		List<String> invalidChannels = new ArrayList<String>();
		for(String channel: channels) {
			if(!channelsJSON.containsKey(channel.toLowerCase()) && Helper.validChannel(channel)) {
				channelsJSON.put(channel.toLowerCase(), false);
			} else {
				invalidChannels.add(channel);
			}
		}
		// Shows user any invalid channel names
		if(!invalidChannels.isEmpty()) {
			System.out.print(Bundle.getBundle("invalidChan", invalidChannels.toString()));
		}
		// Replaces the old channels JSON Object with the updated one and writes changes to the file
		json.put("channels", channelsJSON);
		try {
			FileWriter writer = new FileWriter(BTTN.configFile);
			writer.write(json.toJSONString());
			writer.flush();
			writer.close();
			Logging.logInfo(CLASSNAME, Bundle.getBundle("addedChan"));
		} catch (IOException e) {
			Logging.logError(CLASSNAME, e);
		}
	}

	/**
	 * Helper function used to remove channel(s) from being checked.
	 * @param reader BufferedReader used to get user input from the console 
	 */
	@SuppressWarnings("unchecked")
	public static void removeChannels(BufferedReader reader) {
		// Get the JSON from the config file
		JSONParser parser = new JSONParser();
		JSONObject json = new JSONObject();
		try {
			json = (JSONObject)parser.parse(new FileReader(BTTN.configFile));
		} catch (IOException | ParseException e) {
			Logging.logError(CLASSNAME, e);
		}
		// Make sure JSON contains channels
		if(json.containsKey("channels")) {
			// Gets the JSONObject that hold the channels
			JSONObject channelJSON = (JSONObject) json.get("channels");
			Set<String> channelSet = channelJSON.keySet();
			List<String> channels = new ArrayList<String>();
			channels.addAll(channelSet);
			// Prints the channels and asks the user which they want to remove
			System.out.println(Bundle.getBundle("channelsChecked"));
			for(int i=0; i<channels.size(); i++) {
				int temp = i + 1;
				System.out.println(temp + ". " + channels.get(i));
			}
			System.out.println(Bundle.getBundle("chanRemove"));
			String toRemove = "";
			try {
				toRemove = reader.readLine();
			} catch (IOException e) {
				Logging.logError(CLASSNAME, e);
			}
			// Adds the channels that the user wants to remove to a list
			List<String> removedChannels = new ArrayList<String>();
			toRemove = toRemove.replaceAll(" ", "");
			List<String> toRemoveList = Arrays.asList(toRemove.split(","));
			for(int i=0; i<toRemoveList.size(); i++) {
				removedChannels.add(channels.get(Integer.parseInt(toRemoveList.get(i)) - 1));
			}
			// Removes the channels from the JSONObject
			for(int i=0; i<removedChannels.size(); i++) {
				channelJSON.remove(removedChannels.get(i));
			}
			// Replaces the old channels JSON Object with the updated one and writes changes to the file
			json.put("channels", channelJSON);
			try {
				FileWriter writer = new FileWriter(BTTN.configFile);
				writer.write(json.toJSONString());
				writer.flush();
				writer.close();
				Logging.logInfo(CLASSNAME, Bundle.getBundle("removedChan"));
			} catch (IOException e) {
				Logging.logError(CLASSNAME, e);
			}
		} else {
			Logging.logWarn(CLASSNAME, Bundle.getBundle("noChannels", BTTN.configFile.getName()));
		}
	}
}
