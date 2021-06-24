package com.github.jnstockley;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


/**
 * 
 * Handles reading and writing the all of the users API Keys required for BTTN.
 * 
 * @author Jack Stockley
 * 
 * @version 1.51
 *
 */
public class Auth {

	/**
	 *  Name of the Class used for Logging error
	 */
	private static final String CLASSNAME = Auth.class.getName();

	/**
	 *  Users Twitch Authorization Key
	 */
	private String twitchAuthorization;

	/**
	 *  Users Twitch ClientID
	 */
	private String twitchClientID;

	/**
	 *  List of users Alertzy Account Key(s)
	 */
	private List<String> alertzyAccountKeys;

	/**
	 * Get the Twitch Authorization Key used to check Twitch API
	 * @return The Twitch Authorization API Key
	 */
	public String getTwitchAuthorization() {
		return twitchAuthorization;
	}

	/**
	 * Set the Twitch Authorization Key used to check Twitch API
	 * @param twitchAuthorization The Twitch Authorization API Key
	 */
	public void setTwitchAuthorization(String twitchAuthorization) {
		this.twitchAuthorization = twitchAuthorization;
	}

	/**
	 * Get the Twitch Client ID used to check Twitch API
	 * @return The Twitch Client ID
	 */
	public String getTwitchClientID() {
		return twitchClientID;
	}

	/**
	 * Set the Twitch Client ID used to check Twitch API
	 * @param twitchClientID The Twitch ClientID
	 */
	public void setTwitchClientID(String twitchClientID) {
		this.twitchClientID = twitchClientID;
	}

	/**
	 * Get the Alertzy Account Key(s) used to check send notifications
	 * @return The Alertzy Account Key(s)
	 */
	public List<String> getAlertzyAccountKey() {
		return alertzyAccountKeys;
	}

	/**
	 * Set the Alertzy Account Key(s) used to check send notifications
	 * @param alertzyAccountKey The Alertzy Account Key(s)
	 */
	public void setAlertzyAccountKey(List<String> alertzyAccountKey) {
		this.alertzyAccountKeys = alertzyAccountKey;
	}

	/**
	 * Create an Auth Object which stores all the API keys used to run BTTN and makes sure they are valid
	 * @param file The JSON Config file which stores the API Keys
	 */
	public Auth(File file) {
		// Makes sure the file is not empty
		if(file.length() == 0) {
			Logging.logError(CLASSNAME, Bundle.getBundle("isEmpty", file.getName()));
		}
		// Parse the JSON config file to JSONObject
		JSONParser parser = new JSONParser();
		JSONObject json = new JSONObject();
		try {
			json = (JSONObject) parser.parse((new FileReader(file)));
		} catch (IOException | ParseException e) {
			Logging.logError(CLASSNAME, e);
		}
		// Makes sure JSON contains JSONObject which stores API Keys
		if(json.containsKey("auth")) {
			JSONObject auth = (JSONObject) json.get("auth");
			if(auth.containsKey("twitchAuthorization") && auth.containsKey("twitchClientID") && auth.containsKey("alertzyAccountKeys")) {
				// Sets the API Keys from config file and parser Alertzy Account Key(s) into list
				twitchAuthorization = auth.get("twitchAuthorization").toString();
				twitchClientID = auth.get("twitchClientID").toString();
				String alertzyKeys = auth.get("alertzyAccountKeys").toString();
				alertzyKeys = alertzyKeys.substring(1, alertzyKeys.length()-1);
				alertzyKeys = alertzyKeys.replaceAll("\"", "");
				alertzyAccountKeys = Arrays.asList(alertzyKeys.split(",", -1));
			} else {
				// Missing at least one API Key
				Logging.logError(CLASSNAME, Bundle.getBundle("missingAPIKey", file.getName()));
			}
		} else {
			// No Auth section
			Logging.logError(CLASSNAME, Bundle.getBundle("noAPIKeys", file.getName()));	
		}
	}

