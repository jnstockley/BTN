package com.jstockley.bsn.twitch

import com.jstockley.bsn.getTwitch
import com.jstockley.bsn.getTwitchCred
import mu.KotlinLogging

class TwitchLive: Runnable {

    private val logger = KotlinLogging.logger{}
    override fun run() {

        val apiKeys: Map<String, String> = getTwitchCred()

        val channels: List<String> = getTwitch()

        logger.info { "Started Twitch Live stream service!" }
        println("Checking for Twitch live stream updates...")


        TwitchLiveStreams(channels, apiKeys["clientId"]!!, apiKeys["clientSecret"]!!)
    }
}