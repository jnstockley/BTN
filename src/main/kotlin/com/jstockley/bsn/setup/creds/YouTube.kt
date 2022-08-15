package com.jstockley.bsn.setup.creds

import com.jstockley.bsn.*
import picocli.CommandLine
import picocli.CommandLine.Command
import picocli.CommandLine.Spec
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.util.concurrent.Callable

@Command(name = "YouTube", mixinStandardHelpOptions = true, subcommands = [YouTubeAdd::class, YouTubeList::class, YouTubeUpdate::class, YouTubeRemove::class], version = [version])
class YouTube: Callable<Int> {

    @Spec
    lateinit var spec: CommandLine.Model.CommandSpec

    override fun call(): Int {
        throw CommandLine.ParameterException(spec.commandLine(), "Missing required subcommand for twitch")
    }
}

@Command(name = "Add", mixinStandardHelpOptions = true, description = ["Add YouTube API Keys(s)"], version = [version])
class YouTubeAdd: Callable<Int> {
    override fun call(): Int {
        try {
            val keys = getSelectedItemsList("Add YouTube API Key(s)")
            if (checkKeys(keys)) {
                writeData(YOUTUBE_KEYS, keys)
                return 0
            } else {
                throw YouTubeCredException("At least one you the entered YouTube API Keys is invalid")
            }
        } catch (e: YouTubeCredException) {
            System.err.println(e.message)
            return 1
        }
    }

}

@Command(name = "List", mixinStandardHelpOptions = true, description = ["List YouTube API Keys(s)"], version = [version])
class YouTubeList: Callable<Int> {
    override fun call(): Int {
        try {
            val keys = getDataAsList(YOUTUBE_KEYS)
            if (keys.isNotEmpty()) {
                println("YouTube API Key(s) currently being used:")
                for (key in keys) {
                    println("\tkey")
                }
                return 0
            } else {
                throw YouTubeCredException("YouTube Credentials not setup, unable to API Key(s)")
            }
        } catch (e: YouTubeCredException) {
            System.err.println(e.message)
            return 1
        }
    }
}

@Command(name = "Update", mixinStandardHelpOptions = true, description = ["Update YouTube API Keys(s)"], version = [version])
class YouTubeUpdate: Callable<Int> {
    override fun call(): Int {
        try {
            val currentKeys = getDataAsList(YOUTUBE_KEYS)
            val updatedKeys = getSelectedItemsList("Add/Remove YouTube API Keys", items = currentKeys)
            if (checkKeys(updatedKeys)) {
                writeData(YOUTUBE_KEYS, updatedKeys)
                return 0
            } else {
                throw YouTubeCredException("At least one you the entered YouTube API Keys is invalid")
            }
        } catch (e: YouTubeCredException) {
            System.err.println(e.message)
            return 1
        }
    }
}

@Command(name = "Remove", mixinStandardHelpOptions = true, description = ["Remove YouTube API Keys(s)"], version = [version])
class YouTubeRemove: Callable<Int> {
    override fun call(): Int {
        try {
            val keys = getDataAsList(YOUTUBE_KEYS)

            if (keys.isNotEmpty()) {
                val removedKeys = getSelectedItemsList(keys, "Select YouTube API Key(s) to remove", checkedItems = keys)
                writeData(YOUTUBE_KEYS, removedKeys)
                return 0
            } else {
                throw YouTubeCredException("Alertzy Credentials not setup, unable remove to API key(s)!")
            }
        } catch (e: YouTubeCredException) {
            System.err.println(e.message)
            return 1
        }
    }
}

private fun checkKeys(keys: List<String>): Boolean {
    val url = "https://youtube.googleapis.com/youtube/v3/videos?part=snippet%2CcontentDetails%2Cstatistics&id=Ks-_Mh1QhMc&key="
    val client = HttpClient.newBuilder().build()
    for (key in keys) {
        val request = HttpRequest.newBuilder()
            .uri(URI.create("$url$key"))
            .build()
        val response = client.send(request, HttpResponse.BodyHandlers.ofString())
        if (response.statusCode() != 200) {
            return false
        }
    }
    return true
}