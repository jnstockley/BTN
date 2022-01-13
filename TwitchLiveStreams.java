package com.github.jnstockley;

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

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class TwitchLiveStreams {
	
	private final static String BASEURL = "https://api.twitch.tv/helix/streams?first=100";

	private final static File DATAFILE = new File(System.getProperty("user.home") + "/BTTN/twitch.json");
	
	private HashMap<String, Boolean> previousLiveStatus = new HashMap<String, Boolean>();
	
	private HashMap<String, Boolean> curentliveStatus = new HashMap<String, Boolean>();
	
	private List<TwitchLiveStream> recentlyLiveChannels = new ArrayList<TwitchLiveStream>();

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
	 * @return the liveChannels
	 */
	public List<TwitchLiveStream> getRecentlyLiveChannels() {
		return recentlyLiveChannels;
	}

	/**
	 * @param liveChannels the liveChannels to set
	 */
	public void setRecentlyLiveChannels(List<TwitchLiveStream> liveChannels) {
		this.recentlyLiveChannels = liveChannels;
	}

	@Override
	public String toString() {
		return "Twitch [previousLiveStatus=" + previousLiveStatus + ", curentliveStatus=" + curentliveStatus
				+ ", liveChannels=" + recentlyLiveChannels + "]";
	}
	
	@SuppressWarnings("unchecked")
	public TwitchLiveStreams(Auth auth) {
		//TODO Doesn't respect old live status and doesn't write status change to file
		JSONParser parser = new JSONParser();
		JSONObject json = new JSONObject();
		try {
			json = (JSONObject) parser.parse(new FileReader(DATAFILE));
		} catch (IOException | ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String nameURL = "";
		Set<String> channelNames = json.keySet();
		for(String channel: channelNames) {
			this.previousLiveStatus.put(channel, Boolean.parseBoolean(json.get(channel).toString()));
			nameURL = nameURL + "&user_login=" + channel;
		}
		//this.curentliveStatus = this.previousLiveStatus;
		JSONObject streamJSON = getJSON(nameURL, auth);
		//JSONObject liveStatus = new JSONObject();
		for(String channel: channelNames) {
			if(streamJSON.containsKey(channel)) {
				if(!this.previousLiveStatus.get(channel)) {
					this.recentlyLiveChannels.add(new TwitchLiveStream(channel, (JSONObject) streamJSON.get(channel)));				}
				this.curentliveStatus.put(channel, true);
			} else {
				this.curentliveStatus.put(channel, false);
			}
		}
		/*if(!streamJSON.isEmpty()) {
			Set<String> channels = streamJSON.keySet();
			for(String channel: channels) {
				if(!this.previousLiveStatus.get(channel).booleanValue()) {
					this.recentlyLiveChannels.add(new TwitchLiveStream(channel, (JSONObject) streamJSON.get(channel)));
					this.curentliveStatus.put(channel,true);
				}
			}
			liveStatus = new JSONObject(this.curentliveStatus);
		} else {
			liveStatus = new JSONObject(this.previousLiveStatus);
		}*/
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
	private JSONObject getJSON(String nameURL, Auth auth) {
		OkHttpClient client = new OkHttpClient();
		Request request = new Request.Builder().url(BASEURL + nameURL).header("client-id", auth.getTwitchClientId())
				.header("Authorization", "Bearer " + auth.getTwitchAuthorizationKey()).build();
		try {
			Response response = client.newCall(request).execute();
			JSONParser parser = new JSONParser();
			JSONObject json = (JSONObject) parser.parse(response.body().string());
			JSONArray channelJSON = (JSONArray) json.get("data");
			JSONObject twitchJSON = new JSONObject();
			for(int i=0; i<channelJSON.size(); i++) {
				JSONObject data = (JSONObject) channelJSON.get(i);
				JSONObject channel = new JSONObject();
				channel.put("title", data.get("title"));
				channel.put("game", data.get("game_name"));
				twitchJSON.put(data.get("user_name"), channel);
			}
			return twitchJSON;
		} catch (IOException | ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

}
