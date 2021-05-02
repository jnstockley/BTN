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
		System.out.print("Enter your Twitch.tv Client ID: ");
		String clientID = null;
		try {
			clientID = reader.readLine();
		} catch (IOException e) {
			System.err.println("Auth.java - invalid trouble reading the client id provided!");
			System.exit(1);
		}
		System.out.print("Enter your Twitch.tv Authorization Key: ");
		String authorization = null;
		try {
			authorization = reader.readLine();
		} catch (IOException e) {
			System.err.println("Auth.java - invalid trouble reading the authorization key provided!");
			System.exit(1);
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
				System.err.println("Auth.java - " + filepath + " either not found or not valid JSON file!");
				System.exit(1);
			}
		}
		if(json.containsKey("auth")) {
			if(((JSONObject)json.get("auth")).containsKey("twitch")){
				System.err.println("Auth.java - Twitch API keys are already exist in this file!");
				System.exit(1);
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
			System.err.println("Auth.java - " + filepath + " error writing file to disk!");
			System.exit(1);
		}
		if(update) {
			System.out.println("Updated Twitch API Keys!");
			System.exit(0);
		} else {
			System.out.println("Added Twitch API Keys!");
			System.exit(0);
		}
	}

	/**
	 * 
	 * @param filepath
	 * @param reader
	 */
	@SuppressWarnings("unchecked")
	private static void spontitAddAuth(String filepath, BufferedReader reader, boolean update) {
		System.out.print("Enter your Spontit Authorization Key: ");
		String authorization = null;
		try {
			authorization = reader.readLine();
		} catch (IOException e) {
			System.err.println("Auth.java - invalid trouble reading the authorization key provided!");
			System.exit(1);
		}
		System.out.print("Enter yourr Spontit User ID: ");
		String userID = null;
		try {
			userID = reader.readLine();
		} catch (IOException e) {
			System.err.println("Auth.java - invalid trouble reading the user id provided!");
			System.exit(1);
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
				System.err.println("Auth.java - " + filepath + " either not found or not valid JSON file!");
				System.exit(1);
			}
		}
		if(json.containsKey("auth")) {
			if(((JSONObject)json.get("auth")).containsKey("spontit")){
				System.err.println("Auth.java - Spontit API keys are already exist in this file!");
				System.exit(1);
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
			System.err.println("Auth.java - " + filepath + " error writing file to disk!");
			System.exit(1);
		}
		if(update) {
			System.out.println("Updated Spontit API Keys!");
			System.exit(0);
		} else {
			System.out.println("Added Spontit API Keys!");
			System.exit(0);
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
			System.err.println("Auth.java - " + filepath + " empty file please add Authentications before trying to remove them!");
			System.exit(1);
		}
		try {
			json = (JSONObject) parser.parse(new FileReader(filepath));
		} catch (IOException | ParseException e) {
			System.err.println("Auth.java - " + filepath + " either not found or not valid JSON file!");
			System.exit(1);
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
				System.err.println("Auth.java - " + filepath + " error writing file to disk!");
				System.exit(1);
			}
			if(!update) {
				System.out.println("Removed Twitch API Keys. Please make sure to add new ones before running the program!");
				System.exit(0);
			}
		}else {
			System.err.println("Auth.java - No Twitch.tv API keys found in " + filepath + ". Please add API keys before trying to remove them!");
			System.exit(1);
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
			System.err.println("Auth.java - " + filepath + " empty file please add Authentications before trying to remove them!");
			System.exit(1);
		}
		try {
			json = (JSONObject) parser.parse(new FileReader(filepath));
		} catch (IOException | ParseException e) {
			System.err.println("Auth.java - " + filepath + " either not found or not valid JSON file!");
			System.exit(1);
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
				System.err.println("Auth.java - " + filepath + " error writing file to disk!");
				System.exit(1);
			}
			if(!update) {
				System.out.println("Removed Spontit API Keys. Please make sure to add new ones before running the program!");
				System.exit(0);
			}
		}else {
			System.err.println("Auth.java - No Spontit API keys found in " + filepath + ". Please add API keys before trying to remove them!");
			System.exit(1);
		}
	}

	/**
	 * 
	 * @param reader
	 * @return
	 */
	private static int authSelection(BufferedReader reader) {
		System.out.println("Do you want to add/remove Twitch API Keys or Spontit API Keys");
		System.out.println("1. Twitch");
		System.out.println("2. Spontit");
		System.out.print("Option: ");
		int option = -1;
		try {
			option = Integer.parseInt(reader.readLine());
		} catch (NumberFormatException | IOException e) {
			System.err.println("Auth.java - invalid number provided or trouble reading the number provided!");
			System.exit(1);
		}
		return option;
	}

	/**
	 * 
	 * @param filepath
	 */
	protected static void setupAuth(String filepath) {
		System.out.println("Please select an option below:");
		System.out.println("1. Add an Authentication");
		System.out.println("2. Remove an Authentication");
		System.out.println("3. Update an Authentication");
		System.out.print("Option: ");
		BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
		int option = -1;
		try {
			option = Integer.parseInt(reader.readLine());
		} catch (NumberFormatException | IOException e) {
			System.err.println("Auth.java - invalid number provided or trouble reading the number provided!");
			System.exit(1);
		}
		if(option > 3 || option < 1) {
			System.out.println("Not a valid option inputted!");
			System.exit(1);
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
			System.err.println("Not a valid option inputted! 1");
			System.exit(1);
		case 2:
			switch(service) {
			case 1:
				twitchRemoveAuth(filepath, reader, false);
				break;
			case 2:
				spontitRemoveAuth(filepath, reader, false);
				break;
			default:
				System.err.println("Not a valid option inputted! 2");
				System.exit(1);
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
				System.err.println("Not a valid option inputted! 3");
				System.exit(1);
			}
			break;
		}
	}
}
