package dev.sandwwraith.backchairdb

import dev.sandwwraith.backchairdb.model.User

class StatementExecutor(tableName: String) {
    val userTable: UserTable =
        UserTable.openDatabase(tableName) // hardcoded for now

    internal fun executeInsert(insert: Insert) {
        val user = insert.row as User
        check(user.id > 0) { "Id must not be negative" }
        userTable.insert(user)
    }

    internal fun executeSelect0(): List<User> {
        val c = userTable.start()
        return (0 until userTable.numRows).map {
            userTable.select(c).also { c.advance() }
        }

    }

    internal fun executeSelect(select: Select) {
        val numRows = userTable.numRows
        val list = executeSelect0()
        println("==SELECT $numRows FROM USERS==")
        for (user in list) {
            println(user)
        }
    }

    internal fun execute(statement: Statement) = when (statement) {
        is Insert -> executeInsert(statement)
        is Select -> executeSelect(statement)
    }
}
