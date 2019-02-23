package sample

import kotlin.system.getTimeMillis
import kotlin.system.measureTimeMicros

actual class Sample {
    actual fun checkMe() = 7
}

actual object Platform {
    actual val name: String = "Native"
}

actual fun readLine(): String? = kotlin.io.readLine()


actual fun currentTimeMillis(): Long = getTimeMillis()
