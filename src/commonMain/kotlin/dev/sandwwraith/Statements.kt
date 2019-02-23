package dev.sandwwraith

sealed class Statement

class Select(): Statement()

data class Insert(val row: Row): Statement() {

}

interface Row

data class User(val id: Int, val username: String, val email: String): Row {
    companion object {
        const val USERNAME_LEN = 32
        const val EMAIL_LEN = 255
    }
}
