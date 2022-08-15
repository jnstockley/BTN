package com.jstockley.bsn.setup.creds

import com.jstockley.bsn.version
import picocli.CommandLine
import java.util.concurrent.Callable

@CommandLine.Command(name = "Cred", mixinStandardHelpOptions = true, subcommands = [Alertzy::class, Twitch::class, YouTube::class], version = [version])
class CredSetup: Callable<Int> {
    @CommandLine.Spec
    lateinit var spec: CommandLine.Model.CommandSpec

    override fun call(): Int {
        throw CommandLine.ParameterException(spec.commandLine(), "Missing required subcommand for credentials")
    }
}