package com.github.jnstockley;

import java.io.IOException;
import java.util.List;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import com.github.philippheuer.events4j.simple.SimpleEventHandler;
import com.github.twitch4j.TwitchClient;
import com.github.twitch4j.TwitchClientBuilder;
import com.github.twitch4j.events.ChannelGoLiveEvent;

import okhttp3.OkHttpClient;
import okhttp3.Request;

class Twitch implements Runnable {
	public void run() {
		try {
			TwitchAuth twithAuth = new TwitchAuth(new RedisAuth());
			TwitchClient twitchClient = TwitchClientBuilder.builder().withEnableHelix(true)
					.withClientId(twithAuth.getClientId())
					.withClientSecret(twithAuth.getClientSecret())
					.build();
			List<String> channelNames = Helper.getListFromRedis(new RedisAuth(), "twitch");
			twitchClient.getClientHelper().enableStreamEventListener(channelNames);
			SimpleEventHandler eventHandler = twitchClient.getEventManager().getEventHandler(SimpleEventHandler.class);
			eventHandler.onEvent(ChannelGoLiveEvent.class, event -> {
				TwitchLiveStream stream = new TwitchLiveStream(event.getChannel().getName(), event.getStream().getTitle(), event.getStream().getGameName());
				if(sendTwitchLive(new AlertzyAuth(new RedisAuth()), stream)) {
					Logging.logger.info("Sent Twitch Live Stream Notification(s)!");
				} else {
					Logging.logger.info("No Twitch Live Stream Notifications to send!");
				}
			});
		} catch (Exception e) {
			Logging.logger.severe(e.toString());
			System.exit(1);
		}
	}

	private static boolean sendTwitchLive(AlertzyAuth auth, TwitchLiveStream stream) {
		Notification<TwitchLiveStream> notif = new Notification<TwitchLiveStream>(stream);
		return notif.send(auth);
	}
}

class YouTubeLive implements Runnable {
	public void run() {
		try {
			while (true) {
				if (sendYTLiveStream(new YouTubeAuth(new RedisAuth()))) {
					Logging.logger.info("Sent YouTube Live Stream Notification(s)!");
				} else {
					Logging.logger.info("No YouTube Live Stream Notifications to send!");
				}
				try {
					Thread.sleep(15000);
				} catch (InterruptedException e) {
					Logging.logger.severe(e.toString());
					System.exit(1);
				}
			}
		} catch (Exception e) {
			Logging.logger.severe(e.toString());
			System.exit(1);
		}
	}

	private static boolean sendYTLiveStream(YouTubeAuth auth) {
		YTLiveStreams ytLiveStreams = new YTLiveStreams();
		if (!ytLiveStreams.getRecentlyLiveChannels().isEmpty()) {
			Notification<YTLiveStream> ytLiveNotif = new Notification<YTLiveStream>(
					ytLiveStreams.getRecentlyLiveChannels());
			ytLiveNotif.send(new AlertzyAuth(new RedisAuth()));
			return true;
		} else {
			return false;
		}
	}
}

class YouTubeUpload implements Runnable {
	public void run() {
		try {
			if (sendYouTubeVideo(new YouTubeAuth(new RedisAuth()))) {
				Logging.logger.info("Sent YouTube Video Notification(s)!");
			} else {
				Logging.logger.info("No YouTube Video Notifications to send!");
			}
			try {
				Thread.sleep(15000);
			} catch (InterruptedException e) {
				Logging.logger.severe(e.toString());
				System.exit(1);
			}
		} catch (Exception e) {
			Logging.logger.severe(e.toString());
			System.exit(1);
		}
	}
	
	private static boolean sendYouTubeVideo(YouTubeAuth auth) {
		YouTubeChannels ytChannels = new YouTubeChannels(auth);
		if(!ytChannels.getRecentlyUploaded().isEmpty()) {
			Notification<YouTubeVideo> ytNotif = new Notification<YouTubeVideo>(ytChannels.getRecentlyUploaded());
			ytNotif.send(new AlertzyAuth(new RedisAuth()));
			return true;
		} else {
			return false;
		}
	}
}

public class Main {

	public static void main(String[] args) {
		/**
		 * TODO Lists 
		 * [X] Write YTVideo constructor
		 * [X] Check if curentVideoAmount > previousVideoAmount and make a new YTVideo for specific channel 
		 * [X] Send notification after getting new YTVideo if curentVideoAmount > previousVideoAmount
		 * [X] Remove old maven dependencies
		 * [X] Finish work on YT Live Streams 
		 * [X] Better Structure Main class 
		 * [X] URLEncode all strings being sent to Alertzy
		 * [X] Check and make BTTN folder to store all config and data files
		 * [X] Added ability for BTTN to add data to config and data files
		 * [ ] Check if data file is empty and disable that functionality from checking
		 * [ ] Add ability to update, list and remove data from config and data files
		 * [ ] Write CLI to handle setting up and modifying data and config files
		 * [ ] Add ability to store more than 1 YouTube API key to increase quota
		 * [ ] Write/find logging program for better troubleshooting
		 * [ ] Add argument class to handle arguments
		 * [ ] Switch from files to Redis and allow for custom url and ports with arguments
		 * [ ] Add update checker
		 * [ ] Add support for other languages
		 */

		// TODO Log YouTube JSON when a lower playlist count is detected!
		
		// TODO re-add args handler
		
		// TODO Convert YouTube Live to redis Hash

		// Make sure folders are setup correctly
		
		if (args.length >=1) {
			argsHandler(args);
			System.exit(0);
		}
		
		// Send Alertzy message saying BTTN is working
		sendStartingMessage();

		// Start Twitch events
		Thread twitchObject = new Thread(new Twitch());
		twitchObject.start();

		// Start YouTube events
		Thread youtubeLive = new Thread(new YouTubeLive());
		youtubeLive.start();

		// Start YouTube Video Upload Events
		Thread youtubeUpload = new Thread(new YouTubeUpload());
		youtubeUpload.start();
	}
	
