package com.jstockley.bsn.twitch

import com.jstockley.bsn.TWITCH_CHANNELS
import com.jstockley.bsn.getDataAsList
import com.jstockley.bsn.getDataAsStringMap
import mu.KotlinLogging

class TwitchLive: Runnable {

    private val logger = KotlinLogging.logger{}
    override fun run() {

        val apiKeys: Map<String, String> = getDataAsStringMap(TWITCH_CHANNELS)

        val channels: List<String> = getDataAsList(TWITCH_CHANNELS)

        logger.info { "Started Twitch Live stream service!" }
        println("Checking for Twitch live stream updates...")


        TwitchLiveStreams(channels, apiKeys["clientId"]!!, apiKeys["clientSecret"]!!)
    }
}