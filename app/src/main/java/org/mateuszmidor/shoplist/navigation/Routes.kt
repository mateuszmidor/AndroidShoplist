package org.mateuszmidor.shoplist.navigation

import android.os.Bundle
import androidx.navigation.NavType
import java.util.UUID
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

@Serializable
object Lists

/**
 * A list identifier carried by the [Items] route argument.
 *
 * kotlinx.serialization has no built-in serializer for [UUID], and Navigation
 * cannot resolve a KSerializer declared via `@Serializable(with = ...)` on a
 * class *field* (it fails at runtime). Declaring it on the [ListId] class
 * declaration solves serialization; the [ListIdNavType] supplied via the
 * destination `typeMap` covers route encoding/decoding. ADR-0007 pins
 * navigation route arguments to this single mechanism.
 */
@Serializable(with = ListIdSerializer::class)
data class ListId(val value: UUID)

@Serializable
data class Items(
    val listId: ListId,
)

/**
 * A combined view over a group of shopping lists (ADR-0012). The selection is
 * carried inline in the route as a list of [ListId]s, so the transient combined
 * view is reconstructed from the route alone and holds no state of its own.
 * Navigation encodes the list argument as comma-joined canonical UUID strings,
 * which the registered [ListIdListNavType] argument type round-trips.
 */
@Serializable
data class Combined(
    val listIds: List<ListId>,
)

/**
 * [NavType] for a route argument carrying a list of [ListId]s (ADR-0012).
 * Encodes the collection as a single non-nullable route argument holding
 * comma-joined canonical UUID strings and reconstructs the list by splitting on
 * the separator. The canonical UUID form never contains a comma, so the join is
 * lossless. Mirrors [ListIdNavType] and round-trips each element via its
 * canonical UUID string (reusing [ListIdSerializer]'s element encoding).
 */
val ListIdListNavType: NavType<List<ListId>> = object : NavType<List<ListId>>(isNullableAllowed = false) {
    override fun put(bundle: Bundle, key: String, value: List<ListId>) {
        bundle.putString(key, value.joinToString(",") { ListIdSerializer.serializeElement(it) })
    }

    override fun get(bundle: Bundle, key: String): List<ListId>? =
        bundle.getString(key)?.split(",")?.map { ListId(UUID.fromString(it)) }

    override fun parseValue(value: String): List<ListId> =
        value.split(",").map { ListId(UUID.fromString(it)) }

    override fun serializeAsValue(value: List<ListId>): String =
        value.joinToString(",") { ListIdSerializer.serializeElement(it) }
}

object ListIdSerializer : KSerializer<ListId> {
    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor("ListId", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: ListId) {
        encoder.encodeString(value.value.toString())
    }

    override fun deserialize(decoder: Decoder): ListId =
        ListId(UUID.fromString(decoder.decodeString()))

    fun serializeElement(value: ListId): String = value.value.toString()
}

/**
 * [NavType] for the [ListId] route argument, stored as the canonical UUID
 * string form. Passed to the route destination via its `typeMap`.
 */
val ListIdNavType: NavType<ListId> = object : NavType<ListId>(isNullableAllowed = false) {
    override fun put(bundle: Bundle, key: String, value: ListId) {
        bundle.putString(key, value.value.toString())
    }

    override fun get(bundle: Bundle, key: String): ListId? =
        bundle.getString(key)?.let { ListId(UUID.fromString(it)) }

    override fun parseValue(value: String): ListId = ListId(UUID.fromString(value))

    override fun serializeAsValue(value: ListId): String = value.value.toString()
}