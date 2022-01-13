package com.github.jnstockley;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class Auth {

	private File configFile;

	private String twitchAuthorizationKey;

	private String twitchClientId;

	private String twitchSecret;
	
	private String youtubeAPIKey;

	private List<String> alertzyAccountKeys;

	/**
	 * @return the configFile
	 */
	public File getConfigFile() {
		return configFile;
	}

	/**
	 * @param configFile the configFile to set
	 */
	public void setConfigFile(File configFile) {
		this.configFile = configFile;
	}

	/**
	 * @return the twitchAuthorizationKey
	 */
	public String getTwitchAuthorizationKey() {
		return twitchAuthorizationKey;
	}

	/**
	 * @param twitchAuthorizationKey the twitchAuthorizationKey to set
	 */
	public void setTwitchAuthorizationKey(String twitchAuthorizationKey) {
		this.twitchAuthorizationKey = twitchAuthorizationKey;
	}

	/**
	 * @return the twitchClientId
	 */
	public String getTwitchClientId() {
		return twitchClientId;
	}

	/**
	 * @param twitchClientId the twitchClientId to set
	 */
	public void setTwitchClientId(String twitchClientId) {
		this.twitchClientId = twitchClientId;
	}

	/**
	 * @return the twitchSecret
	 */
	public String getTwitchSecret() {
		return twitchSecret;
	}

	/**
	 * @param twitchSecret the twitchSecret to set
	 */
	public void setTwitchSecret(String twitchSecret) {
		this.twitchSecret = twitchSecret;
	}

	/**
	 * @return the alertzyAccountKeys
	 */
	public List<String> getAlertzyAccountKeys() {
		return alertzyAccountKeys;
	}

	/**
	 * @param alertzyAccountKeys the alertzyAccountKeys to set
	 */
	public void setAlertzyAccountKeys(List<String> alertzyAccountKeys) {
		this.alertzyAccountKeys = alertzyAccountKeys;
	}

	/**
	 * @return the youtubeAPIKey
	 */
	public String getYoutubeAPIKey() {
		return youtubeAPIKey;
	}

	/**
	 * @param youtubeAPIKey the youtubeAPIKey to set
	 */
	public void setYoutubeAPIKey(String youtubeAPIKey) {
		this.youtubeAPIKey = youtubeAPIKey;
	}

	@Override
	public String toString() {
		return "Auth [configFile=" + configFile + ", twitchAuthorizationKey=" + twitchAuthorizationKey
				+ ", twitchClientId=" + twitchClientId + ", twitchSecret=" + twitchSecret + ", youtubeAPIKey="
				+ youtubeAPIKey + ", alertzyAccountKeys=" + alertzyAccountKeys + "]";
	}

	/**
	 * @param configFile
	 */
	@SuppressWarnings("unchecked")
	public Auth(File configFile) {
		super();
		this.configFile = configFile;
		JSONParser parser = new JSONParser();
		JSONObject json = new JSONObject();
		try {
			json = (JSONObject) parser.parse(new FileReader(configFile));
			if (!json.containsKey("twitchAuthorizationKey") && !json.containsKey("twitchClientId")
					&& !json.containsKey("twitchSecret")) {
				System.out.println("Missing auth");
				System.exit(1);
			}
			this.alertzyAccountKeys = (List<String>) json.get("alertzyAccountKeys");
			this.twitchAuthorizationKey = json.get("twitchAuthorizationKey").toString();
			this.twitchClientId = json.get("twitchClientId").toString();
			this.twitchSecret = json.get("twitchSecret").toString();
			this.youtubeAPIKey = json.get("youtubeAPIKey").toString();
		} catch (IOException | ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.exit(1);
		}
	}
}
