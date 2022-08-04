package com.jstockley.bsn.setup.creds

import com.googlecode.lanterna.TerminalSize
import com.googlecode.lanterna.gui2.*
import com.googlecode.lanterna.screen.Screen
import com.googlecode.lanterna.terminal.DefaultTerminalFactory
import com.jstockley.bsn.getAlertzyKey
import com.jstockley.bsn.notification.Notification
import com.jstockley.bsn.notification.NotificationType
import com.jstockley.bsn.version
import com.jstockley.bsn.writeAlertzyKeys
import getSelectedItemsList
import picocli.CommandLine
import picocli.CommandLine.Command
import java.util.concurrent.Callable

@Command(name = "Alertzy", mixinStandardHelpOptions = true, subcommands = [AlertzyAdd::class, AlertzyList::class, AlertzyUpdate::class, AlertzyRemove::class], version = [version])
class Alertzy: Callable<Int> {

    @CommandLine.Spec
    lateinit var spec: CommandLine.Model.CommandSpec

    override fun call(): Int {
        throw CommandLine.ParameterException(spec.commandLine(), "Missing required subcommand for twitch")
    }
}

@Command(name = "Add", mixinStandardHelpOptions = true, description = ["Add Alertzy Account Key(s)"], version = [version])
class AlertzyAdd: Callable<Int> {
    override fun call(): Int {
        try {
            val keys = getSelectedItemsList("Add Alertzy Account Key(s)")
            val notif = Notification("Test Notification", "This is a test BSN Notification", NotificationType.Test)
            val sent = notif.send(keys)
            if (sent) {
                writeAlertzyKeys(keys)
                return 0
            } else {
                throw Exception("")
                // TODO Throw exception, don't write keys
            }
        } catch (e: Exception) {
            return 1
        }
    }
}

@Command(name = "List", mixinStandardHelpOptions = true, description = ["List Alertzy Account Key(s)"], version = [version])
class AlertzyList: Callable<Int> {
    override fun call(): Int {
        try {
            val keys = getAlertzyKey()
            if (keys.isNotEmpty()) {
                println("Alertzy Key(s) currently being used:")
                for (key in keys){
                    println("\t$key")
                }
                return 0
            } else {
                // TODO Throw Exception
                throw Exception("")
            }
        } catch (e: Exception) {
            // TODO
            return 1
        }
    }
}

@Command(name = "Update", mixinStandardHelpOptions = true, description = ["Update Alertzy Account Key(s)"], version = [version])
class AlertzyUpdate: Callable<Int> {
    override fun call(): Int {
        try {
            val currentKeys = getAlertzyKey()
            val updatedKeys = getSelectedItemsList("Add/Remove Alertzy Account Keys", items = currentKeys)
            val notif = Notification("Test Notification", "This is a test BSN Notification", NotificationType.Test)
            val sent = notif.send(updatedKeys)
            if (sent) {
                writeAlertzyKeys(updatedKeys)
                return 0
            } else {
                throw Exception("")
                // TODO Throw exception, don't write keys
            }

        } catch (e: Exception) {
            return 1
        }
    }
}

@Command(name = "Remove", mixinStandardHelpOptions = true, description = ["Remove Alertzy Account Key(s)"], version = [version])
class AlertzyRemove: Callable<Int> {
    override fun call(): Int {
        try {
            val keys = getAlertzyKey()

            if(keys.isNotEmpty()) {
                val removedKeys = getSelectedItemsList(keys, "Selected Alertzy Acccount Key(s) to remove", checkedItems = keys)
                return 0
            } else {
                TODO("Throw exception")
            }
        } catch (e: Exception) {
            return 1
        }
    }
}
