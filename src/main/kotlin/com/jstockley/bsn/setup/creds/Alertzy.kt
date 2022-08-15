package com.jstockley.bsn.setup.creds

import com.jstockley.bsn.*
import com.jstockley.bsn.notification.Notification
import com.jstockley.bsn.notification.NotificationType
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
            val success = sendTestNotif(keys)
            if (success) {
                writeData(ALERTZY_KEYS, keys)
                return 0
            } else {
                throw AlertzyCredException("Failed sending Alertzy test notification to at least one account key!")
            }
        } catch (e: AlertzyCredException) {
            System.err.println(e.message)
            return 1
        }
    }
}

@Command(name = "List", mixinStandardHelpOptions = true, description = ["List Alertzy Account Key(s)"], version = [version])
class AlertzyList: Callable<Int> {
    override fun call(): Int {
        try {
            val keys = getDataAsList(ALERTZY_KEYS)

            if (keys.isNotEmpty()) {
                println("Alertzy Key(s) currently being used:")
                for (key in keys){
                    println("\t$key")
                }
                return 0
            } else {
                throw AlertzyCredException("Alertzy Credentials not setup, unable to account keys!")
            }
        } catch (e: AlertzyCredException) {
            System.err.println(e.message)
            return 1
        }
    }
}

@Command(name = "Update", mixinStandardHelpOptions = true, description = ["Update Alertzy Account Key(s)"], version = [version])
class AlertzyUpdate: Callable<Int> {
    override fun call(): Int {
        try {
            val currentKeys = getDataAsList(ALERTZY_KEYS)

            val updatedKeys = getSelectedItemsList("Add/Remove Alertzy Account Keys", items = currentKeys)
            val success = sendTestNotif(updatedKeys)
            if (success) {
                writeData(ALERTZY_KEYS, updatedKeys)
                return 0
            } else {
                throw AlertzyCredException("Failed sending Alertzy test notification to at least one account key!")
            }
        } catch (e: AlertzyCredException) {
            System.err.println(e.message)
            return 1
        }
    }
}

@Command(name = "Remove", mixinStandardHelpOptions = true, description = ["Remove Alertzy Account Key(s)"], version = [version])
class AlertzyRemove: Callable<Int> {
    override fun call(): Int {
        try {
            val keys = getDataAsList(ALERTZY_KEYS)

            if(keys.isNotEmpty()) {
                val removedKeys = getSelectedItemsList(keys, "Selected Alertzy Account Key(s) to remove", checkedItems = keys)
                writeData(ALERTZY_KEYS, removedKeys)
                return 0
            } else {
                throw AlertzyCredException("Alertzy Credentials not setup, unable to account keys!")
            }
        } catch (e: AlertzyCredException) {
            System.err.println(e.message)
            return 1
        }
    }
}

private fun sendTestNotif(keys: List<String>): Boolean {
    val notif = Notification("Test Notification", "This is a test BSN Notification", NotificationType.Test)
    return notif.send(keys)
}
