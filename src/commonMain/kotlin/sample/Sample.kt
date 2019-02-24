package sample

import dev.sandwwraith.REPL
import kotlinx.fs.core.Path
import kotlin.jvm.JvmStatic

expect class Sample() {
    fun checkMe(): Int
}

expect object Platform {
    val name: String
}

expect fun readLine(): String?
expect fun currentTimeMillis(): Long
// expect fun measureTimeMillis - :(
// expect fun exit(statusCode: Int) would be nice too
// fun Path.toAbsolute()
expect fun Path.fileLen(): Long
expect fun printStackTrace(e: Exception)

fun hello(): String = "Hello from ${Platform.name}"

fun main() {
    val result = REPL.loop()
    if (result != 0) throw AssertionError("Expected 0 status code, got $result")
}

