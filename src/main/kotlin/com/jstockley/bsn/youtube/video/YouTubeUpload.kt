package com.jstockley.bsn.youtube.video

import com.jstockley.bsn.*
import com.jstockley.bsn.notification.Notification
import mu.KotlinLogging
import java.io.File

class YouTubeUpload: Runnable {
    override fun run() {

        val logger = KotlinLogging.logger{}

        val apiKeys: List<String> = getDataAsList(YOUTUBE_KEYS)

        var index = 0

        while (true) {

            var previousVideoIds: MutableMap<String, String> = mutableMapOf()

            if (File(YOUTUBE_IDS).exists()) {
                previousVideoIds = getDataAsStringMap(YOUTUBE_IDS) as MutableMap<String, String>
            }

            var previousVideoAmounts: Map<String, Int> = getDataAsIntMap(YOUTUBE_PLAYLISTS)

            if(hasConnection()){

                val recentlyUploadedVideos = mutableListOf<YouTubeVideo>()

                val ytPlaylist = YouTubePlaylists(previousVideoAmounts.keys.toMutableList(), apiKeys[index])

                for(playlist in ytPlaylist.getCurrentVideoAmounts().keys){
                    if ((ytPlaylist.getCurrentVideoAmounts()[playlist]!! > previousVideoAmounts[playlist]!!) && previousVideoAmounts[playlist]!! > 0){
                        val video = YouTubeVideo(playlist, apiKeys[index])
                        logger.info { "${video.getChannelName()} upload playlist amount increased: ${previousVideoAmounts[playlist]} -> ${ytPlaylist.getCurrentVideoAmounts()[playlist]}" }
                        // Check if video is not the previous video
                        if(previousVideoIds[playlist] != video.getVideoId()){
                            // Check if video is not a livestream
                            if(!video.isLivestream()){
                                logger.info { "${video.getChannelName()} uploaded a new video" }
                                logger.debug { "${previousVideoIds[playlist]} -> ${video.getVideoId()}" }
                                recentlyUploadedVideos.add(video)
                            }
                        }
                        previousVideoIds[playlist] = video.getVideoId()
                    }
                }
                if (recentlyUploadedVideos.isNotEmpty()) {
                    val notif = Notification(recentlyUploadedVideos)
                    notif.send(accountKeys)
                    logger.info { "Sent upload notification for $recentlyUploadedVideos" }
                }

                if (previousVideoAmounts != ytPlaylist.getCurrentVideoAmounts()){
                    previousVideoAmounts = ytPlaylist.getCurrentVideoAmounts()
                    writeData(YOUTUBE_PLAYLISTS, previousVideoAmounts)
                    writeData(YOUTUBE_PLAYLISTS, previousVideoIds)
                }

            } else {
                println("No internet connection!")
                logger.error { "No internet connection!" }
            }

            println("YouTube upload sleeping for 10 seconds...")
            Thread.sleep(10000)

            index = selectAPIKey(apiKeys, index)
        }
    }

    private fun selectAPIKey(apiKeys: List<String>, previousIndex: Int): Int{
        return if (previousIndex == apiKeys.size - 1){
            0
        } else {
            previousIndex + 1
        }
    }
}