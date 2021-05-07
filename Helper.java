//Helper.java
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import org.json.simple.JSONArray;
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
public class Helper {

	/**
	 * 
	 * @param filepath
	 * @return
	 */
	protected static HashMap<String, Boolean> getOldStatus(String filepath) {
		HashMap<String, Boolean> oldStatus = new HashMap<String, Boolean>();
		JSONObject statuses = getSection(filepath, "channels");
		for(Object channel: statuses.keySet()) {
			oldStatus.put(channel.toString(), (Boolean) statuses.get(channel.toString()));
		}
		return oldStatus;
	}

	/**
	 * 
	 * @param filepath
	 * @return
	 */
	protected static HashMap<String, String> getAuth(String filepath) {
		HashMap<String, String> auth = new HashMap<String, String>();
		File file = new File(filepath);
		if(file.length() == 0) {
			Logger.logError(Bundle.getString("emptyFile", filepath));
		}
		JSONObject authJSON = getSection(filepath, "auth");
		if(authJSON.containsKey("twitch")) {
			JSONObject twitch = (JSONObject) authJSON.get("twitch");
			if(twitch.containsKey("clientID") && twitch.containsKey("authorization")) {
				auth.put("twitch-client-id", twitch.get("clientID").toString());
				auth.put("twitch-authorization", "Bearer " + twitch.get("authorization").toString());
			} else {
				Logger.logError(Bundle.getString("noTwitchKey"));
			}
		} else {
			Logger.logError(Bundle.getString("noTwitch", filepath));
		}
		if(authJSON.containsKey("spontit")) {
			JSONObject spontit = (JSONObject) authJSON.get("spontit");
			if(spontit.containsKey("userID") && spontit.containsKey("authorization")) {
				auth.put("spontit-user-id", spontit.get("userID").toString());
				auth.put("spontit-authorization", spontit.get("authorization").toString());
			} else {
				Logger.logError(Bundle.getString("noSpontitKey"));
			}
		} else {
			Logger.logError(Bundle.getString("noSpontit", filepath));
		}
		return auth;
	}

	/**
	 * 
	 * @param channels
	 * @param auth
	 * @return
	 */
	protected static HashMap<String, Boolean> getStatus(Set<String> channels, HashMap<String, String> auth) {
		HashMap<String, Boolean> currStatus = new HashMap<String, Boolean>();
		HashMap<String, String> twitchAuth = new HashMap<String, String>();
		twitchAuth.put("client-id", auth.get("twitch-client-id"));
		twitchAuth.put("Authorization", auth.get("twitch-authorization"));
		JSONParser parser = new JSONParser();
		HashMap<String, String> response;
		for(String channel: channels) {
			response = HTTP.get("https://api.twitch.tv/helix/search/channels?query=" + channel, twitchAuth);
			if(Integer.parseInt(response.get("statusCode"))!=200) {
				Notifications.sendErrorNotification(response, auth);
				Logger.logError(Bundle.getString("twitchStatus", response.toString()));
			} else {
				JSONObject json = null;
				try {
					json = (JSONObject) parser.parse(response.get("data"));
				} catch (ParseException e1) {
					Logger.logError(Bundle.getString("badJSONForm"));
				}
				Object[] jsonArr = ((JSONArray) json.get("data")).toArray();
				List<String> data = new ArrayList<String>();
				for(int i=0; i<jsonArr.length; i++) {
					data.add(jsonArr[i].toString());
				}
				for(String channelData: data) {
					JSONObject channelJson = null;
					try {
						channelJson = (JSONObject)parser.parse(channelData);
					} catch (ParseException e) {
						Logger.logError(Bundle.getString("badJSONForm"));
					}
					if(channelJson.get("display_name").equals(channel) && channelJson.get("is_live").toString().equals("true")) {
						currStatus.put(channel, true);
					}
				}
				if(!currStatus.containsKey(channel)) {
					currStatus.put(channel, false);
				}	
			}
		}
		return currStatus;
	}

	/**
	 * 
	 * @param currStatus
	 * @param filepath
	 */
	@SuppressWarnings("unchecked")
	protected static void updateStatusFile(HashMap<String, Boolean> currStatus, String filepath) {
		JSONParser parser = new JSONParser();
		JSONObject json = new JSONObject();
		try {
			json = (JSONObject) parser.parse(new FileReader(filepath));
		} catch (IOException | ParseException e1) {
			Logger.logError(Bundle.getString("badJSON", filepath));
		}
		JSONObject channelJSON = new JSONObject(currStatus);
		json.remove("channels");
		json.put("channels", channelJSON);
		FileWriter writer;
		try {
			writer = new FileWriter(filepath);
			writer.write(json.toJSONString());
			writer.flush();
			writer.close();
		} catch (IOException e) {
			Logger.logError(Bundle.getString("badWrite", filepath));
		}
	}

	/**
	 * 
	 * @param filepath
	 * @return
	 */
	protected static int getDelay(String filepath) {
		int delay = 30;
		JSONParser parser = new JSONParser();
		JSONObject json = new JSONObject();
		File file = new File(filepath);
		if(file.length() != 0) {
			try {
				json = (JSONObject) parser.parse(new FileReader(filepath));
				if(json.containsKey("updateDelay")) {
					delay = Integer.parseInt(json.get("updateDelay").toString());
				}
			} catch (IOException | ParseException e) {
				Logger.logError(Bundle.getString("badWrite", filepath));
			}
		}
		return delay;
	}

	/**
	 * 
	 * @param filepath
	 * @param section
	 * @return
	 */
	private static JSONObject getSection(String filepath, String section) {
		JSONParser parser = new JSONParser();
		JSONObject jsonSection = new JSONObject();
		try {
			jsonSection = (JSONObject) ((JSONObject) parser.parse(new FileReader(filepath))).get(section);
		} catch (IOException | ParseException e) {
			Logger.logError(Bundle.getString("badWrite", filepath));
		}
		return jsonSection;
	}
}
