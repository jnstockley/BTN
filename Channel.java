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
			Logger.logError(Bundle.getString("badJSON", filepath));
		}
		int oldSize = channels.size();
		System.out.print(Bundle.getString("getChan"));
		List<String> newChannels = null;
		try {
			newChannels = Arrays.asList(reader.readLine().split("\\s*,\\s*"));
		} catch (IOException e) {
			Logger.logError(Bundle.getString("badChan"));
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
				Logger.logError(Bundle.getString("badWrite", filepath));
			}
			System.exit(0);
		} else {
			Logger.logInfo(Bundle.getString("noChanAdd"));
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
			Logger.logError(Bundle.getString("badJSON", filepath));
		}
		int oldSize = channels.size();
		System.out.println(Bundle.getString("delChan"));
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
			Logger.logError(Bundle.getString("badChan"));
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
				Logger.logError(Bundle.getString("badWrite"));
			}
			Logger.logInfo(Bundle.getString("goodDel", filepath));
		} else {
			Logger.logInfo(Bundle.getString("noChanDel"));
		}
	}

	/**
	 * 
	 * @param filepath
	 */
	protected static void setupChannels(String filepath) {
		System.out.println(Bundle.getString("selOpt"));
		System.out.println(Bundle.getString("addChan"));
		System.out.println(Bundle.getString("delChan"));
		System.out.print(Bundle.getString("option"));
		BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
		int option = 0;
		try {
			option = Integer.parseInt(reader.readLine());
		} catch (NumberFormatException | IOException e) {
			Logger.logError(Bundle.getString("invalidNum"));
		}
		switch(option) {
		case 1:
			addChannels(filepath, reader);
			break;
		case 2:
			removeChannels(filepath, reader);
			break;
		default:
			Logger.logError(Bundle.getString("invalidNum"));
		}
	}
}
