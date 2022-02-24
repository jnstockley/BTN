package com.github.jnstockley;

import java.io.IOException;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * 
 * @author Jack Stockley
 *
 */
public class TwitchLiveStream {

	private String streamName;

	private String channelName;

	private String category;

	/**
	 * @return the streamName
	 */
	public String getStreamName() {
		return streamName;
	}

	/**
	 * @param streamName the streamName to set
	 */
	public void setStreamName(String streamName) {
		this.streamName = streamName;
	}

	/**
	 * @return the channelName
	 */
	public String getChannelName() {
		return channelName;
	}

	/**
	 * @param channelName the channelName to set
	 */
	public void setChannelName(String channelName) {
		this.channelName = channelName;
	}

	/**
	 * 
	 * @return the category
	 */
	public String getCategory() {
		return category;
	}

	/**
	 * 
	 * @param category the category to set
	 */
	public void setCategory(String category) {
		this.category = category;
	}

	@Override
	public String toString() {
		return "Twitch [streamName=" + streamName + ", channelName=" + channelName + ", category=" + category + "]";
	}

	/**
	 * @param channelName
	 * @deprecated
	 */
	public TwitchLiveStream(String channelName, JSONObject channel) {
		this(channelName, channel.get("title").toString(), channel.get("game").toString());
	}
	
	/**
	 * 
	 * @param channelName
	 * @param streamName
	 * @param category
	 */
	public TwitchLiveStream(String channelName, String streamName, String category) {
		this.channelName = channelName;
		this.streamName = streamName;
		this.category = category;
	}
	
	public TwitchLiveStream(String channelName, RedisAuth auth) {
		if(validChannel(channelName)) {
			this.channelName = channelName;
			Helper.addListToRedis(auth, "twitch", Arrays.asList(channelName));
		} else {
			Logging.logger.warning(channelName + " is not a valid Twitch channel!");
		}
	}
	
	@SuppressWarnings({ "unchecked", "deprecation" })
	private static boolean validChannel(String channel) {
		// Uses regex to make sure username fits Twitch's username requirements
		Pattern chanPattern = Pattern.compile("[a-zA-Z0-9]{4,25}");
		Matcher chanMatcher = chanPattern.matcher(channel);
		if (!chanMatcher.find()) {
			Logging.logger.severe(channel + " is not a valid Twitch.tv channel name!");
			return false;
		}
		// Builds the JSONObject requires for the Twitch API
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
		// Builds the HTTP POST request with the JSON data
		OkHttpClient client = new OkHttpClient();
		RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), data.toJSONString());
		Request request = new Request.Builder().url("https://gql.twitch.tv/gql#origin=twilight")
				.header("client-id", "kimne78kx3ncx6brgo4mv6wki5h1ko").post(body).build();
		// Sends the HTTP POST request
		try (Response response = client.newCall(request).execute()) {
			// Parse the HTTP response and check if channel is available, if available, then
			// channel is not valid
			JSONParser parser = new JSONParser();
			JSONObject responseJSON = (JSONObject) ((JSONObject) parser.parse(response.body().string())).get("data");
			if (responseJSON.containsKey("isUsernameAvailable")) {
				boolean validChan = !Boolean.parseBoolean(responseJSON.get("isUsernameAvailable").toString());
				if (validChan) {
					Logging.logger.info(channel + " is a valid Twitch.tv channel name!");
					return true;
				}
			}
			Logging.logger.severe(channel + " is not a valid Twitch.tv channel name!");
			return false;
		} catch (IOException | ParseException e) {
			Logging.logger.severe(e.toString());
			System.exit(1);
			return false;
		}
	}

}