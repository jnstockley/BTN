package com.jstockley.bsn.setup

import com.jstockley.bsn.CredException
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class TwitchAddTest {

    private val twitch = TwitchAdd()

    @Test
    fun invalidFile() {
        twitch.path = "nonFile.json"
        assertThrows<CredException> { twitch.call() }
    }
}