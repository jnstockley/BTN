//Notifications.java
import java.util.HashMap;
import java.util.List;

import org.json.simple.JSONObject;

/**
 * 
 * @author Jack Stockley
 * 
 * @version 0.9-beta
 *
 */
public class Notifications {

	/**
	 * 
	 * @param response
	 * @param auth
	 */
	@SuppressWarnings("unchecked")
	protected static void sendErrorNotification(HashMap<String, String> response, HashMap<String, String> auth) {
		JSONObject json = new JSONObject();
		json.put("pushTitle", "Error Checking Live Status");
		json.put("content", "Error Code: " + response.get("statusCode"));
		HashMap<String, String> spontitAuth = new HashMap<String, String>();
		spontitAuth.put("X-Authorization", auth.get("spontit-authorization"));
		spontitAuth.put("X-UserId", auth.get("spontit-user-id"));
		HashMap<String, String> HTTPresponse = HTTP.post("https://api.spontit.com/v3/push", json.toJSONString(), spontitAuth);
		if(HTTPresponse.get("statusCode").equals("200")) {
			System.out.println("Error Notif Sent!");
		} else {
			System.out.println("Error Notif not Sent!");
		}
	}

	/**
	 * 
	 * @param nowLive
	 * @param auth
	 */
	@SuppressWarnings("unchecked")
	protected static void sendLiveNotification(List<String> nowLive, HashMap<String, String> auth) {
		JSONObject json = new JSONObject();
		HashMap<String, String> spontitAuth = new HashMap<String, String>();
		spontitAuth.put("X-Authorization", auth.get("spontit-authorization"));
		spontitAuth.put("X-UserId", auth.get("spontit-user-id"));
		String streamer = "";
		for(int i=0; i<nowLive.size()-1; i++) {
			streamer += nowLive.get(i) + ", ";
		}
		streamer += nowLive.get(nowLive.size()-1);
		if(nowLive.size() == 1) {
			streamer += " is live!";
			json.put("pushTitle", streamer);
			json.put("link", "https://twitch.tv/" + nowLive.get(0));
			json.put("content", "Check them out on twitch.tv/" + nowLive.get(0) + "!");
		} else {
			streamer += " are live!";
			json.put("pushTitle", streamer);
			json.put("content", "Check them out on Twitch!");
		}
		HashMap<String, String> HTTPresponse = HTTP.post("https://api.spontit.com/v3/push", json.toJSONString(), spontitAuth);
		if(HTTPresponse.get("statusCode").equals("200")) {
			System.out.println("Live Notif Sent!");
		} else {
			System.out.println("Live Notif not Sent!");
		}
	}
	
	/**
	 * 
	 * @param version
	 * @param url
	 * @param auth
	 */
	@SuppressWarnings("unchecked")
	protected static void sendUpdateNotification(double version, HashMap<String, String> auth) {
		JSONObject json = new JSONObject();
		HashMap<String, String> spontitAuth = new HashMap<String, String>();
		spontitAuth.put("X-Authorization", auth.get("spontit-authorization"));
		spontitAuth.put("X-UserId", auth.get("spontit-user-id"));
		json.put("pushTitle", "BTTN Update Available!");
		json.put("content", "Please update to version " + version + "! BTTN is not checking for live channels until BTTN is updated.");
		json.put("link", "https://github.com/jnstockley/BTTN/releases");
		HashMap<String, String> HTTPresponse = HTTP.post("https://api.spontit.com/v3/push", json.toJSONString(), spontitAuth);
		if(HTTPresponse.get("statusCode").equals("200")) {
			System.out.println("Update Notif Sent!");
		} else {
			System.out.println("Update Notif not Sent!");
		}
	}
}
