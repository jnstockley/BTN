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

import com.google.common.collect.Iterables;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class YTPlaylist {
	
	private final String BASEURL = "https://youtube.googleapis.com/youtube/v3/playlists?part=contentDetails";
	
	private final static File DATAFILE = new File(System.getProperty("user.home") + "/BTTN/youtube.json");
	
	private HashMap<String, Integer> previousVideoAmount = new HashMap<String, Integer>();
	
	private HashMap<String, Integer> curentVideoAmount = new HashMap<String, Integer>();
	
	private List<YTVideo> recentlyUploadedVideos = new ArrayList<YTVideo>();
	
	/**
	 * @return the previousVideoAmount
	 */
	public HashMap<String, Integer> getPreviousVideoAmount() {
		return previousVideoAmount;
	}

	/**
	 * @param previousVideoAmount the previousVideoAmount to set
	 */
	public void setPreviousVideoAmount(HashMap<String, Integer> previousVideoAmount) {
		this.previousVideoAmount = previousVideoAmount;
	}

	/**
	 * @return the curentVideoAmount
	 */
	public HashMap<String, Integer> getCurentVideoAmount() {
		return curentVideoAmount;
	}

	/**
	 * @param curentVideoAmount the curentVideoAmount to set
	 */
	public void setCurentVideoAmount(HashMap<String, Integer> curentVideoAmount) {
		this.curentVideoAmount = curentVideoAmount;
	}

	/**
	 * @return the recentlyUploadedVideos
	 */
	public List<YTVideo> getRecentlyUploadedVideos() {
		return recentlyUploadedVideos;
	}

	/**
	 * @param recentlyUploadedVideos the recentlyUploadedVideos to set
	 */
	public void setRecentlyUploadedVideos(List<YTVideo> recentlyUploadedVideos) {
		this.recentlyUploadedVideos = recentlyUploadedVideos;
	}

	@Override
	public String toString() {
		return "YTPlaylist [previousVideoAmount=" + previousVideoAmount + ", curentVideoAmount=" + curentVideoAmount
				+ "]";
	}

	@SuppressWarnings("unchecked")
	public YTPlaylist(Auth auth) {
		JSONParser parser = new JSONParser();
		JSONObject json = new JSONObject();
		try {
			json = (JSONObject) parser.parse(new FileReader(DATAFILE));
		} catch (IOException | ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//String idURLS = "";
		Set<String> playlistIds = json.keySet();
		JSONObject playlistsJSON  = new JSONObject();
		for(String playlistId: playlistIds) {
			previousVideoAmount.put(playlistId, Integer.parseInt(json.get(playlistId).toString()));
			//idURLS = idURLS + "&id=" + playlistId;
		}
		int splits = 0;
		while (splits < (playlistIds.size() / 50) + 1) {
			String idURLS = "";
			for(int i=(splits * 50); i< (splits * 50) + 50; i++) {
				if(i == playlistIds.size()) {
					break;
				}
				String playlistId = Iterables.get(playlistIds, i);
				previousVideoAmount.put(playlistId, Integer.parseInt(json.get(playlistId).toString()));
				idURLS = idURLS + "&id=" + playlistId;
			}
			playlistsJSON.putAll(getJSON(idURLS, auth));
			splits++;
		}
		this.curentVideoAmount = playlistsJSON;
		for(String playlistId: playlistIds) {
			if(this.previousVideoAmount.get(playlistId) > 0 && this.curentVideoAmount.get(playlistId) > this.previousVideoAmount.get(playlistId)) {
				this.recentlyUploadedVideos.add(new YTVideo(playlistId, auth));
			}
		}
		try {
			FileWriter writer = new FileWriter(DATAFILE);
			writer.write(playlistsJSON.toJSONString());
			writer.flush();
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@SuppressWarnings("unchecked")
	private JSONObject getJSON(String playlistURL, Auth auth) {
		OkHttpClient client = new OkHttpClient();
		Request request = new Request.Builder().url(BASEURL + playlistURL + "&key=" + auth.getYoutubeAPIKey())
				.build();
		try {
			Response response = client.newCall(request).execute();
			JSONParser parser = new JSONParser();
			JSONObject json = (JSONObject) parser.parse(response.body().string());
			JSONArray items = (JSONArray) json.get("items");
			JSONObject JSONData = new JSONObject();
			for(int i=0; i<items.size(); i++) {
				JSONObject playlist = (JSONObject) items.get(i);
				JSONObject contentDetails = (JSONObject) playlist.get("contentDetails");
				JSONData.put(playlist.get("id").toString(), Integer.parseInt(contentDetails.get("itemCount").toString()));
			}
			return JSONData;
		} catch (IOException | ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

}
