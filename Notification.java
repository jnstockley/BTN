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
		if (data.get(0).getClass().equals(YouTubeVideo.class)) {
			if (data.size() == 1) {
				YouTubeVideo video = (YouTubeVideo) data.get(0);
				try {
					if (video.isPremiere()) {
						this.type = NotificationType.YoutubePremiere;
						this.title = URLEncoder.encode(
								video.getChannelName() + " has scheduled a premiere for " + video.getPremiereDate(),
								StandardCharsets.UTF_8.toString());
					} else if (video.isShorts()) {
						this.type = NotificationType.YoutubeShort;
						this.title = URLEncoder.encode(video.getChannelName() + " has posted a Short!",
								StandardCharsets.UTF_8.toString());
					} else {
						this.type = NotificationType.Youtube;
						this.title = URLEncoder.encode(video.getChannelName() + " has uploaded!",
								StandardCharsets.UTF_8.toString());
					}
					this.message = URLEncoder.encode(video.getName(), StandardCharsets.UTF_8.toString());
					this.url = URLEncoder.encode("https://youtube.com/watch?v=" + video.getId(),
							StandardCharsets.UTF_8.toString());
				} catch (UnsupportedEncodingException e) {
					Logging.logger.severe(e.toString());
					System.exit(1);
				}

			} else if (data.size() > 1) {
				this.title = "";
				for (int i = 0; i < data.size(); i++) {
					YouTubeVideo video = (YouTubeVideo) data.get(i);
					this.title = this.title + video.getChannelName() + ", ";
				}
				try {
					this.title = URLEncoder.encode(this.title.substring(0, this.title.length() - 2) + " have uploaded!",
							StandardCharsets.UTF_8.toString());
					this.message = URLEncoder.encode("Check out their latest YoutTube videos!",
							StandardCharsets.UTF_8.toString());
					this.url = URLEncoder.encode("https://youtube.com/feed/subscriptions/",
							StandardCharsets.UTF_8.toString());
				} catch (UnsupportedEncodingException e) {
					Logging.logger.severe(e.toString());
					System.exit(1);
				}
			}
		} else if (data.get(0).getClass().equals(YTLiveStream.class)) {
			this.type = NotificationType.YoutubeStream;
			if (data.size() == 1) {
				YTLiveStream ytLiveStream = (YTLiveStream) data.get(0);
				try {
					this.title = URLEncoder.encode(ytLiveStream.getChannelName() + " has gone live on YouTube!",
							StandardCharsets.UTF_8.toString());
					this.message = URLEncoder.encode(ytLiveStream.getStreamName(), StandardCharsets.UTF_8.toString());
					this.url = URLEncoder.encode(
							"https://www.youtube.com/channel/" + ytLiveStream.getChannelId() + "/live/",
							StandardCharsets.UTF_8.toString());
				} catch (UnsupportedEncodingException e) {

				}
			} else if (data.size() > 1) {
				for (int i = 0; i < data.size(); i++) {
					YTLiveStream channel = (YTLiveStream) data.get(i);
					this.title = this.title + channel.getChannelName() + ", ";
				}
				try {
					this.title = URLEncoder.encode(
							this.title.substring(0, this.title.length() - 2) + " are live on YouTube!",
							StandardCharsets.UTF_8.toString());
					this.message = URLEncoder.encode("Check them out on YouTube!", StandardCharsets.UTF_8.toString());
					this.url = URLEncoder.encode("https://youtube.com/feed/subscriptions/",
							StandardCharsets.UTF_8.toString());
				} catch (UnsupportedEncodingException e) {
					Logging.logger.severe(e.toString());
					System.exit(1);
				}
			}
		}
	}

	public Notification(TwitchLiveStream stream) {
		this.type = NotificationType.Twitch;
		String channelName = stream.getChannelName();
		try {
			this.title = URLEncoder.encode(channelName + " is live on Twitch!", StandardCharsets.UTF_8.toString());
			this.message = URLEncoder.encode(stream.getStreamName(), StandardCharsets.UTF_8.toString());
			this.url = URLEncoder.encode("https://twitch.tv/" + channelName, StandardCharsets.UTF_8.toString());
		} catch (UnsupportedEncodingException e) {
			Logging.logger.severe(e.toString());
			System.exit(1);
		}
	}

	@Override
	public String toString() {
		return "Notification [title=" + title + ", message=" + message + ", url=" + url + ", type=" + type + "]";
	}

	public boolean send(AlertzyAuth auth) {
		String keysStr = "";
		for (String key : auth.getAccountKeys()) {
			keysStr += key + "_";
		}
		keysStr = keysStr.substring(0, keysStr.length() - 1);
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
				Logging.logger.info("Sent Alertzy Notification: " + this.toString());
				return true;
			} else {
				Logging.logger.severe("Error sending Alertzy Notification: Sent to: " + responseJSON.get("sentTo")
						+ ", Error: " + responseJSON.get("error"));
				return false;
			}
		} catch (IOException | ParseException e) {
			Logging.logger.severe(e.toString());
			return false;
		}
	}
}
