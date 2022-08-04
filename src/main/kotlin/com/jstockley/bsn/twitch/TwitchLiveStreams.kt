package com.jstockley.bsn.twitch

import com.github.twitch4j.TwitchClient
import com.github.twitch4j.TwitchClientBuilder
import com.github.twitch4j.events.ChannelGoLiveEvent
import com.jstockley.bsn.notification.Notification
import com.jstockley.bsn.accountKeys
import mu.KotlinLogging

class TwitchLiveStreams(channels: List<String>, clientId: String, clientSecret: String) {

    private val logger = KotlinLogging.logger{}
    init {
        val twitchClient: TwitchClient = TwitchClientBuilder.builder().withEnableHelix(true)
            .withClientId(clientId)
            .withClientSecret(clientSecret)
            .build()

        twitchClient.clientHelper.enableStreamEventListener(channels)
        twitchClient.eventManager.onEvent(ChannelGoLiveEvent::class.java) {event ->
            val stream = TwitchLiveStream(event.stream.userName, event.stream.title, event.stream.gameName)
            logger.info { "Twitch Channel just went live: $stream" }
            val notif = Notification(stream)
            notif.send(accountKeys)
        }
    }
}