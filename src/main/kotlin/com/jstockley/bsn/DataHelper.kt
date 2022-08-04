package com.jstockley.bsn
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import mu.KotlinLogging
import org.json.JSONObject
import java.io.File
import java.io.FileWriter
import java.io.PrintWriter

val logger = KotlinLogging.logger{}


fun getPlaylists(): MutableMap<String, Int> {
    val file = File("playlists.json")
    if (file.exists()){
        val jsonString: String = file.readText(Charsets.UTF_8)
        return ObjectMapper().readValue(jsonString)
    }
    logger.error { "Failed to Retrieved YouTube Playlists!" }
    return mutableMapOf()
}

fun writePlaylists(json: Map<String, Int>){
    val jsonString: String = JSONObject(json).toString()
    PrintWriter(FileWriter("playlists.json")).use{
        it.write(jsonString)
    }
    logger.info { "Wrote YouTube Playlists changes!" }
}

fun getPreviousVideoId():MutableMap<String, String> {
    val file = File("previousIds.json")
    if(file.exists()) {
        val jsonString: String = file.readText(Charsets.UTF_8)
        return ObjectMapper().readValue(jsonString)
    }
    logger.error { "Failed to Retrieved YouTube Channel Previous Ids!" }
    return mutableMapOf()
}

fun writePreviousVideoId(json: Map<String, String>){
    val jsonString: String = JSONObject(json).toString()
    PrintWriter(FileWriter("previousIds.json")).use {
        it.write(jsonString)
    }
    logger.info { "Wrote YouTube Playlists Channel Previous Ids!" }
}

fun getYouTubeLive(): MutableMap<String, Boolean>{
    val file = File("youtubeLive.json")
    if (file.exists()){
        val jsonString: String = file.readText(Charsets.UTF_8)
        return ObjectMapper().readValue(jsonString)
    }
    logger.error { "Failed to Retrieved YouTube Live Channels!" }
    return mutableMapOf()
}

fun getTwitch(): List<String> {
    val file = File("twitch.csv")
    if (file.exists()){
        val jsonString: String = file.readText(Charsets.UTF_8)
        return jsonString.split(",").map { it.trim() }
    }
    logger.error { "Failed to Retrieved Twitch Channels!" }
    return mutableListOf()
}

fun writeTwitch(channels: List<String>) {
    val file = File("twitch.csv")
    PrintWriter(FileWriter(file)).use {
        it.write(channels.joinToString( separator = ","){ it })
    }
}

fun writeYouTubeLive(json: Map<String, Boolean>) {
    val jsonString: String = JSONObject(json).toString()
    PrintWriter(FileWriter("youtubeLive.json")).use {
        it.write((jsonString))
    }
    logger.info { "Wrote YouTube Live Channels!" }
}

fun getYouTubeCred(): List<String> {
    val file = File("creds.csv")
    if (file.exists()){
        val jsonString: String = file.readText(Charsets.UTF_8)
        logger.info { "Retrieved YouTube Creds!" }
        return jsonString.split(",").map { it.trim() }
    }
    logger.error { "Failed to Retrieved YouTube Creds!" }
    return mutableListOf()
}

fun writeYouTubeCred(creds: List<String>) {
    val file = File("creds.csv")
    PrintWriter(FileWriter(file)).use {
        it.write(creds.joinToString( separator = ","){ it })
    }
}

fun getTwitchCred(): Map<String, String> {
    val file = File("twitchCreds.json")
    if (file.exists()){
        val jsonString: String = file.readText(Charsets.UTF_8)
        logger.info { "Retrieved Twitch Creds!" }
        return ObjectMapper().readValue(jsonString)
    }
    logger.error { "Failed to Retrieved Twitch Creds!" }
    return mutableMapOf()
}

fun writeAlertzyKeys(keys: List<String>) {
    val file = File("alertzyKeys.csv")
    PrintWriter(FileWriter(file)).use {
        it.write(keys.joinToString( separator = ","){ it })
    }
}

fun getAlertzyKey(): List<String> {
    val file = File("alertzyKeys.csv")
    if (file.exists()) {
        val string: String = file.readText(Charsets.UTF_8)
        logger.info { "Retrieved Alertzy Keys(s)" }
        return ObjectMapper().readValue(string)
    }
    logger.error { "Failed to retrieve Alertzy Key(s)" }
    return mutableListOf()
}