package com.github.jnstockley;

import org.json.simple.JSONObject;

public class YTLiveStream {

	private boolean isLive;

	private String channelId;

	private String channelName;

	private String streamName;

	/**
	 * @return the isLive
	 */
	public boolean isLive() {
		return isLive;
	}

	/**
	 * @param isLive the isLive to set
	 */
	public void setLive(boolean isLive) {
		this.isLive = isLive;
	}

	/**
	 * @return the channelId
	 */
	public String getChannelId() {
		return channelId;
	}

	/**
	 * @param channelId the channelId to set
	 */
	public void setChannelId(String channelId) {
		this.channelId = channelId;
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

	@Override
	public String toString() {
		return "YTLiveStream [isLive=" + isLive + ", channelId=" + channelId + ", channelName=" + channelName
				+ ", streamName=" + streamName + "]";
	}

	public YTLiveStream(JSONObject json, String channelId) {
		this.channelId = channelId;
		this.isLive = Boolean.parseBoolean(json.get("isLive").toString());
		this.channelName = json.get("channelName").toString();
		this.streamName = json.get("streamName").toString();
	}

}
