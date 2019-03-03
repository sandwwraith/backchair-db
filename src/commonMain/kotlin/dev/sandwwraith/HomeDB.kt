package dev.sandwwraith

import dev.sandwwraith.StatementCompiler.compile
import dev.sandwwraith.model.User
import dev.sandwwraith.utils.Logger
import sample.printStackTrace
import sample.readLine

class UnrecognizedMetaCommandException(cmd: String): IllegalStateException("Unrecognized command $cmd")
class UnrecognizedStatementException(cmd: String): IllegalStateException("Unrecognized keyword at start of '$cmd'")

object StatementCompiler {
    class SyntaxError(pos: Int?, msg: String): Exception("Error ${if (pos != null) "at $pos" else ""}: $msg")

    internal fun compile(statement: String) = when {
        statement.startsWith("insert", ignoreCase = true) -> parseInsert(statement)
        statement.startsWith("select", ignoreCase = true) -> Select
        else -> throw UnrecognizedStatementException(statement)
    }

    internal fun parseInsert(stmt: String): Insert {
        val parts = stmt.split(' ')
        if (!parts[0].equals("insert", ignoreCase = true)) throw SyntaxError(0, "expected 'insert'")
        val id = parts.getOrNull(1)?.toIntOrNull() ?: throw SyntaxError(stmt.indexOf(' ') + 1, "expected number")
        val username = parts.getOrNull(2) ?: throw SyntaxError(stmt.lastIndex, "expected string")
        val email = parts.getOrNull(3) ?: throw SyntaxError(stmt.lastIndex, "expected string")
        return Insert(User(id, username, email))
    }
}

object REPL {
    private val L = Logger("Backchair DB")
    val executor: StatementExecutor by lazy { StatementExecutor("users") }
    fun loop(): Int {
        print(PROMPT)
        main@while (true) {
            val cmd = (readLine() ?: return 0).trim()
            try {
                when {
                    cmd.isBlank() -> continue@main
                    cmd == ".exit" -> {
                        executor.userTable.closeDatabase()
                        return 0
                    }
                    cmd.startsWith(".") -> doMetaCommand(cmd)
                    else -> {
                        val stmt = compile(cmd)
                        L.debug { "Compiled statement: $stmt" }
                        executor.execute(stmt)
                    }
                }
            } catch (e: Exception) {
                println("An error occurred during evaluation: $e")
                if (L.isDebug) printStackTrace(e)
            }
            print(PROMPT)
        }
    }

    internal fun doMetaCommand(cmd: String) {
        throw UnrecognizedMetaCommandException(cmd)
    }

    private const val PROMPT = "> db\n> "
}
