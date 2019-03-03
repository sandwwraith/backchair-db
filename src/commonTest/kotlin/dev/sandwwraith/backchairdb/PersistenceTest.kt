package dev.sandwwraith.backchairdb

import kotlinx.files.Path
import kotlinx.files.delete
import kotlinx.files.exists
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
        val c = db.start()
        val users1 = (0 until db.numRows).map {
            db.select(c).also { c.advance() }
        }
        assertEquals(users, users1)
    }
}