	/**
	 * Asks the user for the required keys and writes them to the JSON config file
	 * @param reader BufferedReader used to get user input from the console
	 */
	@SuppressWarnings("unchecked")
	public static void addAPIKeys(BufferedReader reader) {
		// Checks if JSON config file exists and creates new file if needed
		if(!BTTN.configFile.exists()) {
			try {
				BTTN.configFile.createNewFile();
			} catch (IOException e) {
				Logging.logError(CLASSNAME, e);
			}
		}
		// Reads in the current JSON config file
		JSONParser parser = new JSONParser();
		JSONObject json = new JSONObject();
		if(BTTN.configFile.length() != 0) {
			try {
				json = (JSONObject) parser.parse(new FileReader(BTTN.configFile));
			} catch (IOException | ParseException e) {
				Logging.logError(CLASSNAME, e);
			}
		}		
		JSONObject authJSON = new JSONObject();
		if(json.containsKey("auth")) {
			authJSON = (JSONObject) json.get("auth");
		}
		// Ask to add Twitch API Keys
		System.out.print(Bundle.getBundle("addTwitchAPI"));
		String option = "";
		try {
			option = reader.readLine();
		} catch (IOException e) {
			Logging.logError(CLASSNAME, e);
		}
		// Add Twitch API Keys
		if(option.equalsIgnoreCase(Bundle.getBundle("y")) || option.equalsIgnoreCase(Bundle.getBundle("yes"))) {
			System.out.print(Bundle.getBundle("twitchSecret"));
			String clientSecret = "";
			try {
				clientSecret = reader.readLine();
			} catch (IOException e) {
				Logging.logError(CLASSNAME, e);
			}
			System.out.print(Bundle.getBundle("twitchClient"));
			String clientID = "";
			try {
				clientID = reader.readLine();
			} catch (IOException e) {
				Logging.logError(CLASSNAME, e);
			}
			// Sends the user's clientID and clientSecret to Twitch to get use's Authorization Key
			String authorization = getAuthKey(clientID, clientSecret);
			if(authorization == null) {
				Logging.logError(CLASSNAME, Bundle.getBundle("invalidTwitchKeys"));
			}
			// Builds the JSONObject
			authJSON.put("twitchAuthorization", authorization);
			authJSON.put("twitchClientID", clientID);
		}
		// Ask to add Alertzy API Key
		System.out.print(Bundle.getBundle("addAlertzy"));
		try {
			option = reader.readLine();
		} catch (IOException e) {
			Logging.logError(CLASSNAME, e);
		}
		// Add Alertzy API Key
		if(option.equalsIgnoreCase(Bundle.getBundle("y")) || option.equalsIgnoreCase(Bundle.getBundle("yes"))) {
			// Asks user how many Alertzy API Keys they want to add
			System.out.print(Bundle.getBundle("numAlertzyKeys"));
			int numKeys = -1;
			try {
				numKeys = Integer.parseInt(reader.readLine());
			} catch (NumberFormatException | IOException e) {
				Logging.logError(CLASSNAME, e);
			}
			// Number of Alertzy API less then 0
			if(numKeys < 0) {
				Logging.logError(CLASSNAME, Bundle.getBundle("noAlertzyKeys"));
			}
			// Asks user for each Alertzy Account Key and add them to the List
			List<String> accountKeys = new ArrayList<String>();
			for(int i=0; i<numKeys; i++) {
				int temp = i + 1;
				System.out.print(Bundle.getBundle("enterAlertzyKey", "(" + temp + "/" + numKeys + ")"));
				String accountKey = "";
				try {
					accountKey = reader.readLine();
				} catch (IOException e) {
					Logging.logError(CLASSNAME, e);
				}
				accountKeys.add(accountKey);
			}
			authJSON.put("alertzyAccountKeys", accountKeys);
		}
		// No API Keys were selected to be added
		if(authJSON.isEmpty()) {
			Logging.logError(CLASSNAME, Bundle.getBundle("noAPIKeysAdded"));
		}
		// Adds API Keys to current JSON and writes file out
		json.put("auth", authJSON);
		try {
			FileWriter writer = new FileWriter(BTTN.configFile);
			writer.write(json.toJSONString());
			writer.flush();
			writer.close();
			Logging.logInfo(CLASSNAME, Bundle.getBundle("APIKeysAdded", BTTN.configFile.getName()));
		} catch (IOException e) {
			Logging.logError(CLASSNAME, e);
		}
	}

