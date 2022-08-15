package com.jstockley.bsn.youtube.live

import com.jstockley.bsn.YouTubeLiveDataException
import mu.KotlinLogging
import org.json.JSONObject
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.net.http.HttpResponse.BodyHandlers


class YouTubeLiveStreams(private val previousLiveStreamStatus: MutableMap<String, Boolean>) {

    private val logger = KotlinLogging.logger{}

    private var channels: MutableSet<String> = mutableSetOf()

    private var currentLiveStatus: MutableMap<String, Boolean> = mutableMapOf()

    private var currentLiveStreams: MutableList<YouTubeLiveStream> = mutableListOf()

    private var retryAmount = 0

    fun getCurrentLiveStreamStatus(): MutableMap<String, Boolean>{
        return this.currentLiveStatus
    }

    fun getCurrentLiveStreams(): List<YouTubeLiveStream> {
        return this.currentLiveStreams
    }

    private fun getYouTubeData(channelIds: Set<String>): MutableMap<String, Boolean> {
        if (retryAmount > 2) {
            throw YouTubeLiveDataException("Retry amount exceeded!")
        }
        val currentLiveStatus = mutableMapOf<String, Boolean>()
        var response: HttpResponse<String>?
        for (channelId in channelIds){
            try {
                val baseURI = "https://www.youtube.com/channel/"
                val client = HttpClient.newBuilder().build()
                val request = HttpRequest.newBuilder()
                    .uri(URI.create("$baseURI$channelId/live/"))
                    .version(HttpClient.Version.HTTP_2)
                    .build()
                response = client.send(request, BodyHandlers.ofString())
                if (response!!.body() == null) {
                    throw YouTubeLiveDataException("YouTube Live Stream request returned null response")
                }
                if (response.body().contains("streamingData")){
                    val data = response.body()
                    val jsonString: String = data.substring(data.indexOf("ytInitialPlayerResponse") + 26,
                        data.indexOf("}}}};</script>") + 4)
                    val json = JSONObject(jsonString)
                    val videoDetails:JSONObject = json.getJSONObject("videoDetails")
                    currentLiveStatus[channelId] = true
                    this.channels.add(videoDetails.getString("author"))
                    if (!this.previousLiveStreamStatus[channelId]!!) {
                        this.currentLiveStreams.add(YouTubeLiveStream(videoDetails.getString("author"), videoDetails.getString("title"), channelId, videoDetails.getString("videoId"),true))
                    }
                } else {
                    currentLiveStatus[channelId] = false
                }
            } catch (e: Exception){
                logger.warn { "Retrying request $retryAmount/3" }
                logger.warn { e.stackTraceToString() }
                retryAmount++
                return getYouTubeData(channelIds)
            } catch (e: YouTubeLiveDataException){
                val msg = e.stackTraceToString()
                logger.error { "Error getting YouTube live stream status: ${e.message}" }
                //logger.error { "${response!!.body()}, ${response!!.statusCode()}" }
                throw YouTubeLiveDataException(msg)
            }
        }
        return currentLiveStatus
    }
    init {
        this.currentLiveStatus = getYouTubeData(previousLiveStreamStatus.keys)
        retryAmount = 0
    }
}

private fun setupChannelString(channelsSet: Set<String>): List<String> {
    val channelString = mutableListOf<String>()
    var channels = mutableListOf<String>()
    for (channelId in channelsSet) {
        if (channels.size == 49) {
            channels.add(channelId)
            channelString.add(channels.joinToString(separator = ",") { it })
            channels = ArrayList()
        } else {
            channels.add(channelId)
        }
    }
    channelString.add(channels.joinToString(separator = ",") { it })
    return channelString
}

private fun getChannels(channels: String, key: String): MutableMap<String, String> {
    try {
        val channelsMap = mutableMapOf<String, String>()
        val baseURI = "https://youtube.googleapis.com/youtube/v3/channels?part=snippet"
        val client = HttpClient.newBuilder().build()
        val request = HttpRequest.newBuilder()
            .uri(URI.create("$baseURI&id=$channels&key=$key"))
            .build()
        val response = client.send(request, BodyHandlers.ofString())
        if (response.body() == null) {
            // TODO
        }
        val json = JSONObject(response.body())
        val items = json.getJSONArray("items")
        for (item in items) {
            val channel = item as JSONObject
            val snippet = channel.getJSONObject("snippet")
            //mutableMapOf<>().add(snippet.getString("title"))
            channelsMap[snippet.getString("title")] = item.getString("id")
        }
        return channelsMap
    } catch(e: Exception) {
        // TODO
        //logger.warn { e.message }
        return mutableMapOf()
    }
}

fun getNames(channels: Set<String>, key: String): Map<String, String> {
    val liveStreamChannels = mutableMapOf<String, String>()
    val channelLists: List<String> = setupChannelString(channels)
    for (channel in channelLists) {
        liveStreamChannels += getChannels(channel, key)
    }
    return liveStreamChannels
}