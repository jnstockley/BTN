package com.jstockley.bsn

import com.jstockley.bsn.notification.Notification
import com.jstockley.bsn.notification.NotificationType
import mu.KotlinLogging

/**
 * BTTN base exception that logs error message and sends the user a notification with the error message
 *
 * @property message Error message
 * @param fullMessage Stack trace string
 */
open class BTTNException(final override var message: String, fullMessage: String): java.lang.Exception() {

    init {
        val logger = KotlinLogging.logger{}
        Notification(message, fullMessage, NotificationType.Error).send(accountKeys)
        logger.error { "Sent error notification!" }
    }
}

/**
 * BTTN base failover exception that logs error message and send user a failover notification with the error message
 *
 * @property message Error message
 * @property fullMessage Stack trace string
 * @param email Email address to send the failover message
 */
open class BTTNFailoverException(override var message: String, private var fullMessage: String, email: String): Exception() {
    init {
        val logger = KotlinLogging.logger{}
        logger.error { "Sent failover error notification!" }
        logger.error { "$message: $fullMessage -> $email" }
    }
}

open class CredException(override var message: String): Exception() {
    init {
        val logger = KotlinLogging.logger{}
        logger.error { message }
    }

    override fun toString(): String {
        return message
    }
}

open class MissingChannelsException(override var message: String): Exception() {
    init {
        val logger = KotlinLogging.logger{}
        logger.error { message }
    }
}

class YTCredException(message: String):
    CredException(message)

class TwitchCredException(message: String):
    CredException(message)

class YouTubeShortException(message: String):
        BTTNException("Error getting YouTube Short data!", message)

class YouTubePremiereException(message: String):
        BTTNException("Error getting YouTube Premiere data!", message)

class YouTubePremiereDateException(message: String):
        BTTNException("Error getting YouTube Premiere date data!", message)

class YouTubeVideoDataException(message: String):
        BTTNException("Error getting YouTube Video data!", message)

class YouTubePlaylistDataException(message: String):
        BTTNException("Error getting YouTube Playlist data!", message)

class YouTubeLiveDataException(message: String):
        BTTNException("Error getting YouTube Live Stream data!", message)

class AlertzyCredException(message: String):
        CredException(message)

class YouTubeCredException(message: String):
        CredException(message)

class AlertzyMixedException(message: String, email: String):
        BTTNFailoverException("Mixed error sending Alertzy Notification!", message, email)

class AlertzyFailException(message: String, email: String):
        BTTNFailoverException("Fail error sending Alertzy Notification!", message, email)

class AlertzyException(message: String, email: String):
        BTTNFailoverException("Error sending Alertzy Notification!", message, email)