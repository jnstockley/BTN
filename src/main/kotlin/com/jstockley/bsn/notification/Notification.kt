package com.jstockley.bsn.notification

import com.jstockley.bsn.AlertzyException
import com.jstockley.bsn.AlertzyFailException
import com.jstockley.bsn.AlertzyMixedException
import com.jstockley.bsn.twitch.TwitchLiveStream
import com.jstockley.bsn.youtube.live.YouTubeLiveStream
import com.jstockley.bsn.youtube.video.YouTubeVideo
import mu.KotlinLogging
import org.json.JSONObject
import java.net.URI
import java.net.URLEncoder
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.nio.charset.StandardCharsets

class Notification() {

    private val logger = KotlinLogging.logger{}

    private lateinit var title: String

    private lateinit var message: String

    private lateinit var url: String

    private lateinit var type: NotificationType

    private var priority: Int = 0

    fun send(accountKeys: List<String>): Boolean{
        try {
            val keys: String = accountKeys.joinToString(separator="_"){ it }
            val requestURL = "https://alertzy.app/send?accountKey=${keys}&title=${this.title}&message=${this.message}&priority=${this.priority}&link=${this.url}"
            val client = HttpClient.newBuilder().build()
            val request = HttpRequest.newBuilder()
                .uri(URI.create(requestURL))
                .build()
            val response = client.send(request, HttpResponse.BodyHandlers.ofString())
            val responseJson =  JSONObject(response.body())
            val message: String = responseJson.getString("response")
            if (message == "fail") {
                throw AlertzyFailException(responseJson.toString(), "jack@jstockley.com")
            } else if (message == "mixed") {
                throw AlertzyMixedException(responseJson.toString(), "jack@jstockley.com")
            }
            logger.info { "Sent Notification: $this" }
            return true
        } catch (e: Exception) {
            val msg = e.stackTraceToString()
            logger.error { "Error sending Alertzy com.jstockley.Notification: $msg" }
            throw AlertzyException(msg, "jack@jstockley.com")
        }
    }

    override fun toString(): String {
        return "Notification(title='$title', message='$message', url='$url', type=$type, priority=$priority)"
    }

    constructor(title: String, message: String, type: NotificationType) : this() {
        this.title = URLEncoder.encode(title, StandardCharsets.UTF_8)
        this.message = URLEncoder.encode(message, StandardCharsets.UTF_8)
        this.url = ""
        this.type = type
        when (type) {
            NotificationType.StartingMessage -> {
                this.priority = 1
            }
            NotificationType.Error -> {
                this.priority = 2
            }
            else -> {
                this.priority = 0
            }
        }
    }

    constructor(data: List<Any>): this() {
        if(data[0]::class.java == YouTubeVideo::class.java){
            if (data.size == 1) {
                val video: YouTubeVideo = data[0] as YouTubeVideo
                if (video.isShort()) {
                    this.title = URLEncoder.encode("${video.getChannelName()} has uploaded a short!", StandardCharsets.UTF_8)
                    this.type = NotificationType.YouTubeShort
                } else if (video.isPremiere()) {
                    this.title = URLEncoder.encode("${video.getChannelName()} has scheduled a premiere for ${video.getPremiereDate()}", StandardCharsets.UTF_8)
                    this.type = NotificationType.YouTubePremiere
                } else {
                    this.title = URLEncoder.encode("${video.getChannelName()} has uploaded a video!", StandardCharsets.UTF_8)
                    this.type = NotificationType.YouTubeVideo
                }
                this.message = URLEncoder.encode(video.getVideoName(), StandardCharsets.UTF_8)
                this.url = URLEncoder.encode("https://youtube.com/watch?v=${video.getVideoId()}", StandardCharsets.UTF_8)
            } else {
                var titleString = ""
                for (i in 1 until (data.size)) {
                    val video = data[i] as YouTubeVideo
                    titleString += ("${video.getChannelName()}, ")
                }
                this.title = URLEncoder.encode("${titleString.substring(0, titleString.length - 2)} have uploaded a video!", StandardCharsets.UTF_8)
                this.message = URLEncoder.encode("Check out their latest YouTube videos!", StandardCharsets.UTF_8)
                this.url = URLEncoder.encode("https://youtube.com/feed/subscriptions/", StandardCharsets.UTF_8)
                this.type = NotificationType.YouTubeVideos
            }
        } else if (data[0]::class.java == YouTubeLiveStream::class.java) {
            this.type = NotificationType.YouTubeLiveStream
            if (data.size == 1) {
                val stream: YouTubeLiveStream = data[0] as YouTubeLiveStream
                this.title = URLEncoder.encode("${stream.getChannel()} has gone live on YouTube!", StandardCharsets.UTF_8)
                this.message = URLEncoder.encode(stream.getTitle(), StandardCharsets.UTF_8)
                this.url = URLEncoder.encode("https://youtube.com/channel/${stream.getChannelId()}/live/", StandardCharsets.UTF_8)
            } else {
                var titleString = ""
                for (i in 1..data.size) {
                    val stream = data[i] as YouTubeLiveStream
                    titleString += ("${stream.getChannel()}, ")
                }
                this.title = URLEncoder.encode("${titleString.substring(0, titleString.length - 2)} have gone live on YouTube!", StandardCharsets.UTF_8)
                this.message = URLEncoder.encode("Check out their YouTube live streams!", StandardCharsets.UTF_8)
                this.url = URLEncoder.encode("https://youtube.com/feed/subscriptions/", StandardCharsets.UTF_8)
            }
        }
    }

    constructor(data: TwitchLiveStream): this() {
        this.title = URLEncoder.encode("${data.getName()} has gone live on Twitch!", StandardCharsets.UTF_8)
        this.message = URLEncoder.encode("${data.getTitle()}: ${data.getCategory()}", StandardCharsets.UTF_8)
        this.url = URLEncoder.encode("https://twitch.tv/${data.getName()}", StandardCharsets.UTF_8)
        this.type = NotificationType.Twitch
    }
}