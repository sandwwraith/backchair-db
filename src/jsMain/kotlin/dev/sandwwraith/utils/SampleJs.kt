package dev.sandwwraith.utils

import kotlinx.files.Path
import kotlin.js.Date

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

actual fun Path.fileLen(): Long {
    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
}
