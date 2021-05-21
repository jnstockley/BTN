//Notifications.java
package com.github.jnstockley;
import java.util.HashMap;
import java.util.List;

import org.json.simple.JSONObject;

/**
 * Handles sending notifications for new live channels, BTTN update, or errors checking for live status changes
 * 
 * @author Jack Stockley
 * 
 * @version 0.12-beta
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
		// Sends HTTP post request to send error notification
		HashMap<String, String> HTTPresponse = HTTP.post("https://api.spontit.com/v3/push", json.toJSONString(), spontitAuth);
		// Makes sure HTTP status code is 200
		if(HTTPresponse.get("statusCode").equals("200")) {
			Logger.logWarn(Bundle.getString("errSent"));
		} else {
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
		// Makes HTTP post request to send update notification
		HashMap<String, String> HTTPresponse = HTTP.post("https://api.spontit.com/v3/push", json.toJSONString(), spontitAuth);
		// Makes sure HTTP status code is 200
		if(HTTPresponse.get("statusCode").equals("200")) {
			Logger.logInfo(Bundle.getString("updateSent"));
		} else {
			Logger.logError(Bundle.getString("updateNotSent"));
		}
	}

	/**
	 * Sends a Spontit Notification alerting the user that a Twitch channel has gone live
	 * @param nowLive List of channel(s) that are now live
	 * @param auth HashMap with the API keys for Spontit
	 */
	@SuppressWarnings("unchecked")
	protected static void sendLiveNotification(List<String> nowLive, HashMap<String, String> auth) {
		// Gets the Spontit API Keys
		HashMap<String, String> spontitAuth = new HashMap<String, String>();
		spontitAuth.put("X-Authorization", auth.get("spontit-authorization"));
		spontitAuth.put("X-UserId", auth.get("spontit-user-id"));
		// Creates the JSON required by Spontit to alert user to a new live channel
		JSONObject json = new JSONObject();
		String streamer = "";
		for(int i=0; i<nowLive.size()-1; i++) {
			streamer += nowLive.get(i) + ", ";
		}
		streamer += nowLive.get(nowLive.size()-1);
		if(nowLive.size() == 1) {
			streamer += " "+ Bundle.getString("isLive");
			json.put("pushTitle", streamer);
			json.put("link", "https://twitch.tv/" + nowLive.get(0));
			json.put("content", Bundle.getString("checkThemLink") + nowLive.get(0) + "!");
		} else {
			streamer += " "+ Bundle.getString("areLive");
			json.put("pushTitle", streamer);
			json.put("content", Bundle.getString("checkThem"));
		}
		// Makes HTTP post request to send update notification
		HashMap<String, String> HTTPresponse = HTTP.post("https://api.spontit.com/v3/push", json.toJSONString(), spontitAuth);
		// Makes sure HTTP status code is 200
		if(HTTPresponse.get("statusCode").equals("200")) {
			Logger.logInfo(Bundle.getString("liveSent") + nowLive);
		} else {
			Logger.logError(Bundle.getString("liveNotSent"));
		}
	}
}
