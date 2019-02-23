package sample

import kotlin.js.Date

actual class Sample {
    actual fun checkMe() = 12
}

actual object Platform {
    actual val name: String = "JS"
}

actual fun readLine(): String? {
    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
}

actual fun currentTimeMillis(): Long {
    return Date.now().toLong()
}

actual fun printStackTrace(e: Exception) {
    TODO("JS sucks")
}
