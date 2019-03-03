package dev.sandwwraith.utils

import dev.sandwwraith.backchairdb.newInputStream
import dev.sandwwraith.backchairdb.newOutputStream
import kotlinx.files.Path
import kotlinx.files.delete
import kotlinx.files.exists
import kotlinx.io.core.readUTF8Line
import kotlinx.io.core.toByteArray
import kotlinx.io.core.use
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse

class SampleTests {
    @Test
    fun testsAreRun() {
        assertEquals(4, 2 + 2)
    }

    @Test
    fun testFileSystem() {
        val path = Path("a.txt")
        val text = "Text in file"
        path.newOutputStream().use {
            it.append(text)
        }
        val line = path.newInputStream().use {  it.readUTF8Line() }
        assertEquals(text, line)
        val len = path.fileLen()
        assertEquals(text.toByteArray().size.toLong(), len)
        path.delete()
        assertFalse(path.exists())
    }
}
