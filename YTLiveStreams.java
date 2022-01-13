package com.github.jnstockley;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class YTLiveStreams {
	
	private final static File DATAFILE = new File(System.getProperty("user.home") + "/BTTN/youtubeLive.json");

	private final String BASEURL = "https://www.youtube.com/channel/";
	
	private HashMap<String, Boolean> previousLiveStatus = new HashMap<String, Boolean>();
	
	private HashMap<String, Boolean> curentliveStatus = new HashMap<String, Boolean>();
	
	private List<YTLiveStream> recentlyLiveChannels = new ArrayList<YTLiveStream>();

	/**
	 * @return the previousLiveStatus
	 */
	public HashMap<String, Boolean> getPreviousLiveStatus() {
		return previousLiveStatus;
	}

	/**
	 * @param previousLiveStatus the previousLiveStatus to set
	 */
	public void setPreviousLiveStatus(HashMap<String, Boolean> previousLiveStatus) {
		this.previousLiveStatus = previousLiveStatus;
	}

	/**
	 * @return the curentliveStatus
	 */
	public HashMap<String, Boolean> getCurentliveStatus() {
		return curentliveStatus;
	}

	/**
	 * @param curentliveStatus the curentliveStatus to set
	 */
	public void setCurentliveStatus(HashMap<String, Boolean> curentliveStatus) {
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

	@Override
	public String toString() {
		return "YTLiveStreams [previousLiveStatus=" + previousLiveStatus + ", curentliveStatus=" + curentliveStatus
				+ ", recentlyLiveChannels=" + recentlyLiveChannels + "]";
	}
	
	@SuppressWarnings("unchecked")
	public YTLiveStreams() {
		JSONParser parser = new JSONParser();
		JSONObject json = new JSONObject();
		try {
			json = (JSONObject) parser.parse(new FileReader(DATAFILE));
		} catch (ParseException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Set<String> channelIds = json.keySet();
		for(String channel: channelIds) {
			this.previousLiveStatus.put(channel, Boolean.parseBoolean(json.get(channel).toString()));
			JSONObject channelJSON = getJSON(channel);
			this.curentliveStatus.put(channel, Boolean.parseBoolean(channelJSON.get("isLive").toString()));
			if(this.curentliveStatus.get(channel) && !this.previousLiveStatus.get(channel)) {
				this.recentlyLiveChannels.add(new YTLiveStream(channelJSON, channel));
			}
		}
		JSONObject liveStatus = new JSONObject(this.curentliveStatus);
		try {
			FileWriter writer = new FileWriter(DATAFILE);
			writer.write(liveStatus.toJSONString());
			writer.flush();
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@SuppressWarnings("unchecked")
	private JSONObject getJSON(String channelId) {
		OkHttpClient client = new OkHttpClient();
		Request request = new Request.Builder().url(BASEURL + channelId + "/live/")
				.build();
		try {
			Response response = client.newCall(request).execute();
			JSONParser parser = new JSONParser();
			String data = response.body().string();
			JSONObject liveJSON = new JSONObject();
			if(data.contains("streamingData")) {
				JSONObject json = (JSONObject) parser.parse(data.substring(data.indexOf("ytInitialPlayerResponse")+26, data.indexOf("}}}};</script>")+4));
				if (json.containsKey("streamingData") && json.containsKey("videoDetails")) {
					liveJSON.put("isLive", true);
					JSONObject videoDetails = (JSONObject) json.get("videoDetails");
					liveJSON.put("streamName", videoDetails.get("title").toString());
					liveJSON.put("channelName", videoDetails.get("author").toString());
					return liveJSON;
				} else {
					liveJSON.put("isLive", false);
					return liveJSON;
				}
			} else {
				liveJSON.put("isLive", false);
				return liveJSON;
			}
		} catch (IOException | ParseException e) {
			e.printStackTrace();
			return null;
		}
	}
}
