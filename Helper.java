package com.github.jnstockley;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.simple.JSONArray;
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
 * Class that holds helper functions used throughout the program
 *
 * @author Jack Stockley
 *
 * @version 1.62
 *
 */
public class Helper {

	/**
	 *  Name of the Class used for Logging error
	 */
	private static final String CLASSNAME = Helper.class.getName();

	/**
	 * Used to help with socket timeout exceptions and avoid infinite loops
	 */
	private static int attempt = 0;

	/**
	 * Uses the Twitch API to check the current status of a channel and get required data if live
	 * @param auth Auth object that holds the API keys
	 * @param channel The name of the channel to check the status of
	 * @return JSONObject of the channel if found otherwise null;
	 */
	public static JSONObject curLiveStatus(Auth auth, String channel) {
		// Checks how many HTTP request attempts have been made
		if(attempt > 2) {
			Logging.logError(CLASSNAME, Bundle.getBundle("socketError"));
			attempt = 0;
			return null;
		}
		OkHttpClient client = new OkHttpClient();
		Request request = buildRequest(auth, channel);
		// Makes the HTTP request
		try(Response response = client.newCall(request).execute()){
			// Parse the HTTP response
			JSONParser parser = new JSONParser();
			JSONObject responseJSON = (JSONObject) parser.parse(response.body().string());
			// Checks if the HTTP response contains stream data
			if(responseJSON.containsKey("data")) {
				JSONArray JSONdata = (JSONArray) responseJSON.get("data");
				for(Object stream: JSONdata) {
					// Find the specific channel and return the JSON Object of the channel
					JSONObject streamJSON = (JSONObject)parser.parse(stream.toString());
					if(streamJSON.get("display_name").toString().equalsIgnoreCase(channel)) {
						attempt = 0;
						return streamJSON;
					}
				}
				// Checks if HTTP response contains an error message
			} else if(responseJSON.containsKey("message") && responseJSON.get("message").equals("Invalid OAuth token")) {
				Notifications.sendErrorNotification(Bundle.getBundle("expiredToken"), auth.getAlertzyAccountKey());
				Logging.logError(CLASSNAME, Bundle.getBundle("expiredToken"));
				attempt = 0;
				return null;
			} else {
				attempt = 0;
				return null;
			}
			attempt = 0;
			return null;
			// Catches Socket Timeout Exception and retry's the request
		} catch(SocketTimeoutException e) {
			attempt++;
			Logging.logWarn(CLASSNAME, Bundle.getBundle("socketErrorRetry"));
			return curLiveStatus(auth, channel);
		}catch(IOException | ParseException e) {
			Notifications.sendErrorNotification(Bundle.getBundle("errorCheckingStatus", channel), auth.getAlertzyAccountKey());
			Logging.logError(CLASSNAME, e);
			attempt = 0;
			return null;
		}
	}

	/**
	 * Reads the JSON config file, and gets the old live status of a channel
	 * @param file The JSON config file
	 * @return A HashMap with the name of the channel and the old live status
	 */
	@SuppressWarnings("unchecked")
	public static HashMap<String, Boolean> getOldStatus(File file) {
		// Read the JSON data and parse it
		JSONParser parser = new JSONParser();
		JSONObject json = new JSONObject();
		try {
			json = (JSONObject) parser.parse(new FileReader(file));
		} catch (IOException | ParseException e) {
			Logging.logError(CLASSNAME, e);
		}
		// Makes sure JSON config has channels to check
		if(json.containsKey("channels")) {
			// Convert JSON to HashMap and return it
			JSONObject channels = (JSONObject) json.get("channels");
			return (HashMap<String, Boolean>) channels;
		} else {
			Logging.logError(CLASSNAME, Bundle.getBundle("noChannels", file.getName()));
			return null;
		}
	}

