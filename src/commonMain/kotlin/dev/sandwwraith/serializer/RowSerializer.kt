package dev.sandwwraith.serializer

import kotlinx.serialization.*
import kotlinx.serialization.CompositeDecoder.Companion.READ_DONE


@SerialInfo
@Target(AnnotationTarget.PROPERTY)
annotation class VarChar(val len: Int)

internal fun SerialDescriptor.varCharSize(index: Int): Int {
    return getElementAnnotations(index).filterIsInstance<VarChar>().singleOrNull()?.len
        ?: if (getElementDescriptor(index).kind is PrimitiveKind.STRING) throw IllegalStateException(
            "@VarChar must be specified on String field ${getElementName(
                index
            )}"
        ) else -1
}

object RowSerializer {
    class BitEncoder(private val array: ByteArray, internal var offset: Int) : TaggedEncoder<Int>() {
        override fun SerialDescriptor.getTag(index: Int): Int {
            return varCharSize(index)

        }

        override fun beginStructure(desc: SerialDescriptor, vararg typeParams: KSerializer<*>): CompositeEncoder {
            if (currentTagOrNull != null) throw IllegalStateException("Can't encode nested values")
            return this
        }

        override fun encodeTaggedValue(tag: Int, value: Any) {
            throw TODO("Only Strings and Ints are supported")
        }

        override fun encodeTaggedInt(tag: Int, value: Int) {
            array[offset++] = (value and 0xFF).toByte()
            array[offset++] = ((value shr 8) and 0xFF).toByte()
            array[offset++] = ((value shr 16) and 0xFF).toByte()
            array[offset++] = ((value shr 24) and 0xFF).toByte()
        }

        override fun encodeTaggedString(tag: Int, value: String) {
            check(tag > 0)
            if (value.length > tag) throw SerializationException("Input string length (${value.length} exceeded maximum specified storage size($tag)")
            repeat(tag) {
                val byteToWrite = if (it >= value.length) {
                    0
                } else {
                    val c = value[it]
                    if (c.toByte().toChar() != c) throw IllegalStateException("Non-ASCII chars not supported, got $c")
                    c.toByte()
                }
                array[offset++] = byteToWrite
            }
        }
    }

    class BitDecoder(private val array: ByteArray, internal var offset: Int) : TaggedDecoder<Int>() {
        override fun SerialDescriptor.getTag(index: Int): Int {
            return varCharSize(index)

        }

        private var idx = 0
        override fun decodeElementIndex(desc: SerialDescriptor): Int {
            return if (idx + 1 > desc.elementsCount) READ_DONE else idx++ // READ_ALL is not supported on Native
        }

        override fun decodeTaggedValue(tag: Int): Any {
            throw TODO("Only Strings and Ints are supported")
        }

        override fun decodeTaggedInt(tag: Int): Int {
            var value = 0
            value = value or (array[offset++].toInt() and 0xFF)
            value = (value) or ((array[offset++].toInt() and 0xFF) shl 8)
            value = (value) or ((array[offset++].toInt() and 0xFF) shl 16)
            value = (value) or ((array[offset++].toInt() and 0xFF) shl 24)
            return value
        }

        override fun decodeTaggedString(tag: Int): String {
            check(tag > 0)
            return buildString {
                repeat(tag) {
                    val b = array[offset++]
                    if (b != 0.toByte()) append(b.toChar())
                }
            }
        }
    }
}
