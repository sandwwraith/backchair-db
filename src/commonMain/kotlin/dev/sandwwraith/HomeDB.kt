package dev.sandwwraith

import dev.sandwwraith.StatementCompiler.compile
import sample.readLine

class UnrecognizedMetaCommandException(cmd: String): IllegalStateException("Unrecognized command $cmd")
class UnrecognizedStatementException(cmd: String): IllegalStateException("Unrecognized keyword at start of '$cmd'")

object StatementCompiler {
    class SyntaxError(val pos: Int?, val msg: String): Exception("Error ${if (pos != null) "at $pos" else ""}: $msg")

    internal fun compile(statement: String) = when {
        statement.startsWith("insert", ignoreCase = true) -> parseInsert(statement)
        statement.startsWith("select", ignoreCase = true) -> Select()
        else -> throw UnrecognizedStatementException(statement)
    }

    internal fun parseInsert(stmt: String): Insert {
        val parts = stmt.split(' ')
        if (!parts[0].equals("insert", ignoreCase = true)) throw SyntaxError(0, "expected 'insert'")
        val id = parts[1].toIntOrNull() ?: throw SyntaxError(stmt.indexOf(' ') + 1, "expected number")
        val username = parts[2]
        val email = parts[3]
        return Insert(User(id, username, email))
    }
}

object REPL {
    private val L = Logger("HOME DB", true)

    fun loop(): Int {
        print(PROMPT)
        main@while (true) {
            val cmd = readLine() ?: return 0
            try {
                when {
                    cmd.isBlank() -> continue@main
                    cmd == ".exit" -> return 0
                    cmd.startsWith(".") -> doMetaCommand(cmd)
                    else -> {
                        val stmt = compile(cmd)
                        L.debug { "Compiled statement: $stmt" }
                        execute(stmt)
                    }
                }
            } catch (e: Exception) {
                println("An error occurred during evaluation: $e")
            }
            print(PROMPT)
        }
    }

    internal fun execute(statement: Statement) = when(statement) {
        is Insert -> println("Inserting $statement")
        is Select -> println("TODO: Select")
    }

    internal fun doMetaCommand(cmd: String) {
        throw UnrecognizedMetaCommandException(cmd)
    }

    private const val PROMPT = "> db\n> "
}
