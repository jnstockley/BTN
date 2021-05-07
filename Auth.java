//Auth.java
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;

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
public class Auth {

	/**
	 * 
	 * @param filepath
	 * @param reader
	 */
	@SuppressWarnings("unchecked")
	private static void twitchAddAuth(String filepath, BufferedReader reader, boolean update) {
		System.out.print(Bundle.getString("twitchClient"));
		String clientID = null;
		try {
			clientID = reader.readLine();
		} catch (IOException e) {
			Logger.logError(Bundle.getString("badClient"));
		}
		System.out.print(Bundle.getString("twitchAuth"));
		String authorization = null;
		try {
			authorization = reader.readLine();
		} catch (IOException e) {
			Logger.logError(Bundle.getString("badAuth"));
		}
		JSONObject twitchAuthJSON = new JSONObject();
		twitchAuthJSON.put("clientID", clientID);
		twitchAuthJSON.put("authorization", authorization);
		JSONParser parser = new JSONParser();
		JSONObject json = new JSONObject();
		File file = new File(filepath);
		if(file.length() != 0) {
			try {
				json = (JSONObject) parser.parse(new FileReader(filepath));
			} catch (IOException | ParseException e) {
				Logger.logError(Bundle.getString("badJSON", filepath));
			}
		}
		if(json.containsKey("auth")) {
			if(((JSONObject)json.get("auth")).containsKey("twitch")){
				Logger.logError(Bundle.getString("twitchKeys"));
			}
			JSONObject authJson = (JSONObject)json.get("auth");
			authJson.put("twitch", twitchAuthJSON);
			json.put("auth", authJson);
		} else {
			JSONObject twitch = new JSONObject();
			twitch.put("twitch", twitchAuthJSON);
			json.put("auth", twitch);
		}
		FileWriter writer;
		try {
			writer = new FileWriter(filepath);
			writer.write(json.toJSONString());
			writer.flush();
			writer.close();
		} catch (IOException e) {
			Logger.logError(Bundle.getString("badWrite", filepath));
		}
		if(update) {
			Logger.logInfo(Bundle.getString("updateTwitch"));
		} else {
			Logger.logInfo(Bundle.getString("addTwitch"));
		}
	}

	/**
	 * 
	 * @param filepath
	 * @param reader
	 */
	@SuppressWarnings("unchecked")
	private static void spontitAddAuth(String filepath, BufferedReader reader, boolean update) {
		System.out.print(Bundle.getString("spontitAuth"));
		String authorization = null;
		try {
			authorization = reader.readLine();
		} catch (IOException e) {
			Logger.logError(Bundle.getString("badAuth"));
		}
		System.out.print(Bundle.getString("spontitUser"));
		String userID = null;
		try {
			userID = reader.readLine();
		} catch (IOException e) {
			Logger.logError(Bundle.getString("badUser"));
		}
		JSONObject spontitAuthJSON = new JSONObject();
		spontitAuthJSON.put("userID", userID);
		spontitAuthJSON.put("authorization", authorization);
		JSONParser parser = new JSONParser();
		JSONObject json = new JSONObject();
		File file = new File(filepath);
		if(file.length() !=0) {
			try {
				json = (JSONObject) parser.parse(new FileReader(filepath));
			} catch (IOException | ParseException e) {
				Logger.logError(Bundle.getString("badJSON", filepath));
			}
		}
		if(json.containsKey("auth")) {
			if(((JSONObject)json.get("auth")).containsKey("spontit")){
				Logger.logError(Bundle.getString("spontitKeys"));
			}
			JSONObject authJson = (JSONObject)json.get("auth");
			authJson.put("spontit", spontitAuthJSON);
			json.put("auth", authJson);
		} else {
			JSONObject spontit = new JSONObject();
			spontit.put("spontit", spontitAuthJSON);
			json.put("auth", spontit);
		}
		FileWriter writer;
		try {
			writer = new FileWriter(filepath);
			writer.write(json.toJSONString());
			writer.flush();
			writer.close();
		} catch (IOException e) {
			Logger.logError(Bundle.getString("badWrite", filepath));
		}
		if(update) {
			Logger.logInfo(Bundle.getString("updateSpontit"));
		} else {
			Logger.logInfo(Bundle.getString("addSpontit"));
		}
	}

