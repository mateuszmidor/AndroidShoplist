package org.mateuszmidor.shoplist.domain

import java.util.UUID
import org.junit.Assert.assertEquals
import org.junit.Test
import org.mateuszmidor.shoplist.data.ShoppingItemEntity

class CombinedItemSorterTest {

    private val listId = UUID.randomUUID()

    private fun item(name: String, bought: Boolean = false, createdAt: Long = 0, id: UUID = UUID.nameUUIDFromBytes(("$name-$createdAt").toByteArray())) =
        ShoppingItemEntity(id = id, listId = listId, name = name, bought = bought, createdAt = createdAt)

    @Test
    fun sort_uncheckedItemsComeBeforeBoughtItems() {
        val items = listOf(
            item("Milk", bought = true, createdAt = 1),
            item("Bread", bought = false, createdAt = 2),
        )

        val sorted = CombinedItemSorter.sort(items)

        assertEquals(listOf("Bread", "Milk"), sorted.map { it.name })
    }

    @Test
    fun sort_withinUncheckedSection_ordersByName() {
        val items = listOf(
            item("lettuce", createdAt = 1),
            item("chips", createdAt = 2),
            item("eggs", createdAt = 3),
        )

        val sorted = CombinedItemSorter.sort(items)

        assertEquals(listOf("chips", "eggs", "lettuce"), sorted.map { it.name })
    }

    @Test
    fun sort_withinBoughtSection_ordersByName() {
        val items = listOf(
            item("milk", bought = true, createdAt = 3),
            item("apples", bought = true, createdAt = 1),
            item("bread", bought = true, createdAt = 2),
        )

        val sorted = CombinedItemSorter.sort(items)

        assertEquals(listOf("apples", "bread", "milk"), sorted.map { it.name })
    }

    @Test
    fun sort_equalNamedItems_areOrderedByCreationTime() {
        val older = item("cheese", createdAt = 10, id = UUID.randomUUID())
        val newer = item("cheese", createdAt = 20, id = UUID.randomUUID())

        val sorted = CombinedItemSorter.sort(listOf(newer, older))

        assertEquals(listOf(older.id, newer.id), sorted.map { it.id })
    }

    @Test
    fun sort_identicalNameAndCreationTime_orderIsDeterministicById() {
        val first = item("cheese", createdAt = 10, id = UUID.nameUUIDFromBytes("a".toByteArray()))
        val second = item("cheese", createdAt = 10, id = UUID.nameUUIDFromBytes("b".toByteArray()))

        val sortedForward = CombinedItemSorter.sort(listOf(second, first))
        val sortedBackward = CombinedItemSorter.sort(listOf(second, first))

        assertEquals(sortedBackward.map { it.id }, sortedForward.map { it.id })
    }
}