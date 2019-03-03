package dev.sandwwraith.backchairdb.serialization

import kotlinx.serialization.PrimitiveKind
import kotlinx.serialization.SerialDescriptor
import kotlinx.serialization.elementDescriptors

internal fun entitySize(tableEntityDescriptor: SerialDescriptor): Int {
    val elementDescriptors = tableEntityDescriptor.elementDescriptors()
    check(elementDescriptors.isNotEmpty())
    return elementDescriptors.mapIndexed { i, it ->
        when (it.kind) {
            is PrimitiveKind.INT -> 4
            is PrimitiveKind.STRING -> tableEntityDescriptor.varCharSize(i)
            else -> TODO()
        }
    }.sum()
}
