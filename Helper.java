package com.github.jnstockley;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

public class Helper {
	
	public static boolean addToRedis(RedisAuth auth, String key, String value) {
		JedisPool pool = new JedisPool(auth.getServer(), auth.getPort(), auth.getUsername(), auth.getPassword());
		Jedis jedis = pool.getResource();
		jedis.set(key, value);
		String redisValue = jedis.get(key);
		jedis.close();
		jedis.disconnect();
		if(value.equals(redisValue)) {
			Logging.logger.info("Added data to redis database: " + value);
			return true;
		} else {
			Logging.logger.warning("Redis database mismatch\nOriginal: " + value + ", Redis Value: " + redisValue);
			return false;
		}
	}
	
	public static boolean addListToRedis(RedisAuth auth, String key, List<String> values) {
		JedisPool pool = new JedisPool(auth.getServer(), auth.getPort(), auth.getUsername(), auth.getPassword());
		Jedis jedis = pool.getResource();
		Set<String> currRedisValues = jedis.smembers(key);
		currRedisValues.addAll(values);
		for (String value: values) {
			jedis.sadd(key, value);
		}
		Set<String> redisValues = jedis.smembers(key);
		jedis.close();
		jedis.disconnect();
		if(values.equals(new ArrayList<String>(redisValues))) {
			Logging.logger.info("Added data to redis database: " + values);
			return true;
		} else {
			Logging.logger.warning("Redis database mismatch\nOriginal: " + values + ", Redis Value: " + redisValues);
			return false;
		}
	}
	
	public static boolean updateListInRedis(RedisAuth auth, String key, List<String> values) {
		JedisPool pool = new JedisPool(auth.getServer(), auth.getPort(), auth.getUsername(), auth.getPassword());
		Jedis jedis = pool.getResource();
		jedis.del(key);
		for (String value: values) {
			jedis.sadd(key, value);
		}
		Set<String> redisValues = jedis.smembers(key);
		jedis.close();
		jedis.disconnect();
		if(values.equals(new ArrayList<String>(redisValues))) {
			Logging.logger.info("Added data to redis database: " + values);
			return true;
		} else {
			Logging.logger.warning("Redis database mismatch\nOriginal: " + values + ", Redis Value: " + redisValues);
			return false;
		}
	}
	
	public static String getFromRedis(RedisAuth auth, String key) {
		JedisPool pool = new JedisPool(auth.getServer(), auth.getPort(), auth.getUsername(), auth.getPassword());
		Jedis jedis = pool.getResource();
		String redisValue = jedis.get(key);
		jedis.close();
		jedis.disconnect();
		if(redisValue == null) {
			Logging.logger.warning("Redis database does not contain key: " + key);
			System.exit(1);
		}
		return redisValue;
	}
	
	public static List<String> getListFromRedis(RedisAuth auth, String key){
		JedisPool pool = new JedisPool(auth.getServer(), auth.getPort(), auth.getUsername(), auth.getPassword());
		Jedis jedis = pool.getResource();
		Set<String> redisValues = jedis.smembers(key);
		jedis.close();
		jedis.disconnect();
		if(redisValues.isEmpty()) {
			Logging.logger.warning("Redis database does not contain key: " + key);
			System.exit(1);
		}
		return new ArrayList<String>(redisValues);
	}
	
	public static boolean deleteFromJedis(RedisAuth auth, String key) {
		JedisPool pool = new JedisPool(auth.getServer(), auth.getPort(), auth.getUsername(), auth.getPassword());
		Jedis jedis = pool.getResource();
		if(jedis.get(key) == null) {
			Logging.logger.warning("Unable to delete " + key + " since key is not in database");
			return true;
		}
		jedis.del(key);
		String redisValue = jedis.get(key);
		jedis.close();
		jedis.disconnect();
		if(redisValue == null) {
			Logging.logger.info("Deleted " + key + " from redis database");
			return true;
		} else {
			Logging.logger.severe("Failed to delete " + key + " from redis database");
			return false;
		}
	}
	
	public static String youtubechannelNameToChannelId(String channelURL) {
		try {
			Document doc = Jsoup.connect(channelURL).get();
			Elements channelIdElement = doc.select("meta[property=og:url]");
			String channelId = channelIdElement.first().attr("content");
			return channelId.substring(channelId.indexOf("/channel/") + 9);
		} catch (IOException e) {
			Logging.logger.severe(e.toString());
			System.exit(1);
			return null;
		}
	}
	
	public static Map<String, String> getSetFromRedis(RedisAuth auth, String key){
		JedisPool pool = new JedisPool(auth.getServer(), auth.getPort(), auth.getUsername(), auth.getPassword());
		Jedis jedis = pool.getResource();
		Map<String, String> redisValue = jedis.hgetAll(key);
		if(redisValue.isEmpty()) {
			Logging.logger.warning("Redis database does not contain key: " + key);
			System.exit(1);
		}
		jedis.close();
		jedis.disconnect();
		return redisValue;
	}
	
	public static boolean addSetToRedis(RedisAuth auth, String key, Map<String, String> value) {
		JedisPool pool = new JedisPool(auth.getServer(), auth.getPort(), auth.getUsername(), auth.getPassword());
		Jedis jedis = pool.getResource();
		Map<String, String> redisValue = jedis.hgetAll(key);
		jedis.hset(key, value);
		value.putAll(redisValue);
		redisValue = jedis.hgetAll(key);
		jedis.close();
		jedis.disconnect();
		if(value.equals(redisValue)) {
			return true;
		} else {
			return false;
		}
	}
	
	public static String getKeyFromRedisSet(RedisAuth auth, String key, String field) {
		JedisPool pool = new JedisPool(auth.getServer(), auth.getPort(), auth.getUsername(), auth.getPassword());
		Jedis jedis = pool.getResource();
		String redisValue = jedis.hget(key, field);
		if(redisValue == null) {
			Logging.logger.warning("Redis database does not contain key: " + key + ", field: " + field);
			System.exit(1);
		}
		jedis.close();
		jedis.disconnect();
		return redisValue;
	}
	
	public static boolean addKeyToRedisSet(RedisAuth auth, String key, String field, String value) {
		JedisPool pool = new JedisPool(auth.getServer(), auth.getPort(), auth.getUsername(), auth.getPassword());
		Jedis jedis = pool.getResource();
		jedis.hset(key, field, value);
		String redisValue = jedis.hget(key, field);
		jedis.close();
		jedis.disconnect();
		if (value.equals(redisValue)) {
			return true;
		} else {
			return false;
		}
	}
}
