package com.github.jnstockley;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class SetupManager {
	
	// TODO Determine what to return and how to send data to redis database
	
	// TODO Add list, which converts ID's to readable names i.e. youtube playlist id -> channel owner
	
	private static final BufferedReader READER = new BufferedReader(new InputStreamReader(System.in));
	
	public static void getTwitchChannel() {
		List<String> channels = Helper.getListFromRedis(new RedisAuth(), "twitch");
		System.out.println("Current channels in Redis Database:");
		System.out.println(channels);
	}
	
	public static void addTwitchChannel() {
		List<String> twitchChannels = getInput("Add Twitch Channel");
		RedisAuth auth = new RedisAuth();
		for(String channel: twitchChannels) {
			new TwitchLiveStream(channel, auth);
		}
		System.out.println(twitchChannels);
	}
	
	public static void updateTwitchChannel() {
		System.out.println("This command will have you remove Twitch channels and then add new Twitch channels!");
		removeTwitchChannel();
		addTwitchChannel();
	}
	
	public static void removeTwitchChannel() {
		List<String> channels = Helper.getListFromRedis(new RedisAuth(), "twitch");
		System.out.println("Please enter the index of the channel(s) you want to remove");
		for (int i=1; i<=channels.size(); i++) {
			System.out.println(i + ". " + channels.get(i-1));
		}
		List<String> indexes = getInput("Channel Index Number");
		for(String index: indexes) {
			channels.remove(Integer.parseInt(index) - 1);
		}
		Helper.updateListInRedis(new RedisAuth(), "twitch", channels);
	}
	
	public static void getYoutubeChannel() {
		YouTubeChannels ytChannels = new YouTubeChannels(new YouTubeAuth(new RedisAuth()));
		System.out.println("Current YouTube channels in Redis Database:");
		System.out.println(ytChannels.getNames().keySet());
	}
	
	public static void addYoutubeChannel() {
		List<String> youtubeChannels = getInput("Add YouTube Channel URL");
		List<String> channelIds = new ArrayList<String>();
		YouTubeAuth auth = new YouTubeAuth(new RedisAuth());
		for(String channel: youtubeChannels) {
			if(channel.contains("youtube.com/c/")) { // Channel Name
				String channelId = Helper.youtubechannelNameToChannelId(channel);
				channelIds.add(channelId);
			} else if (channel.contains("youtube.com/channel/")) { // Channel ID
				String channelId = channel.substring(channel.indexOf("/channel/") + 9);
				channelIds.add(channelId);
			} else { // Not valid YouTube URL
				Logging.logger.severe(channel + " is not a valid YouTube channel URL!");
				System.exit(1);
			}
		}
		if(!channelIds.isEmpty()) {
			new YouTubeChannels(channelIds, auth);
		} else {
			Logging.logger.warning("No YouTube Channels to add");
			System.exit(1);
		}
	}
	
	public static void updateYoutubeChannel() {
		System.out.println("This command will have you remove YouTube channels and then add new YouTube channels!");
		removeYoutubeChannel();
		addYoutubeChannel();
	}
	
	public static void removeYoutubeChannel() {
		System.out.println("Not implemeneted yet!");
		System.exit(0);
	}
	
	public static void getYoutubeLiveChannel() {
		YTLiveStreams ytLiveStreams = new YTLiveStreams();
		System.out.println("Current YouTube Live Streams in Redis Database:");
		System.out.println(ytLiveStreams.getChannelNames());
	}
	
	public static void addYoutubeLiveChannel() {
		List<String> youtubeLiveChannels = getInput("Add YouTube Live Stream Channel URL");
		List<String> channelIds = new ArrayList<String>();
		for(String channel: youtubeLiveChannels) {
			if(channel.contains("youtube.com/c/")) { // Channel Name
				String channelId = Helper.youtubechannelNameToChannelId(channel);
				channelIds.add(channelId);
			} else if (channel.contains("youtube.com/channel/")) { // Channel ID
				String channelId = channel.substring(channel.indexOf("/channel/") + 9);
				channelIds.add(channelId);
			} else { // Not valid YouTube URL
				Logging.logger.severe(channel + " is not a valid YouTube channel URL!");
				//System.exit(1);
			}
		}
		if(!channelIds.isEmpty()) {
			new YTLiveStreams(channelIds);
		} else {
			Logging.logger.warning("No YouTube Live Stream Channels to add");
			System.exit(1);
		}
	}
	
	public static void updateYoutubeLiveChannel() {
		System.out.println("This command will have you remove YouTube Live streams channels and then add new YouTube Live streams channels!");
		removeYoutubeLiveChannel();
		addYoutubeLiveChannel();
	}
	
	public static void removeYoutubeLiveChannel() {
		System.out.println("Not implemeneted yet!");
		System.exit(0);
	}
	
	public static void addRedisAuth() {
		String server = getSingleInput("Redis Server Address");
		int port = Integer.parseInt(getSingleInput("Redis Server Port"));
		String username = getSingleInput("Redis Server Username");
		String password = getSingleInput("Redis Server Password");
		new RedisAuth(server, port, username, password);
	}
	
	public static void updateRedisAuth() {
		System.out.println("WARNING!!! This will overwite exisiting Redis Auth Config, and can't be recovered!");
		addRedisAuth();
	}
	
	public static void removeRedisAuth() {
		System.out.println("WARNING!!! This will PERMANENTLY DELETE exisiting Redis Auth Config!");
		String confirm = getSingleInput("Type \"YES\" EXACTLY to confirm deletion");
		if(confirm.equals("YES")) {
			if(new File(System.getProperty("user.home") + "/BTTN/config.json").delete()) {
				Logging.logger.warning("Redis Auth config deleted locally");
			} else {
				Logging.logger.info("Unable to delete Redis data locally");
			}
		} else {
			Logging.logger.info("Cancelled deletion of Redis Auth, data still present locally");
		}
	}
	
	public static void addTwitchAuth() {
		String clientId = getSingleInput("Twitch ClientID");
		String clientSecret = getSingleInput("Twitch Client Secret");
		new TwitchAuth(clientId, clientSecret);
	}
	
	public static void updateTwitchAuth() {
		System.out.println("WARNING!!! This will overwite exisiting Twitch Auth Config, and can't be recovered!");
		addTwitchAuth();
	}
	
	public static void removeTwitchAuth() {
		System.out.println("WARNING!!! This will PERMANENTLY DELETE exisiting Twitch Auth Config!");
		String confirm = getSingleInput("Type \"YES\" EXACTLY to confirm deletion");
		if(confirm.equals("YES")) {
			RedisAuth auth = new RedisAuth();
			Helper.deleteFromJedis(auth, "twitchClientId");
			Helper.deleteFromJedis(auth, "twitchClientSecret");
			Helper.deleteFromJedis(auth, "twitchAuthKey");
			Logging.logger.warning("Twitch Auth config deleted from redis database");
		} else {
			Logging.logger.info("Cancelled deletion of Twitch Auth, data still present in redis databse");
		}
	}
	
	public static void addYoutubeAuth() {
		String apiKey = getSingleInput("YouTube API Key");
		new YouTubeAuth(apiKey);
	}
	
	public static void updateYoutubeAuth() {
		System.out.println("WARNING!!! This will overwite exisiting YouTube Auth Config, and can't be recovered!");
		addYoutubeAuth();
	}
	
	public static void removeYoutubeAuth() {
		System.out.println("WARNING!!! This will PERMANENTLY DELETE exisiting YouTube Auth Config!");
		String confirm = getSingleInput("Type \"YES\" EXACTLY to confirm deletion");
		if(confirm.equals("YES")) {
			Helper.deleteFromJedis(new RedisAuth(), "youtubeAPIKey");
			Logging.logger.warning("YouTube Auth config deleted from redis database");
		} else {
			Logging.logger.info("Cancelled deletion of YouTube Auth, data still present in redis databse");
		}
	}
	
	public static void addAlertzyAuth() {
		List<String> accountKeys = getInput("Add Alertzy Account Key");
		new AlertzyAuth(accountKeys);
	}
	
	public static void updateAlertzyAuth() {
		System.out.println("WARNING!!! This will overwite exisiting Alertzy Auth Config, and can't be recovered!");
		addAlertzyAuth();
	}
	
	//TODO Add ability to remove specific key
	public static void removeAlertzyAuth() {
		System.out.println("WARNING!!! This will PERMANENTLY DELETE exisiting Alertzy Auth Config!");
		String confirm = getSingleInput("Type \"YES\" EXACTLY to confirm deletion");
		if(confirm.equals("YES")) {
			Helper.deleteFromJedis(new RedisAuth(), "alertzyAccountKeys");
			Logging.logger.warning("Alertzy Auth config deleted from redis database");
		} else {
			Logging.logger.info("Cancelled deletion of Alertzy Auth, data still present in redis databse");
		}
	}
	
	private static List<String> getInput(String prompt) {
		// Setup REPL environment
		System.out.println("Type \"q\" or \"quit\" to finish.");
		List<String> dataList = new ArrayList<String>();
		System.out.print("BTTN " + prompt + ">");
		// Get first string
		String data = "";
		// Catch reader exception
		try{
			data = READER.readLine();
		} catch (IOException e) {
			Logging.logger.severe(e.toString());
			System.exit(1);
			return null;
		}
		// Perform loop part of REPL 
		while (!data.equalsIgnoreCase("q") && !data.equalsIgnoreCase("quit") && !data.equalsIgnoreCase("") && data != null) {
			dataList.add(data);
			System.out.print("BTTN " + prompt + ">");
			// Catch reader exception
			try {
				data = READER.readLine();
			} catch (IOException e) {
				Logging.logger.severe(e.toString());
				System.exit(1);
				return null;
			}
		}
		// Return data list
		return dataList;
	}
	
	private static String getSingleInput(String prompt) {
		System.out.print("BTTN " + prompt + ">");
		try {
			return READER.readLine();
		} catch (IOException e) {
			Logging.logger.severe(e.toString());
			System.exit(1);
			return null;
		}
	}
}