	private static void argsHandler(String[] args) {
		Options options = new Options();
		options.addOption("add", false, "add something");
		options.addOption("list", false, "list something");
		options.addOption("update", false, "update something");
		options.addOption("remove", false, "remove something");
		options.addOption("twitch", false, "modify twitch");
		options.addOption("youtube", false, "modify youtube");
		options.addOption("youtubeLive", false, "modify youtube live stream");
		options.addOption("redis", false, "modify redis auth");
		options.addOption("alertzy", false, "modify alertzy auth");
		options.addOption("twitchAuth", false, "modify twitch auth");
		options.addOption("youtubeAuth", false, "modify youtube auth");
		CommandLineParser parser = new DefaultParser();
		CommandLine cmd = null;
		try {
			cmd = parser.parse(options, args);
		} catch (ParseException e) {
			Logging.logger.severe(e.toString());
			System.exit(1);
		}
				
		if(cmd.hasOption("add")) {
			if(cmd.hasOption("twitch")) {
				SetupManager.addTwitchChannel();
			} else if (cmd.hasOption("youtube")) {
				SetupManager.addYoutubeChannel();
			} else if (cmd.hasOption("youtubeLive")) {
				SetupManager.addYoutubeLiveChannel();
			} else if (cmd.hasOption("redis")) {
				SetupManager.addRedisAuth();
			} else if (cmd.hasOption("alertzy")) {
				SetupManager.addAlertzyAuth();
			} else if (cmd.hasOption("twitchAuth")) {
				SetupManager.addTwitchAuth();
			} else if (cmd.hasOption("youtubeAuth")) {
				SetupManager.addYoutubeAuth();
			} else {
				Logging.logger.severe(cmd.getArgList() + " is an invalid argument");
				System.exit(1);
			}
		} else if(cmd.hasOption("update")) {
			if(cmd.hasOption("twitch")) {
				SetupManager.updateTwitchChannel();
			} else if (cmd.hasOption("youtube")) {
				SetupManager.updateTwitchChannel();
			} else if (cmd.hasOption("youtubeLive")) {
				SetupManager.updateYoutubeLiveChannel();
			} else if (cmd.hasOption("redis")) {
				SetupManager.updateRedisAuth();
			} else if (cmd.hasOption("alertzy")) {
				SetupManager.updateAlertzyAuth();
			} else if (cmd.hasOption("twitchAuth")) {
				SetupManager.updateTwitchAuth();
			} else if (cmd.hasOption("youtubeAuth")) {
				SetupManager.updateYoutubeAuth();
			} else {
				Logging.logger.severe(cmd.getArgList() + " is an invalid argument");
				System.exit(1);
			}
		} else if (cmd.hasOption("remove")) {
			if(cmd.hasOption("twitch")) {
				SetupManager.removeTwitchChannel();
			} else if (cmd.hasOption("youtube")) {
				SetupManager.removeYoutubeChannel();
			} else if (cmd.hasOption("youtubeLive")) {
				SetupManager.removeYoutubeLiveChannel();
			} else if (cmd.hasOption("redis")) {
				SetupManager.removeRedisAuth();
			} else if (cmd.hasOption("alertzy")) {
				SetupManager.removeAlertzyAuth();
			} else if (cmd.hasOption("twitchAuth")) {
				SetupManager.removeTwitchAuth();
			} else if (cmd.hasOption("youtubeAuth")) {
				SetupManager.removeYoutubeAuth();
			} else {
				Logging.logger.severe(cmd.getArgList() + " is an invalid argument");
				System.exit(1);
			}
		} else if (cmd.hasOption("list")) {
			if(cmd.hasOption("twitch")) {
				SetupManager.getTwitchChannel();
			} else if (cmd.hasOption("youtube")) {
				SetupManager.getYoutubeChannel();
			} else if (cmd.hasOption("youtubeLive")) {
				SetupManager.getYoutubeLiveChannel();
			} else {
				Logging.logger.severe(cmd.getArgList() + " is an invalid argument");
				System.exit(1);
			}
		} else {
			Logging.logger.severe(cmd.getArgList() + " is an invalid argument");
			System.exit(1);
		}
	}
	
	private static void sendStartingMessage() {
		String url = "https://alertzy.app/send?accountKey=" + "787xmvo9n0roff6" + "&title=" + "BTTN Started!" + "&message="
				+ "BTTN listening for updates!"; //+ "&link=" + this.url;
		OkHttpClient client = new OkHttpClient();
		Request request = new Request.Builder().url(url).build();
		try {
			client.newCall(request).execute();
		} catch (IOException e) {
			Logging.logger.severe(e.toString());
			System.exit(1);
		}
	}
}
