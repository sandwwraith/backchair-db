package dev.sandwwraith

import dev.sandwwraith.model.User
import kotlin.test.Test
import kotlin.test.assertEquals


class StatementCompilerTest {

    @Test
    fun insertParses() {
        val parsed = StatementCompiler.compile("INSERT 1 John john@example.com")
        assertEquals(Insert(User(1, "John", "john@example.com")), parsed)
    }
}
