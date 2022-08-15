package com.jstockley.bsn.setup

import com.github.twitch4j.TwitchClient
import com.github.twitch4j.TwitchClientBuilder
import com.jstockley.bsn.*
import com.jstockley.bsn.getSelectedItemsList
import picocli.CommandLine.ParameterException
import picocli.CommandLine.Command
import picocli.CommandLine.Model.CommandSpec
import picocli.CommandLine.Option
import picocli.CommandLine.Spec
import java.io.File
import java.io.FileNotFoundException
import java.util.concurrent.Callable

@Command(name = "Twitch", mixinStandardHelpOptions = true, subcommands = [TwitchAdd::class, TwitchList::class, TwitchUpdate::class, TwitchRemove::class], version = [version])
class Twitch : Callable<Int> {

    @Spec
    lateinit var spec: CommandSpec

    override fun call(): Int {
        throw ParameterException(spec.commandLine(), "Missing required subcommand for twitch")
    }
}

@Command(name = "Add", mixinStandardHelpOptions = true, description = ["Add Twitch Channel(s)"], version = [version])
class TwitchAdd : Callable<Int> {

    @Option(names = ["-n", "--name"], required = true, description = ["Twitch Login Name"])
    lateinit var name: String

    override fun call(): Int {
        try{
            val twitchCred = getDataAsStringMap(TWITCH_KEYS)
            if (twitchCred.isNotEmpty()) {
                val channels = mutableMapOf<String, String>()
                val twitchClient: TwitchClient = TwitchClientBuilder.builder().withEnableHelix(true)
                    .withClientId(twitchCred["clientId"])
                    .withClientSecret(twitchCred["clientSecret"])
                    .build()
                val userId = twitchClient.helix.getUsers(null, null, listOf(name)).execute().users[0].id

                val results = twitchClient.helix.getFollowers(null, userId, null, null, 100).execute().follows
                for (result in results) {
                    channels[result.toName] = result.toLogin
                }
                val selectedChannels = getSelectedItemsList(channels, "Select Twitch Channel(s) to Import")
                writeData(TWITCH_CHANNELS, selectedChannels)
                return 0
            } else {
                throw TwitchCredException("Twitch Credentials not setup, unable to list channels!")
            }
        } catch (e: TwitchCredException) {
            System.err.println(e.message)
            return 1
            //throw TwitchCredException(e.message)
        }
    }
}

@Command(name = "List", mixinStandardHelpOptions = true, description = ["List Twitch Channel(s)"], version = [version])
class TwitchList : Callable<Int> {

    override fun call(): Int {
        try {
            val channels = getChannels().keys.toSortedSet(java.lang.String.CASE_INSENSITIVE_ORDER)
            if (channels.isNotEmpty()) {
                println("Twitch Channels currently being checked: ")
                for (channel in channels) {
                    println("\t$channel")
                }
                return 0
            } else {
                throw MissingChannelsException("No Twitch channels to list!")
            }
        } catch (e: MissingChannelsException) {
            System.err.println(e.message)
            return 1
        }
    }

    fun getChannels(): Map<String, String> {
        try {
            val file = File(TWITCH_CHANNELS)
            if (file.exists()) {
                val channels = mutableMapOf<String, String>()
                val chanNames = getDataAsList(TWITCH_CHANNELS)
                val twitchCreds = getDataAsStringMap(TWITCH_KEYS)
                if (twitchCreds.isNotEmpty()) {
                    val twitchClient: TwitchClient = TwitchClientBuilder.builder().withEnableHelix(true)
                        .withClientId(twitchCreds["clientId"])
                        .withClientSecret(twitchCreds["clientSecret"])
                        .build()
                    val chanList = twitchClient.helix.getUsers(null, null, chanNames).execute().users
                    for (channel in chanList) {
                        channels[channel.displayName] = channel.login
                    }
                    return channels
                } else {
                    throw TwitchCredException("YouTube Credentials not setup, unable to list channels!")
                }
            } else {
                throw FileNotFoundException("Please add Twitch channels to be checked, $file is not found!")
            }
        } catch (e: FileNotFoundException) {
            System.err.println(e.message)
        } catch (e: TwitchCredException) {
            System.err.println(e.message)
        }
        return mutableMapOf()
    }
}

@Command(name = "Update", mixinStandardHelpOptions = true, description = ["Update Twitch Channel(s)"], version = [version])
class TwitchUpdate : Callable<Int> {

    @Option(names = ["-n", "--name"], defaultValue = "", required = false, description = ["Twitch Login Name, optional use if you want to add channels"])
    lateinit var name: String

    override fun call(): Int {
        try {
            val checkedChannels = TwitchList().getChannels()

            if (checkedChannels.isNotEmpty()) {
                val channelMap = mutableMapOf<String, String>()
                if (name == "") {
                    val twitchCred = getDataAsStringMap(TWITCH_KEYS)
                    if (twitchCred.isNotEmpty()) {
                        val channels = mutableMapOf<String, String>()
                        val twitchClient: TwitchClient = TwitchClientBuilder.builder().withEnableHelix(true)
                            .withClientId(twitchCred["clientId"])
                            .withClientSecret(twitchCred["clientSecret"])
                            .build()
                        val userId = twitchClient.helix.getUsers(null, null, listOf(name)).execute().users[0].id

                        val results = twitchClient.helix.getFollowers(null, userId, null, null, 100).execute().follows
                        for (result in results) {
                            channels[result.toName] = result.toLogin
                        }
                    } else {
                        throw TwitchCredException("YouTube Credentials not setup, unable to list channels!")
                    }
                }

                channelMap.putAll(checkedChannels)

                val updatedChannels = getSelectedItemsList(channelMap, "Select Twitch Channel(s) to Add/Remove", checkedItems = checkedChannels)
                writeData(TWITCH_CHANNELS, updatedChannels)
                return 0
            } else {
                throw MissingChannelsException("No Twitch channels added. Please add channels first.")
            }
        } catch (e: TwitchCredException) {
            System.err.println(e.message)
            return 1
        } catch (e: MissingChannelsException) {
            System.err.println(e.message)
            return 1
        }
    }
}

@Command(name = "Remove", mixinStandardHelpOptions = true, description = ["Remove Twitch Channel(s)"], version = [version])
class TwitchRemove : Callable<Int> {

    override fun call(): Int {
        try {
            val channels = TwitchList().getChannels()

            if (channels.isNotEmpty()) {
                val removedChannels = getSelectedItemsList(channels, "Select Twitch Channel(s) to Remove", checkedItems = channels)
                writeData(TWITCH_KEYS, removedChannels)
                return 0
            } else {
                throw MissingChannelsException("No Twitch channels added. Please add channels first.")
            }
        } catch (e: MissingChannelsException) {
            System.err.println(e.message)
            return 1
        }
    }
}