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
		json.put("pushTitle", Bundle.getString("errorStatus"));
		json.put("content", Bundle.getString("errCode") + response.get("statusCode"));
		HashMap<String, String> spontitAuth = new HashMap<String, String>();
		spontitAuth.put("X-Authorization", auth.get("spontit-authorization"));
		spontitAuth.put("X-UserId", auth.get("spontit-user-id"));
		HashMap<String, String> HTTPresponse = HTTP.post("https://api.spontit.com/v3/push", json.toJSONString(), spontitAuth);
		if(HTTPresponse.get("statusCode").equals("200")) {
			Logger.logWarn(Bundle.getString("errSent"));
		} else {
			Logger.logError(Bundle.getString("errNotSent"));
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
			streamer += Bundle.getString("isLive");
			json.put("pushTitle", streamer);
			json.put("link", "https://twitch.tv/" + nowLive.get(0));
			json.put("content", Bundle.getString("checkThemLink") + nowLive.get(0) + "!");
		} else {
			streamer += Bundle.getString("areLive");
			json.put("pushTitle", streamer);
			json.put("content", Bundle.getString("checkThem"));
		}
		HashMap<String, String> HTTPresponse = HTTP.post("https://api.spontit.com/v3/push", json.toJSONString(), spontitAuth);
		if(HTTPresponse.get("statusCode").equals("200")) {
			Logger.logInfo(Bundle.getString("liveSent") + nowLive);
		} else {
			Logger.logError(Bundle.getString("liveNotSent"));
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
		json.put("pushTitle", Bundle.getString("BTTNUpdate"));
		json.put("content", Bundle.getString("updateTo", Double.toString(version)));
		json.put("link", "https://github.com/jnstockley/BTTN/releases");
		HashMap<String, String> HTTPresponse = HTTP.post("https://api.spontit.com/v3/push", json.toJSONString(), spontitAuth);
		if(HTTPresponse.get("statusCode").equals("200")) {
			Logger.logInfo(Bundle.getString("updateSent"));
		} else {
			Logger.logError(Bundle.getString("updateNotSent"));
		}
	}
}
