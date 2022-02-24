package com.github.jnstockley;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class YTLiveStreams {

	private static int attempt = 0;
	
	private final String BASEURL = "https://www.youtube.com/channel/";

	private Map<String, String> previousLiveStatus = new HashMap<String, String>();

	private Map<String, String> curentliveStatus = new HashMap<String, String>();

	private List<YTLiveStream> recentlyLiveChannels = new ArrayList<YTLiveStream>();
	
	private List<String> channelNames = new ArrayList<String>();

	/**
	 * @return the previousLiveStatus
	 */
	public Map<String, String> getPreviousLiveStatus() {
		return previousLiveStatus;
	}

	/**
	 * @param previousLiveStatus the previousLiveStatus to set
	 */
	public void setPreviousLiveStatus(Map<String, String> previousLiveStatus) {
		this.previousLiveStatus = previousLiveStatus;
	}

	/**
	 * @return the curentliveStatus
	 */
	public Map<String, String> getCurentliveStatus() {
		return curentliveStatus;
	}

	/**
	 * @param curentliveStatus the curentliveStatus to set
	 */
	public void setCurentliveStatus(Map<String, String> curentliveStatus) {
		this.curentliveStatus = curentliveStatus;
	}

	/**
	 * @return the recentlyLiveChannels
	 */
	public List<YTLiveStream> getRecentlyLiveChannels() {
		return recentlyLiveChannels;
	}

	/**
	 * @param recentlyLiveChannels the recentlyLiveChannels to set
	 */
	public void setRecentlyLiveChannels(List<YTLiveStream> recentlyLiveChannels) {
		this.recentlyLiveChannels = recentlyLiveChannels;
	}

	/**
	 * @return the channelNames
	 */
	public List<String> getChannelNames() {
		return channelNames;
	}

	/**
	 * @param channelNames the channelNames to set
	 */
	public void setChannelNames(List<String> channelNames) {
		this.channelNames = channelNames;
	}

	@Override
	public String toString() {
		return "YTLiveStreams [previousLiveStatus=" + previousLiveStatus + ", curentliveStatus=" + curentliveStatus
				+ ", recentlyLiveChannels=" + recentlyLiveChannels + "]";
	}

	public YTLiveStreams(List<String> channelIds) {
		for (String channel : channelIds) {
			this.previousLiveStatus.put(channel, Boolean.toString(false));
			JSONObject channelJSON = getJSON(channel);
			this.curentliveStatus.put(channel, channelJSON.get("isLive").toString());
			if (Boolean.parseBoolean(this.curentliveStatus.get(channel)) && !Boolean.parseBoolean(this.previousLiveStatus.get(channel))) {
				this.recentlyLiveChannels.add(new YTLiveStream(channelJSON, channel));
			}
		}
		RedisAuth auth = new RedisAuth();
		if(Helper.addSetToRedis(auth, "youtubeLive", this.curentliveStatus)) {
			Logging.logger.info("Wrote updated YouTube live status to redis server at: " + auth.getServer()+":" + auth.getPort());
		} else {
			Logging.logger.severe("Failed to update YouTube live status on redist server at: " + auth.getServer() + ":" + auth.getPort());
		}
	}

	public YTLiveStreams() {
		RedisAuth auth = new RedisAuth();
		this.previousLiveStatus = Helper.getSetFromRedis(auth, "youtubeLive");
		Set<String> channelIds = this.previousLiveStatus.keySet();
		for (String channel : channelIds) {
			JSONObject channelJSON = getJSON(channel);
			this.channelNames.add(channelJSON.get("channelName").toString());
			this.curentliveStatus.put(channel, channelJSON.get("isLive").toString());
			if (Boolean.parseBoolean(this.curentliveStatus.get(channel)) && !Boolean.parseBoolean(this.previousLiveStatus.get(channel))) {
				this.recentlyLiveChannels.add(new YTLiveStream(channelJSON, channel));
			}
		}
		if (Helper.addSetToRedis(auth, "youtubeLive", this.curentliveStatus)) {
			Logging.logger.info("Wrote updated YouTube live status to redis server at: " + auth.getServer()+":" + auth.getPort());
		} else {
			Logging.logger.severe("Failed to update YouTube live status on redist server at: " + auth.getServer() + ":" + auth.getPort());
		}
	}

	@SuppressWarnings("unchecked")
	// TODO Possibly add logging for returned JSON
	private JSONObject getJSON(String channelId) {
		OkHttpClient client = new OkHttpClient();
		Request request = new Request.Builder().url(BASEURL + channelId + "/live/").build();
		try {
			Response response = client.newCall(request).execute();
			JSONParser parser = new JSONParser();
			String data = response.body().string();
			JSONObject liveJSON = new JSONObject();
			if (data.contains("streamingData")) {
				JSONObject json = (JSONObject) parser.parse(data.substring(data.indexOf("ytInitialPlayerResponse") + 26,
						data.indexOf("}}}};</script>") + 4));
				if (json.containsKey("streamingData") && json.containsKey("videoDetails")) {
					liveJSON.put("isLive", true);
					JSONObject videoDetails = (JSONObject) json.get("videoDetails");
					liveJSON.put("streamName", videoDetails.get("title").toString());
					liveJSON.put("channelName", videoDetails.get("author").toString());
					attempt = 0;
					return liveJSON;
				} else {
					liveJSON.put("isLive", false);
					attempt = 0;
					return liveJSON;
				}
			} else {
				liveJSON.put("isLive", false);
				Document doc = Jsoup.connect(BASEURL + channelId + "/live/").get();
				Elements titleElement = doc.select("title");
				String title = titleElement.first().html();
				String channelName = title.substring(0, title.indexOf(" - YouTube"));
				liveJSON.put("channelName", channelName);
				attempt = 0;
				return liveJSON;
			}
		} catch (IOException | ParseException e) {
			if(attempt < 3) {
				attempt ++;
				Logging.logger.warning("Attempt: " + attempt + "; " + e.toString());
				return getJSON(channelId);
			}
			attempt = 0;
			Logging.logger.severe("Max attempts: " + e.toString());
			System.exit(1);
			return null;
		}
	}	
}