	/**
	 * Asks the user which set of API Keys to remove and removes them
	 * @param reader BufferedReader used to get user input from the console
	 */
	@SuppressWarnings("unchecked")
	public static void removeAPIKeys(BufferedReader reader) {
		JSONParser parser = new JSONParser();
		JSONObject json = new JSONObject();
		// Ask to remove Twitch API Keys
		System.out.print(Bundle.getBundle("removeTwitchAPI"));
		String option = "";
		try {
			option = reader.readLine();
		} catch (IOException e) {
			Logging.logError(CLASSNAME, e);
		}
		if(!BTTN.configFile.exists() || BTTN.configFile.length() == 0) {
			Logging.logError(CLASSNAME, Bundle.getBundle("isEmpty", BTTN.configFile.getName()));
		}
		// Remove Twitch API Keys
		if(option.equalsIgnoreCase(Bundle.getBundle("y")) || option.equalsIgnoreCase(Bundle.getBundle("yes"))) {
			try {
				json = (JSONObject) parser.parse(new FileReader(BTTN.configFile));
			} catch (IOException | ParseException e) {
				Logging.logError(CLASSNAME, e);
			}
			if(json.containsKey("auth")) {
				JSONObject authJSON = (JSONObject) json.get("auth");
				if(authJSON.containsKey("twitchAuthorization") && authJSON.containsKey("twitchClientID")) {
					authJSON.remove("twitchAuthorization");
					authJSON.remove("twitchClientID");
					json.put("auth", authJSON);
				}
			}
		}
		// Ask to remove Alertzy API Key
		System.out.print(Bundle.getBundle("removeAlertzyAPI"));
		try {
			option = reader.readLine();
		} catch (IOException e) {
			Logging.logError(CLASSNAME, e);
		}
		// Remove Alertzy API Key
		if(option.equalsIgnoreCase(Bundle.getBundle("y")) || option.equalsIgnoreCase(Bundle.getBundle("yes"))) {
			if(json.isEmpty()) {
				try {
					json = (JSONObject) parser.parse(new FileReader(BTTN.configFile));
				} catch (IOException | ParseException e) {
					Logging.logError(CLASSNAME, e);
				}
			}
			if(json.containsKey("auth")) {
				JSONObject authJSON = (JSONObject) json.get("auth");
				if(authJSON.containsKey("alertzyAccountKeys")) {
					authJSON.remove("alertzyAccountKeys");
					json.put("auth", authJSON);
				}
			}
		}
		if(json.isEmpty()) {
			Logging.logError(CLASSNAME, Bundle.getBundle("noAPIKeysSelRem"));
		}
		//Write JSON to file
		try {
			FileWriter writer = new FileWriter(BTTN.configFile);
			writer.write(json.toJSONString());
			writer.flush();
			writer.close();
			Logging.logInfo(CLASSNAME, Bundle.getBundle("APIKeysRemoved"));
		} catch (IOException e) {
			Logging.logError(CLASSNAME, e);
		}
	}

	/**
	 * Helper functions which sends the users clientID and clientSecret to the Twitch API to get their Authorization key
	 * required to access the Channel Status API
	 * @param clientID User's Twitch clientID
	 * @param clientSecret User's Twitch clientSecret
	 * @return The user's Twitch Authorization key
	 */
	private static String getAuthKey(String clientID, String clientSecret) {
		// Building the HTTP request
		OkHttpClient client = new OkHttpClient();
		RequestBody formBody = new FormBody.Builder()
				.add("", "")
				.build();
		Request request = new Request.Builder()
				.url("https://id.twitch.tv/oauth2/token?client_id=" + clientID + "&client_secret=" + clientSecret + "&grant_type=client_credentials")
				.post(formBody)
				.build();
		// Send the request and make sure request is valid and return key
		try(Response response = client.newCall(request).execute()){
			JSONParser parser = new JSONParser();
			JSONObject responseJSON = (JSONObject) parser.parse(response.body().string());
			if(responseJSON.containsKey("access_token")) {
				return responseJSON.get("access_token").toString();
			}
			return null;
		}catch(IOException | ParseException e) {
			Logging.logError(CLASSNAME, e);
			return null;
		}
	}

}
