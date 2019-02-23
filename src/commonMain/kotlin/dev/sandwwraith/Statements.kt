package dev.sandwwraith

sealed class Statement

class Select(): Statement()

data class Insert(val row: Row): Statement() {

}

interface Row

