package com.github.jnstockley;

import java.io.IOException;
import java.util.List;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 *
 * Sends all the notification used throughout BTTN and handles sending failover notification if needed
 *
 * @author Jack Stockley
 *
 * @version 1.6
 *
 */
public class Notifications {

	/**
	 *  Name of the file used to log data
	 */
	private static final String CLASSNAME = Notifications.class.getName();

	/**
	 * Builds the error notification to be sent when an error occurs
	 * @param error The string representation of the error
	 * @param keys List of Strings representing the Alertzy API Key(s)
	 */
	public static void sendErrorNotification(String error, List<String> keys) {
		// Builds the key string used to send notifications to multiple people with Alertzy
		String keysStr = "";
		for(String key: keys) {
			keysStr += key + "_";
		}
		// Builds the title of the notification and the url to the send the error notification
		keysStr = keysStr.substring(0, keysStr.length()-1);
		String title = Bundle.getBundle("encounteredError");
		String url = "https://alertzy.app/send?accountKey=" + keysStr + "&title=" + title + "&message=" + error;
		// True if the notification was sent otherwise false
		boolean success = sendNotification(url);
		if(success) {
			Logging.logWarn(CLASSNAME, Bundle.getBundle("errorNotificationSent"));
		} else {
			Failover failover = new Failover(BTTN.configFile);
			failover.sendFailover(title, error, "");
			// Determine if the notification was not sent at all or not sent to all the keys
			if(keys.size() > 1) {
				Logging.logError(CLASSNAME, Bundle.getBundle("notSentEveryone"));
			} else {
				Logging.logError(CLASSNAME, Bundle.getBundle("notSent"));
			}
		}
	}

	/**
	 * Builds the update notification to be sent when an update is available
	 * @param newVersion The new version number of BTTN
	 * @param keys List of Strings representing the Alertzy API Key(s)
	 */
	@SuppressWarnings("unchecked")
	public static void sendUpdateNotification(String newVersion, List<String> keys) {
		// Builds the key string used to send notifications to multiple people with Alertzy
		String keysStr = "";
		for(String key: keys) {
			keysStr += key + "_";
		}
		// Builds the title of the notification and the url to the send the update notification
		keysStr = keysStr.substring(0, keysStr.length()-1);
		String title = Bundle.getBundle("newVersion");
		String message = Bundle.getBundle("downloadUpdate");
		String link = "https://github.com/jnstockley/BTTN/releases";
		// Creates the JSONObject used to add a button to open up the GitHub releases page in Alertzy
		JSONObject button = new JSONObject();
		button.put("text", Bundle.getBundle("viewUpdate"));
		button.put("link", link);
		button.put("color", "primary");
		String url = "https://alertzy.app/send?accountKey=" + keysStr + "&title=" + title + "&message=" + message + "&buttons=[" + button.toJSONString() + "]";
		// True if the notification was sent otherwise false
		boolean success = sendNotification(url);
		if(success) {
			Logging.logInfo(CLASSNAME, Bundle.getBundle("updateTo", newVersion));
		} else {
			// Builds the failover message
			Failover failover = new Failover(BTTN.configFile);
			failover.sendFailover(title, message, link);
			// Determine if the notification was not sent at all or not sent to all the keys
			if(keys.size() > 1) {
				Logging.logError(CLASSNAME, Bundle.getBundle("notSentEveryone"));
			} else {
				Logging.logError(CLASSNAME, Bundle.getBundle("notSent"));
			}
		}
	}

	/**
	 * Builds the live notification to be sent when a channel has gone live
	 * @param channels List of channel objects that have just gone live
	 * @param keys List of Strings representing the Alertzy API Key(s)
	 */
	@SuppressWarnings("unchecked")
	public static void sendLiveNotification(List<Channel> channels, List<String> keys) {
		// Builds the key string used to send notifications to multiple people with Alertzy
		String keysStr = "";
		for(String key: keys) {
			keysStr += key + "_";
		}
		// Builds the title of the notification and the url to the send the live notification
		keysStr = keysStr.substring(0, keysStr.length()-1);
		String title = "";
		String message = "";
		String link = "";
		// One channel has gone live, includes stream name, stream category and streamer name
		if(channels.size() == 1) {
			title = Bundle.getBundle("isLive", channels.get(0).getChannelName());
			message = channels.get(0).getStreamName() + ": " + channels.get(0).getCategory();
			link = "twitch://stream" + channels.get(0).getChannelName();
			// Multiple channels have gone live includes the name of the streamers
		} else {
			for(Channel channel: channels) {
				title += channel.getChannelName() + ", ";
			}
			title = Bundle.getBundle("areLive",title.substring(0, title.length()-2));
			link = "https://twitch.tv";
		}
		// Creates the JSONObject used to add a button to open up the Twitch Stream(s) in Alertzy
		JSONObject button = new JSONObject();
		button.put("text", Bundle.getBundle("openStream"));
		button.put("link", link);
		button.put("color", "primary");
		String url = "https://alertzy.app/send?accountKey=" + keysStr + "&title=" + title + "&message=" + message + "&buttons=[" + button.toJSONString() + "]";
		// True if the notification was sent otherwise false
		boolean success = sendNotification(url);
		if(success) {
			Logging.logInfo(CLASSNAME, Bundle.getBundle("notificationSent", channels.toString()));
		} else {
			// Builds the failover message
			Failover failover = new Failover(BTTN.configFile);
			if(message.isEmpty()) {
				failover.sendFailover(title, title, link);
			} else {
				failover.sendFailover(title, message, link);
			}
			// Determine if the notification was not sent at all or not sent to all the keys
			if(keys.size() > 1) {
				Logging.logError(CLASSNAME, Bundle.getBundle("notSentEveryone"));
			} else {
				Logging.logError(CLASSNAME, Bundle.getBundle("notSent"));
			}
		}
	}

	/**
	 * Sends a test notification to make sure Alertzy is valid and works
	 * @param keys The user's Alertzy Key(s)
	 * @return True if the notification was sent otherwise false
	 */
	public static boolean sendTestNotification(List<String> keys) {
		// Builds the key string used to send notifications to multiple people with Alertzy
		String keysStr = "";
		for(String key: keys) {
			keysStr += key + "_";
		}
		// Builds the title of the notification and the url to the send the live notification
		keysStr = keysStr.substring(0, keysStr.length()-1);
		String title = Bundle.getBundle("testTitle");
		String message = Bundle.getBundle("testMessage");
		String url = "https://alertzy.app/send?accountKey=" + keysStr + "&title=" + title + "&message=" + message;
		return sendNotification(url);
	}

	/**
	 * The function that makes the HTTP request to send the notification
	 * @param url The URL to send the HTTP request to
	 * @return True if the notification was send, otherwise false
	 */
	private static boolean sendNotification(String url) {
		// Build the HTTP request
		OkHttpClient client = new OkHttpClient();
		Request request = new Request.Builder()
				.url(url)
				.build();
		// Send the HTTP request
		try(Response response = client.newCall(request).execute()){
			// Parse the HTTP response and makes sure it was sent
			JSONParser parser = new JSONParser();
			JSONObject responseJSON = (JSONObject) parser.parse(response.body().string());
			String success = responseJSON.get("response").toString();
			if(success.equalsIgnoreCase("success")) {
				return true;
			} else if(success.equalsIgnoreCase("mixed")) {
				return false;
			} else {
				return false;
			}
		} catch(IOException | ParseException e) {
			Logging.logError(CLASSNAME, e);
			return false;
		}
	}
}