package com.jstockley.bsn.setup

import com.github.doyaaaaaken.kotlincsv.dsl.csvReader
import com.jstockley.bsn.*
import com.jstockley.bsn.youtube.live.YouTubeLiveStreams
import com.jstockley.bsn.youtube.live.getNames
import getSelectedItemsMap
import picocli.CommandLine.Command
import picocli.CommandLine.Model.CommandSpec
import picocli.CommandLine.Option
import picocli.CommandLine.ParameterException
import picocli.CommandLine.Spec
import java.io.File
import java.io.FileNotFoundException
import java.util.concurrent.Callable

@Command(name = "YouTubeLive", mixinStandardHelpOptions = true, subcommands = [YouTubeLiveAdd::class, YouTubeLiveList::class, YouTubeLiveUpdate::class, YouTubeLiveRemove::class], version = [version])
class YouTubeLive: Callable<Int> {

    @Spec
    lateinit var spec: CommandSpec

    override fun call(): Int {
        throw ParameterException(spec.commandLine(), "Missing required subcommand for youtubelive")
    }
}

@Command(name = "Add", mixinStandardHelpOptions = true, description = ["Add YouTube Live Stream Channel(s)"], version = [version])
class YouTubeLiveAdd: Callable<Int> {

    @Option(names = ["-f", "--file"], required = true, description = ["Path to Subscriptions CSV file"])
    lateinit var file: File

    override fun call(): Int {
        try {
            if (file.exists() && file.extension == "csv") {
                val channels = mutableMapOf<String, String>()
                csvReader().open(file){
                    readAllAsSequence().forEach { row ->
                        if (row[0] != "Channel Id") {
                            channels[row[2]] = row[0]
                        }
                    }
                }

                // TODO Not needed currently

                val ytCred = getYouTubeCred()

                if (ytCred.isNotEmpty()) {
                    val selectedChannels = getSelectedItemsMap(channels, "Select YouTube Live Channel(s) to Import")
                    writeYouTubeLive(YouTubeLiveStreams(selectedChannels).getCurrentLiveStreamStatus())
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

@Command(name = "List", mixinStandardHelpOptions = true, description = ["List YouTube Live Stream Channel(s)"], version = [version])
class YouTubeLiveList: Callable<Int> {

    override fun call(): Int {
        try {
            val channels = getChannels().keys.toSortedSet(java.lang.String.CASE_INSENSITIVE_ORDER)
            if (channels.isNotEmpty()) {
                println("YouTube Live Channels currently being checked:")
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
        try {
            val file = File("youtubeLive.json")
            if (file.exists()) {
                val liveStreams = getYouTubeLive().keys
                val ytCred = getYouTubeCred()
                if (ytCred.isNotEmpty()) {
                    return getNames(liveStreams, ytCred[0])
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

@Command(name = "Update", mixinStandardHelpOptions = true, description = ["Update YouTube Live Stream Channel(s)"], version = [version])
class YouTubeLiveUpdate: Callable<Int> {

    @Option(names = ["-f", "--file"], defaultValue = "", required = false, description = ["Path to Subscriptions CSV file, optional use if you want to add channels"])
    lateinit var file: File

    override fun call(): Int {
        try {
            val checkedChannels = YouTubeLiveList().getChannels()

            if (checkedChannels.isNotEmpty()) {
                val channelMap = mutableMapOf<String, String>()
                if (file.path != "") {
                    if (file.exists() && file.extension == "csv") {
                        csvReader().open(file) {
                            readAllAsSequence().forEach { row ->
                                if (row[0] != "Channel Id") {
                                    channelMap[row[2]] = row[0]
                                }
                            }
                        }
                    } else {
                        throw FileNotFoundException("$file is not a valid path or invalid file type!")
                    }
                }

                channelMap.putAll(checkedChannels)

                val updatedChannels = getSelectedItemsMap(channelMap, "Select YouTube Live Channel(s) to Add/Remove", checkedItems = checkedChannels)

                writeYouTubeLive(YouTubeLiveStreams(updatedChannels).getCurrentLiveStreamStatus())
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

@Command(name = "Remove", mixinStandardHelpOptions = true, description = ["Remove YouTube Live Stream Channel(s)"], version = [version])
class YouTubeLiveRemove: Callable<Int> {

    override fun call(): Int {
        try {
            val channels = YouTubeLiveList().getChannels()

            if (channels.isNotEmpty()) {
                val removedChannels = getSelectedItemsMap(channels, "Select YouTube Live Channel(s) to Remove", checkedItems = channels
                )

                writeYouTubeLive(YouTubeLiveStreams(removedChannels).getCurrentLiveStreamStatus())

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