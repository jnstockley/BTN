package com.github.jnstockley;

import java.io.IOException;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class TwitchAuth {

	private String clientId;
	
	private String clientSecret;
	
	private String authorizationKey;
	
	/**
	 * @return the clientId
	 */
	public String getClientId() {
		return clientId;
	}

	/**
	 * @return the clientSecret
	 */
	public String getClientSecret() {
		return clientSecret;
	}

	/**
	 * @return the authorizationKey
	 */
	public String getAuthorizationKey() {
		return authorizationKey;
	}
	
	// TODO Write data to redis database
	// TODO Re-write to support adding key to hash
	public TwitchAuth(String clientId, String clientSecret) {
		this.clientId = clientId;
		this.clientSecret = clientSecret;
		this.authorizationKey = getTwitchAuthKey(this.clientId, this.clientSecret);
		RedisAuth auth = new RedisAuth();
		Helper.addKeyToRedisSet(auth, "auth", "twitchClientId", this.clientId);
		Helper.addKeyToRedisSet(auth, "auth", "twitchClientSecret", this.clientSecret);
		Helper.addKeyToRedisSet(auth, "auth", "twitchAuthKey", this.authorizationKey);
	}
	
	public TwitchAuth(RedisAuth auth) {
		this.clientId = Helper.getKeyFromRedisSet(auth, "auth", "twitchClientId");
		this.clientSecret = Helper.getKeyFromRedisSet(auth, "auth", "twitchClientSecret");
		this.authorizationKey = Helper.getKeyFromRedisSet(auth, "auth", "twitchAuthKey");
	}
	
	private static String getTwitchAuthKey(String clientID, String clientSecret) {
		// Building the HTTP request
		OkHttpClient client = new OkHttpClient();
		RequestBody formBody = new FormBody.Builder().add("", "").build();
		Request request = new Request.Builder().url("https://id.twitch.tv/oauth2/token?client_id=" + clientID
				+ "&client_secret=" + clientSecret + "&grant_type=client_credentials").post(formBody).build();
		// Send the request and make sure request is valid and return key
		try (Response response = client.newCall(request).execute()) {
			JSONParser parser = new JSONParser();
			JSONObject responseJSON = (JSONObject) parser.parse(response.body().string());
			if (responseJSON.containsKey("access_token")) {
				return responseJSON.get("access_token").toString();
			} else if (responseJSON.containsKey("message")) {
				Logging.logger.severe("Invalid Twitch API Keys: " + responseJSON.get("message").toString());
			}
			return null;
		} catch (IOException | ParseException e) {
			Logging.logger.severe(e.toString());
			return null;
		}
	}
	
}
