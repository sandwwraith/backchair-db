package dev.sandwwraith.utils

class Logger(val name: String = "", val isDebug: Boolean = LOG_GLOBAL) {
    fun println(message: Any?) = kotlin.io.println(message)

    fun info(message: Any?) = println("$name @ ${formatTimeMillis(currentTimeMillis())} - $message")

    fun debug(lazyMessage: () -> Any?) = if (isDebug) info(lazyMessage()) else Unit

    internal fun formatTimeMillis(time: Long): String {
        val str = time.toString()
        val dotPos = str.length - 3
        return str.substring(0, dotPos) + "." + str.substring(dotPos, str.length)
    }
}

const val LOG_GLOBAL = true
