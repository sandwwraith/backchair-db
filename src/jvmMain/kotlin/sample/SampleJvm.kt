package sample

import kotlin.system.exitProcess
import kotlin.system.measureTimeMillis

actual class Sample {
    actual fun checkMe() = 42
}

actual object Platform {
    actual val name: String = "JVM"
}

actual fun readLine(): String? = kotlin.io.readLine()
actual fun currentTimeMillis(): Long = System.currentTimeMillis()
