//Notifications.java
package com.github.jnstockley;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.json.simple.JSONObject;

/**
 * Handles sending notifications for new live channels, BTTN update, or errors checking for live status changes
 * 
 * @author Jack Stockley
 * 
 * @version 1.01
 *
 */
public class Notifications {

	/**
	 * Sends a Spontit Notification alerting user an error when checking the Twitch API
	 * @param response The response from the Twitch API
	 * @param auth HashMap with the API keys for Spontit
	 */
	@SuppressWarnings("unchecked")
	protected static void sendErrorNotification(HashMap<String, String> response, HashMap<String, String> auth) {
		// Creates the JSON required by Spontit to alert user to error
		JSONObject json = new JSONObject();
		json.put("pushTitle", Bundle.getString("errorStatus"));
		json.put("content", Bundle.getString("errCode") + response.get("statusCode"));
		// Gets Spontit API Keys
		HashMap<String, String> spontitAuth = new HashMap<String, String>();
		spontitAuth.put("X-Authorization", auth.get("spontit-authorization"));
		spontitAuth.put("X-UserId", auth.get("spontit-user-id"));
		if(sendNotification(json, spontitAuth)) {
			Logger.logInfo(Bundle.getString("errSent"));
		}else {
			Logger.logError(Bundle.getString("errNotSent"));
		}
	}

	/**
	 * Sends a Spontit Notification alerting the user that BTTN has an update available
	 * @param version Double representing the new version number to update to
	 * @param auth HashMap with the API keys for Spontit
	 */
	@SuppressWarnings("unchecked")
	protected static void sendUpdateNotification(double version, HashMap<String, String> auth) {
		// Creates the JSON required by Spontit to alert user to BTTN update
		JSONObject json = new JSONObject();
		json.put("pushTitle", Bundle.getString("BTTNUpdate"));
		json.put("content", Bundle.getString("updateTo", Double.toString(version)));
		json.put("link", "https://github.com/jnstockley/BTTN/releases");
		// Gets Spontit API Keys
		HashMap<String, String> spontitAuth = new HashMap<String, String>();
		spontitAuth.put("X-Authorization", auth.get("spontit-authorization"));
		spontitAuth.put("X-UserId", auth.get("spontit-user-id"));
		if(sendNotification(json, spontitAuth)) {
			Logger.logInfo(Bundle.getString("updateSent"));
		} else {
			Logger.logError(Bundle.getString("updateNotSent"));
		}
	}

	/**
	 * Sends Spontit Notification(s) alerting the user that a Twitch channel
	 * has gone live with the category the streamer is streaming in
	 * @param nowLive List of channel(s) that are now live
	 * @param auth HashMap with the API keys for Spontit
	 */
	@SuppressWarnings("unchecked")
	protected static void sendLiveNotification(HashMap<String, HashMap<String, String>> nowLive, HashMap<String, String> auth) {
		// Gets the Spontit API Keys
		HashMap<String, String> spontitAuth = new HashMap<String, String>();
		spontitAuth.put("X-Authorization", (auth.get("spontit-authorization")));
		spontitAuth.put("X-UserId", auth.get("spontit-user-id"));
		// Creates the JSON required by Spontit to alert user to a new live channel
		JSONObject json = new JSONObject();
		String streamer = nowLive.keySet().toString();
		streamer = streamer.substring(1, streamer.length()-1);
		if(nowLive.size() == 1) {
			json.put("pushTitle", streamer + " " + Bundle.getString("isLive"));
			json.put("link", "https://twitch.tv/" + streamer);
			json.put("content", nowLive.get(streamer).get("title") + ": " + nowLive.get(streamer).get("game"));
		} else {
			streamer += " "+ Bundle.getString("areLive");
			json.put("pushTitle", streamer);
			json.put("content", Bundle.getString("checkThem"));
		}
		if(sendNotification(json, spontitAuth)) {
			Logger.logInfo(Bundle.getString("liveSent") + streamer);
		} else {
			Logger.logError(Bundle.getString("liveNotSent"));
		}
	}

	/**
	 * Sends the given notification to the device using the JSON data provided and the spontit auth credentials
	 * @param json The JSON spontit uses to build the notification
	 * @param spontitAuth The authentication needed to send the spontit notification
	 * @return True if notification was sent, otherwise false
	 */
	private static boolean sendNotification(JSONObject json, HashMap<String, String> spontitAuth) {
		// Parsing for multiple Spontit API Keys
		String userIDsStr = spontitAuth.get("X-UserId");
		userIDsStr = userIDsStr.substring(1, userIDsStr.length()-1);
		String authKeysStr = spontitAuth.get("X-Authorization");
		authKeysStr = authKeysStr.substring(1, authKeysStr.length()-1);
		List<String> userIDs = Arrays.asList(userIDsStr.split(",", -1));
		List<String> authKeys = Arrays.asList(authKeysStr.split(",", -1));
		// Loops through all Spontit Key(s) and send notification
		for(int i=0; i< userIDs.size(); i++) {
			HashMap<String, String> userAuth = new HashMap<String, String>();
			userAuth.put("X-Authorization", authKeys.get(i).replace("\"", ""));
			userAuth.put("X-UserId", userIDs.get(i).replace("\"", ""));
			// Makes HTTP post request to send update notification
			HashMap<String, String> HTTPresponse = HTTP.post("https://api.spontit.com/v3/push", json.toJSONString(), userAuth);
			// Makes sure HTTP status code is 200
			if(!HTTPresponse.get("statusCode").equals("200")) {
				return false;
			} 
		}
		return true;
	}
}
