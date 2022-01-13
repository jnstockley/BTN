package com.github.jnstockley;

import org.json.simple.JSONObject;

/**
 * 
 * @author Jack Stockley
 *
 */
public class TwitchLiveStream {
	
	private String streamName;

	private String channelName;

	private String category;

	//private boolean isLive;

	//private boolean wasLive;

	/**
	 * @return the streamName
	 */
	public String getStreamName() {
		return streamName;
	}

	/**
	 * @param streamName the streamName to set
	 */
	public void setStreamName(String streamName) {
		this.streamName = streamName;
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
	public void setChannelName(String channelName) {
		this.channelName = channelName;
	}

	/**
	 * 
	 * @return the category
	 */
	public String getCategory() {
		return category;
	}

	/**
	 * 
	 * @param category the category to set
	 */
	public void setCategory(String category) {
		this.category = category;
	}

	@Override
	public String toString() {
		return "Twitch [streamName=" + streamName + ", channelName=" + channelName + ", category=" + category
				+ "]";
	}

	/**
	 * @param channelName
	 */
	public TwitchLiveStream(String channelName, JSONObject channel) {
		this.channelName = channelName;
		this.streamName = channel.get("title").toString();
		this.category = channel.get("game").toString();
	}

}