	/**
	 * Gets the current live status from the channels list and writes it to the JSON config file
	 * @param channels List of channel objects
	 * @param file JSON config file which contains the channels
	 */
	@SuppressWarnings("unchecked")
	public static void updateStatus(List<Channel> channels, File file) {
		// Reads the JSON from the config file and parses it
		JSONParser parser = new JSONParser();
		JSONObject json = new JSONObject();
		try {
			json = (JSONObject) parser.parse(new FileReader(file));
		} catch (IOException | ParseException e) {
			Logging.logError(CLASSNAME, e);
		}
		// Gets the channels JSON object and updates it with the new live status
		JSONObject channelsJSON = (JSONObject) json.get("channels");
		for(Channel channel: channels) {
			channelsJSON.put(channel.getChannelName(), channel.isLiveStatus());
		}
		// Overwrite the channels JSON Object and write to file
		json.put("channels", channelsJSON);
		try {
			FileWriter writer = new FileWriter(file);
			writer.write(json.toJSONString());
			writer.flush();
			writer.close();
		} catch (IOException e) {
			Logging.logError(CLASSNAME, e);
		}
	}

	/**
	 * Checks if the Twitch channel is valid using regex and the Twitch API
	 * @param channel The name of the channel to check if it's valid
	 * @return True if the channel name is valid otherwise false
	 */
	@SuppressWarnings({ "unchecked", "deprecation" })
	public static boolean validChannel(String channel) {
		// Uses regex to make sure username fits Twitch's username requirements
		Pattern chanPattern = Pattern.compile("[a-zA-Z0-9]{4,25}");
		Matcher chanMatcher = chanPattern.matcher(channel);
		if(!chanMatcher.find()) {
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
		Request request = new Request.Builder()
				.url("https://gql.twitch.tv/gql#origin=twilight")
				.header("client-id", "kimne78kx3ncx6brgo4mv6wki5h1ko")
				.post(body)
				.build();
		// Sends the HTTP POST request
		try(Response response = client.newCall(request).execute()){
			// Parse the HTTP response and check if channel is available, if available, then channel is not valid
			JSONParser parser = new JSONParser();
			JSONObject responseJSON = (JSONObject)((JSONObject) parser.parse(response.body().string())).get("data");
			if(responseJSON.containsKey("isUsernameAvailable")) {
				boolean validChan = !Boolean.parseBoolean(responseJSON.get("isUsernameAvailable").toString());
				if(!validChan) {
					Logging.logWarn(CLASSNAME, Bundle.getBundle("invalidChan", channel));
				}
				return validChan;
			}
		}catch(IOException | ParseException e) {
			Logging.logError(CLASSNAME, e);
		}
		return false;
	}

	/**
	 * Simple function to make sure JSON config file contains the required keys
	 * @return True if JSON config is valid, otherwise false
	 */
	public static boolean validConfig() {
		JSONObject json = new JSONObject();
		JSONParser parser = new JSONParser();
		try {
			json = (JSONObject) parser.parse(new FileReader(BTTN.configFile));
		} catch (IOException | ParseException e) {
			Logging.logError(CLASSNAME, e);
			return false;
		}
		if(!json.containsKey("failover")) {
			Logging.logWarn(CLASSNAME, Bundle.getBundle("failoverNotEnabled"));
		}
		if(!json.containsKey("channels")) {
			return false;
		}
		JSONObject channels = (JSONObject) json.get("channels");
		if(channels.size() == 0) {
			return false;
		}
		if(!json.containsKey("auth")) {
			return false;
		}
		JSONObject auth = (JSONObject) json.get("auth");
		if(!auth.containsKey("twitchAuthorization")) {
			return false;
		}
		if(!auth.containsKey("twitchClientID")) {
			return false;
		}
		if(!auth.containsKey("alertzyAccountKeys")) {
			return false;
		}
		return true;
	}

	/**
	 * Helper functions that builds a POST request
	 * @param auth The API Keys required to send the request
	 * @param channel The name of the Twitch channel
	 * @return A HTTP POST request
	 */
	private static Request buildRequest(Auth auth, String channel) {
		// Build the HTTP POST request
		Request request = new Request.Builder()
				.url("https://api.twitch.tv/helix/search/channels?query=" + channel)
				.header("client-id", auth.getTwitchClientID())
				.header("Authorization", "Bearer " + auth.getTwitchAuthorization())
				.build();
		return request;
	}
}