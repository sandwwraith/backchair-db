package dev.sandwwraith

import dev.sandwwraith.model.User
import kotlin.test.Test
import kotlin.test.assertEquals


class StatementExecutorTest() {
    private val users = listOf(
        User(1, "1", "1"),
        User(2, "2", "2"),
        User(449, "John", "John@example.com"),
        User(544453, "Richard K Dick", "mail+me@gmail.com")
    )

    @Test
    fun canPutAndRetrieveUsers() {
        users.map { Insert(it) }.forEach { StatementExecutor.execute(it) }
        val result = StatementExecutor.executeSelect0()
        assertEquals(users, result)
    }
}
