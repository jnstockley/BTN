package com.jstockley.bsn.setup.creds

import com.github.twitch4j.TwitchClient
import com.github.twitch4j.TwitchClientBuilder
import com.jstockley.bsn.*
import com.netflix.hystrix.exception.HystrixRuntimeException
import picocli.CommandLine
import picocli.CommandLine.Command
import picocli.CommandLine.Spec
import java.util.concurrent.Callable

@Command(name = "Twitch", mixinStandardHelpOptions = true, subcommands = [TwitchAdd::class, TwitchList::class, TwitchUpdate::class, TwitchRemove::class], version = [version])
class Twitch: Callable<Int> {

    @Spec
    lateinit var spec: CommandLine.Model.CommandSpec

    override fun call(): Int {
        throw CommandLine.ParameterException(spec.commandLine(), "Missing required subcommand for twitch")
    }
}

@Command(name = "Add", mixinStandardHelpOptions = true, description = ["Add Twitch Client Id(s) and Client Secret(s)"], version = [version])
class TwitchAdd: Callable<Int> {
    override fun call(): Int {
        try {
            val keys = getMultiTextBox(listOf("Twitch Client ID", "Twitch Client Secret"), "Add Twitch API Keys")
            if (checkKeys(keys)) {
                writeData(TWITCH_KEYS, keys)
                return 0
            } else {
                throw TwitchCredException("Invalid Twitch API Keys entered")
            }
        } catch (e: TwitchCredException) {
            System.err.println(e.message)
            return 1
        }
    }

}

@Command(name = "List", mixinStandardHelpOptions = true, description = ["List Twitch Client Id(s) and Client Secret(s)"], version = [version])
class TwitchList: Callable<Int> {
    override fun call(): Int {
        try {
            val keys = getDataAsStringMap(TWITCH_KEYS)
            if (keys.isNotEmpty()) {
                println("Twitch API Keys currently being used:")
                for (item in keys) {
                    println("${item.key}: ${keys[item.key]}")
                }
                return 0
            } else {
                throw TwitchCredException("Twitch Credentials not setup, unable to list API Key(s)")
            }
        } catch (e: TwitchCredException) {
            System.err.println(e.message)
            return 1
        }
    }

}

@Command(name = "Update", mixinStandardHelpOptions = true, description = ["Update Twitch Client Id(s) and Client Secret(s)"], version = [version])
class TwitchUpdate: Callable<Int> {
    override fun call(): Int {
        try {
            val currentKeys = getDataAsStringMap(TWITCH_KEYS)
            val updatedKeys = getMultiTextBox(listOf("Twitch Client ID", "Twitch Client Secret"), "Add/Remove Twitch API Keys", currentKeys.values.toList())
            if (checkKeys(updatedKeys)) {
                writeData(TWITCH_KEYS, updatedKeys)
                return 0
            } else {
                throw TwitchCredException("At least one you the entered Twitch API Keys is invalid")
            }
        } catch (e: TwitchCredException) {
            System.err.println(e.message)
            return 1
        }
    }
}

@Command(name = "Remove", mixinStandardHelpOptions = true, description = ["Remove Twitch Client Id(s) and Client Secret(s)"], version = [version])
class TwitchRemove: Callable<Int> {
    override fun call(): Int {
        try {
            val currentKeys = getDataAsStringMap(TWITCH_KEYS)
            val updatedKeys = getMultiTextBox(listOf("Twitch Client ID", "Twitch Client Secret"), "Remove Twitch API Keys", currentKeys.values.toList())
            if (checkKeys(updatedKeys) || updatedKeys.isEmpty()) {
                writeData(TWITCH_KEYS, updatedKeys)
                return 0
            } else {
                throw TwitchCredException("At least one you the entered Twitch API Keys is invalid")
            }
        } catch (e: TwitchCredException) {
            System.err.println(e.message)
            return 1
        }
    }
}

private fun checkKeys(keys: Map<String, String>): Boolean {
    if ("clientId" !in keys.keys && "clientSecret" !in keys.keys) {
        return false
    }

    val twitchClient: TwitchClient = TwitchClientBuilder.builder().withEnableHelix(true)
        .withClientId(keys["clientId"])
        .withClientSecret(keys["clientSecret"])
        .build()

    try {
        twitchClient.helix.getStreams(null, null, null, 5, null, null, null, null).execute()
    } catch (e: HystrixRuntimeException) {
        return false
    }
    return true
}