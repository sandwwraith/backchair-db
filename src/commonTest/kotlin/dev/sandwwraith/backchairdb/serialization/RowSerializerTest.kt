package dev.sandwwraith.backchairdb.serialization

import dev.sandwwraith.backchairdb.model.User
import kotlin.test.Test
import kotlin.test.assertEquals

class RowSerializerTest {
    @Test
    fun testSerializeUser() {
        val user = User(447, "John", "foo.bar@example.com")
        val totalLen = 4 + User.USERNAME_LEN + User.EMAIL_LEN
        val arr = ByteArray(totalLen)
        val enc = RowSerializer.BitEncoder(arr, 0)
        User.serializer().serialize(enc, user)
        assertEquals(totalLen, enc.offset)
        val dec = RowSerializer.BitDecoder(arr, 0)
        val user2 = User.serializer().deserialize(dec)
        assertEquals(user, user2)
    }
}
