package com.jstockley.bsn.setup.creds

import com.github.stefanbirkner.systemlambda.SystemLambda.tapSystemErr
import com.github.stefanbirkner.systemlambda.SystemLambda.tapSystemOut
import com.jstockley.bsn.getDataAsList
import com.jstockley.bsn.main
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.assertThrows
import org.testng.Assert.*
import org.testng.annotations.Test
import java.io.ByteArrayOutputStream
import java.io.FileNotFoundException
import java.io.PrintStream

class AlertzyTest {

    //private val stdOut = ByteArrayOutputStream()

    @BeforeEach
    fun setup() {
        //System.setOut(PrintStream(stdOut))
    }

    @Test
    fun testAlertzyNoArgs() {

    }

    @Test
    fun testAlertzyAdd() {
        var alertzyAdd = AlertzyAdd()
        alertzyAdd.fileName = "config/alertzy/keys-test.json"
        alertzyAdd.fileName
        val badKey = "abcdefghijklmnop"
        alertzyAdd.keys = listOf(badKey)
        val stdErr = tapSystemErr {
            alertzyAdd.call()
        }
        assertEquals(stdErr.trim(), "Failed sending Alertzy test notification to at least one account key!")
        alertzyAdd = AlertzyAdd()
        alertzyAdd.fileName = "config/alertzy/keys-test.json"
        val goodKey = "787xmvo9n0roff6"
        alertzyAdd.keys = listOf(goodKey)
        assertEquals(alertzyAdd.call(), 0)
        val keys = getDataAsList(alertzyAdd.fileName)
        assertEquals(keys, listOf(goodKey))
    }

    @Test
    fun testAlertzyList() {
        val alertzyList = AlertzyList()
        alertzyList.fileName = "config/alertzy/nonExistence.json"
        assertThrows<FileNotFoundException>{ alertzyList.call() }
        val alertzyAdd = AlertzyAdd()
        alertzyAdd.fileName = "config/alertzy/keys-test.json"
        alertzyList.fileName = "config/alertzy/keys-test.json"
        val goodKey = "787xmvo9n0roff6"
        alertzyAdd.keys = listOf(goodKey)
        assertEquals(alertzyAdd.call(), 0)
        val stdOut = tapSystemOut {
            alertzyList.call()
        }
        assertTrue(stdOut.trim().contains(goodKey))
    }

    @Test
    fun testAlertzyUpdate() {

    }

    @Test
    fun testAlertzyRemove() {
        val alertzyAdd = AlertzyAdd()
        alertzyAdd.fileName = "config/alertzy/keys-test.json"
        val goodKey = "787xmvo9n0roff6"
        alertzyAdd.keys = listOf(goodKey)
        assertEquals(alertzyAdd.call(), 0)
        val alertzyRemove = AlertzyRemove()
        alertzyRemove.removedKeys = listOf(goodKey)
        assertEquals(alertzyRemove.call(), 0)
        alertzyRemove.fileName = "config/alertzy/nonExistence.json"
        assertThrows<FileNotFoundException>{ alertzyRemove.call() }
    }
}