package com.jstockley.bsn.setup

import com.jstockley.bsn.setup.creds.CredSetup
import com.jstockley.bsn.version
import picocli.CommandLine
import picocli.CommandLine.Command
import java.util.concurrent.Callable

@Command(name = "BSN", mixinStandardHelpOptions = true, subcommands = [CredSetup::class, YouTube::class, YouTubeLive::class, Twitch::class, Test::class], version = [version])
class Setup: Callable<Int> {

    @CommandLine.Spec
    lateinit var spec: CommandLine.Model.CommandSpec
    override fun call(): Int {
        throw CommandLine.ParameterException(spec.commandLine(), "Missing required subcommand for twitch")
    }
}

@Command(name = "Test", hidden = true)
class Test: Callable<Int> {
    override fun call(): Int {
        println("Testing")
        return 0
    }
}

/**@Command(name = "addYTChannel", mixinStandardHelpOptions = true, description = ["Add YouTube Channel"])
class AddYTChannels: Callable<Int> {

    @Option(names = ["-f", "--file"], required = true, description = ["Path to file"])
    lateinit var file: File

    override fun call(): Int {
        println("Add channel")
        println(file)
        return 0
    }

}**/