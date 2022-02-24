package com.github.jnstockley;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class YouTubeChannels {

	private static final String BASEURL = "https://www.googleapis.com/youtube/v3/channels/?part=statistics,snippet";

	private Map<String, String> names = new HashMap<String, String>();

	private Map<String, String> previousVideoAmounts = new HashMap<String, String>();

	private Map<String, String> currentVideoAmounts = new HashMap<String, String>();

	private List<YouTubeVideo> recentlyUploaded = new ArrayList<YouTubeVideo>();
	
	/**
	 * @return the names
	 */
	public Map<String, String> getNames() {
		return names;
	}

	/**
	 * @return the previousVideoAmounts
	 */
	public Map<String, String> getPreviousVideoAmounts() {
		return previousVideoAmounts;
	}

	/**
	 * @return the currentVideoAmounts
	 */
	public Map<String, String> getCurrentVideoAmounts() {
		return currentVideoAmounts;
	}

	/**
	 * @return the recentlyUploaded
	 */
	public List<YouTubeVideo> getRecentlyUploaded() {
		return recentlyUploaded;
	}

	public YouTubeChannels(List<String> channelIds, YouTubeAuth auth) {
		String URL = BASEURL;
		for (String channelId : channelIds) {
			URL = URL + "&id=" + channelId;
		}
		URL += "&key=" + auth.getApiKey();
		OkHttpClient client = new OkHttpClient();
		Request request = new Request.Builder().url(URL).build();
		try {
			Response response = client.newCall(request).execute();
			JSONParser parser = new JSONParser();
			JSONObject json = (JSONObject) parser.parse(response.body().string());
			JSONArray items = (JSONArray) json.get("items");
			for (int i = 0; i < items.size(); i++) {
				JSONObject item = (JSONObject) items.get(i);
				JSONObject statistics = (JSONObject) item.get("statistics");
				JSONObject snippet = (JSONObject) item.get("snippet");
				String name = snippet.get("title").toString();
				String id = item.get("id").toString();
				String videoAmount = statistics.get("videoCount").toString();
				this.currentVideoAmounts.put(id, videoAmount);
				this.previousVideoAmounts.put(id, videoAmount);
				this.names.put(id, name);
			}
			RedisAuth redisAuth = new RedisAuth();
			if (Helper.addSetToRedis(redisAuth, "youtube", this.currentVideoAmounts)) {
				Logging.logger.info("Added YouTube channels to redis database: " + this.currentVideoAmounts);
			} else {
				Logging.logger.severe("Failed adding YouTube channels to redis database: " + this.currentVideoAmounts);
				System.exit(1);
			}
			if (Helper.addSetToRedis(redisAuth, "youtubeNames", this.names)) {
				Logging.logger.info("Added YouTube channel names to redis database: " + this.names);
			} else {
				Logging.logger.severe("Failed adding YouTube channel names to redis database: " + this.names);
				System.exit(1);
			}
		} catch (IOException | ParseException e) {
			Logging.logger.severe(e.toString());
			System.exit(1);
		}
	}

	public YouTubeChannels(YouTubeAuth auth) {
		RedisAuth redisAuth = new RedisAuth();
		this.previousVideoAmounts = Helper.getSetFromRedis(redisAuth, "youtube");
		this.names = Helper.getSetFromRedis(redisAuth, "youtubeNames");
		Set<String> channelIds = this.previousVideoAmounts.keySet();
		String URL = BASEURL;
		for (String channelId: channelIds) {
			URL = URL + "&id=" + channelId;
		}
		URL += "&key=" + auth.getApiKey();
		OkHttpClient client = new OkHttpClient();
		Request request = new Request.Builder().url(URL).build();
		try {
			Response response = client.newCall(request).execute();
			JSONParser parser = new JSONParser();
			JSONObject json = (JSONObject) parser.parse(response.body().string());
			JSONArray items = (JSONArray) json.get("items");
			for (int i=0; i < items.size(); i++) {
				JSONObject item = (JSONObject) items.get(i);
				JSONObject statistics = (JSONObject) item.get("statistics");
				String id = item.get("id").toString();
				String videoAmount = statistics.get("videoCount").toString();
				this.currentVideoAmounts.put(id, videoAmount);
				if(Integer.parseInt(videoAmount) > Integer.parseInt(this.previousVideoAmounts.get(id))) {
					this.recentlyUploaded.add(new YouTubeVideo(id, auth));
				}
			}
			if(Helper.addSetToRedis(redisAuth, "youtube", this.currentVideoAmounts)) {
				Logging.logger.info("Updated YouTube channels on redis database: " + this.currentVideoAmounts);
			} else {
				Logging.logger.severe("Failed updating YouTube channels on redis database: " + this.currentVideoAmounts);
				System.exit(1);
			}
		} catch (IOException | ParseException e) {
			Logging.logger.severe(e.toString());
			System.exit(1);
		}
	}
}
