package dev.sandwwraith.utils

import kotlinx.files.JvmPath
import kotlinx.files.Path

actual object Platform {
    actual val name: String = "JVM"
}

actual fun readLine(): String? = kotlin.io.readLine()
actual fun currentTimeMillis(): Long = System.currentTimeMillis()
actual fun printStackTrace(e: Exception) = e.printStackTrace()
actual fun Path.fileLen(): Long {
    return (this as JvmPath).toJavaPath().toFile().length()
}
