package dev.sandwwraith

import kotlin.test.Test
import kotlin.test.assertEquals


class LoggerTest {

    @Test
    fun formatTimeMillis() {
        val l = Logger()
        assertEquals( "12.345",l.formatTimeMillis(12345))
    }
}
