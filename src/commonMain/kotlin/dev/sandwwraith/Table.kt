package dev.sandwwraith

const val PAGE_SIZE = 4096

inline class Page(val array: ByteArray) {
    companion object {
        fun allocate(): Page = Page(ByteArray(PAGE_SIZE))
    }
}

class Table {
    private val pages: MutableList<Page> = mutableListOf(Page.allocate())
}
