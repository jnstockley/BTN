package com.jstockley.bsn.setup

import com.github.doyaaaaaken.kotlincsv.dsl.csvReader
import com.jstockley.bsn.*
import com.jstockley.bsn.youtube.video.YouTubePlaylists
import getSelectedItemsList
import picocli.CommandLine.ParameterException
import picocli.CommandLine.Command
import picocli.CommandLine.Model.CommandSpec
import picocli.CommandLine.Option
import picocli.CommandLine.Spec
import java.io.File
import java.io.FileNotFoundException
import java.util.concurrent.Callable

@Command(name = "YouTube", mixinStandardHelpOptions = true, subcommands = [YouTubeAdd::class, YouTubeList::class, YouTubeUpdate::class, YouTubeRemove::class], version = [version])
class YouTube: Callable<Int> {

    @Spec
    lateinit var spec: CommandSpec

    override fun call(): Int {
        throw ParameterException(spec.commandLine(), "Missing required subcommand for youtube")
    }
}

@Command(name = "Add", mixinStandardHelpOptions = true, description = ["Add YouTube Channel(s)"], version = [version])
class YouTubeAdd: Callable<Int> {

    @Option(names = ["-f", "--file"], required = true, description = ["Path to Subscriptions CSV file"])
    lateinit var file: File

    override fun call(): Int {
        try{
            if (file.exists() && file.extension == "csv"){
                val channels = mutableMapOf<String, String>()
                csvReader().open(file) {
                    readAllAsSequence().forEach { row ->
                        if (row[0] != "Channel Id") {
                            val channel = "UU" + row[0].substring(2)
                            channels[row[2]] = channel
                        }
                    }
                }

                val ytCred = getYouTubeCred()

                if (ytCred.isNotEmpty()){
                    val selectedChannels = getSelectedItemsList(channels, "Select YouTube Channel(s) to Import")
                    writePlaylists(YouTubePlaylists(selectedChannels, ytCred[0]).getCurrentVideoAmounts())
                    return 0
                } else {
                    throw YTCredException("YouTube Credentials not setup, unable to add channels!")
                }
            } else {
                throw FileNotFoundException("$file is not a valid path or invalid file type!")
            }
        } catch (e: YTCredException) {
            System.err.println(e.message)
            return 1
        } catch (e: FileNotFoundException) {
            System.err.println(e.message)
            return 1
        }
    }
}

@Command(name = "List", mixinStandardHelpOptions = true, description = ["List YouTube Channel(s)"], version = [version])
class YouTubeList: Callable<Int> {

    override fun call(): Int {
        try {
            val channels = getChannels().keys.toSortedSet(java.lang.String.CASE_INSENSITIVE_ORDER)
            if (channels.isNotEmpty()) {
                println("YouTube Channels currently being checked:")
                for (channel in channels) {
                    println("\t$channel")
                }
                return 0
            } else {
                throw MissingChannelsException("No YouTube channels added. Please add channels first.")
            }
        } catch (e: MissingChannelsException) {
            System.err.println(e.message)
            return 1
        }
    }

    fun getChannels(): Map<String, String> {
        try{
            val file = File("playlists.json")
            if (file.exists()) {
                val channels = mutableMapOf<String, String>()
                val playlists = getPlaylists().keys
                val ytCred = getYouTubeCred()
                if (ytCred.isNotEmpty()) {
                    val channelNames = YouTubePlaylists(playlists.toMutableList(), ytCred[0]).getChannels()
                    for (i in channelNames.indices) {
                        channels[channelNames.elementAt(i)] = playlists.elementAt(i)
                    }
                    print(channels)
                    return channels
                } else {
                    throw YTCredException("YouTube Credentials not setup, unable to list channels!")
                }
            } else {
                throw FileNotFoundException("Please add YouTube channels to be checked, $file is not found!")
            }
        } catch (e: YTCredException) {
            System.err.println(e.message)
        } catch (e: FileNotFoundException) {
            System.err.println(e.message)
        }
        return mutableMapOf()
    }
}

@Command(name = "Update", mixinStandardHelpOptions = true, description = ["Update YouTube Channel(s)"], version = [version])
class YouTubeUpdate: Callable<Int> {

    @Option(names = ["-f", "--file"], defaultValue = "", required = false, description = ["Path to Subscriptions CSV file, optional use if you want to add channels"])
    lateinit var file: File

    override fun call(): Int {
        try {
            val checkedChannels = YouTubeList().getChannels()

            if (checkedChannels.isNotEmpty()) {
                val channelMap = mutableMapOf<String, String>()
                if (file.path != ""){
                    if(file.exists() && file.extension == "csv"){
                        csvReader().open(file) {
                            readAllAsSequence().forEach { row ->
                                if (row[0] != "Channel Id") {
                                    val channel = "UU" + row[0].substring(2)
                                    channelMap[row[2]] = channel
                                }
                            }
                        }
                    } else {
                        throw FileNotFoundException("$file is not a valid path or invalid file type!")
                    }
                }

                channelMap.putAll(checkedChannels)

                val updatedChannels = getSelectedItemsList(channelMap, "Select YouTube Channel(s) to Add/Remove", checkedItems = checkedChannels)

                val ytCred = getYouTubeCred()

                writePlaylists(YouTubePlaylists(updatedChannels, ytCred[0]).getCurrentVideoAmounts())
                return 0
            } else {
                throw MissingChannelsException("No YouTube channels added. Please add channels first.")
            }
        } catch (e: FileNotFoundException) {
            System.err.println(e.message)
            return 1
        } catch (e: MissingChannelsException) {
            System.err.println(e.message)
            return 1
        }

    }
}

@Command(name = "Remove", mixinStandardHelpOptions = true, description = ["Remove YouTube Channel(s)"], version = [version])
class YouTubeRemove: Callable<Int> {

    override fun call(): Int {
        try {
            val channels = YouTubeList().getChannels()

            if (channels.isNotEmpty()) {
                val removedChannels = getSelectedItemsList(channels, "Select YouTube Channel(s) to Remove", checkedItems = channels)

                val ytCred = getYouTubeCred()

                writePlaylists(YouTubePlaylists(removedChannels, ytCred[0]).getCurrentVideoAmounts())

                return 0
            } else {
                throw MissingChannelsException("No YouTube channels added. Please add channels first.")
            }
        } catch (e: MissingChannelsException) {
            System.err.println(e.message)
            return 1
        }
    }
}