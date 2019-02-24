package dev.sandwwraith.pager

import dev.sandwwraith.Logger
import kotlinx.fs.core.*
import kotlinx.io.core.discardExact
import kotlinx.io.core.use
import sample.fileLen

const val PAGE_SIZE = 1024

inline class Page(val array: ByteArray) {
    companion object {
        fun allocate(): Page = Page(ByteArray(PAGE_SIZE))
    }
}

class Pager private constructor(private val file: Path, internal val len: Long) {
    private val pages : MutableList<Page?> = MutableList(MAX_PAGES) { null }

    private val pagesCached by lazy {
        var pagesWeHave = len / PAGE_SIZE
        if ((len % PAGE_SIZE.toLong()) != 0.toLong()) pagesWeHave++
        pagesWeHave
    }

    fun getPage(pageNum: Int): Page {
        if (pageNum > MAX_PAGES) throw Exception("Requested $pageNum page, max is $MAX_PAGES")
        if (pages[pageNum] == null) {
            L.debug { "Requested $pageNum was not found in memory" }
            // create empty page
            val newPage = Page.allocate()
            // it may be cached on disk
            if (pageNum <= pagesCached) {
                file.newInputStream().use {
                    it.discard((pageNum * PAGE_SIZE).toLong())
                    it.readAvailable(newPage.array, 0, PAGE_SIZE)
                }
                L.debug { "Requested $pageNum was found on disk" }
            }
            pages[pageNum] = newPage
        }
        return pages[pageNum]!!
    }

    fun flushAll(pagesLimit: Int, lastPageOffset: Int) {
        L.debug { "Flushing $pagesLimit pages + $lastPageOffset bytes on disk..." }
        file.newOutputStream().use {
            for (i in 0 until pagesLimit) {
                val page = pages[i] ?: break
                it.writeFully(page.array, 0, PAGE_SIZE)
            }
            val lastPage = pages[pagesLimit] ?: return
            it.writeFully(lastPage.array, 0, lastPageOffset)
        }
    }

    companion object {
        private val L = Logger("DB PAGER")

        fun open(fileName: String): Pager {
            val path = Paths.getPath(fileName)
            if (!path.exists()) path.createFile()
            val len = path.fileLen()
            L.debug { "Creating a pager with File $path and size of $len" }
            return Pager(path, len)
        }
        const val MAX_PAGES = 100
    }
}
