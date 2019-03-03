package dev.sandwwraith.backchairdb.model

import dev.sandwwraith.backchairdb.Row
import dev.sandwwraith.backchairdb.serialization.VarChar
import kotlinx.serialization.Serializable

@Serializable
data class User(val id: Int, @VarChar(USERNAME_LEN) val username: String, @VarChar(EMAIL_LEN) val email: String) :
    Row {
    companion object {
        const val USERNAME_LEN = 32
        const val EMAIL_LEN = 255
    }
}
