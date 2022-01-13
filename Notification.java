package com.github.jnstockley;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class Notification<E> {

	private String title;

	private String message;

	private String url;

	public NotificationType type;

	public Notification(List<E> data) {
		if (data.get(0).getClass().equals(YTVideo.class)) {
			this.type = NotificationType.Youtube;
			if (data.size() == 1) {
				YTVideo video = (YTVideo) data.get(0);
				try {
					if (video.isShorts()) {
						this.title = URLEncoder.encode(video.getChannelName() + " has posted a Short!", StandardCharsets.UTF_8.toString());
					} else {
						this.title =  URLEncoder.encode(video.getChannelName() + " has uploaded!", StandardCharsets.UTF_8.toString());
					}
					this.message = URLEncoder.encode(video.getVideoName(), StandardCharsets.UTF_8.toString());
					this.url = URLEncoder.encode("https://youtube.com/watch?v=" + video.getVideoId(), StandardCharsets.UTF_8.toString());
				} catch (UnsupportedEncodingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			} else if (data.size() > 1) {
				this.title = "";
				for (int i = 0; i < data.size(); i++) {
					YTVideo video = (YTVideo) data.get(i);
					this.title = this.title + video.getChannelName() + ", ";
				}
				try {
					this.title = URLEncoder.encode(this.title.substring(0, this.title.length() - 2) + " have uploaded!", StandardCharsets.UTF_8.toString());
					this.message = URLEncoder.encode("Check out their latest YoutTube videos!", StandardCharsets.UTF_8.toString());
					this.url = URLEncoder.encode("https://youtube.com/feed/subscriptions/", StandardCharsets.UTF_8.toString());
				} catch (UnsupportedEncodingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		} else if (data.get(0).getClass().equals(TwitchLiveStream.class)) {
			this.type = NotificationType.Twitch;
			if (data.size() == 1) {
				TwitchLiveStream channel = (TwitchLiveStream) data.get(0);
				String channelName = channel.getChannelName();
				try {
					this.title = URLEncoder.encode(channelName + " is live on Twitch!", StandardCharsets.UTF_8.toString());
					this.message = URLEncoder.encode(channel.getStreamName(), StandardCharsets.UTF_8.toString());
					this.url = URLEncoder.encode("https://twitch.tv/" + channelName, StandardCharsets.UTF_8.toString());
				} catch (UnsupportedEncodingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} else if (data.size() > 1) {
				for (int i = 0; i < data.size(); i++) {
					TwitchLiveStream channel = (TwitchLiveStream) data.get(i);
					this.title = this.title + channel.getChannelName() + ", ";
				}
				try {
					this.title = URLEncoder.encode(this.title.substring(0, this.title.length() - 2) + " are live on Twitch!", StandardCharsets.UTF_8.toString());
					this.message = URLEncoder.encode("Check them out on twitch.tv!", StandardCharsets.UTF_8.toString());
					this.url = URLEncoder.encode("https://twitch.tv/", StandardCharsets.UTF_8.toString());
				} catch (UnsupportedEncodingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}	
			}
		} else if (data.get(0).getClass().equals(YTLiveStream.class)) {
			this.type = NotificationType.YoutubeStream;
			if (data.size() == 1) {
				YTLiveStream ytLiveStream = (YTLiveStream) data.get(0);
				try {
					this.title = URLEncoder.encode(ytLiveStream.getChannelName() + " has gone live on YouTube!", StandardCharsets.UTF_8.toString());
					this.message = URLEncoder.encode(ytLiveStream.getStreamName(), StandardCharsets.UTF_8.toString());
					this.url = URLEncoder.encode("https://www.youtube.com/channel/" + ytLiveStream.getChannelId() + "/live/", StandardCharsets.UTF_8.toString());
				} catch (UnsupportedEncodingException e) {
					
				}
			} else if (data.size() > 1) {
				for (int i = 0; i < data.size(); i++) {
					YTLiveStream channel = (YTLiveStream) data.get(i);
					this.title = this.title + channel.getChannelName() + ", ";
				}
				try {
					this.title = URLEncoder.encode(this.title.substring(0, this.title.length() - 2) + " are live on YouTube!", StandardCharsets.UTF_8.toString());
					this.message = URLEncoder.encode("Check them out on YouTube!", StandardCharsets.UTF_8.toString());
					this.url = URLEncoder.encode("https://youtube.com/feed/subscriptions/", StandardCharsets.UTF_8.toString());
				} catch (UnsupportedEncodingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
		}
	}

	@Override
	public String toString() {
		return "Notification [title=" + title + ", message=" + message + ", url=" + url + ", type=" + type + "]";
	}

	public boolean send(Auth auth) {
		String keysStr = "";
		for (String key : auth.getAlertzyAccountKeys()) {
			keysStr += key + "_";
		}
		String url = "https://alertzy.app/send?accountKey=" + keysStr + "&title=" + this.title + "&message="
				+ this.message + "&link=" + this.url;
		OkHttpClient client = new OkHttpClient();
		Request request = new Request.Builder().url(url).build();
		try (Response response = client.newCall(request).execute()) {
			// Parse the HTTP response and makes sure it was sent
			JSONParser parser = new JSONParser();
			JSONObject responseJSON = (JSONObject) parser.parse(response.body().string());
			String success = responseJSON.get("response").toString();
			if (success.equalsIgnoreCase("success")) {
				return true;
			} else {
				return false;
			}
		} catch (IOException | ParseException e) {
			// TODO Error logging
			return false;
		}
	}
}