	/**
	 * 
	 * @param filepath
	 * @param reader
	 */
	private static void twitchRemoveAuth(String filepath, BufferedReader reader, boolean update) {
		JSONParser parser = new JSONParser();
		JSONObject json = new JSONObject();
		File file = new File(filepath);
		if(file.length() ==0) {
			Logger.logError(Bundle.getString("emptyAuth", filepath));
		}
		try {
			json = (JSONObject) parser.parse(new FileReader(filepath));
		} catch (IOException | ParseException e) {
			Logger.logError(Bundle.getString("badJSON", filepath));
		}
		if(json.containsKey("auth") && ((JSONObject)json.get("auth")).containsKey("twitch")){
			((JSONObject)json.get("auth")).remove("twitch");
			FileWriter writer;
			try {
				writer = new FileWriter(filepath);
				writer.write(json.toJSONString());
				writer.flush();
				writer.close();
			} catch (IOException e) {
				Logger.logError(Bundle.getString("badWrite", filepath));
			}
			if(!update) {
				Logger.logWarn(Bundle.getString("removeTwitch"));
			}
		}else {
			Logger.logWarn(Bundle.getString("noTwitch", filepath));
		}
	}

	/**
	 * 
	 * @param filepath
	 * @param reader
	 */
	private static void spontitRemoveAuth(String filepath, BufferedReader reader, boolean update) {
		JSONParser parser = new JSONParser();
		JSONObject json = new JSONObject();
		File file = new File(filepath);
		if(file.length() ==0) {
			Logger.logError(Bundle.getString("emptyAuth", filepath));
		}
		try {
			json = (JSONObject) parser.parse(new FileReader(filepath));
		} catch (IOException | ParseException e) {
			Logger.logError(Bundle.getString("badJSON", filepath));
		}
		if(json.containsKey("auth") && ((JSONObject)json.get("auth")).containsKey("spontit")){
			((JSONObject)json.get("auth")).remove("spontit");
			FileWriter writer;
			try {
				writer = new FileWriter(filepath);
				writer.write(json.toJSONString());
				writer.flush();
				writer.close();
			} catch (IOException e) {
				Logger.logError(Bundle.getString("badWrite", filepath));
			}
			if(!update) {
				Logger.logInfo(Bundle.getString("removeSpontit"));
			}
		}else {
			Logger.logError(Bundle.getString("noSpontit", filepath));
		}
	}

	/**
	 * 
	 * @param reader
	 * @return
	 */
	private static int authSelection(BufferedReader reader) {
		System.out.println(Bundle.getString("authType"));
		System.out.println(Bundle.getString("twitch"));
		System.out.println(Bundle.getString("spontit"));
		System.out.print(Bundle.getString("option"));
		int option = -1;
		try {
			option = Integer.parseInt(reader.readLine());
		} catch (NumberFormatException | IOException e) {
			Logger.logError(Bundle.getString("invalidOpt"));
		}
		return option;
	}

	/**
	 * 
	 * @param filepath
	 */
	protected static void setupAuth(String filepath) {
		System.out.println(Bundle.getString("selOpt"));
		System.out.println(Bundle.getString("addAuth"));
		System.out.println(Bundle.getString("removeAuth"));
		System.out.println(Bundle.getString("updateAuth"));
		System.out.print(Bundle.getString("option"));
		BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
		int option = -1;
		try {
			option = Integer.parseInt(reader.readLine());
		} catch (NumberFormatException | IOException e) {
			Logger.logError(Bundle.getString("invalidOpt"));
		}
		if(option > 3 || option < 1) {
			Logger.logError(Bundle.getString("invalidOpt"));
		}
		int service = authSelection(reader);
		switch(option) {
		case 1:
			switch(service) {
			case 1:
				twitchAddAuth(filepath, reader, false);
				break;
			case 2:
				spontitAddAuth(filepath, reader, false);
				break;
			}
			break;
		default:
			Logger.logError(Bundle.getString("invalidOpt"));
		case 2:
			switch(service) {
			case 1:
				twitchRemoveAuth(filepath, reader, false);
				break;
			case 2:
				spontitRemoveAuth(filepath, reader, false);
				break;
			default:
				Logger.logError(Bundle.getString("invalidOpt"));
			}
			break;
		case 3:
			switch(service) {
			case 1:
				twitchRemoveAuth(filepath, reader, true);
				twitchAddAuth(filepath, reader, true);
				break;
			case 2:
				spontitRemoveAuth(filepath, reader, true);
				spontitAddAuth(filepath, reader, true);
				break;
			default:
				Logger.logError(Bundle.getString("invalidOpt"));
			}
			break;
		}
	}
}
