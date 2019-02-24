package sample

import kotlinx.fs.core.Path
import kotlinx.fs.core.exists
import kotlinx.fs.core.internal.UnixPath
import kotlinx.io.core.ExperimentalIoApi
import kotlinx.io.errors.IOException
import kotlinx.io.errors.PosixException
import platform.posix.*
import kotlin.system.getTimeMillis
import kotlin.system.measureTimeMicros

actual class Sample {
    actual fun checkMe() = 7
}

actual object Platform {
    actual val name: String = "Native"
}

@ExperimentalIoApi
actual fun Path.fileLen(): Long {
    if (!this.exists()) return 0
    if (this !is UnixPath) throw UnsupportedOperationException("Work only on Unix systems")
    val fd = open(this.toString(), O_RDONLY)
    val err = errno
    if (fd == -1) throw IOException("Can't open $this with err#$err " , PosixException.forErrno(err))
    val len = lseek(fd, 0, SEEK_END)
    close(fd)
    return len
}

actual fun readLine(): String? = kotlin.io.readLine()

actual fun printStackTrace(e: Exception) = e.printStackTrace()

actual fun currentTimeMillis(): Long = getTimeMillis()
