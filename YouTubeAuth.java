package com.github.jnstockley;

import java.io.IOException;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class YouTubeAuth {

	private String apiKey;
	
	public String getApiKey() {
		return this.apiKey;
	}
	
	// TODO Write data to redis database
	// TODO Re-write to support adding key to hash
	public YouTubeAuth(String apiKey) {
		if (validApiKey(apiKey)) {
			this.apiKey = apiKey;
		} else {
			this.apiKey = "";
		}
		RedisAuth auth = new RedisAuth();
		//Helper.addToRedis(auth, "youtubeAPIKey", apiKey);
		Helper.addKeyToRedisSet(auth, "auth", "youtubeAPIKey", this.apiKey);
	}
	
	public YouTubeAuth(RedisAuth auth) {
		this.apiKey = Helper.getKeyFromRedisSet(auth, "auth", "youtubeAPIKey");
	}
	
	private static boolean validApiKey(String apiKey) {
		OkHttpClient client = new OkHttpClient();
		Request request = new Request.Builder()
				.url("https://youtube.googleapis.com/youtube/v3/videos?part=snippet&id=Ks-_Mh1QhMc&key=" + apiKey)
				.build();
		try (Response response = client.newCall(request).execute()) {
			JSONParser parser = new JSONParser();
			JSONObject responseJSON = (JSONObject) parser.parse(response.body().string());
			if (responseJSON.containsKey("error")) {
				Logging.logger.severe("Invalid YouTube API Key: " + responseJSON.get("error").toString());
				return false;
			}
			Logging.logger.info("Valid YouTube API Key found!");
			return true;
		} catch (IOException | ParseException e) {
			Logging.logger.severe(e.toString());
			return false;
		}
	}
	
}
