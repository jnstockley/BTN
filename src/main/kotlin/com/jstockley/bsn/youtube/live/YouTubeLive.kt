package com.jstockley.bsn.youtube.live

import com.jstockley.bsn.*
import com.jstockley.bsn.notification.Notification
import mu.KotlinLogging

class YouTubeLive: Runnable {

    override fun run() {
        val logger = KotlinLogging.logger{}


        while (true) {
            val previousLiveStatus: MutableMap<String, Boolean> = getDataAsBooleanMap("youtubeLive.json") as MutableMap<String, Boolean>

            if (hasConnection()) {

                val youtubeLive = YouTubeLiveStreams(previousLiveStatus)


                if (youtubeLive.getCurrentLiveStreams().isNotEmpty()) {
                    if (youtubeLive.getCurrentLiveStreams().size == 1) {
                        logger.info { "YouTube Channel just went live: ${youtubeLive.getCurrentLiveStreams()[0]}" }
                    } else {
                        logger.info { "YouTube Channels just went line: ${youtubeLive.getCurrentLiveStreams()}" }
                    }
                    val notif = Notification(youtubeLive.getCurrentLiveStreams())
                    notif.send(accountKeys)
                }

                if(previousLiveStatus != youtubeLive.getCurrentLiveStreamStatus()){
                    writeData(YOUTUBE_LIVE_CHANNELS, youtubeLive.getCurrentLiveStreams())
                }

            } else {
                println("No internet connection!")
                logger.error { "No internet connection!" }
            }

            println("YouTube Live sleeping for 10 seconds...")
            Thread.sleep(10000)
        }
    }
}