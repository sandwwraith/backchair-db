package dev.sandwwraith.backchairdb

import dev.sandwwraith.backchairdb.model.User
import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals


class UserTest {
    @Test
    fun isSerializable() {
        val user = User(1, "1", "1")
        val s = Json.stringify(User.serializer(), user)
        assertEquals("""{"id":1,"username":"1","email":"1"}""", s)
    }
}
