package sample

import dev.sandwwraith.REPL
import kotlin.jvm.JvmStatic

expect class Sample() {
    fun checkMe(): Int
}

expect object Platform {
    val name: String
}

expect fun readLine(): String?
expect fun currentTimeMillis(): Long
// expect fun exit(statusCode: Int) would be nice too

fun hello(): String = "Hello from ${Platform.name}"

fun main() {
    val result = REPL.loop()
    if (result != 0) throw AssertionError("Expected 0 status code, got $result")
}
