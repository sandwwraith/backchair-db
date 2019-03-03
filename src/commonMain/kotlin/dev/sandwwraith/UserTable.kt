package dev.sandwwraith

import dev.sandwwraith.model.User
import dev.sandwwraith.pager.PAGE_SIZE
import dev.sandwwraith.pager.Page
import dev.sandwwraith.pager.Pager
import dev.sandwwraith.serialization.RowSerializer
import dev.sandwwraith.serialization.entitySize
import dev.sandwwraith.utils.Logger

class Cursor<E : Row>(private val table: Table<E>, currentRow: Int) {
    var rowNum: Int = currentRow
        private set

    var isEnd = table.numRows == currentRow
        private set

    fun advance() {
        rowNum++
        if (table.numRows == rowNum)
            isEnd = true
    }
}

interface Table<Entity : Row> {
    val numRows: Int
}

class UserTable private constructor(val databaseName: String, private val pager: Pager) : Table<User> {
    private val L = Logger("TABLE USERS")
    private val rowSize = entitySize(User.serializer().descriptor)
    private val rowsPerPage = PAGE_SIZE / rowSize

    override var numRows: Int = 0
        private set

    private fun valueAt(cursor: Cursor<User>): Pair<Page, Int> {
        val rowNum = cursor.rowNum
        val pageIdx = rowNum / rowsPerPage
        val page = pager.getPage(pageIdx)
        val offset = (rowNum % rowsPerPage) * rowSize
        L.debug { "Row $rowNum located at page#$pageIdx:$offset" }
        return page to offset
    }

    fun insert(user: User) {
        val (page, offset) = valueAt(end())
        User.serializer().serialize(RowSerializer.BitEncoder(page.array, offset), user)
        numRows++
    }

    fun select(cursor: Cursor<User>): User {
        val (page, offset) = valueAt(cursor)
        return User.serializer().deserialize(RowSerializer.BitDecoder(page.array, offset))
    }

    fun closeDatabase() {
        val pageMax = numRows / rowsPerPage
        val leftover = numRows % rowsPerPage * rowSize
        pager.flushAll(pageMax, leftover)
    }

    fun start(): Cursor<User> {
        return Cursor(this, 0)
    }

    fun end(): Cursor<User> {
        return Cursor(this, numRows)
    }

    companion object {
        fun openDatabase(name: String): UserTable {
            val pager = Pager.open("$name.db")
            return UserTable(name, pager).apply { numRows = pager.len.toInt() / rowSize }
        }
    }

}

