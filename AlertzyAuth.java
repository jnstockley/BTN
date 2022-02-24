package com.github.jnstockley;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class AlertzyAuth {

	private List<String> accountKeys;
	
	public List<String> getAccountKeys() {
		return this.accountKeys;
	}
	
	// TODO Write data to redis database
	public AlertzyAuth(List<String> accountKeys) {
		if(validApiKeys(accountKeys)) {
			this.accountKeys = accountKeys;
		} else {
			this.accountKeys = new ArrayList<String>();
		}
		Helper.addListToRedis(new RedisAuth(), "alertzyAccountKeys", this.accountKeys);
	}
	
	public AlertzyAuth(RedisAuth auth) {
		this.accountKeys = Helper.getListFromRedis(auth, "alertzyAccountKeys");
	}
	
	private static boolean validApiKeys(List<String> apiKeys) {
		String keysStr = "";
		for (String key : apiKeys) {
			keysStr += key + "_";
		}
		keysStr = keysStr.substring(0, keysStr.length() - 1);
		String title = "BTTN Test Notification!";
		String message = "This is a test notification from BTTN to make sure all the API Key(s) provided are valid!";
		OkHttpClient client = new OkHttpClient();
		Request request = new Request.Builder()
				.url("https://alertzy.app/send?accountKey=" + keysStr + "&title=" + title + "&message=" + message)
				.build();
		try (Response response = client.newCall(request).execute()) {
			JSONParser parser = new JSONParser();
			JSONObject responseJSON = (JSONObject) parser.parse(response.body().string());
			if (responseJSON.containsKey("error")) {
				Logging.logger.severe("Invalid Alertzy API Key(s): " + responseJSON.get("error").toString());
				return false;
			}
			Logging.logger.info("Valid Alertzy API Key(s) found!");
			return true;
		} catch (IOException | ParseException e) {
			Logging.logger.severe(e.toString());
			return false;
		}
	}
	
}
