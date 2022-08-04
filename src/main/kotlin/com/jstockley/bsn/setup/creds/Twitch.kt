package com.jstockley.bsn.setup.creds

import com.jstockley.bsn.version
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
        TODO("Not yet implemented")
    }

}

@Command(name = "List", mixinStandardHelpOptions = true, description = ["List Twitch Client Id(s) and Client Secret(s)"], version = [version])
class TwitchList: Callable<Int> {
    override fun call(): Int {
        TODO("Not yet implemented")
    }

}

@Command(name = "Update", mixinStandardHelpOptions = true, description = ["Update Twitch Client Id(s) and Client Secret(s)"], version = [version])
class TwitchUpdate: Callable<Int> {
    override fun call(): Int {
        TODO("Not yet implemented")
    }

}

@Command(name = "Remove", mixinStandardHelpOptions = true, description = ["Remove Twitch Client Id(s) and Client Secret(s)"], version = [version])
class TwitchRemove: Callable<Int> {
    override fun call(): Int {
        TODO("Not yet implemented")
    }

}