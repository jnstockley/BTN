package com.github.jnstockley;

import java.io.IOException;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class YTVideo {

	private final String BASEURL = "https://youtube.googleapis.com/youtube/v3/playlistItems?part=snippet&playlistId=";

	private final String SHORTSURL = "https://youtube.googleapis.com/youtube/v3/videos?part=snippet&id=";

	private String playlistId;

	private String videoId;

	private String channelName;

	private String videoName;

	private boolean isShorts;

	/**
	 * @return the playlistId
	 */
	public String getPlaylistId() {
		return playlistId;
	}

	/**
	 * @param playlistId the playlistId to set
	 */
	public void setPlaylistId(String playlistId) {
		this.playlistId = playlistId;
	}

	/**
	 * @return the videeoId
	 */
	public String getVideoId() {
		return videoId;
	}

	/**
	 * @param videeoId the videeoId to set
	 */
	public void setVideoId(String videoId) {
		this.videoId = videoId;
	}

	/**
	 * @return the channelName
	 */
	public String getChannelName() {
		return channelName;
	}

	/**
	 * @param channelName the channelName to set
	 */
	public void setChannelName(String channelName, Auth auth) {
		this.channelName = channelName;
	}

	/**
	 * @return the videoName
	 */
	public String getVideoName() {
		return videoName;
	}

	/**
	 * @param videoName the videoName to set
	 */
	public void setVideoName(String videoName) {
		this.videoName = videoName;
	}

	/**
	 * @return the isShorts
	 */
	public boolean isShorts() {
		return isShorts;
	}

	/**
	 * @param isShorts the isShorts to set
	 */
	public void setShorts(boolean isShorts) {
		this.isShorts = isShorts;
	}

	@Override
	public String toString() {
		return "YTVideo [playlistId=" + playlistId + ", videoId=" + videoId + ", channelName=" + channelName
				+ ", videoName=" + videoName + ", isShorts=" + isShorts + "]";
	}

	public YTVideo(String playlistId, Auth auth) {
		this.playlistId = playlistId;
		JSONObject videoJSON = getJSON(this.playlistId, auth);
		this.channelName = videoJSON.get("channelTitle").toString();
		this.videoName = videoJSON.get("title").toString();
		this.videoId = videoJSON.get("videoId").toString();
		this.isShorts = Boolean.parseBoolean(videoJSON.get("isShorts").toString());
	}

	@SuppressWarnings("unchecked")
	private JSONObject getJSON(String playlistId, Auth auth) {
		OkHttpClient client = new OkHttpClient();
		Request request = new Request.Builder().url(BASEURL + playlistId + "&key=" + auth.getYoutubeAPIKey()).build();
		try {
			Response response = client.newCall(request).execute();
			JSONParser parser = new JSONParser();
			JSONObject json = (JSONObject) parser.parse(response.body().string());
			JSONArray items = (JSONArray) json.get("items");
			JSONObject video = (JSONObject) items.get(0);
			JSONObject snippet = (JSONObject) video.get("snippet");
			JSONObject resourceId = (JSONObject) snippet.get("resourceId");
			JSONObject JSONData = new JSONObject();
			JSONData.put("title", snippet.get("title").toString());
			JSONData.put("channelTitle", snippet.get("channelTitle").toString());
			JSONData.put("videoId", resourceId.get("videoId").toString());
			if (snippet.get("title").toString().toLowerCase().contains("#short")
					|| snippet.get("title").toString().toLowerCase().contains("#shorts")) {
				JSONData.put("isShorts", true);
			} else {
				request = new Request.Builder()
						.url(SHORTSURL + JSONData.get("videoId") + "&key=" + auth.getYoutubeAPIKey()).build();
				response = client.newCall(request).execute();
				json = (JSONObject) parser.parse(response.body().string());
				items = (JSONArray) json.get("items");
				video = (JSONObject) items.get(0);
				snippet = (JSONObject) video.get("snippet");
				JSONData.put("isShorts", false);
				if (snippet.containsKey("tags")) {
					JSONArray tags = (JSONArray) snippet.get("tags");
					for (int i = 0; i < tags.size(); i++) {
						if (tags.get(i).toString().toLowerCase().equals("shorts")
								|| tags.get(i).toString().toLowerCase().equals("short")) {
							JSONData.put("isShorts", true);
							break;
						}
					}
				}

			}
			return JSONData;
		} catch (IOException | ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

}
