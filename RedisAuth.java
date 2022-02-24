package com.github.jnstockley;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.exceptions.JedisConnectionException;

public class RedisAuth {
	
	private final static File CONFIG = new File(System.getProperty("user.home") + "/BTTN/config.json");

	private String server;

	private int port;

	private String username;

	private String password;

	/**
	 * @return the server
	 */
	public String getServer() {
		return server;
	}

	/**
	 * @return the port
	 */
	public int getPort() {
		return port;
	}

	/**
	 * @return the username
	 */
	public String getUsername() {
		return username;
	}

	/**
	 * @return the password
	 */
	public String getPassword() {
		return password;
	}

	@SuppressWarnings("unchecked")
	public RedisAuth(String server, int port, String username, String password) {
		if (validRedisConfig(server, port, username, password)) {
			this.server = server;
			this.port = port;
			this.username = username;
			this.password = password;
			JSONObject redis = new JSONObject();
			redis.put("server", this.server);
			redis.put("port", this.port);
			redis.put("username", this.username);
			redis.put("password", this.password);
			try {
				FileWriter writer = new FileWriter(CONFIG);
				writer.write(redis.toJSONString());
				writer.flush();
				writer.close();
				Logging.logger.info("Wrote redis config to: " + CONFIG);
			} catch (IOException e) {
				Logging.logger.severe(e.toString());
				System.exit(1);
			}
		} else {
			this.server = "";
			this.port = 0;
			this.username = "";
			this.password = "";
			Logging.logger.severe("Invalid redis config provided!");
		}
	}
	
	public RedisAuth() {
		if (CONFIG.exists() || CONFIG.length() > 0) {
			JSONParser parser = new JSONParser();
			JSONObject json = new JSONObject();
			try {
				json = (JSONObject) parser.parse(new FileReader(CONFIG));
				if (json.containsKey("server") && json.containsKey("port") && json.containsKey("username") && json.containsKey("password")) {
					this.server = json.get("server").toString();
					this.port = Integer.parseInt(json.get("port").toString());
					this.username = json.get("username").toString();
					this.password = json.get("password").toString();
					if(!validRedisConfig(this.server, this.port, this.username, this.password)) {
						Logging.logger.severe("Local config file contains an invalid redis config. Please run \"BTTN UPDATE REDIS\"");
						System.exit(1);
					}
				} else {
					Logging.logger.severe("Local config file is missing required values. Please run \"BTTN ADD REDIS\"");
					System.exit(1);
				}
			} catch (IOException | ParseException e) {
				Logging.logger.severe(e.toString());
				System.exit(1);
			}
		} else {
			Logging.logger.severe("Local config file doesn't exist or is empty. Please run \"BTTN ADD REDIS\"");
			System.exit(1);
		}
	}

	private boolean validRedisConfig(String server, int port, String username, String password) {
		try (JedisPool pool = new JedisPool(server, port, username, password)) {
			Jedis jedis = pool.getResource();
			jedis.close();
			jedis.disconnect();
			return true;
		} catch (JedisConnectionException e) {
			Logging.logger.severe(e.toString());
			return false;
		}
	}
}
