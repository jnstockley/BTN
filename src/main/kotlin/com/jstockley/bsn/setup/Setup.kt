package com.jstockley.bsn.setup

import com.jstockley.bsn.setup.creds.CredSetup
import com.jstockley.bsn.version
import picocli.CommandLine
import picocli.CommandLine.Command
import java.util.concurrent.Callable

@Command(name = "BSN", mixinStandardHelpOptions = true, subcommands = [CredSetup::class, YouTube::class, YouTubeLive::class, Twitch::class], version = [version])
class Setup: Callable<Int> {

    @CommandLine.Spec
    lateinit var spec: CommandLine.Model.CommandSpec
    override fun call(): Int {
        throw CommandLine.ParameterException(spec.commandLine(), "Missing required subcommand for setup")
    }
}
