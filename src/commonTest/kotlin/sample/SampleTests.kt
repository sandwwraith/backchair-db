package sample

import kotlinx.fs.core.*
import kotlinx.io.core.readUTF8Line
import kotlinx.io.core.toByteArray
import kotlinx.io.core.use
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class SampleTests {
    @Test
    fun testMe() {
        assertTrue(Sample().checkMe() > 0)
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
