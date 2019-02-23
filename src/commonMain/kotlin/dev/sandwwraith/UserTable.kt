package dev.sandwwraith

import dev.sandwwraith.model.User
import dev.sandwwraith.serializer.RowSerializer
import dev.sandwwraith.serializer.varCharSize
import kotlinx.serialization.PrimitiveKind
import kotlinx.serialization.SerialDescriptor
import kotlinx.serialization.elementDescriptors
import kotlin.native.concurrent.ThreadLocal

const val PAGE_SIZE = 1024

inline class Page(val array: ByteArray) {
    companion object {
        fun allocate(): Page = Page(ByteArray(PAGE_SIZE))
    }
}

private fun entitySize(tableEntityDescriptor: SerialDescriptor): Int {
    val elementDescriptors = tableEntityDescriptor.elementDescriptors()
    check(elementDescriptors.isNotEmpty())
    return elementDescriptors.mapIndexed { i, it -> when(it.kind) {
        is PrimitiveKind.INT -> 4
        is PrimitiveKind.STRING -> tableEntityDescriptor.varCharSize(i)
        else -> TODO()
    } }.sum()
}


class UserTable {
    private val L = Logger("TABLE USERS")
    private val pages: MutableList<Page> = mutableListOf(Page.allocate())

    var numRows: Int = 0
        private set

    private val rowSize = entitySize(User.serializer().descriptor)
    private val rowsPerPage = PAGE_SIZE / rowSize


    private fun rowSlot(rowNum: Int): Pair<Page, Int> {
        val pageIdx = rowNum / rowsPerPage
        if (pageIdx >= pages.size) pages.add(Page.allocate())
        val page = pages[pageIdx]
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

}

@ThreadLocal // global table makes K/N complain sometimes
object StatementExecutor {
    private val userTable: UserTable = UserTable() // hardcoded for now

    internal fun executeInsert(insert: Insert) {
        val user = insert.row as User
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
