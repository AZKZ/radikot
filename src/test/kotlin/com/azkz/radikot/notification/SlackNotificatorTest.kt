package com.azkz.radikot.notification

import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*

internal class SlackNotificatorTest {

    @Test
    fun notification() {
        val slackNotificator = SlackNotificator()
        val message = Message("TEST SUBJECT","TEST BODY")
        val result = slackNotificator.notification(message)
        assertTrue((result))
    }
}