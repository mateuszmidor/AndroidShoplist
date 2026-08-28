package org.mateuszmidor.shoplist.data

import java.util.UUID
import org.junit.Assert.assertEquals
import org.junit.Test

class ConvertersTest {

    private val converters = Converters()

    @Test
    fun uuidRoundTrip_preservesValue() {
        val uuid = UUID.randomUUID()

        val stored = converters.fromUuid(uuid)
        val restored = converters.toUuid(stored)

        assertEquals(uuid, restored)
    }

    @Test
    fun fromUuid_usesCanonicalString() {
        val uuid = UUID.randomUUID()

        assertEquals(uuid.toString(), converters.fromUuid(uuid))
    }
}