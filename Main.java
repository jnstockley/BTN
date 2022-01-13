package com.github.jnstockley;

import java.io.File;

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
		 */
				
		//TODO Possible way to add more youtube API keys if quota is exceeded due to youtube quota
		
		//TODO due to YouTube API quota, max number of channels stored is 300???
		
		
		Helper.setupFolder();
				
		Auth auth = new Auth(new File(System.getProperty("user.home") + "/BTTN/config.json"));		
		
		if(sendTwitchLive(auth)) {
			System.out.println("Sent Twitch Live Stream Notification(s)!");
		} else {
			System.out.println("No Twitch Live Stream Notifications to send!");
		}
		
		if(sendYTLiveStream(auth)) {
			System.out.println("Sent YouTube Live Stream Notification(s)!");
		} else {
			System.out.println("No YouTube Live Stream Notifications to send!");
		}
		
		if(sendYouTubeVideo(auth)) {
			System.out.println("Sent YouTube Video Notification(s)!");
		} else {
			System.out.println("No YouTube Video Notifications to send!");
		}		
	}
	
	private static boolean sendTwitchLive(Auth auth){
		TwitchLiveStreams twitch = new TwitchLiveStreams(auth);
		if(!twitch.getRecentlyLiveChannels().isEmpty()) {
			Notification<TwitchLiveStream> twitchNotif = new Notification<TwitchLiveStream>(twitch.getRecentlyLiveChannels());
			twitchNotif.send(auth);
			return true;
		} else {
			return false;
		}
	}
	
	private static boolean sendYouTubeVideo(Auth auth) {
		YTPlaylist ytPlaylists = new YTPlaylist(auth);
		if(!ytPlaylists.getRecentlyUploadedVideos().isEmpty()) {
			Notification<YTVideo> ytNotif = new Notification<YTVideo>(ytPlaylists.getRecentlyUploadedVideos());
			ytNotif.send(auth);
			return true;
		} else {
			return false;
		}
	}
	
	private static boolean sendYTLiveStream(Auth auth) {
		YTLiveStreams ytLiveStreams = new YTLiveStreams();
		if(!ytLiveStreams.getRecentlyLiveChannels().isEmpty()) {
			Notification<YTLiveStream> ytLiveNotif = new Notification<YTLiveStream>(ytLiveStreams.getRecentlyLiveChannels());
			ytLiveNotif.send(auth);
			return true;
		} else {
			return false;
		}
	}

}
