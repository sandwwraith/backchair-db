package dev.sandwwraith

import dev.sandwwraith.model.User
import dev.sandwwraith.pager.PAGE_SIZE
import dev.sandwwraith.pager.Page
import dev.sandwwraith.pager.Pager
import dev.sandwwraith.serializer.RowSerializer
import dev.sandwwraith.serializer.varCharSize
import kotlinx.serialization.PrimitiveKind
import kotlinx.serialization.SerialDescriptor
import kotlinx.serialization.elementDescriptors
import kotlin.native.concurrent.ThreadLocal

private fun entitySize(tableEntityDescriptor: SerialDescriptor): Int {
    val elementDescriptors = tableEntityDescriptor.elementDescriptors()
    check(elementDescriptors.isNotEmpty())
    return elementDescriptors.mapIndexed { i, it -> when(it.kind) {
        is PrimitiveKind.INT -> 4
        is PrimitiveKind.STRING -> tableEntityDescriptor.varCharSize(i)
        else -> TODO()
    } }.sum()
}

class UserTable private constructor(val databaseName: String, private val pager: Pager){
    private val L = Logger("TABLE USERS")
    private val rowSize = entitySize(User.serializer().descriptor)

    var numRows: Int = 0
        private set

    private val rowsPerPage = PAGE_SIZE / rowSize

    private fun rowSlot(rowNum: Int): Pair<Page, Int> {
        val pageIdx = rowNum / rowsPerPage
        val page = pager.getPage(pageIdx)
        val offset = (rowNum % rowsPerPage) * rowSize
        L.debug { "Row $rowNum located at page#$pageIdx:$offset" }
        return page to offset
    }

    fun insert(user: User) {
        val (page, offset) = rowSlot(numRows)
        User.serializer().serialize(RowSerializer.BitEncoder(page.array, offset), user)
        numRows++
    }

    fun select(rowNum: Int): User {
        val (page, offset) = rowSlot(rowNum)
        return User.serializer().deserialize(RowSerializer.BitDecoder(page.array, offset))
    }

    fun closeDatabase() {
        val pageMax = numRows / rowsPerPage
        val leftover = numRows % rowsPerPage * rowSize
        pager.flushAll(pageMax, leftover)
    }

    companion object {
        fun openDatabase(name: String): UserTable {
            val pager = Pager.open("$name.db")
            return UserTable(name, pager).apply { numRows = pager.len.toInt() / rowSize }
        }
    }

}

class StatementExecutor(val tableName: String) {
    val userTable: UserTable = UserTable.openDatabase(tableName) // hardcoded for now

    internal fun executeInsert(insert: Insert) {
        val user = insert.row as User
        check(user.id > 0) { "Id must not be negative" }
        userTable.insert(user)
    }

    internal fun executeSelect0(): List<User> {
        val numRows = userTable.numRows
        return (0 until numRows).map { userTable.select(it) }

    }

    internal fun executeSelect(select: Select) {
        val numRows = userTable.numRows
        val list = executeSelect0()
        println("==SELECT $numRows FROM USERS==")
        for (user in list) {
            println(user)
        }
    }

    internal fun execute(statement: Statement) = when(statement) {
        is Insert -> executeInsert(statement)
        is Select -> executeSelect(statement)
    }
}
