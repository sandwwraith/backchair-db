package dev.sandwwraith

sealed class Statement

object Select : Statement()

data class Insert(val row: Row) : Statement()

interface Row
