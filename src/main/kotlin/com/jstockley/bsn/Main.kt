package com.jstockley.bsn

import com.jstockley.bsn.notification.Notification
import com.jstockley.bsn.notification.NotificationType
import com.jstockley.bsn.setup.Setup
import com.jstockley.bsn.twitch.TwitchLive
import com.jstockley.bsn.youtube.live.YouTubeLive
import com.jstockley.bsn.youtube.video.YouTubeUpload
import mu.KotlinLogging
import picocli.CommandLine
import java.io.IOException
import java.net.InetSocketAddress
import java.net.Socket
import kotlin.system.exitProcess

var accountKeys: List<String> = mutableListOf()

const val version = "2.0-alpha3"

fun main(args: Array<String>) {


    /**
     * TODO
     * 1. Improve exceptions
     *      Exception in init overwrites exception from lower class
     *      Print stack trace rather then message (gets stacktrace as string, check if working)
     *  2. Log videoId in new file is is upcoming live stream, send notif when becomes live
     */

    val logger = KotlinLogging.logger{}

    accountKeys = listOf("787xmvo9n0roff6")

    /**
     * TODO
     * GUI add finish/close button
     * Move list to GUI
      */

    print("")

    if (args.isNotEmpty()) {
        exitProcess(CommandLine(Setup()).setSubcommandsCaseInsensitive(true).execute(*args))
    }

    if (hasConnection()){
        startingMessage(accountKeys)
        logger.info { "Sent Starting Message!" }
    }

    //addLiveStreamChannels("subscriptions.csv")

    val youtubeUpload = Thread(YouTubeUpload())
    youtubeUpload.start()


    val youtubeLive = Thread(YouTubeLive())
    youtubeLive.start()

    val twitch = Thread(TwitchLive())
    twitch.start()

}

private fun startingMessage(accountKeys: List<String>){
    val notif = Notification("BTTN Started!","BTTN listening for updates!", NotificationType.StartingMessage)
    notif.send(accountKeys)
}

fun hasConnection(): Boolean {
    return try {
        val socket = Socket()
        socket.connect(InetSocketAddress("youtube.googleapis.com", 443), 15000)
        true
    } catch (e: IOException) {
        false
    }
}
