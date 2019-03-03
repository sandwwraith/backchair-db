package dev.sandwwraith.backchairdb

import dev.sandwwraith.backchairdb.model.User
import kotlinx.fs.core.Path
import kotlinx.fs.core.delete
import kotlinx.fs.core.exists
import kotlin.test.*

val users = listOf(
    User(1, "1", "1"),
    User(2, "2", "2"),
    User(449, "John", "John@example.com"),
    User(544453, "Richard K Dick", "mail+me@gmail.com"),
    User(544457, "Richard K Dick", "mail+me+once@gmail.com")
)

class StatementExecutorTest() {

    @AfterTest
    fun cleanUp() {
        val path = Path("usersTest.db")
        if (path.exists()) path.delete()
    }

    @BeforeTest
    fun init() {
        executor = StatementExecutor("usersTest")
    }

    lateinit var executor: StatementExecutor

    @Test
    fun canPutAndRetrieveUsers() {

        users.map { Insert(it) }.forEach { executor.execute(it) }
        val result = executor.executeSelect0()
        assertEquals(users, result)
    }

    @Test
    fun cantPutNegativeId() {
        assertFails { executor.execute(Insert(User(-1, "", ""))) }
    }

    @Test
    fun cantPutTooLongString() {
        val longString = buildString { repeat(300) {append('a')} }
        val user = User(1, longString, longString)
        assertFails { executor.execute(Insert(user)) }
    }
}
