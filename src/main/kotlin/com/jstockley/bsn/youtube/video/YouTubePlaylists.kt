package com.jstockley.bsn.youtube.video

import com.jstockley.bsn.YouTubePlaylistDataException
import mu.KotlinLogging
import org.json.JSONArray
import org.json.JSONObject
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse

class YouTubePlaylists (private val playlistIds: List<String>, apiKey: String) {

    private val logger = KotlinLogging.logger{}

    private var channels: MutableSet<String> = mutableSetOf()

    private var currentVideoAmounts: Map<String, Int> = mutableMapOf()

    private var retryAmount = 0

    fun getCurrentVideoAmounts(): Map<String, Int>{
        return this.currentVideoAmounts
    }

    fun getChannels(): Set<String> {
        return this.channels
    }

    /**
     * Takes a list of playlist ids and splits it into lists of string of at most 50 ids
     */
    private fun setupPlaylistString(playlistsList: List<String>): List<String> {
        val playlistString = mutableListOf<String>()
        var playlists = mutableListOf<String>()
        for (playlistId in playlistsList) {
            if (playlists.size == 49) {
                playlists.add(playlistId)
                playlistString.add(playlists.joinToString(separator = ",") { it })
                playlists = ArrayList()
            } else {
                playlists.add(playlistId)
            }
        }
        playlistString.add(playlists.joinToString(separator = ",") { it })
        return playlistString
    }

    /**
     * Gets the data from YouTube Data API and
     */
    private fun getYouTubeData(playlists: String, apiKey: String): Map<String, Int> {
        if (retryAmount > 2) {
            throw YouTubePlaylistDataException("Retry amount exceeded!")
        }
        var response: HttpResponse<String>? = null
        try {
            val currentVideoAmounts: MutableMap<String, Int> = mutableMapOf()
            val baseURI = "https://youtube.googleapis.com/youtube/v3/playlists?part=contentDetails,snippet"
            val client = HttpClient.newBuilder().build()
            val request = HttpRequest.newBuilder()
                .uri(URI.create("$baseURI&id=$playlists&key=$apiKey"))
                .build()
            response = client.send(request, HttpResponse.BodyHandlers.ofString())
            if(response.body() == null) {
               throw YouTubePlaylistDataException("YouTube Data API returned null response")
            }
            val json = JSONObject(response.body())
            val items: JSONArray = json.getJSONArray("items")
            for(item in items){
                val playlist = item as JSONObject
                val id: String = playlist.getString("id")
                val contentDetails: JSONObject = playlist.getJSONObject("contentDetails")
                val videoAmount: Int = contentDetails.getInt("itemCount")
                currentVideoAmounts[id] = videoAmount
                val snippet: JSONObject = playlist.getJSONObject("snippet")
                val channelTitle: String = snippet.getString("channelTitle")
                this.channels.add(channelTitle)
            }
            retryAmount = 0
            return currentVideoAmounts
        } catch (e: Exception){
            logger.warn { "Retrying request $retryAmount/3" }
            retryAmount ++
            return getYouTubeData(playlists, apiKey)
        } catch (e: YouTubePlaylistDataException){
            val msg = e.stackTraceToString()
            logger.error { "Error getting YouTube Playlist data: ${e.message}" }
            logger.error { "${response!!.body()}, ${response.statusCode()}" }
            retryAmount = 0
            throw YouTubePlaylistDataException(msg)
        }
    }

    init {
        val playlistList: List<String> = setupPlaylistString(this.playlistIds)
        for(playlists in playlistList){
           currentVideoAmounts = currentVideoAmounts + getYouTubeData(playlists, apiKey)
        }
    }
}