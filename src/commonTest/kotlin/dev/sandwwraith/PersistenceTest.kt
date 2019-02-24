package dev.sandwwraith

import kotlinx.fs.core.Path
import kotlinx.fs.core.delete
import kotlinx.fs.core.exists
import kotlin.test.*

class PersistenceTest {
    private val DBName = "mydb"
    private val DBPath = Path("$DBName.db")

    @BeforeTest
    @AfterTest
    fun cleanUp() {
        if (DBPath.exists()) DBPath.delete()
    }

    @Test
    fun canPersist() {
        var db = UserTable.openDatabase(DBName)
        assertTrue(DBPath.exists())
        users.forEach { db.insert(it) }
        db.closeDatabase()

        db = UserTable.openDatabase(DBName)
        val users1 = (0 until db.numRows).map { db.select(it) }
        assertEquals(users, users1)
    }
}
