//Channel.java
import java.io.BufferedReader;
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
 * 
 * @author Jack Stockley
 * 
 * @version 0.9-beta
 *
 */
public class Channel {

	/**
	 * 
	 * @param filepath
	 * @param reader
	 */
	@SuppressWarnings("unchecked")
	private static void addChannels(String filepath, BufferedReader reader) {
		JSONParser parser = new JSONParser();
		JSONObject channels = null;
		try {
			channels = (JSONObject) parser.parse(new FileReader(filepath));
		} catch (IOException | ParseException e) {
			System.err.println("Channel.java - " + filepath + " either not found or not valid JSON file!");
			System.exit(1);
		}
		int oldSize = channels.size();
		System.out.print("Enter a channel to get notifications for, seperate each channel by a comma: ");
		List<String> newChannels = null;
		try {
			newChannels = Arrays.asList(reader.readLine().split("\\s*,\\s*"));
		} catch (IOException e) {
			System.err.println("Channel.java - invalid channels list provided, make sure you are seperating every channel by a ','!");
			System.exit(1);
		}
		for(String channel: newChannels) {
			if(!channels.containsKey(channel)) {
				channels.put(channel, false);
			}
		}
		if(channels.size() != oldSize) {
			FileWriter writer;
			try {
				writer = new FileWriter(filepath);
				writer.write(channels.toJSONString());
				writer.flush();
				writer.close();
			} catch (IOException e) {
				System.err.println("Channel.java - " + filepath + " error writing file to disk!");
				System.exit(1);
			}
			System.exit(0);
		} else {
			System.out.println("No new channels added, file remains the same!");
			System.exit(0);
		}
	}

	/**
	 * 
	 * @param filepath
	 * @param reader
	 */
	@SuppressWarnings("unchecked")
	private static void removeChannels(String filepath, BufferedReader reader) {
		JSONParser parser = new JSONParser();
		JSONObject channels = null;
		try {
			channels = (JSONObject) parser.parse(new FileReader(filepath));
		} catch (IOException | ParseException e) {
			System.err.println("Channel.java - " + filepath + " either not found or not valid JSON file!");
			System.exit(1);
		}
		int oldSize = channels.size();
		System.out.println("Enter the number(s) of the channels you want to no longer recieve notifications for, sperate each index by a comma:");
		int index = 1;
		Set<String> channelSet = channels.keySet();
		List<String> channelNames = new ArrayList<String>();
		for(Object channel: channelSet) {
			channelNames.add(channel.toString());
		}
		Collections.sort(channelNames);
		for(String channel: channelNames) {
			System.out.println(index + ": " + channel);
			index++;
		}
		System.out.print("Channels: ");
		List<String> indexs = null;
		try {
			indexs = Arrays.asList((reader.readLine().split("\\s*,\\s*")));
		} catch (IOException e) {
			System.err.println("Channel.java - invalid channels list provided, make sure you are seperating every channel by a ','!");
			System.exit(1);
		}
		for(String channelIndex: indexs) {
			channels.remove(channelNames.get(Integer.parseInt(channelIndex)-1));
		}
		if(channels.size() != oldSize) {
			FileWriter writer;
			try {
				writer = new FileWriter(filepath);
				writer.write(channels.toJSONString());
				writer.flush();
				writer.close();
			} catch (IOException e) {
				System.err.println("Channel.java - " + filepath + " error writing file to disk!");
				System.exit(1);
			}
			
			System.exit(0);
		} else {
			System.out.println("No channels selected to be removed, file remains the same!");
			System.exit(0);
		}
	}

	/**
	 * 
	 * @param filepath
	 */
	protected static void setupChannels(String filepath) {
		System.out.println("Please select an option: ");
		System.out.println("1. Add Channel");
		System.out.println("2. Remove Channel");
		System.out.print("Option: ");
		BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
		int option = 0;
		try {
			option = Integer.parseInt(reader.readLine());
		} catch (NumberFormatException | IOException e) {
			System.err.println("Channel.java - invalid number provided or trouble reading the number provided!");
			System.exit(1);
		}
		switch(option) {
		case 1:
			addChannels(filepath, reader);
			break;
		case 2:
			removeChannels(filepath, reader);
			break;
		default:
			System.err.println("Not a valid option inputted!");
			System.exit(1);
		}
	}
}
