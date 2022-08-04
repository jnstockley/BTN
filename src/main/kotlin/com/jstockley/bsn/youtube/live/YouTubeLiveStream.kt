package com.jstockley.bsn.youtube.live

class YouTubeLiveStream(private val channel: String, private val title: String, private val channelId: String, private val videoId: String, private val isLive: Boolean) {
    fun getChannel(): String {
        return this.channel
    }

    fun getTitle(): String {
        return this.title
    }

    fun getChannelId():String {
        return this.channelId
    }

    override fun toString(): String {
        return "YouTubeLiveStream(channel='$channel', title='$title', channelId='$channelId', videoId='$videoId', isLive=$isLive)"
    }

}