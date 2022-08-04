package com.jstockley.bsn.youtube.video

import com.jstockley.bsn.YouTubePremiereDateException
import com.jstockley.bsn.YouTubePremiereException
import com.jstockley.bsn.YouTubeShortException
import com.jstockley.bsn.YouTubeVideoDataException
import mu.KotlinLogging
import org.json.JSONObject
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.text.DateFormat
import java.time.Instant
import java.time.format.DateTimeFormatter
import java.time.temporal.TemporalAccessor
import java.util.*

class YouTubeVideo (playlistId: String, apiKey: String){

    private val logger = KotlinLogging.logger{}

    private var channelName: String

    private var videoName: String

    private var videoId: String

    private var isShort: Boolean

    private var isPremiere: Boolean

    private var isLivestream: Boolean = false

    private var premiereDate: String = ""

    fun getChannelName(): String {
        return channelName
    }

    fun getVideoName(): String {
        return videoName
    }

    fun getVideoId(): String {
        return videoId
    }

    fun isShort(): Boolean {
        return isShort
    }

    fun isPremiere(): Boolean {
        return isPremiere
    }

    fun isLivestream(): Boolean {
        return isLivestream
    }

    fun getPremiereDate(): String {
        return premiereDate
    }

    /**
     * If the YouTube video title or descriptions contains the #short expression, determining if a video is a short
     */
    private fun isShort(snippet: JSONObject): String {
        try{
            val title: String = snippet.get("title").toString().lowercase()
            val desc: String = snippet.get("description").toString().lowercase()
            if(title.contains("#shorts") || desc.contains("#short")) {
                return "true"
            }
            return "false"
        } catch(e: Exception){
            val msg = e.stackTraceToString()
            logger.error { "Error getting YouTube Short data: $msg" }
            throw YouTubeShortException(msg)
        }
    }

    /**
     * Checks if a video is a scheduled premiere
     */
    private fun isPremiere(videoId: String, apiKey: String): String {
        var response: HttpResponse<String>? = null
        try {
            val baseURI = "https://youtube.googleapis.com/youtube/v3/videos?part=liveStreamingDetails%2Csnippet%2CcontentDetails"
            val client = HttpClient.newBuilder().build()
            val request = HttpRequest.newBuilder()
                .uri(URI.create("$baseURI&id=$videoId&key=$apiKey"))
                .build()
            response = client.send(request, HttpResponse.BodyHandlers.ofString())
            val json = JSONObject(response.body())
            val premiere: JSONObject = json.getJSONArray("items").getJSONObject(0)
            val snippet: JSONObject = premiere.getJSONObject("snippet")
            val liveBroadcastContent: String = snippet.getString("liveBroadcastContent")
            val contentDetails: JSONObject = premiere.getJSONObject("contentDetails")
            val duration: String = contentDetails.getString("duration")
            if (liveBroadcastContent == "upcoming") {
                logger.debug { "Upcoming Live stream: $videoId" }
                if (duration == "P0D") {
                    isLivestream = true
                    return "false"
                }
                setPremiereDate(premiere)
                return "true"
            } else if (liveBroadcastContent == "live") {
                logger.debug { "Live stream: $videoId" }
            }
            return "false"
        } catch(e: Exception) {
            val msg = e.stackTraceToString()
            e.printStackTrace()
            logger.error { "Error getting YouTube Premiere data: $msg" }
            logger.debug { "${response!!.body()}, ${response.statusCode()}" }
            throw YouTubePremiereException(msg)
        }
    }

    /**
     * If a video is a premiere this function is ran and sets the premiere date and time
     */
    private fun setPremiereDate(premiere: JSONObject) {
        try {
            val liveStreamingDetails: JSONObject = premiere.get("liveStreamingDetails") as JSONObject
            val startTime: String = liveStreamingDetails.get("scheduledStartTime").toString()
            val ta: TemporalAccessor = DateTimeFormatter.ISO_INSTANT.parse(startTime)
            val i: Instant = Instant.from(ta)
            val date: Date = Date.from(i)
            val dateFormatter: DateFormat? = DateFormat.getDateTimeInstance()
            if (dateFormatter != null) {
                this.premiereDate = dateFormatter.format(date)
            }
        } catch (e: Exception) {
            val msg = e.stackTraceToString()
            logger.error { "Error getting YouTube Premiere date data: $msg" }
            throw YouTubePremiereDateException(msg)
        }
    }

    /**
     * Take a playlist id as a string and gets the most recent video from that
     * playlist and returns the data from it
     */
    /**
     * Take a playlist id as a string and gets the most recent video from that playlist and returns the data from it
     *
     * @property playlistId The playlist ID
     * @property apiKey The API Key used by the YouTube API
     *
     * @return A map containing 'channelName', 'videoName', 'videoId', 'isShort' and 'isPremiere'
     */
    private fun getYouTubeData(playlistId: String, apiKey: String): Map<String, String>{
        var response: HttpResponse<String>? = null
        try {
            val video:  MutableMap<String, String> = mutableMapOf()
            val baseURI = "https://youtube.googleapis.com/youtube/v3/playlistItems?part=snippet"
            val client = HttpClient.newBuilder().build()
            val request = HttpRequest.newBuilder()
                .uri(URI.create("$baseURI&playlistId=$playlistId&key=$apiKey"))
                .build()
            response = client.send(request, HttpResponse.BodyHandlers.ofString())
            val json = JSONObject(response.body())
            val item: JSONObject = json.getJSONArray("items").getJSONObject(0)//.asJsonArray.get(0).asJsonObject
            val snippet: JSONObject = item.getJSONObject("snippet")
            val channelName: String = snippet.getString("channelTitle")
            val videoName: String = snippet.getString("title")
            val resourceId: JSONObject = snippet.getJSONObject("resourceId")
            val videoId: String = resourceId.getString("videoId")
            video["channelName"] = channelName
            video["videoName"] = videoName
            video["videoId"] = videoId
            video["isShort"] = isShort(snippet)
            video["isPremiere"] = isPremiere(videoId, apiKey)
            return video
        } catch(e: Exception) {
            val msg = e.stackTraceToString()
            e.printStackTrace()
            logger.error { "Error getting YouTube Video data: $msg" }
            logger.error { "${response!!.body()}, ${response.statusCode()}" }

            throw YouTubeVideoDataException(msg)
        }
    }

    override fun toString(): String {
        return "YouTubeVideo(channelName='$channelName', videoName='$videoName', videoId='$videoId', isShort=$isShort, isLivestream=$isLivestream, isPremiere=$isPremiere, premiereDate='$premiereDate')"
    }

    /**
     * The most recent video uploaded by channeld owner of the playlist
     *
     * @constructor
     */
    init {
        val video: Map<String, String> = getYouTubeData(playlistId, apiKey)
        channelName = video["channelName"]!!
        videoName = video["videoName"]!!
        videoId = video["videoId"]!!
        isShort = video["isShort"].toBoolean()
        isPremiere = video["isPremiere"].toBoolean()
    }
}