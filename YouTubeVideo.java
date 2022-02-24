package com.github.jnstockley;

import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class YouTubeVideo {

	private final String CHANNEL = "https://www.youtube.com/channel/";
	
	private final String VIDEO = "https://youtube.googleapis.com/youtube/v3/videos?part=snippet,contentDetails,liveStreamingDetails";
	
	private String id;
	
	private String name;
	
	private String channelName;
	
	private boolean shorts;
	
	private boolean premiere;
	
	private String premiereDate;

	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return the channelName
	 */
	public String getChannelName() {
		return channelName;
	}

	/**
	 * @return the shorts
	 */
	public boolean isShorts() {
		return shorts;
	}

	/**
	 * @return the premiere
	 */
	public boolean isPremiere() {
		return premiere;
	}
	
	
	
	/**
	 * @return the premiereDate
	 */
	public String getPremiereDate() {
		return premiereDate;
	}

	@Override
	public String toString() {
		return "YouTubeVideo [id=" + id + ", name=" + name + ", channelName=" + channelName + ", shorts=" + shorts
				+ ", premiere=" + premiere + ", premiereDate=" + premiereDate + "]";
	}

	public YouTubeVideo(String channelId, YouTubeAuth auth) {
		this.id = getVideoId(channelId);
		String URL = VIDEO + "&id=" + this.id + "&key=" + auth.getApiKey();
		OkHttpClient client = new OkHttpClient();
		Request request = new Request.Builder().url(URL).build();
		try {
			Response response = client.newCall(request).execute();
			JSONParser parser = new JSONParser();
			JSONObject JSON = (JSONObject) parser.parse(response.body().string());
			JSONArray items = (JSONArray) JSON.get("items");
			JSONObject item = (JSONObject) items.get(0);
			JSONObject snippet = (JSONObject) item.get("snippet");
			this.name = snippet.get("title").toString();
			this.channelName = snippet.get("channelTitle").toString();
			this.shorts = isShorts(item);
			this.premiere = isPremiere(item);
			if(this.premiere) {
				this.premiereDate = setPremiereDate((JSONObject)item.get("liveStreamingDetails"));
			} else {
				this.premiereDate = new Date().toString();
			}
		} catch (IOException | ParseException e) {
			
		}
	}
	
	private String getVideoId(String channelId) {
		String URL = CHANNEL + channelId + "/videos?view=0&sort=dd";
		try {
			Document doc = Jsoup.connect(URL).get();
			String body = doc.html();
			String jsonText = body.substring(body.indexOf("var ytInitialData = {") + 20, body.indexOf("}}};") + 3);
			JSONParser parser = new JSONParser();
			JSONObject JSON = (JSONObject) parser.parse(jsonText);
			JSONObject contents = (JSONObject) JSON.get("contents");
			JSONObject twoColumnBrowseResultsRenderer = (JSONObject) contents.get("twoColumnBrowseResultsRenderer");
			JSONArray tabs = (JSONArray) twoColumnBrowseResultsRenderer.get("tabs");
			JSONObject firstIndex = (JSONObject) tabs.get(1);
			JSONObject tabRenderer = (JSONObject) firstIndex.get("tabRenderer");
			JSONObject content = (JSONObject) tabRenderer.get("content");
			JSONObject sectionListRenderer = (JSONObject) content.get("sectionListRenderer");
			JSONArray contents2 = (JSONArray) sectionListRenderer.get("contents");
			JSONObject firstIndex2 = (JSONObject) contents2.get(0);
			JSONObject itemSectionRenderer = (JSONObject) firstIndex2.get("itemSectionRenderer");
			JSONArray contents3 = (JSONArray) itemSectionRenderer.get("contents");
			JSONObject firstIndex3 = (JSONObject) contents3.get(0);
			JSONObject gridRenderer = (JSONObject) firstIndex3.get("gridRenderer");
			JSONArray contents4 = (JSONArray) gridRenderer.get("items");
			JSONObject firstIndex4 = (JSONObject) contents4.get(0);
			JSONObject gridVideoRenderer2 = (JSONObject) firstIndex4.get("gridVideoRenderer");
			return gridVideoRenderer2.get("videoId").toString();
		} catch (IOException | ParseException e) {
			Logging.logger.severe(e.toString());
			System.exit(1);
			return null;
		}
	}
	
	private boolean isShorts(JSONObject JSON) {
		if(this.name.contains("#shorts")) {
			JSONObject contentDetails = (JSONObject) JSON.get("contentDetails");
			Long duration = Duration.parse(contentDetails.get("duration").toString()).toSeconds();
			if(duration <= 60) {
				return true;
			} else {
				return false;
			}
		} else {
			return false;
		}
	}
	
	private boolean isPremiere(JSONObject JSON) {
		JSONObject snippet = (JSONObject) JSON.get("snippet");
		if(snippet.get("liveBroadcastContent").toString().equalsIgnoreCase("upcoming")) {
			return true;
		} else {
			return false;
		}
	}
	
	private String setPremiereDate(JSONObject JSON) {
		Instant instant = Instant.parse(JSON.get("scheduledStartTime").toString());
		long seconds = instant.getEpochSecond();
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy - hh:mm a");
		ZonedDateTime zdt = Instant.ofEpochSecond(seconds).atZone(ZoneId.systemDefault());
		String formattedString = zdt.format(formatter);
		return formattedString;
		
	}
	
}
